import { Component, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';
import { SelectModule } from 'primeng/select';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';

interface User {
  id?: number;
  login: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: string;
  active: boolean;
  password?: string;
  createdAt?: string;
  updatedAt?: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    TranslateModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    FormsModule,
    CheckboxModule,
    SelectModule,
    ConfirmDialogModule,
    ToastModule,
    TooltipModule
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './users.html'
})
export class Users implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  displayDialog = signal(false);
  selectedUser = signal<User | null>(null);
  isEditMode = signal(false);

  roles = [
    { label: 'USER', value: 'USER' },
    { label: 'ADMIN', value: 'ADMIN' }
  ];

  // Form model
  userForm: User = {
    login: '',
    email: '',
    firstName: '',
    lastName: '',
    role: 'USER',
    active: true,
    password: ''
  };

  constructor(
    private http: HttpClient,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading.set(true);
    this.http.get<User[]>('/api/admin/users').subscribe({
      next: (data) => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.loading.set(false);
        this.messageService.add({
          severity: 'error',
          summary: this.translate.instant('users.error'),
          detail: this.translate.instant('users.errorLoadingUsers')
        });
      }
    });
  }

  openNew() {
    this.userForm = {
      login: '',
      email: '',
      firstName: '',
      lastName: '',
      role: 'USER',
      active: true,
      password: ''
    };
    this.isEditMode.set(false);
    this.displayDialog.set(true);
  }

  editUser(user: User) {
    this.userForm = { ...user, password: '' };
    this.selectedUser.set(user);
    this.isEditMode.set(true);
    this.displayDialog.set(true);
  }

  deleteUser(user: User) {
    this.confirmationService.confirm({
      message: `${this.translate.instant('users.confirmDeleteMessage')} ${user.login} ?`,
      header: this.translate.instant('users.confirmDelete'),
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: this.translate.instant('users.yes'),
      rejectLabel: this.translate.instant('users.no'),
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text p-button-secondary',
      accept: () => {
        this.http.delete(`/api/admin/users/${user.id}`).subscribe({
          next: () => {
            this.loadUsers();
            this.messageService.add({
              severity: 'success',
              summary: this.translate.instant('users.userDeleted'),
              detail: this.translate.instant('users.userDeletedSuccess')
            });
          },
          error: (err) => {
            console.error('Error deleting user:', err);
            this.messageService.add({
              severity: 'error',
              summary: this.translate.instant('users.error'),
              detail: this.translate.instant('users.errorDeletingUser')
            });
          }
        });
      }
    });
  }

  saveUser() {
    if (this.isEditMode()) {
      // Update
      this.http.put<User>(`/api/admin/users/${this.selectedUser()?.id}`, this.userForm).subscribe({
        next: () => {
          this.loadUsers();
          this.displayDialog.set(false);
          this.messageService.add({
            severity: 'success',
            summary: this.translate.instant('users.userUpdated'),
            detail: this.translate.instant('users.userUpdatedSuccess')
          });
        },
        error: (err) => {
          console.error('Error updating user:', err);
          this.messageService.add({
            severity: 'error',
            summary: this.translate.instant('users.error'),
            detail: this.translate.instant('users.errorUpdatingUser')
          });
        }
      });
    } else {
      // Create
      this.http.post<User>('/api/admin/users', this.userForm).subscribe({
        next: () => {
          this.loadUsers();
          this.displayDialog.set(false);
          this.messageService.add({
            severity: 'success',
            summary: this.translate.instant('users.userCreated'),
            detail: this.translate.instant('users.userCreatedSuccess')
          });
        },
        error: (err) => {
          console.error('Error creating user:', err);
          this.messageService.add({
            severity: 'error',
            summary: this.translate.instant('users.error'),
            detail: this.translate.instant('users.errorCreatingUser')
          });
        }
      });
    }
  }

  hideDialog() {
    this.displayDialog.set(false);
  }
}
