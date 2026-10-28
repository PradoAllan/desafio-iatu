package com.aprado.transacaoApi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.time.Clock;

import com.aprado.transacaoApi.repository.TransacaoRepository;
import com.aprado.transacaoApi.dto.EstatisticaDTO;
import com.aprado.transacaoApi.model.TransacaoModel;

@Service
public class EstatisticaService {

    private final TransacaoRepository transacaoRepository;
    private final long statisticsTime;
    private final Clock clock;

    private static final Logger log = LoggerFactory.getLogger(EstatisticaService.class);

    public EstatisticaService(TransacaoRepository transacaoRepository,
        @Value("${statistics.time}") long statisticsTime,
        Clock clock
    ) {
        this.transacaoRepository = transacaoRepository;
        this.statisticsTime = statisticsTime;
        this.clock = clock;

        log.info("The application is configured to use {} seconds to calculate the Statistics.", this.statisticsTime);
    }

    public EstatisticaDTO getStatistics() {
        DoubleSummaryStatistics summaryStatistics = generateSummaryStatistics();
        boolean semTransacoes = summaryStatistics.getCount() == 0;
        
        EstatisticaDTO estatisticaDTO = new EstatisticaDTO(
            summaryStatistics.getCount(),
            summaryStatistics.getSum(),
            semTransacoes ? 0.0 : summaryStatistics.getAverage(),
            semTransacoes ? 0.0 : summaryStatistics.getMin(),
            semTransacoes ? 0.0 : summaryStatistics.getMax()
        );
        log.debug("Statistics generated with {} transactions.", summaryStatistics.getCount());
        return estatisticaDTO;
    }
    
    private DoubleSummaryStatistics generateSummaryStatistics() {
        log.debug("Collecting transaction from the last {} seconds.", this.statisticsTime);
        DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
        OffsetDateTime limit = OffsetDateTime.now(this.clock).minusSeconds(this.statisticsTime);
        List<TransacaoModel> transacaoList = transacaoRepository.findByDataHoraAfter(limit);

        for (TransacaoModel transacao: transacaoList) {
            summaryStatistics.accept(transacao.getValor());
        }

        log.debug("{} transactions collected.", transacaoList.size());
        return summaryStatistics;
    }
}
