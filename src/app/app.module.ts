import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Layout & Core Components
import { ToastComponent } from './components/layout/toast/toast.component';
import { SidebarComponent } from './components/layout/sidebar/sidebar.component';
import { NavbarComponent } from './components/layout/navbar/navbar.component';

// Views
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmployeeListComponent } from './components/employees/employee-list/employee-list.component';
import { EmployeeFormModalComponent } from './components/employees/employee-form-modal/employee-form-modal.component';
import { EmployeeDetailModalComponent } from './components/employees/employee-detail-modal/employee-detail-modal.component';
import { SalaryListComponent } from './components/salaries/salary-list/salary-list.component';
import { SalaryFormModalComponent } from './components/salaries/salary-form-modal/salary-form-modal.component';
import { SalarySlipModalComponent } from './components/salaries/salary-slip-modal/salary-slip-modal.component';
import { TaxCalculatorComponent } from './components/salaries/tax-calculator/tax-calculator.component';
import { MasterDataComponent } from './components/masters/master-data.component';

// Interceptors & Services
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    ToastComponent,
    SidebarComponent,
    NavbarComponent,
    LoginComponent,
    DashboardComponent,
    EmployeeListComponent,
    EmployeeFormModalComponent,
    EmployeeDetailModalComponent,
    SalaryListComponent,
    SalaryFormModalComponent,
    SalarySlipModalComponent,
    TaxCalculatorComponent,
    MasterDataComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
