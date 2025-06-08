// src/main/java/fin/dam/padel/controller/PublicacionController.java

package fin.dam.padel.controller;

import fin.dam.padel.model.Publicacion;
import fin.dam.padel.repository.PublicacionRepository;
import fin.dam.padel.service.PublicacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final PublicacionRepository publicacionRepo;

    public PublicacionController(PublicacionService publicacionService, PublicacionRepository publicacionRepo) {
        this.publicacionService = publicacionService;
        this.publicacionRepo = publicacionRepo;
    }

    @PostMapping
    public ResponseEntity<?> crearPublicacion(@RequestBody Map<String, Object> datos) {
        return publicacionService.crearPublicacion(datos);
    }

    @GetMapping
    public List<Publicacion> listarPublicaciones() {
        return publicacionService.listarPublicaciones();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!publicacionRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        publicacionRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/por-comunidad/{comunidadId}")
    public List<Publicacion> obtenerPorComunidadId(@PathVariable Long comunidadId) {
        System.out.println("📥 Solicitando publicaciones para comunidadId: " + comunidadId);
        return publicacionRepo.findByComunidadIdOrderByFechaDesc(comunidadId);
    }
}