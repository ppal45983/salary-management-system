import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CountryInfo, Department, Designation, Employee } from '../../../core/models/models';
import { EmployeeService } from '../../../core/services/employee.service';
import { MasterDataService } from '../../../core/services/master-data.service';

@Component({
  selector: 'app-employee-form-modal',
  template: `
    <div class="modal-backdrop" (click)="onBackdropClick($event)">
      <div class="modal-content">
        <div class="modal-header">
          <div>
            <h3 class="modal-title">{{ isEdit ? 'Edit Employee' : 'Add New Employee' }}</h3>
            <p class="modal-sub">{{ isEdit ? 'Update employee record and work details' : 'Register a new employee in the ACME organization' }}</p>
          </div>
          <button class="btn-icon" (click)="closeModal()">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>

        <form (ngSubmit)="saveEmployee()">
          <div class="modal-body">
            <div class="form-row">
              <div class="form-group flex-1">
                <label>First Name *</label>
                <input type="text" class="form-control" [(ngModel)]="formData.firstName" name="firstName" required />
              </div>
              <div class="form-group flex-1">
                <label>Last Name *</label>
                <input type="text" class="form-control" [(ngModel)]="formData.lastName" name="lastName" required />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group flex-1">
                <label>Corporate Email *</label>
                <input type="email" class="form-control" [(ngModel)]="formData.email" name="email" required />
              </div>
              <div class="form-group flex-1">
                <label>Phone Number</label>
                <input type="tel" class="form-control" [(ngModel)]="formData.phone" name="phone" placeholder="+1-555-0192" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group flex-1">
                <label>Department *</label>
                <select class="form-control" [(ngModel)]="formData.departmentId" name="departmentId" required (change)="onDeptChange()">
                  <option *ngFor="let d of departments" [value]="d.id">{{ d.name }}</option>
                </select>
              </div>
              <div class="form-group flex-1">
                <label>Job Designation *</label>
                <select class="form-control" [(ngModel)]="formData.designationId" name="designationId" required>
                  <option *ngFor="let des of designations" [value]="des.id">{{ des.title }} ({{ des.level }})</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group flex-1">
                <label>Country *</label>
                <select class="form-control" [(ngModel)]="formData.country" name="country" required (change)="onCountryChange()">
                  <option *ngFor="let c of countries" [value]="c.name">{{ c.name }} ({{ c.currency }})</option>
                </select>
              </div>
              <div class="form-group flex-1">
                <label>Payroll Currency</label>
                <input type="text" class="form-control" [(ngModel)]="formData.currency" name="currency" readonly />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group flex-1">
                <label>Hire Date *</label>
                <input type="date" class="form-control" [(ngModel)]="formData.hireDate" name="hireDate" required />
              </div>
              <div class="form-group flex-1">
                <label>Status</label>
                <select class="form-control" [(ngModel)]="formData.status" name="status">
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="ON_LEAVE">ON_LEAVE</option>
                  <option value="INACTIVE">INACTIVE</option>
                </select>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
            <button type="submit" class="btn btn-primary" [disabled]="saving">
              <i *ngIf="saving" class="fa-solid fa-spinner fa-spin"></i>
              <span>{{ saving ? 'Saving...' : 'Save Employee' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .modal-title { font-size: 1.25rem; font-weight: 800; color: #0f172a; }
    .modal-sub { font-size: 0.8rem; color: #64748b; margin-top: 2px; }
    .form-row { display: flex; gap: 16px; margin-bottom: 12px; }
    .flex-1 { flex: 1; }
  `]
})
export class EmployeeFormModalComponent implements OnInit {
  @Input() employee: Employee | null = null;
  @Input() departments: Department[] = [];
  @Input() countries: CountryInfo[] = [];
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<Employee>();

  designations: Designation[] = [];
  isEdit = false;
  saving = false;

  formData: Partial<Employee> = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    departmentId: 1,
    designationId: 1,
    country: 'United States',
    currency: 'USD',
    hireDate: new Date().toISOString().split('T')[0],
    status: 'ACTIVE'
  };

  constructor(
    private employeeService: EmployeeService,
    private masterDataService: MasterDataService
  ) {}

  ngOnInit() {
    this.masterDataService.getDesignations().subscribe(d => this.designations = d);

    if (this.employee) {
      this.isEdit = true;
      this.formData = { ...this.employee };
    }
  }

  onDeptChange() {
    // Keep designation in sync if needed
  }

  onCountryChange() {
    const c = this.countries.find(item => item.name === this.formData.country);
    if (c) {
      this.formData.currency = c.currency;
    }
  }

  closeModal() {
    this.close.emit();
  }

  onBackdropClick(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.closeModal();
    }
  }

  saveEmployee() {
    this.saving = true;
    if (this.isEdit && this.employee) {
      this.employeeService.updateEmployee(this.employee.id, this.formData).subscribe(res => {
        this.saving = false;
        this.saved.emit(res);
      });
    } else {
      this.employeeService.createEmployee(this.formData).subscribe(res => {
        this.saving = false;
        this.saved.emit(res);
      });
    }
  }
}
