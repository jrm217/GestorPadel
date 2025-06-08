package fin.dam.padel.repository;

import fin.dam.padel.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuario solo por email (sin contraseña)
    Optional<Usuario> findByEmail(String email);

    // Verificar si un email ya existe
    boolean existsByEmail(String email);
    long countByRol(String rol);
}
