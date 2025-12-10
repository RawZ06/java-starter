import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { BatchService } from '../../../services/batch.service';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './jobs.html',
})
export class JobsComponent implements OnInit {
  batchService = inject(BatchService);
  selectedJob = signal<string | null>(null);

  ngOnInit() {
    this.batchService.loadJobs();
  }

  viewExecutions(jobName: string) {
    this.selectedJob.set(jobName);
    this.batchService.loadExecutions(jobName);
  }

  backToJobs() {
    this.selectedJob.set(null);
    this.batchService.executions.set([]);
  }

  viewExecutionDetails(jobName: string, executionId: number) {
    this.batchService.loadExecutionDetails(jobName, executionId);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300';
      case 'FAILED':
        return 'bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-300';
      case 'STARTED':
      case 'STARTING':
        return 'bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-300';
      default:
        return 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-300';
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('fr-FR');
  }

  getDuration(start: string | undefined, end: string | undefined): string {
    if (!start || !end) return '-';
    const duration = new Date(end).getTime() - new Date(start).getTime();
    const seconds = Math.floor(duration / 1000);
    const minutes = Math.floor(seconds / 60);
    if (minutes > 0) {
      return `${minutes}m ${seconds % 60}s`;
    }
    return `${seconds}s`;
  }
}
