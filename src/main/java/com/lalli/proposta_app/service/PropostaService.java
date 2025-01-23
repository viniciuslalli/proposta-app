package com.lalli.proposta_app.service;

import com.lalli.proposta_app.dto.PropostaRequestDTO;
import com.lalli.proposta_app.dto.PropostaResponseDTO;
import com.lalli.proposta_app.entity.Proposta;
import com.lalli.proposta_app.mapper.PropostaMapper;
import com.lalli.proposta_app.repository.PropostaRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {

    private PropostaRepository propostaRepository;

    private NotificarRabbitMQService notificarRabbitMQService;

    private String exchange;

    public PropostaService(PropostaRepository propostaRepository,
                           NotificarRabbitMQService notificarRabbitMQService,
                           @Value("${rabbitmq.propostapendente.exchange}") String exchange) {
        this.propostaRepository = propostaRepository;
        this.notificarRabbitMQService = notificarRabbitMQService;
        this.exchange = exchange;
    }

    public PropostaResponseDTO criar(PropostaRequestDTO requestDTO) {
        Proposta proposta = PropostaMapper.INSTANCE.convertDtoToProposta(requestDTO);
        propostaRepository.save(proposta);

        PropostaResponseDTO response = PropostaMapper.INSTANCE.convertEntityToDto(proposta);
        notificarRabbitMQService.notificar(response, exchange);

        return response;
    }

    public List<PropostaResponseDTO> obterProposta() {
       return  PropostaMapper.INSTANCE.convertListEntityToListDto(propostaRepository.findAll());
    }
}
