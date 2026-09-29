package com.curtavento.curta_vento.clima;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.usina.UsinaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clima")
public class ClimaController {

    private final UsinaService usinaService;
    private final LeituraClimaticaService leituraService;

    public ClimaController(UsinaService usinaService, LeituraClimaticaService leituraService) {
        this.usinaService = usinaService;
        this.leituraService = leituraService;
    }

    @GetMapping("/usinas/{id}/historico")
    public List<LeituraClimatica> buscarHistorico(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return leituraService.buscarHistorico(id, inicio, fim);
    }

    @PostMapping("/leituras")
    public LeituraClimatica cadastrar(@RequestBody LeituraClimatica leitura) {
        return leituraService.salvar(leitura);
    }

    @GetMapping("/estacoes")
    public ResponseEntity<List<Map<String, Object>>> getEstacoes() {
        List<Map<String, Object>> estacoes = new ArrayList<>();
        for (Usina u : usinaService.listarTodas()) {
            LeituraClimatica leitura = leituraService.buscarUltimaLeitura(u.getId());
            Map<String, Object> dto = new HashMap<>();
            dto.put("usinaId", u.getId());
            dto.put("usina", u.getNome());
            dto.put("operador", u.getOperador());
            dto.put("latitude", u.getLatitude());
            dto.put("longitude", u.getLongitude());

            if (leitura != null) {
                dto.put("temperaturaC", leitura.getTemperaturaC() != null ? leitura.getTemperaturaC().doubleValue() : null);
                dto.put("ventoAtual", leitura.getVelocidadeVentoMs() != null ? leitura.getVelocidadeVentoMs().doubleValue() : null);
                dto.put("vento24h", leitura.getVentoPrevisao24hMs() != null ? leitura.getVentoPrevisao24hMs().doubleValue() : null);
                dto.put("vento48h", leitura.getVentoPrevisao48hMs() != null ? leitura.getVentoPrevisao48hMs().doubleValue() : null);
                dto.put("direcaoGraus", leitura.getDirecaoVentoGraus() != null ? leitura.getDirecaoVentoGraus().doubleValue() : null);
                dto.put("fonte", leitura.getFonte());
                dto.put("ultimaAtualizacao", leitura.getDataHora() != null ? leitura.getDataHora().toString() : null);

                Double ventoMs = leitura.getVelocidadeVentoMs() != null ? leitura.getVelocidadeVentoMs().doubleValue() : null;
                if (ventoMs != null) {
                    if (ventoMs <= 10.0) {
                        dto.put("janelaOM", "LIBERADO");
                    } else if (ventoMs <= 15.0) {
                        dto.put("janelaOM", "ALERTA");
                    } else {
                        dto.put("janelaOM", "BLOQUEADO");
                    }
                } else {
                    dto.put("janelaOM", "N/D");
                }
            } else {
                dto.put("temperaturaC", null);
                dto.put("ventoAtual", null);
                dto.put("vento24h", null);
                dto.put("vento48h", null);
                dto.put("direcaoGraus", null);
                dto.put("fonte", null);
                dto.put("ultimaAtualizacao", null);
                dto.put("janelaOM", "N/D");
            }
            estacoes.add(dto);
        }
        return ResponseEntity.ok(estacoes);
    }

    @GetMapping("/previsao-rajadas")
    public ResponseEntity<List<Map<String, Object>>> getPrevisaoRajadas() {
        List<Map<String, Object>> rajadas = new ArrayList<>();
        for (Usina u : usinaService.listarTodas()) {
            LeituraClimatica leitura = leituraService.buscarUltimaLeitura(u.getId());
            Map<String, Object> dto = new HashMap<>();
            dto.put("usina", u.getNome());
            if (leitura != null) {
                dto.put("atual", leitura.getVelocidadeVentoMs() != null ? leitura.getVelocidadeVentoMs().doubleValue() : 0);
                dto.put("prev24h", leitura.getVentoPrevisao24hMs() != null ? leitura.getVentoPrevisao24hMs().doubleValue() : 0);
                dto.put("prev48h", leitura.getVentoPrevisao48hMs() != null ? leitura.getVentoPrevisao48hMs().doubleValue() : 0);
            } else {
                dto.put("atual", 0);
                dto.put("prev24h", 0);
                dto.put("prev48h", 0);
            }
            rajadas.add(dto);
        }
        return ResponseEntity.ok(rajadas);
    }

    @GetMapping("/rosa-ventos")
    public ResponseEntity<Map<String, Object>> getRosaVentos() {
        String[] direcoes = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        double[] intensidades = new double[8];
        int[] contagens = new int[8];

        for (Usina u : usinaService.listarTodas()) {
            LeituraClimatica leitura = leituraService.buscarUltimaLeitura(u.getId());
            if (leitura != null && leitura.getDirecaoVentoGraus() != null && leitura.getVelocidadeVentoMs() != null) {
                double graus = leitura.getDirecaoVentoGraus().doubleValue();
                double vento = leitura.getVelocidadeVentoMs().doubleValue();
                int setor = (int) Math.round(graus / 45.0) % 8;
                intensidades[setor] += vento;
                contagens[setor]++;
            }
        }

        double[] medias = new double[8];
        for (int i = 0; i < 8; i++) {
            medias[i] = contagens[i] > 0 ? Math.round((intensidades[i] / contagens[i]) * 100.0) / 100.0 : 0;
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("direcoes", direcoes);
        resultado.put("intensidades", medias);
        return ResponseEntity.ok(resultado);
    }
}
