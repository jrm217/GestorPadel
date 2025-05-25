package fin.dam.padel.controller;

import fin.dam.padel.model.Reserva;
import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.ReservaRepository;
import fin.dam.padel.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, Object> datos) {
        if (!datos.containsKey("usuarioId") || !datos.containsKey("pista") || !datos.containsKey("fechaHora")) {
            return ResponseEntity.badRequest().body("Faltan parámetros obligatorios.");
        }

        Long usuarioId = Long.parseLong(datos.get("usuarioId").toString());
        String pista = datos.get("pista").toString();
        String fechaHora = datos.get("fechaHora").toString();

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        LocalDateTime fechaYHora;
        try {
            fechaYHora = LocalDateTime.parse(fechaHora);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de fecha y hora incorrecto. Usa YYYY-MM-DDTHH:mm.");
        }

        LocalDate fecha = fechaYHora.toLocalDate();
        LocalTime inicio = fechaYHora.toLocalTime();
        LocalTime fin = inicio.plusMinutes(90);

        Optional<Reserva> reservaUsuario = reservaRepository.findByUsuarioAndHorario(usuarioOpt.get(), inicio, fin);
        if (reservaUsuario.isPresent()) {
            return ResponseEntity.badRequest().body("Ya tienes una reserva en este horario.");
        }

        Optional<Reserva> reservaExistente = reservaRepository.findByPistaAndHorario(pista, inicio, fin);
        if (reservaExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Pista ya reservada en este horario");
        }

        Reserva nuevaReserva = new Reserva(usuarioOpt.get(), pista, fecha, inicio, fin);
        reservaRepository.save(nuevaReserva);

        System.out.println("✔️ Reserva creada: " +
                "Usuario " + usuarioOpt.get().getNombre() +
                " | Día " + fecha +
                " | Horario " + inicio + " - " + fin);

        return ResponseEntity.ok(nuevaReserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            System.out.println("🗑️ Reserva cancelada con ID: " + id);
            return ResponseEntity.noContent().build();
        }
        System.out.println("⚠️ Intento de cancelar una reserva inexistente. ID: " + id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Reserva>> obtenerTodasLasReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorUsuario(@PathVariable Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(reservas);
    }
}