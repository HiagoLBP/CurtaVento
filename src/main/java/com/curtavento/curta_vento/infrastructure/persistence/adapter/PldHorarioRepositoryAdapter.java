package com.curtavento.curta_vento.infrastructure.persistence.adapter;

import com.curtavento.curta_vento.domain.model.PldHorario;
import com.curtavento.curta_vento.domain.port.out.PldHorarioRepositoryPort;
import com.curtavento.curta_vento.infrastructure.persistence.entity.PldHorarioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataPldHorarioRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class PldHorarioRepositoryAdapter implements PldHorarioRepositoryPort {
    private final SpringDataPldHorarioRepository repository;

    public PldHorarioRepositoryAdapter(SpringDataPldHorarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PldHorario> findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(LocalDate dia, Integer hora, String submercado, Integer periodoComercializacao) {
        return repository.findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(dia, hora, submercado, periodoComercializacao)
                .map(this::toDomain);
    }

    @Override
    public PldHorario save(PldHorario pldHorario) {
        PldHorarioEntity entity = toEntity(pldHorario);
        PldHorarioEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private PldHorario toDomain(PldHorarioEntity entity) {
        PldHorario domain = new PldHorario();
        domain.setId(entity.getId());
        domain.setMesReferencia(entity.getMesReferencia());
        domain.setSubmercado(entity.getSubmercado());
        domain.setPeriodoComercializacao(entity.getPeriodoComercializacao());
        domain.setDia(entity.getDia());
        domain.setHora(entity.getHora());
        domain.setPldHora(entity.getPldHora());
        return domain;
    }

    private PldHorarioEntity toEntity(PldHorario domain) {
        PldHorarioEntity entity = new PldHorarioEntity();
        entity.setId(domain.getId());
        entity.setMesReferencia(domain.getMesReferencia());
        entity.setSubmercado(domain.getSubmercado());
        entity.setPeriodoComercializacao(domain.getPeriodoComercializacao());
        entity.setDia(domain.getDia());
        entity.setHora(domain.getHora());
        entity.setPldHora(domain.getPldHora());
        return entity;
    }
}
