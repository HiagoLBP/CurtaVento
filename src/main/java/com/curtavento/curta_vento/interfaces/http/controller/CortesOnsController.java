package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.application.usecase.CalcularRessarcimentoAneelUseCase;
import com.curtavento.curta_vento.application.usecase.HistoricoCorteOnsService;
import com.curtavento.curta_vento.domain.model.PrecoEnergia;
import com.curtavento.curta_vento.infrastructure.persistence.entity.HistoricoCorteOnsEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsBlocoRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataHistoricoCorteOnsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/cortes-ons")
public class CortesOnsController {

    private final SpringDataHistoricoCorteOnsRepository repository;
    private final SpringDataHistoricoCorteOnsBlocoRepository blocoRepository;
    private final HistoricoCorteOnsService service;
    private final CalcularRessarcimentoAneelUseCase calcularRessarcimentoAneelUseCase;

    public CortesOnsController(SpringDataHistoricoCorteOnsRepository repository,
                               SpringDataHistoricoCorteOnsBlocoRepository blocoRepository,
                               HistoricoCorteOnsService service,
                               CalcularRessarcimentoAneelUseCase calcularRessarcimentoAneelUseCase) {
        this.repository = repository;
        this.blocoRepository = blocoRepository;
        this.service = service;
        this.calcularRessarcimentoAneelUseCase = calcularRessarcimentoAneelUseCase;
    }

    @PostMapping
    public HistoricoCorteOnsEntity registrar(@RequestBody HistoricoCorteOnsEntity corte) {
        return service.registrarCorte(corte);
    }

    @GetMapping("/usina/{usinaId}")
    public List<HistoricoCorteOnsEntity> listarPorUsina(@PathVariable Long usinaId) {
        return service.buscarPorUsina(usinaId);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getRanking(@RequestParam(defaultValue = "2026") int ano) {
        LocalDateTime start = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(ano, 12, 31, 23, 59, 59);
        List<Object[]> resultados = repository.findRankingCortesAno(start, end);
        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Object[] linha : resultados) {
            String usinaNome = (String) linha[0];
            BigDecimal totalCortado = (BigDecimal) linha[1];
            if (totalCortado == null) totalCortado = BigDecimal.ZERO;

            double perdaFinanceira = totalCortado.doubleValue() * PrecoEnergia.PRECO_MWH;

            Map<String, Object> map = new HashMap<>();
            map.put("usina", usinaNome);
            map.put("totalCortadoMwh", totalCortado.doubleValue());
            map.put("perdaFinanceira", perdaFinanceira);
            ranking.add(map);
        }
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/evolucao")
    public ResponseEntity<List<Map<String, Object>>> getEvolucao(@RequestParam(defaultValue = "2026") int ano) {
        LocalDateTime start = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(ano, 12, 31, 23, 59, 59);
        List<Object[]> resultados = repository.findEvolucaoMensalCortes(start, end);
        List<Map<String, Object>> evolucao = new ArrayList<>();

        double[] meses = new double[12];
        for (Object[] linha : resultados) {
            int mes = ((Number) linha[0]).intValue();
            BigDecimal total = (BigDecimal) linha[1];
            meses[mes - 1] = total != null ? total.doubleValue() : 0.0;
        }

        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        for (int i = 0; i < 12; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("mes", nomesMeses[i]);
            map.put("total", meses[i]);
            evolucao.add(map);
        }
        return ResponseEntity.ok(evolucao);
    }

    @GetMapping("/motivos")
    public ResponseEntity<List<Map<String, Object>>> getMotivos(@RequestParam(defaultValue = "2026") int ano) {
        LocalDateTime start = LocalDateTime.of(ano, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        List<Object[]> resultados = repository.findMotivosCorte(start, end);
        if (resultados == null || resultados.isEmpty()) {
            resultados = blocoRepository.findResumoPorMotivo(start, end);
        }

        List<Map<String, Object>> motivos = new ArrayList<>();
        for (Object[] linha : resultados) {
            String codigo = linha[0] != null ? linha[0].toString().toUpperCase(Locale.ROOT) : "NAO_CLASSIFICADO";
            BigDecimal total = (BigDecimal) linha[1];
            double valor = total != null ? total.doubleValue() : 0.0;

            Map<String, Object> item = new HashMap<>();
            item.put("codigo", codigo);
            item.put("motivo", formatarMotivo(codigo));
            item.put("valor", valor);
            motivos.add(item);
        }

        return ResponseEntity.ok(motivos);
    }

    @GetMapping("/ressarcimento")
    public ResponseEntity<Map<String, Object>> getRessarcimento(
            @RequestParam(defaultValue = "2026") int ano,
            @RequestParam(required = false) Long usinaId,
            @RequestParam(defaultValue = "78") double franquiaHoras,
            @RequestParam(defaultValue = "false") boolean aplicarNt183,
            @RequestParam(required = false) BigDecimal precoMwh) {
        return ResponseEntity.ok(calcularRessarcimentoAneelUseCase.executar(
                ano, usinaId, franquiaHoras, aplicarNt183, precoMwh));
    }

    @GetMapping("/auditoria")
    public ResponseEntity<List<Map<String, Object>>> getAuditoria() {
        List<HistoricoCorteOnsEntity> ultimos = repository.findUltimosCortes();
        List<Map<String, Object>> auditoria = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HistoricoCorteOnsEntity c : ultimos) {
            Map<String, Object> map = new HashMap<>();
            map.put("dataHora", c.getDataCorte() != null ? c.getDataCorte().format(formatter) : "");
            map.put("usina", c.getUsina() != null ? c.getUsina().getNome() : "");
            map.put("motivo", c.getCodRazaoRestricao() != null ? c.getCodRazaoRestricao() : (c.getMotivoOns() != null ? c.getMotivoOns() : "N/I"));
            map.put("subestacao", c.getSubestacaoConexao() != null ? c.getSubestacaoConexao() : "N/D");
            map.put("duracaoMinutos", c.getDuracaoMinutos());
            map.put("energiaCortada", c.getEnergiaCortadaMwh() != null ? c.getEnergiaCortadaMwh().doubleValue() : 0.0);
            auditoria.add(map);
        }
        return ResponseEntity.ok(auditoria);
    }

    private String formatarMotivo(String codigo) {
        return switch (codigo) {
            case "ENE" -> "ENE (Sobreoferta)";
            case "REL" -> "REL (Indisp. Externa)";
            case "CNF" -> "CNF (Confiabilidade)";
            case "PAR" -> "PAR (Paralisação)";
            default -> "Não Classificado (ONS)";
        };
    }
}
