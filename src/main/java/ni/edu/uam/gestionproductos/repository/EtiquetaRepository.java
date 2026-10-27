package ni.edu.uam.gestionproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ni.edu.uam.gestionproductos.entity.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
}
