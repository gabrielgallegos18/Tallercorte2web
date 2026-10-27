package ni.edu.uam.gestionproductos.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ni.edu.uam.gestionproductos.dto.NombreRequestDTO;
import ni.edu.uam.gestionproductos.entity.Categoria;
import ni.edu.uam.gestionproductos.repository.CategoriaRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listar() {
        return repository.findAll();
    }

    @Transactional
    public Categoria guardar(NombreRequestDTO dto) {
        String nombre = dto.nombre().strip();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("El nombre ya existe");
        }
        Categoria entidad = new Categoria();
        entidad.setNombre(nombre);
        return repository.save(entidad);
    }
}
