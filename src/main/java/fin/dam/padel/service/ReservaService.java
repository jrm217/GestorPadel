package fin.dam.padel.service;

import fin.dam.padel.model.Reserva;
import fin.dam.padel.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public Reserva crearReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
}
