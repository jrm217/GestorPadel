package fin.dam.padel.controller;

import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        System.out.println("Intentando iniciar sesión con: " + usuario.getEmail());

        if (usuario.getEmail() == null || usuario.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email y contraseña son obligatorios.");
        }

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail(usuario.getEmail());

        if (usuarioEncontrado.isEmpty()) {
            System.out.println("Usuario no encontrado con email: " + usuario.getEmail());
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }

        Usuario user = usuarioEncontrado.get();
        
        // Verificar la contraseña manualmente
        if (!user.getPassword().equals(usuario.getPassword())) {
            System.out.println("Contraseña incorrecta para el usuario: " + user.getEmail());
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }

        System.out.println("Inicio de sesión exitoso: " + user.getEmail());
        return ResponseEntity.ok(user);
    }
}