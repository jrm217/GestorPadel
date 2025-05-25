package fin.dam.padel.repository;

import fin.dam.padel.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PistaRepository extends JpaRepository<Pista, Long> {
}
