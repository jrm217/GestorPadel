package fin.dam.padel.service;

import fin.dam.padel.model.Usuario;
import fin.dam.padel.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario crearUsuario(Usuario usuario) {
        long totalUsuarios = usuarioRepository.count();
        
        // El primer usuario será ADMIN, los demás serán USER por defecto
        if (totalUsuarios == 0) {
            usuario.setRol("ADMIN");
        } else {
            usuario.setRol("USER");
        }
        
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizarUsuario(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellidos(usuarioActualizado.getApellidos());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setPassword(usuarioActualizado.getPassword()); // Sin encriptar
            usuario.setRol(usuarioActualizado.getRol());
            return usuarioRepository.save(usuario);
        });
    }

    public boolean eliminarUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            long admins = usuarioRepository.countByRol("ADMIN");

            // No permitir eliminar el último ADMIN
            if (usuario.get().getRol().equals("ADMIN") && admins <= 1) {
                return false;
            }

            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
