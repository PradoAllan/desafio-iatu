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
        private TransacaoModel recentTransacao;
        
        @BeforeEach 
        void setUpTransacoes() {
            oldTransacao = new TransacaoModel(123.45, OffsetDateTime.now().minusSeconds(70));
            recentTransacao = new TransacaoModel(42.42, OffsetDateTime.now().minusSeconds(40));
            transacaoRepository.addTransacao(oldTransacao);
            transacaoRepository.addTransacao(recentTransacao);
        }

        @Test 
        public void shouldDeleteAllTransacoes() {
            transacaoRepository.deleteAll();
            var count = transacaoRepository.getTransacaoCount();
            
            assertThat(count).isEqualTo(0);
        }

        @Test 
        public void shouldReturnOnlyTransacoesAfterTheLimit() {
            List<TransacaoModel> result = transacaoRepository.findByDataHoraAfter(OffsetDateTime.now().minusMinutes(1L));
    
            assertThat(result).containsExactly(recentTransacao);
        }
    }
}
