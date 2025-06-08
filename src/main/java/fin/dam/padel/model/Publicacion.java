package fin.dam.padel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "publicacion")
public class Publicacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String comentario;

	@Column(name = "imagen_url")
	private String imagenUrl;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	@JsonIgnoreProperties({"reservas", "publicaciones", "comunidad", "password"})
	private Usuario autor;

	@ManyToOne
	@JoinColumn(name = "comunidad_id")
	@JsonIgnoreProperties({"usuarios", "pistas"})
	private Comunidad comunidad;

	@Column(nullable = false)
	private LocalDateTime fecha;

	public Publicacion() {
	}

	public Publicacion(String comentario, String imagenUrl, Usuario autor, LocalDateTime fecha) {
		this.comentario = comentario;
		this.imagenUrl = imagenUrl;
		this.autor = autor;
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	
	public Comunidad getComunidad() {
		return comunidad;
	}
	
	public void setComunidad (Comunidad comunidad) {
		this.comunidad = comunidad;
	}
}
