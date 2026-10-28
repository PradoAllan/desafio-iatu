package com.aprado.transacaoApi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.aprado.transacaoApi.dto.EstatisticaDTO;
import com.aprado.transacaoApi.service.EstatisticaService;

@RestController
@RequestMapping("/estatistica")
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    public EstatisticaController(EstatisticaService estatisticaService) {
        this.estatisticaService = estatisticaService;
    }
    
    @GetMapping
    public EstatisticaDTO generateStatistics() {
        return estatisticaService.getStatistics();
    }
}
