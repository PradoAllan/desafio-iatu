package com.aprado.transacaoApi.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.aprado.transacaoApi.dto.EstatisticaDTO;
import com.aprado.transacaoApi.model.TransacaoModel;
import com.aprado.transacaoApi.repository.TransacaoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

public class EstatisticaServiceTests {

    private EstatisticaService estatisticaService;
    private TransacaoRepository transacaoRepositoryMock;
    private Clock clock;
    private final OffsetDateTime instante = OffsetDateTime.parse("2020-08-07T12:30:00.789-03:00");

    @BeforeEach 
    public void initService() {
        transacaoRepositoryMock = mock(TransacaoRepository.class);
        clock = Clock.fixed(instante.toInstant(), ZoneOffset.UTC);
        estatisticaService = new EstatisticaService(transacaoRepositoryMock, 60L, clock);
    }

    @Nested 
    public class GetStatisticsWithMockedTransacoes {

        @BeforeEach 
        public void prepareFindByDataHoraAfter() {
            TransacaoModel transacao1 = new TransacaoModel(40.0, OffsetDateTime.now(clock));
            TransacaoModel transacao2 = new TransacaoModel(10.0, OffsetDateTime.now(clock));
            when(transacaoRepositoryMock.findByDataHoraAfter(OffsetDateTime.now(clock).minusSeconds(60L)))
                .thenReturn(List.of(transacao1, transacao2));
        }
    
        @Test 
        public void shouldReturnCorrectStatistics() {
            EstatisticaDTO expected = new EstatisticaDTO(2L, 50.0, 25.0, 10.0, 40.0);
            EstatisticaDTO result = estatisticaService.getStatistics();
    
            assertThat(result).isEqualTo(expected);
        }

        @Test 
        public void shouldQueryRepositoryUsingConfigurationWindow() {
            ArgumentCaptor<OffsetDateTime> captor = ArgumentCaptor.forClass(OffsetDateTime.class);
    
            estatisticaService.getStatistics();
            verify(transacaoRepositoryMock).findByDataHoraAfter(captor.capture());
            OffsetDateTime captured = captor.getValue();
            assertThat(captured).isAtSameInstantAs(instante.minusSeconds(60L));
        }
    }

    @Nested 
    public class GetStatisticsWithSpecificWindowForTransacoes {

        @BeforeEach 
        public void initServiceWithSpecificWindow() {
            transacaoRepositoryMock = mock(TransacaoRepository.class);
            clock = Clock.fixed(instante.toInstant(), ZoneOffset.UTC);
            estatisticaService = new EstatisticaService(transacaoRepositoryMock, 120L, clock);
        }
    
        @Test 
        public void shouldQueryRepositoryUsingConfigurationWindow() {
            ArgumentCaptor<OffsetDateTime> captor = ArgumentCaptor.forClass(OffsetDateTime.class);
    
            estatisticaService.getStatistics();
            verify(transacaoRepositoryMock).findByDataHoraAfter(captor.capture());
            OffsetDateTime captured = captor.getValue();
            assertThat(captured).isAtSameInstantAs(instante.minusSeconds(120L));
        }
    }
    
    @Test 
    public void shouldReturnNoStatistics() {
        EstatisticaDTO expected = new EstatisticaDTO(0L, 0.0, 0.0, 0.0, 0.0);
        EstatisticaDTO result = estatisticaService.getStatistics();

        assertThat(result).isEqualTo(expected);
    }

}
