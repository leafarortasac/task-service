package com.br.task_service.producer;

import com.br.shared.contracts.model.TaskRepresentation;
import com.br.task_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviaTaskParaFila(TaskRepresentation task) {
        log.info("Encaminhando pedido {} para a fila de processamento", task.getId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                task
        );
    }
}