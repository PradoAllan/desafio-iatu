package com.aprado.transacaoApi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.aprado.transacaoApi.dto.EstatisticaDTO;
import com.aprado.transacaoApi.service.EstatisticaService;

@WebMvcTest(EstatisticaController.class)
public class EstatisticaControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstatisticaService estatisticaServiceMock;

    @Test 
    public void shouldReturnCorrectStatistics() throws Exception {
        when(estatisticaServiceMock.getStatistics()).thenReturn(new EstatisticaDTO(
            3L, 30.0, 10.0, 5.0, 15.0));

        mockMvc.perform(get("/estatistica"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.count").value(3))
            .andExpect(jsonPath("$.sum").value(30.0))
            .andExpect(jsonPath("$.avg").value(10.0))
            .andExpect(jsonPath("$.min").value(5.0))
            .andExpect(jsonPath("$.max").value(15.0));
    }
}
