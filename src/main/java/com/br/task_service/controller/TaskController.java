package com.br.task_service.controller;

import com.br.shared.contracts.api.TaskApi;
import com.br.shared.contracts.model.PaginaRepresentation;
import com.br.shared.contracts.model.StatusRepresentation;
import com.br.shared.contracts.model.TaskRepresentation;
import com.br.shared.contracts.model.TaskResponseRepresentation;
import com.br.task_service.mapper.TaskMapper;
import com.br.task_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @Override
    public ResponseEntity<Void> deleteTasks(List<TaskRepresentation> list) {

        log.info("Recebendo solicitação para deletar {} tarefas", list.size());
        taskService.deleteTasks(taskMapper.toEntityList(list));

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TaskResponseRepresentation> listTaks(
            final UUID id,
            final String usuarioId,
            final StatusRepresentation status,
            final LocalDateTime dataCriacaoDe,
            final LocalDateTime dataCriacaoAte,
            final Integer limit,
            final Boolean unPaged) {

        var page = taskService.getTasks(id, usuarioId, status, dataCriacaoDe, dataCriacaoAte, limit, unPaged);

        var tasks = taskMapper.toRepresentationList(page.getContent());

        var paginaInfo = new PaginaRepresentation();
        paginaInfo.setTotalPaginas(page.getTotalPages());
        paginaInfo.setTotalElementos(page.getTotalElements());

        var response = new TaskResponseRepresentation();
        response.setRegistros(tasks);
        response.setPagina(paginaInfo);

        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<Void> postTasks(
            final List<TaskRepresentation> list) {

        log.info("Recebendo solicitação para criar {} tarefas", list.size());
        taskService.createTasks(taskMapper.toEntityList(list));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> putTasks(
            final List<TaskRepresentation> list) {

        log.info("Recebendo solicitação para atualizar {} tarefas", list.size());
        taskService.updateTasks(taskMapper.toEntityList(list));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
