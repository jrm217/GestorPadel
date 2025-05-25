// src/main/java/fin/dam/padel/repository/ReservaRepository.java

package fin.dam.padel.repository;

import fin.dam.padel.model.Pista;
import fin.dam.padel.model.Reserva;
import fin.dam.padel.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r WHERE r.pista = :pista AND " +
           "((:horaInicio BETWEEN r.horaInicio AND r.horaFin) OR " +
           "(:horaFin BETWEEN r.horaInicio AND r.horaFin))")
    Optional<Reserva> findByPistaAndHorario(@Param("pista") Pista pista,
                                            @Param("horaInicio") LocalTime horaInicio,
                                            @Param("horaFin") LocalTime horaFin);

    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.horaInicio = :inicio AND r.horaFin = :fin")
    Optional<Reserva> findByUsuarioAndHorario(@Param("usuario") Usuario usuario,
                                              @Param("inicio") LocalTime inicio,
                                              @Param("fin") LocalTime fin);

    List<Reserva> findByUsuarioId(Long usuarioId);
}
