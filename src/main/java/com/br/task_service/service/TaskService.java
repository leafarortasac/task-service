package com.br.task_service.service;

import com.br.shared.contracts.model.OperacaoRepresentation;
import com.br.shared.contracts.model.StatusRepresentation;
import com.br.task_service.entity.Task;
import com.br.task_service.mapper.TaskMapper;
import com.br.task_service.producer.TaskProducer;
import com.br.task_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskProducer taskProducer;

    private final TaskMapper taskMapper;

    @Cacheable(value = "tasks",
            key = "{#id, #usuarioId, #status, #dataDe, #dataAte, #limit, #unPaged}")
    public Page<Task> getTasks(
            final UUID id,
            final String usuarioId,
            final StatusRepresentation status,
            final LocalDateTime dataCriacaoDe,
            final LocalDateTime dataCriacaoAte,
            final Integer limit,
            final Boolean unPaged) {

        Pageable pageable = (unPaged != null && unPaged)
                ? PageRequest.of(0, Integer.MAX_VALUE, Sort.by("dataCriacao").descending())
                : PageRequest.of(0, limit, Sort.by("dataCriacao").descending());

        return taskRepository.findByFilters(id, usuarioId, status, dataCriacaoDe, dataCriacaoAte, pageable);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void createTasks(final List<Task> tasks) {
        tasks.forEach(task -> {
            var savedTask = taskRepository.save(task);
            taskProducer.enviaTaskParaFila(taskMapper.toRepresentation(savedTask));
        });
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void updateTasks(List<Task> tasks) {

        tasks.forEach(task -> {

            var existingTask = taskRepository.findById(task.getId())
                    .orElseThrow(() -> new RuntimeException("Tarefa não encontrada para o ID: " + task.getId()));

            existingTask.setTitulo(task.getTitulo());
            existingTask.setDescricao(task.getDescricao());
            existingTask.setStatus(task.getStatus());
            existingTask.setOperacao(OperacaoRepresentation.ALTERACAO);

            var updatedTask = taskRepository.save(existingTask);
            taskProducer.enviaTaskParaFila(taskMapper.toRepresentation(updatedTask));
        });
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTasks(List<Task> tasks) {

        List<UUID> ids = tasks.stream()
                .map(Task::getId)
                .toList();

        taskRepository.deleteAllById(ids);
    }
}
