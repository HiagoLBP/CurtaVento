package com.curtavento.curta_vento.usina;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.municipio.MunicipioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsinaService {

    private final UsinaRepository usinaRepository;
    private final MunicipioRepository municipioRepository;

    public UsinaService(UsinaRepository usinaRepository, MunicipioRepository municipioRepository) {
        this.usinaRepository = usinaRepository;
        this.municipioRepository = municipioRepository;
    }

    public List<Usina> listarTodas() {
        return usinaRepository.findAll();
    }

    public Usina salvar(Usina usina) {
        if (usina.getMunicipio() != null && usina.getMunicipio().getId() != null) {
            Municipio municipioExistente = municipioRepository.findById(usina.getMunicipio().getId())
                    .orElseThrow(() -> new RuntimeException("Município não encontrado com o ID: " + usina.getMunicipio().getId()));

            usina.setMunicipio(municipioExistente);
        } else {
            throw new RuntimeException("É obrigatório informar o município da usina.");
        }

        return usinaRepository.save(usina);
    }
}