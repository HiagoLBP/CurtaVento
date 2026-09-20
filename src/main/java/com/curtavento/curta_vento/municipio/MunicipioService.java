package com.curtavento.curta_vento.municipio;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MunicipioService {

    private final MunicipioRepository repository;

    public MunicipioService(MunicipioRepository repository) {
        this.repository = repository;
    }

    public List<Municipio> listarTodos() {
        return repository.findAll();
    }

    public Municipio salvar(Municipio municipio) {
        return repository.save(municipio);
    }
}