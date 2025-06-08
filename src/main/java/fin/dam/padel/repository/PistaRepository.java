package fin.dam.padel.repository;

import fin.dam.padel.model.Pista;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {

    Optional<Pista> findByNombre(String nombre);
    List<Pista> findByComunidadId(Long comunidadId);
    List<Pista> findByNombreAndComunidadId(String nombre, Long comunidadId);
}
