package com.curtavento.curta_vento.usuario;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsinaRepository usinaRepository;
    private final MunicipioRepository municipioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsinaRepository usinaRepository, MunicipioRepository municipioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usinaRepository = usinaRepository;
        this.municipioRepository = municipioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getUsina() != null && usuario.getUsina().getId() != null) {
            Usina usina = usinaRepository.findById(usuario.getUsina().getId())
                    .orElseThrow(() -> new RuntimeException("Usina não encontrada com o ID: " + usuario.getUsina().getId()));
            usuario.setUsina(usina);
        }

        if (usuario.getMunicipio() != null && usuario.getMunicipio().getId() != null) {
            Municipio municipio = municipioRepository.findById(usuario.getMunicipio().getId())
                    .orElseThrow(() -> new RuntimeException("Município não encontrado com o ID: " + usuario.getMunicipio().getId()));
            usuario.setMunicipio(municipio);
        }

        // Força sempre o perfil VISUALIZADOR para novos cadastros pela API pública
        usuario.setPerfil("VISUALIZADOR");

        // Hash da senha usando BCrypt antes de salvar no banco
        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }
}