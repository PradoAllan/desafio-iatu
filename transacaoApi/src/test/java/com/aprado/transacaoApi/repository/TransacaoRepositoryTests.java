package com.aprado.transacaoApi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;

import com.aprado.transacaoApi.model.TransacaoModel;

public class TransacaoRepositoryTests {
    
    private TransacaoRepository transacaoRepository = new TransacaoRepository();
    
    @Test 
    public void shouldAddNewTransacao() {
        TransacaoModel transacao = new TransacaoModel(1.5, OffsetDateTime.now());
        
        transacaoRepository.addTransacao(transacao);
        
        var count = transacaoRepository.getTransacaoCount();
        assertThat(count).isEqualTo(1);
    }
    
    @Nested 
    class WhenRepositoryHasTransacoes {
        
        private TransacaoModel oldTransacao;
        private TransacaoModel onTheLimitTransacao;
        private TransacaoModel recentTransacao;
        private final OffsetDateTime instante = OffsetDateTime.parse("2020-08-07T12:30:00.789-03:00");
        
        @BeforeEach 
        void setUpTransacoes() {
            oldTransacao = new TransacaoModel(123.45, instante.minusSeconds(61));
            onTheLimitTransacao = new TransacaoModel(12.23, instante.minusSeconds(60));
            recentTransacao = new TransacaoModel(42.42, instante.minusSeconds(59));
            transacaoRepository.addTransacao(oldTransacao);
            transacaoRepository.addTransacao(onTheLimitTransacao);
            transacaoRepository.addTransacao(recentTransacao);
        }

        @Test 
        public void shouldDeleteAllTransacoes() {
            transacaoRepository.deleteAll();
            var count = transacaoRepository.getTransacaoCount();
            
            assertThat(count).isEqualTo(0);
        }

        @Test 
        public void shouldExcludeTransacaoExactlyAtTheLimit() {
            List<TransacaoModel> result = transacaoRepository.findByDataHoraAfter(instante.minusSeconds(60));
    
            assertThat(result).containsExactly(recentTransacao);
        }
    }
}
