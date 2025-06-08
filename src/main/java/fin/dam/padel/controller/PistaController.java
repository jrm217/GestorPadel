package fin.dam.padel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fin.dam.padel.model.Comunidad;
import fin.dam.padel.model.Pista;
import fin.dam.padel.repository.ComunidadRepository;
import fin.dam.padel.service.PistaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pistas")
@CrossOrigin(origins = "*")
public class PistaController {

    @Autowired
    private PistaService pistaService;

    @Autowired
    private ComunidadRepository comunidadRepo;

    @GetMapping
    public ResponseEntity<?> obtenerPistas() {
        return ResponseEntity.ok(List.of(Map.of("id", 1, "nombre", "Pista 1", "disponible", true)));
    }

    @PostMapping
    public ResponseEntity<?> agregarPista(@RequestBody Map<String, Object> datos) {
        String nombre = (String) datos.get("nombre");
        Integer comunidadId = (Integer) datos.get("comunidadId");

        if (nombre == null || comunidadId == null) {
            return ResponseEntity.badRequest().body("Faltan datos: nombre o comunidadId");
        }

        Comunidad comunidad = comunidadRepo.findById(Long.valueOf(comunidadId))
            .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        Pista pista = new Pista();
        pista.setNombre(nombre);
        pista.setDisponible(true);
        pista.setComunidad(comunidad);

        pistaService.crearPista(pista);

        return ResponseEntity.ok(Map.of("message", "Pista creada", "id", pista.getId()));
    }

    @GetMapping("/comunidad/{id}")
    public List<Pista> obtenerPorComunidad(@PathVariable Long id) {
        return pistaService.obtenerPorComunidad(id);
    }
}