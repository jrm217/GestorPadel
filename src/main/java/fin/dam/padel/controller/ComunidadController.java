package fin.dam.padel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fin.dam.padel.model.Comunidad;
import fin.dam.padel.repository.ComunidadRepository;

@RestController
@RequestMapping("/api/comunidades")
@CrossOrigin(origins = "*")
public class ComunidadController {

    @Autowired
    private ComunidadRepository comunidadRepo;

    @GetMapping
    public List<Comunidad> listar() {
        return comunidadRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearComunidad(@RequestBody Map<String, String> datos, @RequestHeader("user-rol") String rol) {
        if (!"superadmin".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        String nombre = datos.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nombre requerido");
        }

        Comunidad nueva = new Comunidad();
        nueva.setNombre(nombre);
        comunidadRepo.save(nueva);

        return new ResponseEntity<>(nueva, HttpStatus.CREATED); // 👈 Ahora devuelve 201
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarComunidad(
            @PathVariable Long id,
            @RequestBody Map<String, Object> datos,
            @RequestHeader("Authorization") String token) {

        Comunidad comunidad = comunidadRepo.findById(id).orElse(null);
        if (comunidad == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comunidad no encontrada");
        }

        if (datos.containsKey("nombre")) {
            comunidad.setNombre((String) datos.get("nombre"));
        }

        if (datos.containsKey("numeroPistas")) {
            comunidad.setNumeroPistas((Integer) datos.get("numeroPistas"));
        }

        comunidadRepo.save(comunidad);

        return ResponseEntity.ok(comunidad);
    }

    
}
