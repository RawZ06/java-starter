package fr.rawz06.starter.web.controller.admin;

import fr.rawz06.starter.api.controller.BatchApi;
import fr.rawz06.starter.api.dto.*;
import fr.rawz06.starter.web.mapper.BatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BatchJobController implements BatchApi {

    private final JobExplorer jobExplorer;
    private final BatchMapper batchMapper;

    @Override
    public ResponseEntity<List<BatchJobDto>> getAllBatchJobs() {
        List<String> jobNames = jobExplorer.getJobNames();

        List<BatchJobDto> jobs = jobNames.stream().map(jobName -> {
            List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
            Long jobInstanceCount;
            try {
                jobInstanceCount = jobExplorer.getJobInstanceCount(jobName);
            } catch (NoSuchJobException e) {
                throw new RuntimeException(e);
            }

            BatchJobDto jobDto = new BatchJobDto();
            jobDto.setName(jobName);
            jobDto.setInstanceCount(jobInstanceCount);

            if (!jobInstances.isEmpty()) {
                JobInstance lastInstance = jobInstances.get(0);
                List<JobExecution> executions = jobExplorer.getJobExecutions(lastInstance);
                if (!executions.isEmpty()) {
                    JobExecution lastExecution = executions.get(0);
                    jobDto.setLastExecutionStatus(lastExecution.getStatus().toString());
                    if (lastExecution.getStartTime() != null) {
                        jobDto.setLastExecutionTime(lastExecution.getStartTime().atOffset(ZoneOffset.UTC));
                    }
                    if (lastExecution.getEndTime() != null) {
                        jobDto.setLastExecutionEndTime(lastExecution.getEndTime().atOffset(ZoneOffset.UTC));
                    }
                }
            }

            return jobDto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @Override
    public ResponseEntity<List<JobExecutionDto>> getJobExecutions(String jobName, Integer page, Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, pageNum * pageSize, pageSize);

        List<JobExecutionDto> executions = jobInstances.stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream()
                        .map(execution -> batchMapper.toJobExecutionDto(execution, jobName)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(executions);
    }

    @Override
    public ResponseEntity<JobExecutionDetailsDto> getJobExecutionDetails(String jobName, Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);

        if (execution == null) {
            return ResponseEntity.notFound().build();
        }

        JobExecutionDetailsDto details = batchMapper.toJobExecutionDetailsDto(execution, jobName);

        return ResponseEntity.ok(details);
    }
}
