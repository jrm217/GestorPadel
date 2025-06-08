package fin.dam.padel.service;

import fin.dam.padel.model.Pista;
import fin.dam.padel.repository.PistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

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
    
    public List<Pista> obtenerPorComunidad(Long comunidadId) {
        return pistaRepository.findByComunidadId(comunidadId);
    }

}