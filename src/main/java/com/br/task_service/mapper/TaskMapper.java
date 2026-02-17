package com.br.task_service.mapper;

import com.br.shared.contracts.model.TaskRepresentation;
import com.br.task_service.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskRepresentation toRepresentation(Task task);

    List<TaskRepresentation> toRepresentationList(List<Task> tasks);

    List<Task> toEntityList(List<TaskRepresentation> representations);
}
