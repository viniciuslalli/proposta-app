package com.lalli.proposta_app.agendador;

import com.lalli.proposta_app.repository.PropostaRepository;
import com.lalli.proposta_app.service.NotificacoRabbitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropostaSemIntegracao {

    private PropostaRepository propostaRepository;

    private NotificacoRabbitService notificacoRabbitService;

    private String exchange;


    public PropostaSemIntegracao(PropostaRepository propostaRepository,
                                 NotificacoRabbitService notificacoRabbitService,
                                 @Value("${rabbitmq.propostapendente.exchange}") String exchange) {
        this.propostaRepository = propostaRepository;
        this.notificacoRabbitService = notificacoRabbitService;
        this.exchange = exchange;
    }

    public void buscarPropostaSemIntegracao() {
        propostaRepository.findAllByIntegradaIsFalse().forEach(proposta -> {
            try {
                notificacoRabbitService.notificar(proposta, exchange);
                proposta.setIntegrada(true);
                propostaRepository.save(proposta);
            } catch (RuntimeException ex){
                System.out.printf(ex.getMessage());
            }
        });
    }
}
