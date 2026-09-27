package com.curtavento.curta_vento.corte;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cortes-ons")
public class CortesOnsController {

    private final HistoricoCorteOnsRepository repository;

    public CortesOnsController(HistoricoCorteOnsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getRanking(@RequestParam(defaultValue = "2026") int ano) {
        java.time.LocalDate hoje = LocalDate.now();
        java.time.LocalDate dataFim = ano == hoje.getYear() ? hoje
                : (ano < hoje.getYear() ? LocalDate.of(ano, 12, 31) : LocalDate.of(ano, 1, 1).minusDays(1));
        java.time.LocalDateTime start = LocalDate.of(ano, 1, 1).atStartOfDay();
        java.time.LocalDateTime end = dataFim.atTime(23, 59, 59);
        List<Object[]> resultados = repository.findRankingCortesAno(start, end);
        List<Map<String, Object>> ranking = new ArrayList<>();
        
        for (Object[] linha : resultados) {
            String usinaNome = (String) linha[0];
            BigDecimal totalCortado = (BigDecimal) linha[1];
            
            Map<String, Object> map = new HashMap<>();
            map.put("usina", usinaNome);
            map.put("totalCortadoMwh", totalCortado != null ? totalCortado.doubleValue() : null);
            map.put("perdaFinanceira", null);
            map.put("financeiroDisponivel", false);
            ranking.add(map);
        }
        
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/evolucao")
    public ResponseEntity<List<Map<String, Object>>> getEvolucao(@RequestParam(defaultValue = "2026") int ano) {
        java.time.LocalDate hoje = LocalDate.now();
        java.time.LocalDate dataFim = ano == hoje.getYear() ? hoje
                : (ano < hoje.getYear() ? LocalDate.of(ano, 12, 31) : LocalDate.of(ano, 1, 1).minusDays(1));
        java.time.LocalDateTime start = LocalDate.of(ano, 1, 1).atStartOfDay();
        java.time.LocalDateTime end = dataFim.atTime(23, 59, 59);
        List<Object[]> resultados = repository.findEvolucaoMensalCortes(start, end);
        List<Map<String, Object>> evolucao = new ArrayList<>();
        
        // Inicializa 12 meses
        Double[] meses = new Double[12];
        for (Object[] linha : resultados) {
            int mes = ((Number) linha[0]).intValue();
            BigDecimal total = (BigDecimal) linha[1];
            meses[mes - 1] = total != null ? total.doubleValue() : 0.0;
        }
        
        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        for (int i = 0; i < 12; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("mes", nomesMeses[i]);
            boolean observado = !dataFim.isBefore(LocalDate.of(ano, i + 1, 1));
            map.put("total", observado ? (meses[i] != null ? meses[i] : 0.0) : null);
            map.put("observado", observado);
            evolucao.add(map);
        }
        
        return ResponseEntity.ok(evolucao);
    }

    @GetMapping("/motivos")
    public ResponseEntity<List<Map<String, Object>>> getMotivos(@RequestParam(defaultValue = "2026") int ano) {
        java.time.LocalDate hoje = LocalDate.now();
        java.time.LocalDate dataFim = ano == hoje.getYear() ? hoje
                : (ano < hoje.getYear() ? LocalDate.of(ano, 12, 31) : LocalDate.of(ano, 1, 1).minusDays(1));
        java.time.LocalDateTime start = LocalDate.of(ano, 1, 1).atStartOfDay();
        java.time.LocalDateTime end = dataFim.atTime(23, 59, 59);
        List<Map<String, Object>> motivos = new ArrayList<>();
        for (Object[] linha : repository.findDistribuicaoMotivos(start, end)) {
            String motivo = linha[0] != null && !linha[0].toString().isBlank()
                    ? linha[0].toString()
                    : "Não informado";
            BigDecimal total = (BigDecimal) linha[1];
            Map<String, Object> item = new HashMap<>();
            item.put("motivo", motivo);
            item.put("valor", total != null ? total.doubleValue() : null);
            motivos.add(item);
        }

        return ResponseEntity.ok(motivos);
    }

    @GetMapping("/auditoria")
    public ResponseEntity<List<Map<String, Object>>> getAuditoria() {
        List<HistoricoCorteOns> ultimos = repository.findUltimosCortes();
        List<Map<String, Object>> auditoria = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (HistoricoCorteOns c : ultimos) {
            Map<String, Object> map = new HashMap<>();
            map.put("dataHora", c.getDataCorte() != null ? c.getDataCorte().format(formatter) : "");
            map.put("usina", c.getUsina() != null ? c.getUsina().getNome() : "");
            map.put("duracaoMinutos", c.getDuracaoMinutos());
            map.put("energiaCortada", c.getEnergiaCortadaMwh() != null ? c.getEnergiaCortadaMwh().doubleValue() : null);
            auditoria.add(map);
        }
        
        return ResponseEntity.ok(auditoria);
    }
}
