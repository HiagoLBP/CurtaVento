package com.curtavento.curta_vento.application.usecase;

import com.curtavento.curta_vento.domain.model.PldHorario;
import com.curtavento.curta_vento.domain.port.out.PldHorarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImportarPldHorarioUseCase {

    private final PldHorarioRepositoryPort repositoryPort;

    public ImportarPldHorarioUseCase(PldHorarioRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Transactional
    public ImportResult importar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo CSV do PLD é obrigatório.");
        }

        int lidas = 0;
        int importadas = 0;
        int rejeitadas = 0;
        Map<String, Integer> colunas = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            boolean cabecalho = true;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) {
                    mapearColunas(linha, colunas);
                    cabecalho = false;
                    continue;
                }
                lidas++;
                try {
                    String[] campos = linha.split(";", -1);
                    PldHorario registro = converter(campos, colunas);
                    PldHorario existente = repositoryPort
                            .findByDiaAndHoraAndSubmercadoAndPeriodoComercializacao(
                                    registro.getDia(), registro.getHora(), registro.getSubmercado(),
                                    registro.getPeriodoComercializacao())
                            .orElse(null);
                    if (existente != null) {
                        existente.setMesReferencia(registro.getMesReferencia());
                        existente.setPeriodoComercializacao(registro.getPeriodoComercializacao());
                        existente.setPldHora(registro.getPldHora());
                        repositoryPort.save(existente);
                    } else {
                        repositoryPort.save(registro);
                    }
                    importadas++;
                } catch (RuntimeException ex) {
                    rejeitadas++;
                }
            }
        }
        return new ImportResult(lidas, importadas, rejeitadas);
    }

    private PldHorario converter(String[] campos, Map<String, Integer> colunas) {
        int mesReferencia = inteiro(campos, colunas, "MES_REFERENCIA");
        String submercado = texto(campos, colunas, "SUBMERCADO").toUpperCase();
        int periodo = inteiro(campos, colunas, "PERIODO_COMERCIALIZACAO");
        int diaMes = inteiro(campos, colunas, "DIA");
        int hora = inteiro(campos, colunas, "HORA");
        BigDecimal pld = decimal(campos, colunas, "PLD_HORA");
        if (mesReferencia < 100001 || mesReferencia > 999912 || hora < 0 || hora > 23) {
            throw new IllegalArgumentException("Data/hora fora do domínio.");
        }
        int ano = mesReferencia / 100;
        int mes = mesReferencia % 100;
        PldHorario registro = new PldHorario();
        registro.setMesReferencia(mesReferencia);
        registro.setSubmercado(submercado);
        registro.setPeriodoComercializacao(periodo);
        try {
            registro.setDia(LocalDate.of(ano, mes, diaMes));
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Dia inválido.", ex);
        }
        registro.setHora(hora);
        registro.setPldHora(pld);
        return registro;
    }

    private void mapearColunas(String linha, Map<String, Integer> colunas) {
        String[] cabecalho = linha.replace("\uFEFF", "").split(";", -1);
        for (int i = 0; i < cabecalho.length; i++) {
            colunas.put(cabecalho[i].trim().toUpperCase(), i);
        }
        for (String obrigatoria : new String[]{"MES_REFERENCIA", "SUBMERCADO",
                "PERIODO_COMERCIALIZACAO", "DIA", "HORA", "PLD_HORA"}) {
            if (!colunas.containsKey(obrigatoria)) {
                throw new IllegalArgumentException("Coluna obrigatória ausente: " + obrigatoria);
            }
        }
    }

    private String texto(String[] campos, Map<String, Integer> colunas, String coluna) {
        String valor = campos[colunas.get(coluna)].trim();
        if (valor.isBlank()) throw new IllegalArgumentException("Campo vazio: " + coluna);
        return valor;
    }

    private int inteiro(String[] campos, Map<String, Integer> colunas, String coluna) {
        return Integer.parseInt(texto(campos, colunas, coluna));
    }

    private BigDecimal decimal(String[] campos, Map<String, Integer> colunas, String coluna) {
        return new BigDecimal(texto(campos, colunas, coluna).replace(",", "."));
    }

    public record ImportResult(int lidas, int importadas, int rejeitadas) {}
}
