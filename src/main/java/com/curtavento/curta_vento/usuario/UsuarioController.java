package com.curtavento.curta_vento.usuario;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listarTodos().stream().map(UsuarioResponseDTO::new).collect(Collectors.toList());
    }

    @PostMapping
    public UsuarioResponseDTO cadastrar(@RequestBody UsuarioCreateDTO dto) {
        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setSenha(dto.getSenha());
        return new UsuarioResponseDTO(service.salvar(u));
    }
}