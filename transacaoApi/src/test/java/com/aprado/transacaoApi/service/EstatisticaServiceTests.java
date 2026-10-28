package com.aprado.transacaoApi.service;

import org.junit.jupiter.api.Test;

import com.aprado.transacaoApi.dto.EstatisticaDTO;
import com.aprado.transacaoApi.model.TransacaoModel;
import com.aprado.transacaoApi.repository.TransacaoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

public class EstatisticaServiceTests {

    private EstatisticaService estatisticaService;
    private TransacaoRepository transacaoRepositoryMock;

    @BeforeEach 
    public void initService() {
        transacaoRepositoryMock = mock(TransacaoRepository.class);
        estatisticaService = new EstatisticaService(transacaoRepositoryMock, 60L, Clock.systemUTC());
    }

    @Nested 
    public class GetStatisticsWithMockedTransacoes {

        @BeforeEach 
        public void prepareFindByDataHoraAfter() {
            TransacaoModel transacao1 = new TransacaoModel(40.0, OffsetDateTime.now());
            TransacaoModel transacao2 = new TransacaoModel(10.0, OffsetDateTime.now());
            when(transacaoRepositoryMock.findByDataHoraAfter(any()))
                .thenReturn(List.of(transacao1, transacao2));
        }
    
        @Test 
        public void shouldReturnCorrectStatistics() {
            EstatisticaDTO expected = new EstatisticaDTO(2L, 50.0, 25.0, 10.0, 40.0);
            EstatisticaDTO result = estatisticaService.getStatistics();
    
            assertThat(result).isEqualTo(expected);
        }
    }
    
    @Test 
    public void shouldReturnNoStatistics() {
        EstatisticaDTO expected = new EstatisticaDTO(0L, 0.0, 0.0, 0.0, 0.0);
        EstatisticaDTO result = estatisticaService.getStatistics();

        assertThat(result).isEqualTo(expected);
    }
}
