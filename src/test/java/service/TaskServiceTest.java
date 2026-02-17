package service;

import com.br.shared.contracts.model.TaskRepresentation;
import com.br.task_service.entity.Task;
import com.br.task_service.mapper.TaskMapper;
import com.br.task_service.producer.TaskProducer;
import com.br.task_service.repository.TaskRepository;
import com.br.task_service.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock private TaskProducer taskProducer;
    @Mock private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Deve salvar tarefa e enviar para a fila de notificações")
    void deveCriarTarefaEEnviarNotificacao() {

        var task = new Task();
        task.setTitulo("Testar Ecossistema");
        List<Task> tasks = List.of(task);

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toRepresentation(any(Task.class))).thenReturn(new TaskRepresentation());

        taskService.createTasks(tasks);

        verify(taskRepository, times(1)).save(task);
        verify(taskProducer, times(1)).enviaTaskParaFila(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar atualizar tarefa inexistente")
    void deveLancarExcecaoNoUpdate() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        task.setId(id);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTasks(List.of(task)));
    }

    @Test
    @DisplayName("Deve deletar tarefas pelo ID")
    void deveDeletarTarefas() {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Task t1 = new Task(); t1.setId(id1);
        Task t2 = new Task(); t2.setId(id2);
        List<Task> tasks = List.of(t1, t2);

        taskService.deleteTasks(tasks);
        
        verify(taskRepository, times(1)).deleteAllById(List.of(id1, id2));
    }
}
