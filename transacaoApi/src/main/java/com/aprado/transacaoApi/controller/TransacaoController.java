package com.aprado.transacaoApi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

import com.aprado.transacaoApi.dto.TransacaoDTO;
import com.aprado.transacaoApi.service.TransacaoService;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<Void> createTransacao(@Valid @RequestBody TransacaoDTO transacao) {
        transacaoService.addTransacao(transacao);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTransacao() {
        transacaoService.deleteAll();
        return ResponseEntity.ok().build();
    }
}
