package fin.dam.padel.controller;

import fin.dam.padel.model.Partido;
import fin.dam.padel.model.SolicitudUnirsePartido;
import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.PartidoRepository;
import fin.dam.padel.repository.SolicitudUnirsePartidoRepository;
import fin.dam.padel.repository.UsuarioRepository;
import fin.dam.padel.service.FirebaseMessagingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private SolicitudUnirsePartidoRepository solicitudRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PartidoRepository partidoRepo;
    
    @Autowired
    private FirebaseMessagingService firebaseService;

    @PostMapping
    public ResponseEntity<?> solicitarUnirse(@RequestBody Map<String, Long> datos) {
        Long partidoId = datos.get("partidoId");
        Long usuarioId = datos.get("usuarioId");

        Optional<Partido> partidoOpt = partidoRepo.findById(partidoId);
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(usuarioId);

        if (partidoOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Partido o usuario no encontrado.");
        }

        Optional<SolicitudUnirsePartido> yaExiste =
                solicitudRepo.findByPartidoIdAndSolicitanteId(partidoId, usuarioId);
        if (yaExiste.isPresent()) {
            return ResponseEntity.badRequest().body("Ya has enviado una solicitud.");
        }

        SolicitudUnirsePartido solicitud = new SolicitudUnirsePartido();
        solicitud.setPartido(partidoOpt.get());
        solicitud.setSolicitante(usuarioOpt.get());
        solicitud.setEstado("PENDIENTE");

        solicitudRepo.save(solicitud);

        // Enviar notificación FCM al creador del partido
        Usuario creador = partidoOpt.get().getCreador();
        String token = creador.getFcmToken();
        if (token != null && !token.isEmpty()) {
            String titulo = "Nueva solicitud de partido";
            String cuerpo = usuarioOpt.get().getNombre() + " quiere unirse a tu partido en " +
                            partidoOpt.get().getPista().getNombre() + " a las " +
                            partidoOpt.get().getHoraInicio();
            try {
				firebaseService.enviarNotificacion(token, titulo, cuerpo);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
        }

        return ResponseEntity.ok("Solicitud enviada.");
    }


    @PostMapping("/{solicitudId}/aceptar")
    public ResponseEntity<?> aceptarSolicitud(@PathVariable Long solicitudId) {
        Optional<SolicitudUnirsePartido> solicitudOpt = solicitudRepo.findById(solicitudId);

        if (solicitudOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SolicitudUnirsePartido solicitud = solicitudOpt.get();
        Partido partido = solicitud.getPartido();

        if (!partido.getParticipantes().contains(solicitud.getSolicitante())) {
            partido.getParticipantes().add(solicitud.getSolicitante());
        }

        solicitud.setEstado("ACEPTADA");

        partidoRepo.save(partido);
        solicitudRepo.save(solicitud);

        // ✅ Notificar al solicitante
        Usuario solicitante = solicitud.getSolicitante();
        String token = solicitante.getFcmToken();
        if (token != null && !token.isBlank()) {
            String titulo = "Solicitud aceptada";
            String cuerpo = "Te han aceptado en el partido de " + partido.getPista().getNombre() +
                            " a las " + partido.getHoraInicio();
            try {
				firebaseService.enviarNotificacion(token, titulo, cuerpo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return ResponseEntity.ok("Solicitud aceptada.");
    }


    @PostMapping("/{solicitudId}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long solicitudId) {
        Optional<SolicitudUnirsePartido> solicitudOpt = solicitudRepo.findById(solicitudId);

        if (solicitudOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SolicitudUnirsePartido solicitud = solicitudOpt.get();
        solicitud.setEstado("RECHAZADA");
        solicitudRepo.save(solicitud);

        // ✅ Notificar al solicitante
        Partido partido = solicitud.getPartido();
        Usuario solicitante = solicitud.getSolicitante();
        String token = solicitante.getFcmToken();
        if (token != null && !token.isBlank()) {
            String titulo = "Solicitud rechazada";
            String cuerpo = "Tu solicitud para el partido en " + partido.getPista().getNombre() +
                            " a las " + partido.getHoraInicio() + " ha sido rechazada.";
            try {
				firebaseService.enviarNotificacion(token, titulo, cuerpo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return ResponseEntity.ok("Solicitud rechazada.");
    }


    @GetMapping("/partido/{partidoId}")
    public List<SolicitudUnirsePartido> obtenerSolicitudesPorPartido(@PathVariable Long partidoId) {
        return solicitudRepo.findByPartidoId(partidoId);
    }
    
    @GetMapping("/creador/{creadorId}")
    public ResponseEntity<?> obtenerSolicitudesPorCreador(@PathVariable Long creadorId) {
        List<SolicitudUnirsePartido> todas = solicitudRepo.findAll();
        List<SolicitudUnirsePartido> filtradas = todas.stream()
            .filter(s -> s.getPartido().getCreador().getId().equals(creadorId))
            .filter(s -> s.getEstado().equalsIgnoreCase("PENDIENTE"))
            .toList();

        return ResponseEntity.ok(filtradas);
    }


    @GetMapping("/partido/{partidoId}/pendientes")
    public List<SolicitudUnirsePartido> solicitudesPendientesPorPartido(@PathVariable Long partidoId) {
        return solicitudRepo.findByPartidoId(partidoId).stream()
            .filter(s -> s.getEstado().equalsIgnoreCase("PENDIENTE"))
            .toList();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitudUnirsePartido> solicitudesDelUsuario(@PathVariable Long usuarioId) {
        return solicitudRepo.findAll().stream()
            .filter(s -> s.getSolicitante().getId().equals(usuarioId))
            .toList();
    }
    
    @GetMapping("/pendientes/creador/{creadorId}/count")
    public ResponseEntity<?> contarSolicitudesPendientesPorCreador(@PathVariable Long creadorId) {
        List<SolicitudUnirsePartido> solicitudes = solicitudRepo.findAll();
        long count = solicitudes.stream()
            .filter(s -> s.getEstado().equalsIgnoreCase("PENDIENTE"))
            .filter(s -> s.getPartido().getCreador().getId().equals(creadorId))
            .count();

        return ResponseEntity.ok(count);
    }
}