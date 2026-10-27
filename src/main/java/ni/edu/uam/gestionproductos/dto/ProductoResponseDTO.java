package ni.edu.uam.gestionproductos.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Comparator;
import ni.edu.uam.gestionproductos.entity.Producto;

public record ProductoResponseDTO(Integer id, String codigo, String nombre,
        BigDecimal precioVenta, Integer existencia, Referencia categoria, List<Referencia> etiquetas) {
    public record Referencia(Integer id, String nombre) { }
    public static ProductoResponseDTO desde(Producto producto) {
        return new ProductoResponseDTO(producto.getId(), producto.getCodigo(), producto.getNombre(),
            producto.getPrecioVenta(), producto.getExistencia(),
            new Referencia(producto.getCategoria().getId(), producto.getCategoria().getNombre()),
            producto.getEtiquetas().stream().map(e -> new Referencia(e.getId(), e.getNombre()))
                .sorted(Comparator.comparing(Referencia::id)).toList());
    }
}
