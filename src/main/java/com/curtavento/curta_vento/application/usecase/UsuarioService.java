package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataUsinaRepository springDataUsinaRepository;
    private final SpringDataMunicipioRepository springDataMunicipioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(SpringDataUsuarioRepository usuarioRepository,
                          SpringDataUsinaRepository springDataUsinaRepository,
                          SpringDataMunicipioRepository springDataMunicipioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.springDataUsinaRepository = springDataUsinaRepository;
        this.springDataMunicipioRepository = springDataMunicipioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioEntity> listarTodos() {
        return usuarioRepository.findAll();
    }

    public UsuarioEntity salvar(UsuarioEntity usuario) {
        if (usuario.getUsina() != null && usuario.getUsina().getId() != null) {
            UsinaEntity usina = springDataUsinaRepository.findById(usuario.getUsina().getId())
                    .orElseThrow(() -> new RuntimeException("UsinaEntity n�o encontrada com o ID: " + usuario.getUsina().getId()));
            usuario.setUsina(usina);
        }

        if (usuario.getMunicipio() != null && usuario.getMunicipio().getId() != null) {
            MunicipioEntity municipio = springDataMunicipioRepository.findById(usuario.getMunicipio().getId())
                    .orElseThrow(() -> new RuntimeException("Munic�pio n�o encontrado com o ID: " + usuario.getMunicipio().getId()));
            usuario.setMunicipio(municipio);
        }

        usuario.setPerfil("VISUALIZADOR");

        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }
}