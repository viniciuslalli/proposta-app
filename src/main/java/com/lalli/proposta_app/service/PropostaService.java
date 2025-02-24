package com.lalli.proposta_app.service;

import com.lalli.proposta_app.dto.PropostaRequestDTO;
import com.lalli.proposta_app.dto.PropostaResponseDTO;
import com.lalli.proposta_app.entity.Proposta;
import com.lalli.proposta_app.mapper.PropostaMapper;
import com.lalli.proposta_app.repository.PropostaRepository;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {

    private PropostaRepository propostaRepository;

    private NotificacaoRabbitService notificacaoRabbitService;

    private String exchange;

    public PropostaService(PropostaRepository propostaRepository,
                           NotificacaoRabbitService notificacaoRabbitService,
                           @Value("${rabbitmq.propostapendente.exchange}") String exchange) {
        this.propostaRepository = propostaRepository;
        this.notificacaoRabbitService = notificacaoRabbitService;
        this.exchange = exchange;
    }

    public PropostaResponseDTO criar(PropostaRequestDTO requestDto) {
        Proposta proposta = PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        int prioridade = proposta.getUsuario().getRenda() > 10000 ? 10 : 5;

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setPriority(prioridade);
            return message;
        };

        notificarRabbitMQ(proposta, messagePostProcessor);

        return PropostaMapper.INSTANCE.convertEntityToDto(proposta);
    }

    private void notificarRabbitMQ(Proposta proposta, MessagePostProcessor messagePostProcessor) {
        try {
            notificacaoRabbitService.notificar(proposta, exchange, messagePostProcessor);
        } catch (RuntimeException ex) {
            proposta.setIntegrada(false);
            propostaRepository.save(proposta);
        }
    }

    public List<PropostaResponseDTO> obterProposta() {
        return PropostaMapper.INSTANCE.convertListEntityToListDto(propostaRepository.findAll());
    }
}
