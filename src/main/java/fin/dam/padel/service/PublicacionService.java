// src/main/java/fin/dam/padel/service/PublicacionService.java

package fin.dam.padel.service;

import fin.dam.padel.model.Publicacion;
import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.PublicacionRepository;
import fin.dam.padel.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    public PublicacionService(PublicacionRepository publicacionRepository, UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ResponseEntity<?> crearPublicacion(Map<String, Object> datos) {
        if (!datos.containsKey("usuarioId") || !datos.containsKey("comentario")) {
            return ResponseEntity.badRequest().body("Faltan campos obligatorios");
        }

        Long usuarioId = Long.parseLong(datos.get("usuarioId").toString());
        String comentario = datos.get("comentario").toString();
        String urlImagen = datos.containsKey("urlImagen") ? datos.get("urlImagen").toString() : null;

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Publicacion p = new Publicacion();
        p.setComentario(comentario);
        p.setImagenUrl(urlImagen);
        p.setAutor(usuarioOpt.get());
        p.setFecha(LocalDateTime.now());
        p.setComunidad(usuarioOpt.get().getComunidad());

        publicacionRepository.save(p);
        return ResponseEntity.ok(p);
    }

    public List<Publicacion> listarPublicaciones() {
        return publicacionRepository.findAll();
    }
    
    public List<Publicacion> listarPorComunidad(Long comunidadId) {
        return publicacionRepository.findByComunidadIdOrderByFechaDesc(comunidadId);
    }

}
