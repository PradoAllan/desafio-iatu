package com.aprado.transacaoApi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aprado.transacaoApi.repository.TransacaoRepository;
import com.aprado.transacaoApi.model.TransacaoModel;
import com.aprado.transacaoApi.dto.TransacaoDTO;

@Service 
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    
    private static final Logger log = LoggerFactory.getLogger(TransacaoService.class);

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public void addTransacao(TransacaoDTO dto) {
        TransacaoModel transacao = new TransacaoModel(dto.valor(), dto.dataHora());
        transacaoRepository.addTransacao(transacao);
        log.debug("New transaction added.");
    }

    public void deleteAll() {
        int transacaoCount = transacaoRepository.getTransacaoCount();
        transacaoRepository.deleteAll();
        log.info("{} transactions deleted.", transacaoCount);
    }
}
