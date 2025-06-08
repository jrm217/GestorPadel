package fin.dam.padel.controller;

import fin.dam.padel.model.Partido;
import fin.dam.padel.model.Pista;
import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.PartidoRepository;
import fin.dam.padel.repository.PistaRepository;
import fin.dam.padel.repository.ReservaRepository;
import fin.dam.padel.repository.SolicitudUnirsePartidoRepository;
import fin.dam.padel.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoController {

    @Autowired
    private PartidoRepository partidoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PistaRepository pistaRepo;

    @Autowired
    private ReservaRepository reservaRepo;
    
    @Autowired
    private SolicitudUnirsePartidoRepository solicitudRepo;

    @PostMapping
    public ResponseEntity<?> crearPartido(@RequestBody Map<String, Object> datos) {
        try {
            Long creadorId = Long.valueOf(datos.get("creadorId").toString());
            Long pistaId = Long.valueOf(datos.get("pistaId").toString());
            String fechaStr = datos.get("fecha").toString();
            String horaInicioStr = datos.get("horaInicio").toString();

            Optional<Usuario> creador = usuarioRepo.findById(creadorId);
            Optional<Pista> pista = pistaRepo.findById(pistaId);

            if (creador.isEmpty() || pista.isEmpty()) {
                return ResponseEntity.badRequest().body("Datos inválidos: pista o creador no encontrados");
            }

            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);

            int minutosDesdeInicio = (horaInicio.toSecondOfDay() - LocalTime.of(9, 0).toSecondOfDay()) / 60;
            if (minutosDesdeInicio < 0 || minutosDesdeInicio % 90 != 0 || horaInicio.isAfter(LocalTime.of(21, 0))) {
                return ResponseEntity.badRequest().body("Hora inválida. Usa bloques de 90 minutos desde las 09:00 hasta las 21:00.");
            }

            // Verificar conflicto con partidos
            if (partidoRepo.existsPartidoEnPista(pistaId, fecha, horaInicio)) {
                return ResponseEntity.badRequest().body("Ya existe un partido en esa pista y horario.");
            }

            if (partidoRepo.existsPartidoCreadoPorUsuario(creadorId, fecha, horaInicio)) {
                return ResponseEntity.badRequest().body("Ya has creado un partido a esa hora.");
            }

            if (partidoRepo.existsUsuarioParticipando(creadorId, fecha, horaInicio)) {
                return ResponseEntity.badRequest().body("Ya estás participando en un partido a esa hora.");
            }

            // Verificar conflicto con reservas
            if (reservaRepo.existsByPistaIdAndFechaAndHoraInicio(pistaId, fecha, horaInicio)) {
                return ResponseEntity.badRequest().body("Ya hay una reserva en esa pista y horario.");
            }

            Partido partido = new Partido();
            partido.setCreador(creador.get());
            partido.setPista(pista.get());
            partido.setFecha(fecha);
            partido.setHoraInicio(horaInicio);
            partido.setHoraFin(horaInicio.plusMinutes(90));
            partido.getParticipantes().add(creador.get());

            partidoRepo.save(partido);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", partido.getId());
            respuesta.put("mensaje", "Partido creado correctamente");

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear partido: " + e.getMessage());
        }
    }

    @GetMapping("/comunidad/{comunidadId}")
    public List<Partido> partidosPorComunidad(@PathVariable Long comunidadId) {
        return partidoRepo.findByComunidadId(comunidadId);
    }
    

    @DeleteMapping("/{partidoId}")
    public ResponseEntity<?> eliminarPartido(@PathVariable Long partidoId) {
        Optional<Partido> partidoOpt = partidoRepo.findById(partidoId);
        if (partidoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Eliminar solicitudes asociadas
        solicitudRepo.deleteAll(solicitudRepo.findByPartidoId(partidoId));

        // Eliminar el partido
        partidoRepo.deleteById(partidoId);

        return ResponseEntity.ok("Partido eliminado.");
    }


    @DeleteMapping("/{partidoId}/expulsar/{usuarioId}")
    public ResponseEntity<?> expulsarParticipante(@PathVariable Long partidoId, @PathVariable Long usuarioId) {
        Optional<Partido> partidoOpt = partidoRepo.findById(partidoId);
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(usuarioId);

        if (partidoOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Partido partido = partidoOpt.get();
        Usuario usuario = usuarioOpt.get();

        if (partido.getCreador().getId().equals(usuario.getId())) {
            return ResponseEntity.badRequest().body("No puedes eliminar al creador del partido.");
        }

        if (!partido.getParticipantes().contains(usuario)) {
            return ResponseEntity.badRequest().body("El usuario no está en el partido.");
        }

        partido.getParticipantes().remove(usuario);
        partidoRepo.save(partido);
        return ResponseEntity.ok("Participante eliminado del partido.");
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> partidosPorCreador(@PathVariable Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepo.findById(usuarioId);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Partido> partidos = partidoRepo.findByCreador(usuario.get());
        return ResponseEntity.ok(partidos);
    }
}
