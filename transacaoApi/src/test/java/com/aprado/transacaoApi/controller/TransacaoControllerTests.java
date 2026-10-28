package com.aprado.transacaoApi.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.aprado.transacaoApi.dto.TransacaoDTO;
import com.aprado.transacaoApi.service.TransacaoService;

@WebMvcTest(TransacaoController.class)
public class TransacaoControllerTests {
    
    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private TransacaoService transacaoServiceMock;

    private ResultActions performTransacaoPost(String json) throws Exception {
        return mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                    );
    }

    @Test 
    public void shouldAddNewTransacao() throws Exception {
        String transacao = """
                {"valor": 123.45, "dataHora": "2020-08-07T12:34:56.789-03:00"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().isCreated());

        ArgumentCaptor<TransacaoDTO> captor = ArgumentCaptor.forClass(TransacaoDTO.class);
        verify(transacaoServiceMock).addTransacao(captor.capture());
        TransacaoDTO captured = captor.getValue();
        assertThat(captured.valor()).isEqualTo(123.45);
        assertThat(captured.dataHora()).isAtSameInstantAs(OffsetDateTime.parse("2020-08-07T12:34:56.789-03:00"));
    }

    @Test 
    public void shouldAddNewTransacaoWithValueZero() throws Exception {
        String transacao = """
                {"valor": 0, "dataHora": "2020-08-07T12:34:56.789-03:00"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().isCreated());
    }

    @Test 
    public void shouldNotAddTransacaoWithNegativeValue() throws Exception {
        String transacao = """
                {"valor": -42.42, "dataHora": "2020-08-07T12:34:56.789-03:00"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(422));
    }

    @Test 
    public void shouldNotAddTransacaoWithDataHoraInTheFuture() throws Exception {
        String transacao = """
                {"valor": 123.45, "dataHora": "2100-01-01T12:00:00-03:00"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(422));
    }

    @Test 
    public void shouldNotAddTransacaoWithNullValue() throws Exception {
        String transacao = """
                {"valor": null, "dataHora": "2020-08-07T12:34:56.789-03:00"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(422));
    }

    @Test 
    public void shouldNotAddTransacaoWithNullDataHora() throws Exception {
        String transacao = """
                {"valor": 123.45, "dataHora": null}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(422));
    }

    @Test 
    public void shouldNotAddTransacaoWithWrongValueJson() throws Exception {
        String transacao = """
                {"valor": "abcd", "dataHora": "2020-08-07T12:34:56.789-03:00"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(400));
    }

    @Test 
    public void shouldNotAddTransacaoWithWrongDataHoraJson() throws Exception {
        String transacao = """
                {"valor": 123.45, "dataHora": "2026-01-01"}
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(400));
    }

    @Test 
    public void shouldNotAddTransacaoWithBadFormatJson() throws Exception {
        String transacao = """
                {"valor": 42.42, "dataHora": "2020-08-07T12:34:56.789-03:00"
            """;
        ResultActions response = performTransacaoPost(transacao);
        response.andExpect(status().is(400));
    }

    @Test
    public void shouldDeleteAllTransacoes() throws Exception {
        mockMvc.perform(delete("/transacao")).andExpect(status().is(200));
        verify(transacaoServiceMock).deleteAll();
    }
}
