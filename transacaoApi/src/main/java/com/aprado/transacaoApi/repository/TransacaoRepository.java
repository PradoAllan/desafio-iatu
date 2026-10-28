package com.aprado.transacaoApi.repository;

import com.aprado.transacaoApi.model.TransacaoModel;
import java.util.List;
import java.util.Collections;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

@Repository
public class TransacaoRepository {

    private final List<TransacaoModel> transacaoList = Collections.synchronizedList(new ArrayList<>());

    public void addTransacao(TransacaoModel transacao) {
        this.transacaoList.add(transacao);
    }

    public void deleteAll() {
        this.transacaoList.clear();
    }

    public List<TransacaoModel> findByDataHoraAfter(OffsetDateTime limit) {
        List<TransacaoModel> filteredList;
        synchronized (this.transacaoList) {
            filteredList = this.transacaoList
                .stream()
                .filter(transacao -> transacao.getDataHora().isAfter(limit))
                .toList();
        }
        return (filteredList);
    }

    public int getTransacaoCount() {
        return (this.transacaoList.size());
    }
}
