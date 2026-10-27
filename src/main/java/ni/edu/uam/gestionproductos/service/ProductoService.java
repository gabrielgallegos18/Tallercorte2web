package ni.edu.uam.gestionproductos.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ni.edu.uam.gestionproductos.dto.ProductoRequestDTO;
import ni.edu.uam.gestionproductos.dto.ProductoResponseDTO;
import ni.edu.uam.gestionproductos.entity.Categoria;
import ni.edu.uam.gestionproductos.entity.Etiqueta;
import ni.edu.uam.gestionproductos.entity.Producto;
import ni.edu.uam.gestionproductos.repository.CategoriaRepository;
import ni.edu.uam.gestionproductos.repository.EtiquetaRepository;
import ni.edu.uam.gestionproductos.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EtiquetaRepository etiquetaRepository;

    public ProductoService(ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository, EtiquetaRepository etiquetaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.etiquetaRepository = etiquetaRepository;
    }

    public List<ProductoResponseDTO> listar() {
        return convertir(productoRepository.findAll());
    }

    public ProductoResponseDTO buscarPorId(Integer id) {
        return ProductoResponseDTO.desde(buscarEntidad(id));
    }

    private Producto buscarEntidad(Integer id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }

    private Categoria buscarCategoria(Integer id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
    }

    private Etiqueta buscarEtiqueta(Integer id) {
        return etiquetaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Etiqueta no encontrada"));
    }

    @Transactional
    public ProductoResponseDTO guardar(ProductoRequestDTO dto) {
        String codigo = dto.getCodigo().strip();
        if (productoRepository.existsByCodigo(codigo)) {
            throw new ConflictoException("El código del producto ya existe");
        }
        Producto producto = new Producto();
        aplicar(producto, dto);
        return ProductoResponseDTO.desde(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDTO actualizar(Integer id, ProductoRequestDTO dto) {
        Producto producto = buscarEntidad(id);
        if (productoRepository.existsByCodigoAndIdNot(dto.getCodigo().strip(), id)) {
            throw new ConflictoException("El código del producto ya existe");
        }
        aplicar(producto, dto);
        return ProductoResponseDTO.desde(productoRepository.save(producto));
    }

    private void aplicar(Producto producto, ProductoRequestDTO dto) {
        producto.setCategoria(buscarCategoria(dto.getCategoriaId()));
        producto.setCodigo(dto.getCodigo().strip());
        producto.setNombre(dto.getNombre().strip());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setExistencia(dto.getExistencia());
    }

    @Transactional
    public void eliminar(Integer id) {
        productoRepository.delete(buscarEntidad(id));
    }

    public List<ProductoResponseDTO> listarPorCategoria(Integer categoriaId) {
        buscarCategoria(categoriaId);
        return convertir(productoRepository.findByCategoriaId(categoriaId));
    }

    public List<ProductoResponseDTO> listarPorEtiqueta(Integer etiquetaId) {
        buscarEtiqueta(etiquetaId);
        return convertir(productoRepository.findByEtiquetasId(etiquetaId));
    }

    @Transactional
    public ProductoResponseDTO agregarEtiqueta(Integer productoId, Integer etiquetaId) {
        Producto producto = buscarEntidad(productoId);
        producto.getEtiquetas().add(buscarEtiqueta(etiquetaId));
        return ProductoResponseDTO.desde(producto);
    }

    @Transactional
    public void eliminarEtiqueta(Integer productoId, Integer etiquetaId) {
        Producto producto = buscarEntidad(productoId);
        Etiqueta etiqueta = buscarEtiqueta(etiquetaId);
        if (!producto.getEtiquetas().remove(etiqueta)) {
            throw new RecursoNoEncontradoException("El producto no tiene esa etiqueta");
        }
    }

    private List<ProductoResponseDTO> convertir(List<Producto> productos) {
        return productos.stream().map(ProductoResponseDTO::desde).toList();
    }
}
