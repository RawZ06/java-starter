import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface BatchJob {
  name: string;
  instanceCount: number;
  lastExecutionStatus?: string;
  lastExecutionTime?: string;
  lastExecutionEndTime?: string;
}

export interface BatchJobExecution {
  id: number;
  instanceId: number;
  jobName: string;
  status: string;
  startTime: string;
  endTime: string;
  exitCode: string;
  exitDescription: string;
}

export interface BatchJobExecutionDetails extends BatchJobExecution {
  createTime: string;
  lastUpdated: string;
  steps: Array<{
    stepName: string;
    status: string;
    readCount: number;
    writeCount: number;
    commitCount: number;
    rollbackCount: number;
    startTime: string;
    endTime: string;
  }>;
}

@Injectable({
  providedIn: 'root',
})
export class BatchService {
  private http = inject(HttpClient);

  jobs = signal<BatchJob[]>([]);
  executions = signal<BatchJobExecution[]>([]);
  executionDetails = signal<BatchJobExecutionDetails | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  async loadJobs(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const jobs = await this.http.get<BatchJob[]>('/api/admin/batch/jobs').toPromise();
      this.jobs.set(jobs || []);
    } catch (err: any) {
      this.error.set(err.message || 'Failed to load jobs');
    } finally {
      this.loading.set(false);
    }
  }

  async loadExecutions(jobName: string, page: number = 0, size: number = 10): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const executions = await this.http
        .get<BatchJobExecution[]>(`/api/admin/batch/jobs/${jobName}/executions`, {
          params: { page: page.toString(), size: size.toString() },
        })
        .toPromise();
      this.executions.set(executions || []);
    } catch (err: any) {
      this.error.set(err.message || 'Failed to load executions');
    } finally {
      this.loading.set(false);
    }
  }

  async loadExecutionDetails(jobName: string, executionId: number): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const details = await this.http
        .get<BatchJobExecutionDetails>(`/api/admin/batch/jobs/${jobName}/executions/${executionId}`)
        .toPromise();
      this.executionDetails.set(details || null);
    } catch (err: any) {
      this.error.set(err.message || 'Failed to load execution details');
    } finally {
      this.loading.set(false);
    }
  }
}
