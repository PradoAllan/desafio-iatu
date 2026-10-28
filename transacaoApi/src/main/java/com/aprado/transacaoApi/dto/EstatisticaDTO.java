package com.aprado.transacaoApi.dto;

public record EstatisticaDTO(
    Long count,
    Double sum,
    Double avg,
    Double min,
    Double max
) {}
