package fin.dam.padel.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String pista;
    private LocalDate fecha; // Agregar campo de fecha
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Reserva() {}

    public Reserva(Usuario usuario, String pista, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.usuario = usuario;
        this.pista = pista;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getPista() { return pista; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
}

