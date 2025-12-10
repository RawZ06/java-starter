package fr.rawz06.starter.web.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BatchJobController {

    private final JobExplorer jobExplorer;

    @GetMapping("/jobs")
    public ResponseEntity<List<Map<String, Object>>> getAllJobs() {
        List<String> jobNames = jobExplorer.getJobNames();

        List<Map<String, Object>> jobs = jobNames.stream().map(jobName -> {
            List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
            Long jobInstanceCount = null;
            try {
                jobInstanceCount = jobExplorer.getJobInstanceCount(jobName);
            } catch (NoSuchJobException e) {
                throw new RuntimeException(e);
            }

            Map<String, Object> jobInfo = new HashMap<>();
            jobInfo.put("name", jobName);
            jobInfo.put("instanceCount", jobInstanceCount);

            if (!jobInstances.isEmpty()) {
                JobInstance lastInstance = jobInstances.get(0);
                List<JobExecution> executions = jobExplorer.getJobExecutions(lastInstance);
                if (!executions.isEmpty()) {
                    JobExecution lastExecution = executions.get(0);
                    jobInfo.put("lastExecutionStatus", lastExecution.getStatus().toString());
                    jobInfo.put("lastExecutionTime", lastExecution.getStartTime());
                    jobInfo.put("lastExecutionEndTime", lastExecution.getEndTime());
                }
            }

            return jobInfo;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{jobName}/executions")
    public ResponseEntity<List<Map<String, Object>>> getJobExecutions(
            @PathVariable String jobName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, page * size, size);

        List<Map<String, Object>> executions = jobInstances.stream().flatMap(instance ->
            jobExplorer.getJobExecutions(instance).stream().map(execution -> {
                Map<String, Object> executionInfo = new HashMap<>();
                executionInfo.put("id", execution.getId());
                executionInfo.put("instanceId", instance.getInstanceId());
                executionInfo.put("jobName", jobName);
                executionInfo.put("status", execution.getStatus().toString());
                executionInfo.put("startTime", execution.getStartTime());
                executionInfo.put("endTime", execution.getEndTime());
                executionInfo.put("exitCode", execution.getExitStatus().getExitCode());
                executionInfo.put("exitDescription", execution.getExitStatus().getExitDescription());
                return executionInfo;
            })
        ).collect(Collectors.toList());

        return ResponseEntity.ok(executions);
    }

    @GetMapping("/jobs/{jobName}/executions/{executionId}")
    public ResponseEntity<Map<String, Object>> getJobExecutionDetails(
            @PathVariable String jobName,
            @PathVariable Long executionId) {

        JobExecution execution = jobExplorer.getJobExecution(executionId);

        if (execution == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> details = new HashMap<>();
        details.put("id", execution.getId());
        details.put("jobName", jobName);
        details.put("status", execution.getStatus().toString());
        details.put("startTime", execution.getStartTime());
        details.put("endTime", execution.getEndTime());
        details.put("exitCode", execution.getExitStatus().getExitCode());
        details.put("exitDescription", execution.getExitStatus().getExitDescription());
        details.put("createTime", execution.getCreateTime());
        details.put("lastUpdated", execution.getLastUpdated());

        // Step executions
        List<Map<String, Object>> steps = execution.getStepExecutions().stream().map(step -> {
            Map<String, Object> stepInfo = new HashMap<>();
            stepInfo.put("stepName", step.getStepName());
            stepInfo.put("status", step.getStatus().toString());
            stepInfo.put("readCount", step.getReadCount());
            stepInfo.put("writeCount", step.getWriteCount());
            stepInfo.put("commitCount", step.getCommitCount());
            stepInfo.put("rollbackCount", step.getRollbackCount());
            stepInfo.put("startTime", step.getStartTime());
            stepInfo.put("endTime", step.getEndTime());
            return stepInfo;
        }).collect(Collectors.toList());

        details.put("steps", steps);

        return ResponseEntity.ok(details);
    }
}
