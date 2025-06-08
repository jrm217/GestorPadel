package fin.dam.padel.repository;

import fin.dam.padel.model.Pista;
import fin.dam.padel.model.Reserva;
import fin.dam.padel.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Evita solapamientos de reservas (misma pista, misma fecha)
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.pista = :pista
          AND r.fecha = :fecha
          AND r.horaInicio < :horaFin
          AND r.horaFin > :horaInicio
    """)
    Optional<Reserva> findSolapada(
        @Param("pista") Pista pista,
        @Param("fecha") LocalDate fecha,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.usuario = :usuario
          AND r.fecha = :fecha
          AND r.horaInicio < :horaFin
          AND r.horaFin > :horaInicio
    """)
    Optional<Reserva> findByUsuarioAndHorario(
        @Param("usuario") Usuario usuario,
        @Param("fecha") LocalDate fecha,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin
    );

    List<Reserva> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.fecha = :fecha")
    List<Reserva> findByUsuarioAndFecha(@Param("usuario") Usuario usuario,
                                        @Param("fecha") LocalDate fecha);
    
    boolean existsByPistaIdAndFechaAndHoraInicio(Long pistaId, LocalDate fecha, LocalTime horaInicio);
}


