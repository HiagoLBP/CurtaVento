package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.usuario.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository repository;

    public AutenticacaoService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // Este é o método que o Spring chama automaticamente quando alguém clica em "Login"
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // O "username" recebido aqui é o e-mail que foi digitado
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + username));
    }
}