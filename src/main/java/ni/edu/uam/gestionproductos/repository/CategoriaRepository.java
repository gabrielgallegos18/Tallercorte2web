package ni.edu.uam.gestionproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ni.edu.uam.gestionproductos.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
}
