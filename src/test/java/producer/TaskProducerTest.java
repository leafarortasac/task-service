package producer;

import com.br.shared.contracts.model.TaskRepresentation;
import com.br.task_service.config.RabbitMQConfig;
import com.br.task_service.producer.TaskProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TaskProducer taskProducer;

    @Test
    @DisplayName("Deve enviar a tarefa para o RabbitMQ com os parâmetros corretos")
    void deveEnviarMensagemParaFila() {

        TaskRepresentation task = new TaskRepresentation();
        task.setId(UUID.randomUUID());
        task.setTitulo("Teste de Integração RabbitMQ");

        taskProducer.enviaTaskParaFila(task);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq(RabbitMQConfig.ROUTING_KEY),
                eq(task)
        );
    }
}