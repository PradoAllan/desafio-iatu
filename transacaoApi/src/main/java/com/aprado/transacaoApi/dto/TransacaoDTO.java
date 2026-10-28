package com.aprado.transacaoApi.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.PastOrPresent;

public record TransacaoDTO(
    @NotNull(message = "Value can not be empty")
    @PositiveOrZero(message = "Value can not be negative")
    Double valor,

    @NotNull(message = "Data Hora can not be empty")
    @PastOrPresent(message = "Data Hora can not be in the future")
    OffsetDateTime dataHora
) {}
