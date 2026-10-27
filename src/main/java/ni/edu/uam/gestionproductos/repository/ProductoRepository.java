package ni.edu.uam.gestionproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ni.edu.uam.gestionproductos.entity.Producto;
import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByCategoriaId(Integer categoriaId);
    List<Producto> findByEtiquetasId(Integer etiquetaId);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Integer id);
}
