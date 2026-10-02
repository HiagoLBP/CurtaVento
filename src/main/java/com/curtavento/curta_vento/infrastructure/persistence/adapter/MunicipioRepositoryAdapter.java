package com.curtavento.curta_vento.infrastructure.persistence.adapter;

import com.curtavento.curta_vento.domain.model.Municipio;
import com.curtavento.curta_vento.domain.port.out.MunicipioRepositoryPort;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MunicipioRepositoryAdapter implements MunicipioRepositoryPort {
    
    private final SpringDataMunicipioRepository repository;

    public MunicipioRepositoryAdapter(SpringDataMunicipioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Municipio> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Municipio> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Municipio save(Municipio municipio) {
        MunicipioEntity entity = toEntity(municipio);
        MunicipioEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    public Municipio toDomain(MunicipioEntity entity) {
        if (entity == null) return null;
        Municipio domain = new Municipio();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setCodigoIbge(entity.getCodigoIbge());
        domain.setIpm(entity.getIpm());
        domain.setVafAnoBase(entity.getVafAnoBase());
        domain.setAnoBase(entity.getAnoBase());
        return domain;
    }

    public MunicipioEntity toEntity(Municipio domain) {
        if (domain == null) return null;
        MunicipioEntity entity = new MunicipioEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setCodigoIbge(domain.getCodigoIbge());
        entity.setIpm(domain.getIpm());
        entity.setVafAnoBase(domain.getVafAnoBase());
        entity.setAnoBase(domain.getAnoBase());
        return entity;
    }
}
