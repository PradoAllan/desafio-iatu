package com.aprado.transacaoApi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest(properties = "statistics.time=" + TransacaoApiIntegrationTests.STATISTICS_TIME)
@AutoConfigureMockMvc 
public class TransacaoApiIntegrationTests {

    static final long STATISTICS_TIME = 60L;
    
    @Autowired 
    private MockMvc mockMvc;

    private ResultActions postTransacao(String json) throws Exception {
        return mockMvc.perform(post("/transacao")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
    }

    private void postTransacoes(List<Double> valores, OffsetDateTime dataHora) throws Exception {
        for (Double valor: valores) {
            String transacao = """
                {"valor": %s, "dataHora": "%s"}
                """.formatted(valor, dataHora);
            ResultActions response = postTransacao(transacao);
            response.andExpect(status().isCreated());
        }
    }

    @BeforeEach 
    public void cleanTransacoes() throws Exception {
        mockMvc.perform(delete("/transacao"));
    }

    @Test 
    public void shouldDeleteAllTransacaoAndReturnOk() throws Exception {
        postTransacoes(List.of(5.0, 15.0, 10.0), OffsetDateTime.now().minusSeconds(5L));

        mockMvc.perform(delete("/transacao")).andExpect(status().isOk());

        mockMvc.perform(get("/estatistica"))
            .andExpect(jsonPath("$.count").value(0))
            .andExpect(jsonPath("$.sum").value(0))
            .andExpect(jsonPath("$.avg").value(0))
            .andExpect(jsonPath("$.min").value(0))
            .andExpect(jsonPath("$.max").value(0));
    }

    @Test 
    public void shouldReturnCorrectStatistics() throws Exception {
        postTransacoes(List.of(5.0, 15.0, 10.0), OffsetDateTime.now().minusSeconds(5L));

        mockMvc.perform(get("/estatistica"))
            .andExpect(jsonPath("$.count").value(3))
            .andExpect(jsonPath("$.sum").value(30))
            .andExpect(jsonPath("$.avg").value(10))
            .andExpect(jsonPath("$.min").value(5))
            .andExpect(jsonPath("$.max").value(15));
    }

    @Test
    public void shouldNotReturnAnyStatistics() throws Exception {
        String transacao = """
            {"valor": 123.45, "dataHora": "%s"}
            """.formatted(OffsetDateTime.now().plusSeconds(10L));
        ResultActions response = postTransacao(transacao);
        response.andExpect(status().is(422));

        mockMvc.perform(get("/estatistica"))
            .andExpect(jsonPath("$.count").value(0))
            .andExpect(jsonPath("$.sum").value(0))
            .andExpect(jsonPath("$.avg").value(0))
            .andExpect(jsonPath("$.min").value(0))
            .andExpect(jsonPath("$.max").value(0));
    }

    @Test 
    public void shouldReturnStatisticsAfterInterval() throws Exception {
        postTransacoes(List.of(5.0, 15.0, 10.0), OffsetDateTime.now().minusSeconds(STATISTICS_TIME + 10L));
        postTransacoes(List.of(5.0, 15.0, 10.0), OffsetDateTime.now().minusSeconds(STATISTICS_TIME - 20L));

        mockMvc.perform(get("/estatistica"))
            .andExpect(jsonPath("$.count").value(3))
            .andExpect(jsonPath("$.sum").value(30))
            .andExpect(jsonPath("$.avg").value(10))
            .andExpect(jsonPath("$.min").value(5))
            .andExpect(jsonPath("$.max").value(15));
    }
}