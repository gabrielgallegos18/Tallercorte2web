package ni.edu.uam.gestionproductos.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ni.edu.uam.gestionproductos.dto.NombreRequestDTO;
import ni.edu.uam.gestionproductos.entity.Etiqueta;
import ni.edu.uam.gestionproductos.repository.EtiquetaRepository;

@Service
@Transactional(readOnly = true)
public class EtiquetaService {
    private final EtiquetaRepository repository;

    public EtiquetaService(EtiquetaRepository repository) {
        this.repository = repository;
    }

    public List<Etiqueta> listar() {
        return repository.findAll();
    }

    @Transactional
    public Etiqueta guardar(NombreRequestDTO dto) {
        String nombre = dto.nombre().strip();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("El nombre ya existe");
        }
        Etiqueta entidad = new Etiqueta();
        entidad.setNombre(nombre);
        return repository.save(entidad);
    }
}
