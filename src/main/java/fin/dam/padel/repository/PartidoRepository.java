package fin.dam.padel.repository;

import fin.dam.padel.model.Partido;
import fin.dam.padel.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

    @Query("SELECT p FROM Partido p WHERE p.pista.comunidad.id = :comunidadId")
    List<Partido> findByComunidadId(Long comunidadId);

    @Query("SELECT COUNT(p) > 0 FROM Partido p WHERE p.pista.id = :pistaId AND p.fecha = :fecha AND p.horaInicio = :horaInicio")
    boolean existsPartidoEnPista(Long pistaId, LocalDate fecha, LocalTime horaInicio);

    @Query("SELECT COUNT(p) > 0 FROM Partido p WHERE p.creador.id = :usuarioId AND p.fecha = :fecha AND p.horaInicio = :horaInicio")
    boolean existsPartidoCreadoPorUsuario(Long usuarioId, LocalDate fecha, LocalTime horaInicio);

    @Query("SELECT COUNT(p) > 0 FROM Partido p JOIN p.participantes u WHERE u.id = :usuarioId AND p.fecha = :fecha AND p.horaInicio = :horaInicio")
    boolean existsUsuarioParticipando(Long usuarioId, LocalDate fecha, LocalTime horaInicio);
    
    List<Partido> findByCreador(Usuario creador);
}
