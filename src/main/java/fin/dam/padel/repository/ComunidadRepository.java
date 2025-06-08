package fin.dam.padel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fin.dam.padel.model.Comunidad;

public interface ComunidadRepository extends JpaRepository<Comunidad, Long> {
    Optional<Comunidad> findByNombre(String nombre);
}