package com.aprado.transacaoApi.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.aprado.transacaoApi.dto.TransacaoDTO;
import com.aprado.transacaoApi.model.TransacaoModel;
import com.aprado.transacaoApi.repository.TransacaoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;

public class TransacaoServiceTests {
    
    private TransacaoService transacaoService;
    private TransacaoRepository transacaoRepositoryMock;

    @BeforeEach
    public void initService() {
        transacaoRepositoryMock = mock(TransacaoRepository.class);
        transacaoService = new TransacaoService(transacaoRepositoryMock);
    }

    @Test
    public void shouldAddNewTransacao() {
        TransacaoDTO transacao = new TransacaoDTO(123.45, OffsetDateTime.now());
        ArgumentCaptor<TransacaoModel> captor = ArgumentCaptor.forClass(TransacaoModel.class);

        transacaoService.addTransacao(transacao);

        verify(transacaoRepositoryMock).addTransacao(captor.capture());

        TransacaoModel captured = captor.getValue();
        assertThat(captured.getValor()).isEqualTo(transacao.valor());
        assertThat(captured.getDataHora()).isEqualTo(transacao.dataHora());
    }

}
