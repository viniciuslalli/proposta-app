package com.lalli.proposta_app.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Bean
    public Queue criarFilaPropostaPendenteMsAnaliseCredito() {
        return QueueBuilder.durable("proposta-pendente.ms-analise-credito").build();
    }

    @Bean
    public Queue criarFilaPropostaPendenteMsNotificacao() {
        return  QueueBuilder.durable("proposta-pendente.ms-notificacao").build();
    }

    @Bean
    public Queue criarFilaPropostaConcluidaMsProposta() {
        return  QueueBuilder.durable("proposta-concluida.ms-proposta").build();
    }

    @Bean
    public Queue criarFilaPropostaConcluidaMsNotificacao() {
        return QueueBuilder.durable("proposta-concluida.ms-notificacao").build();
    }

    @Bean
    public RabbitAdmin criarRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    // Para conseguir criar nossas exchanges e filas
    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public FanoutExchange criarFanoutExchangePropostaPendente(){
        return ExchangeBuilder.fanoutExchange("proposta-pendente.ex").build();
    }

    @Bean
    public Binding criarBindingPropostaPendenteMSAnaliseCredito(){
        return BindingBuilder.bind(criarFilaPropostaPendenteMsAnaliseCredito())
                .to(criarFanoutExchangePropostaPendente());
    }

    @Bean
    public Binding criarBindingPropostaPendenteMSNotificacao(){
        return BindingBuilder.bind(criarFilaPropostaPendenteMsNotificacao())
                .to(criarFanoutExchangePropostaPendente());
    }

    @Bean
    public MessageConverter jackson2JsonMessageConvertergeConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConvertergeConverter());

        return rabbitTemplate;
    }
}
