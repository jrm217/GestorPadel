package fin.dam.padel.repository;

import fin.dam.padel.model.SolicitudUnirsePartido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudUnirsePartidoRepository extends JpaRepository<SolicitudUnirsePartido, Long> {
    List<SolicitudUnirsePartido> findByPartidoId(Long partidoId);
    Optional<SolicitudUnirsePartido> findByPartidoIdAndSolicitanteId(Long partidoId, Long solicitanteId);
}
