import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmployeeListComponent } from './components/employees/employee-list/employee-list.component';
import { SalaryListComponent } from './components/salaries/salary-list/salary-list.component';
import { TaxCalculatorComponent } from './components/salaries/tax-calculator/tax-calculator.component';
import { MasterDataComponent } from './components/masters/master-data.component';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'employees', component: EmployeeListComponent, canActivate: [AuthGuard] },
  { path: 'salaries', component: SalaryListComponent, canActivate: [AuthGuard] },
  { path: 'tax-calculator', component: TaxCalculatorComponent, canActivate: [AuthGuard] },
  { path: 'masters', component: MasterDataComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
