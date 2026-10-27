package ni.edu.uam.gestionproductos.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ni.edu.uam.gestionproductos.dto.ProductoRequestDTO;
import ni.edu.uam.gestionproductos.dto.ProductoResponseDTO;
import ni.edu.uam.gestionproductos.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponseDTO> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ProductoResponseDTO buscar(@PathVariable @Positive Integer id) {
        return productoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> guardar(@Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO producto = productoService.guardar(dto);
        return ResponseEntity.created(URI.create("/api/productos/" + producto.id())).body(producto);
    }

    @PutMapping("/{id}")
    public ProductoResponseDTO actualizar(@PathVariable @Positive Integer id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        return productoService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoriaId}")
    public List<ProductoResponseDTO> listarPorCategoria(@PathVariable @Positive Integer categoriaId) {
        return productoService.listarPorCategoria(categoriaId);
    }

    @GetMapping("/etiqueta/{etiquetaId}")
    public List<ProductoResponseDTO> listarPorEtiqueta(@PathVariable @Positive Integer etiquetaId) {
        return productoService.listarPorEtiqueta(etiquetaId);
    }

    @PostMapping("/{productoId}/etiquetas/{etiquetaId}")
    public ProductoResponseDTO agregarEtiqueta(@PathVariable @Positive Integer productoId,
            @PathVariable @Positive Integer etiquetaId) {
        return productoService.agregarEtiqueta(productoId, etiquetaId);
    }

    @DeleteMapping("/{productoId}/etiquetas/{etiquetaId}")
    public ResponseEntity<Void> eliminarEtiqueta(@PathVariable @Positive Integer productoId,
            @PathVariable @Positive Integer etiquetaId) {
        productoService.eliminarEtiqueta(productoId, etiquetaId);
        return ResponseEntity.noContent().build();
    }
}
