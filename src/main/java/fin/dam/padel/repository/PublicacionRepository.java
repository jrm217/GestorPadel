package fin.dam.padel.repository;

import fin.dam.padel.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findAllByOrderByFechaDesc();
    List<Publicacion> findByComunidadId(Long comunidadId);
    List<Publicacion> findByComunidadIdOrderByFechaDesc(Long comunidadId);

}
