package com.aprado.transacaoApi.model;

import java.time.OffsetDateTime;

public class TransacaoModel {

    private final Double valor;
    private final OffsetDateTime dataHora;

    public TransacaoModel(Double valor, OffsetDateTime dataHora) {
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public Double getValor() {
        return (this.valor);
    }

    public OffsetDateTime getDataHora() {
        return (this.dataHora);
    }
}
