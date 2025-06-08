package fin.dam.padel.controller;

import fin.dam.padel.model.Partido;
import fin.dam.padel.model.Pista;
import fin.dam.padel.model.Reserva;
import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.PartidoRepository;
import fin.dam.padel.repository.PistaRepository;
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
    private final PistaRepository pistaRepository;
    private final PartidoRepository partidoRepository;

    public ReservaController(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              PistaRepository pistaRepository,
                              PartidoRepository partidoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pistaRepository = pistaRepository;
        this.partidoRepository = partidoRepository;
    }

    @GetMapping
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, Object> datos) {
        if (!datos.containsKey("usuarioId") || !datos.containsKey("pista") || !datos.containsKey("fechaHora") || !datos.containsKey("comunidadId")) {
            return ResponseEntity.badRequest().body("Faltan parámetros obligatorios.");
        }

        Long usuarioId = Long.parseLong(datos.get("usuarioId").toString());
        String pistaNombre = datos.get("pista").toString();
        Long comunidadId = Long.parseLong(datos.get("comunidadId").toString());
        String fechaHora = datos.get("fechaHora").toString();

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        List<Pista> pistas = pistaRepository.findByNombreAndComunidadId(pistaNombre, comunidadId);
        if (pistas.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontró la pista con ese nombre en la comunidad.");
        }

        Pista pista = pistas.get(0);

        LocalDateTime fechaYHora;
        try {
            fechaYHora = LocalDateTime.parse(fechaHora);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de fecha y hora incorrecto. Usa YYYY-MM-DDTHH:mm.");
        }

        LocalDate fecha = fechaYHora.toLocalDate();
        LocalTime inicio = fechaYHora.toLocalTime();
        LocalTime fin = inicio.plusMinutes(90);

        // Validación: el usuario ya tiene una reserva exactamente en ese horario
        Optional<Reserva> reservaUsuario = reservaRepository.findByUsuarioAndHorario(usuarioOpt.get(), fecha, inicio, fin);
        if (reservaUsuario.isPresent()) {
            return ResponseEntity.badRequest().body("Ya tienes una reserva en este horario.");
        }

        // Validación: máximo 2 reservas por día
        List<Reserva> reservasDelDia = reservaRepository.findByUsuarioAndFecha(usuarioOpt.get(), fecha);
        if (reservasDelDia.size() >= 2) {
            return ResponseEntity.badRequest().body("Has alcanzado el máximo de 2 reservas para este día.");
        }

        // Validación: la pista ya está ocupada por otra reserva
        Optional<Reserva> reservaExistente = reservaRepository.findSolapada(pista, fecha, inicio, fin);
        if (reservaExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Pista ya reservada en este horario.");
        }

        // 🔴 NUEVA VALIDACIÓN: la pista tiene un partido en ese horario
        boolean hayPartido = partidoRepository.existsPartidoEnPista(pista.getId(), fecha, inicio);
        if (hayPartido) {
            return ResponseEntity.badRequest().body("No se puede reservar la pista. Ya hay un partido programado en ese horario.");
        }

        // Crear reserva
        Reserva nuevaReserva = new Reserva(usuarioOpt.get(), pista, fecha, inicio, fin);
        reservaRepository.save(nuevaReserva);

        return ResponseEntity.ok(nuevaReserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Reserva>> obtenerTodasLasReservas() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaRepository.findByUsuarioId(usuarioId));
    }
}

