package fin.dam.padel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pistas")
@CrossOrigin(origins = "*")
public class PistaController {

    @GetMapping
    public ResponseEntity<?> obtenerPistas() {
        return ResponseEntity.ok(List.of(Map.of("id", 1, "nombre", "Pista 1", "disponible", true)));
    }

    @PostMapping
    public ResponseEntity<?> agregarPista(@RequestBody Map<String, String> pistaData) {
        return ResponseEntity.ok(Map.of("message", "Pista agregada con éxito", "nombre", pistaData.get("nombre")));
    }
}
