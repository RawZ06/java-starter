package fr.rawz06.starter.web.mapper;

import fr.rawz06.starter.api.dto.JobExecutionDetailsDto;
import fr.rawz06.starter.api.dto.JobExecutionDto;
import fr.rawz06.starter.api.dto.StepExecutionDto;
import org.mapstruct.*;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    // JobExecution → JobExecutionDto
    @Mapping(target = "id", source = "jobExecution.id")
    @Mapping(target = "instanceId", source = "jobExecution.jobInstance.instanceId")
    @Mapping(target = "jobName", source = "jobName")
    @Mapping(target = "status", expression = "java(jobExecution.getStatus().toString())")
    @Mapping(target = "startTime", source = "jobExecution.startTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "endTime", source = "jobExecution.endTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "exitCode", expression = "java(jobExecution.getExitStatus().getExitCode())")
    @Mapping(target = "exitDescription", expression = "java(jobExecution.getExitStatus().getExitDescription())")
    JobExecutionDto toJobExecutionDto(JobExecution jobExecution, String jobName);

    // JobExecution → JobExecutionDetailsDto
    @Mapping(target = "id", source = "jobExecution.id")
    @Mapping(target = "jobName", source = "jobName")
    @Mapping(target = "status", expression = "java(jobExecution.getStatus().toString())")
    @Mapping(target = "startTime", source = "jobExecution.startTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "endTime", source = "jobExecution.endTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "exitCode", expression = "java(jobExecution.getExitStatus().getExitCode())")
    @Mapping(target = "exitDescription", expression = "java(jobExecution.getExitStatus().getExitDescription())")
    @Mapping(target = "createTime", source = "jobExecution.createTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "lastUpdated", source = "jobExecution.lastUpdated", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "steps", source = "jobExecution.stepExecutions")
    JobExecutionDetailsDto toJobExecutionDetailsDto(JobExecution jobExecution, String jobName);

    // StepExecution → StepExecutionDto
    @Mapping(target = "stepName", source = "stepName")
    @Mapping(target = "status", expression = "java(stepExecution.getStatus().toString())")
    @Mapping(target = "readCount", expression = "java((int) stepExecution.getReadCount())")
    @Mapping(target = "writeCount", expression = "java((int) stepExecution.getWriteCount())")
    @Mapping(target = "commitCount", expression = "java((int) stepExecution.getCommitCount())")
    @Mapping(target = "rollbackCount", expression = "java((int) stepExecution.getRollbackCount())")
    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "localDateTimeToOffsetDateTime")
    StepExecutionDto toStepExecutionDto(StepExecution stepExecution);

    // Helper method for date conversion
    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}