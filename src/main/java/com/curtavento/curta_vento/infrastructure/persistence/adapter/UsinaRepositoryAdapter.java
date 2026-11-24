package com.curtavento.curta_vento.infrastructure.persistence.adapter;

import com.curtavento.curta_vento.domain.model.Usina;
import com.curtavento.curta_vento.domain.port.out.UsinaRepositoryPort;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataUsinaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsinaRepositoryAdapter implements UsinaRepositoryPort {

    private final SpringDataUsinaRepository repository;
    private final MunicipioRepositoryAdapter municipioAdapter;

    public UsinaRepositoryAdapter(SpringDataUsinaRepository repository, MunicipioRepositoryAdapter municipioAdapter) {
        this.repository = repository;
        this.municipioAdapter = municipioAdapter;
    }

    @Override
    public List<Usina> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Usina> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Usina save(Usina usina) {
        UsinaEntity entity = toEntity(usina);
        UsinaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    public Usina toDomain(UsinaEntity entity) {
        if (entity == null) return null;
        Usina domain = new Usina();
        domain.setId(entity.getId());
        domain.setNome(entity.getNome());
        domain.setOperador(entity.getOperador());
        domain.setMunicipio(municipioAdapter.toDomain(entity.getMunicipio()));
        domain.setCapacidadeInstaladaMw(entity.getCapacidadeInstaladaMw());
        domain.setLatitude(entity.getLatitude());
        domain.setLongitude(entity.getLongitude());
        domain.setDataInicioOperacao(entity.getDataInicioOperacao());
        domain.setIdOns(entity.getIdOns());
        domain.setCeg(entity.getCeg());
        domain.setNomeConjuntoOns(entity.getNomeConjuntoOns());
        domain.setSubestacaoConexao(entity.getSubestacaoConexao());
        domain.setPrecoPpaMwh(entity.getPrecoPpaMwh());
        return domain;
    }

    public UsinaEntity toEntity(Usina domain) {
        if (domain == null) return null;
        UsinaEntity entity = new UsinaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setOperador(domain.getOperador());
        entity.setMunicipio(municipioAdapter.toEntity(domain.getMunicipio()));
        entity.setCapacidadeInstaladaMw(domain.getCapacidadeInstaladaMw());
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        entity.setDataInicioOperacao(domain.getDataInicioOperacao());
        entity.setIdOns(domain.getIdOns());
        entity.setCeg(domain.getCeg());
        entity.setNomeConjuntoOns(domain.getNomeConjuntoOns());
        entity.setSubestacaoConexao(domain.getSubestacaoConexao());
        entity.setPrecoPpaMwh(domain.getPrecoPpaMwh() != null ? domain.getPrecoPpaMwh() : new BigDecimal("200.00"));
        return entity;
    }
}
