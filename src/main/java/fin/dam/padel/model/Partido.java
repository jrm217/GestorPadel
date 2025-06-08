package fin.dam.padel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Partido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "pista_id")
	@JsonIgnoreProperties({"partidos", "reservas", "comunidad"})
	private Pista pista;

	private LocalDate fecha;
	@JsonFormat(pattern = "HH:mm")
	private LocalTime horaInicio;
	@JsonFormat(pattern = "HH:mm")
	private LocalTime horaFin;

	@ManyToOne
	@JoinColumn(name = "creador_id")
	@JsonIgnoreProperties({"password", "reservas", "partidos", "comunidad"})
	private Usuario creador;

	@ManyToMany
	@JoinTable(
		name = "partido_usuarios",
		joinColumns = @JoinColumn(name = "partido_id"),
		inverseJoinColumns = @JoinColumn(name = "usuario_id")
	)
	@JsonIgnoreProperties({"password", "reservas", "partidos", "comunidad"})
	private Set<Usuario> participantes = new HashSet<>();

	private String estado = "PENDIENTE"; // o CONFIRMADO, CANCELADO

	// Getters y setters...

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pista getPista() {
		return pista;
	}

	public void setPista(Pista pista) {
		this.pista = pista;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public Usuario getCreador() {
		return creador;
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
	}

	public Set<Usuario> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(Set<Usuario> participantes) {
		this.participantes = participantes;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
