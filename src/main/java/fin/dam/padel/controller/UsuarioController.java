package fin.dam.padel.controller;

import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.ComunidadRepository;
import fin.dam.padel.repository.UsuarioRepository;
import fin.dam.padel.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final ComunidadRepository comunidadRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, ComunidadRepository comunidadRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.comunidadRepository = comunidadRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        System.out.println("Intentando registrar: " + usuario.getEmail());

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya está registrado.");
        }

        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellidos(usuarioActualizado.getApellidos());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setRol(usuarioActualizado.getRol());

            if (usuarioActualizado.getComunidad() != null && usuarioActualizado.getComunidad().getId() != null) {
                comunidadRepository.findById(usuarioActualizado.getComunidad().getId()).ifPresent(usuario::setComunidad);
            }

            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }
    
    @PostMapping(value = "/google", produces = "application/json;charset=UTF-8")

    public ResponseEntity<Usuario> crearOObtenerDesdeGoogle(@RequestBody Usuario nuevo) {
        Optional<Usuario> existente = usuarioRepository.findByEmail(nuevo.getEmail());

        if (existente.isPresent()) {
            Usuario usuario = existente.get();

            // Actualizar el token si ha cambiado
            if (nuevo.getFcmToken() != null && !nuevo.getFcmToken().equals(usuario.getFcmToken())) {
                usuario.setFcmToken(nuevo.getFcmToken());
                usuarioRepository.save(usuario);
            }

            return ResponseEntity.ok(usuario);
        }

        nuevo.setRol("USER");
        nuevo.setComunidad(null); // o comunidad por defecto
        Usuario creado = usuarioRepository.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

}