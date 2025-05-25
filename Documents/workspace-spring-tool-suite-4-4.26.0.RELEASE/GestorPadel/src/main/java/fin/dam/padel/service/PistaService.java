package fin.dam.padel.service;

import fin.dam.padel.model.Pista;
import fin.dam.padel.repository.PistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PistaService {

    @Autowired
    private PistaRepository pistaRepository;

    public List<Pista> listarPistas() {
        return pistaRepository.findAll();
    }

    public Pista crearPista(Pista pista) {
        return pistaRepository.save(pista);
    }
}
