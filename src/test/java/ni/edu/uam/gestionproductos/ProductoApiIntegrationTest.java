package ni.edu.uam.gestionproductos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ni.edu.uam.gestionproductos.repository.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductoApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ProductoRepository productos;
    @Autowired EtiquetaRepository etiquetas;

    private JsonNode crear(String ruta, String json) throws Exception {
        return mapper.readTree(mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    private String datos(String codigo, int categoria, String precio, int existencia) {
        return """
            {"codigo":"%s","nombre":"Teclado mecánico","precioVenta":%s,"existencia":%d,"categoriaId":%d}
            """.formatted(codigo, precio, existencia, categoria);
    }
    @Test
    void crudRelacionesYRetosConPersistenciaReal() throws Exception {
        int categoria = crear("/api/categorias", "{\"nombre\":\"Categoría de prueba\"}").get("id").asInt();
        int etiqueta = crear("/api/etiquetas", "{\"nombre\":\"Etiqueta de prueba\"}").get("id").asInt();
        int producto = crear("/api/productos", datos("PRUEBA-CRUD", categoria, "75.50", 20)).get("id").asInt();
        String ruta = "/api/productos/" + producto;
        mvc.perform(get("/api/productos")).andExpect(status().isOk());
        mvc.perform(get(ruta)).andExpect(status().isOk()).andExpect(jsonPath("$.categoria.id").value(categoria));
        mvc.perform(put(ruta).contentType(MediaType.APPLICATION_JSON)
            .content(datos("PRUEBA-CRUD", categoria, "80.00", 15)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.existencia").value(15));
        mvc.perform(get("/api/productos/categoria/" + categoria))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(producto));
        String asociacion = ruta + "/etiquetas/" + etiqueta;
        mvc.perform(post(asociacion)).andExpect(status().isOk()).andExpect(jsonPath("$.etiquetas.length()").value(1));
        mvc.perform(post(asociacion)).andExpect(status().isOk()).andExpect(jsonPath("$.etiquetas.length()").value(1));
        mvc.perform(get("/api/productos/etiqueta/" + etiqueta))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(producto));
        mvc.perform(delete(asociacion)).andExpect(status().isNoContent());
        assertTrue(productos.existsById(producto));
        assertTrue(etiquetas.existsById(etiqueta));
        mvc.perform(get("/api/productos/etiqueta/" + etiqueta))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete(asociacion)).andExpect(status().isNotFound());
        mvc.perform(post(asociacion)).andExpect(status().isOk());
        mvc.perform(delete(ruta)).andExpect(status().isNoContent());
        productos.flush();
        assertFalse(productos.existsById(producto));
        assertTrue(etiquetas.existsById(etiqueta));
        mvc.perform(get(ruta)).andExpect(status().isNotFound());
        mvc.perform(delete(ruta)).andExpect(status().isNotFound());
    }
    @Test
    void rechazaDatosInvalidosDuplicadosYRecursosInexistentes() throws Exception {
        int categoria = crear("/api/categorias", "{\"nombre\":\"Validaciones\"}").get("id").asInt();
        String valido = datos("PRUEBA-VALIDACION", categoria, "10.00", 1);
        crear("/api/productos", valido);
        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(valido))
            .andExpect(status().isConflict());
        for (String json : new String[] {
                datos("NEGATIVO", categoria, "-1.00", 1),
                datos("STOCK", categoria, "1.00", -1),
                datos("DECIMALES", categoria, "1.123", 1),
                datos(" ", categoria, "1.00", 1),
                "{}",
                valido.replace("\"existencia\":1", "\"existencia\":1.5"),
                valido.replace("\"nombre\":", "\"desconocido\":"),
                "{mal"}) {
            mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        }
        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON)
            .content(datos("SIN-CATEGORIA", 2147483647, "1.00", 1))).andExpect(status().isNotFound());
        mvc.perform(get("/api/productos/0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/productos/abc")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/productos/2147483647")).andExpect(status().isNotFound());
        mvc.perform(get("/api/productos/categoria/2147483647")).andExpect(status().isNotFound());
        mvc.perform(get("/api/productos/etiqueta/2147483647")).andExpect(status().isNotFound());
        crear("/api/etiquetas", "{\"nombre\":\"Duplicada\"}");
        mvc.perform(post("/api/etiquetas").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"duplicada\"}")).andExpect(status().isConflict());
    }
}
