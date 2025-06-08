package fin.dam.padel.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pista")
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private boolean disponible;

    @OneToMany(mappedBy = "pista", cascade = CascadeType.ALL)
    private List<Reserva> reservas;
    
    @ManyToOne
    @JoinColumn(name = "comunidad_id")
    private Comunidad comunidad;


    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public Comunidad getComunidad() {
		return comunidad;
	}
	
	public void setComunidad (Comunidad comunidad) {
		this.comunidad = comunidad;
	}
}
