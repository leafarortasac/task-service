package com.br.task_service.repository;

import com.br.shared.contracts.model.StatusRepresentation;
import com.br.task_service.entity.Task;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:id IS NULL OR t.id = :id) AND " +
            "(:usuarioId IS NULL OR t.usuarioId = :usuarioId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:dataCriacaoDe IS NULL OR t.dataCriacao >= :dataCriacaoDe) AND " +
            "(:dataCriacaoAte IS NULL OR t.dataCriacao <= :dataCriacaoAte)")
    Page<Task> findByFilters(
            @Param("id") final UUID id,
            @Param("usuarioId") final String usuarioId,
            @Param("status") final StatusRepresentation status,
            @Param("dataCriacaoDe") final LocalDateTime dataCriacaoDe,
            @Param("dataCriacaoAte") final LocalDateTime dataCriacaoAte,
            Pageable pageable);
}
