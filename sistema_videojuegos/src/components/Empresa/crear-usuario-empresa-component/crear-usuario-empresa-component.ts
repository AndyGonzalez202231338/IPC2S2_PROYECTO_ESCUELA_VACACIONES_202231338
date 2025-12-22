import { Component, OnInit } from '@angular/core';
import { CountsService } from '../../../services/Usuario/counts.service';
import { LoginService } from '../../../services/Login/login.services';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HeaderAdminEmpresa } from '../../Header/header-admin-empresa/header-admin-empresa';
import { Footer } from '../../footer/footer';

@Component({
  selector: 'app-crear-usuario-empresa-component',
  imports: [CommonModule, ReactiveFormsModule, Footer, HeaderAdminEmpresa],
  templateUrl: './crear-usuario-empresa-component.html',
  styleUrl: './crear-usuario-empresa-component.css',
})
export class CrearUsuarioEmpresaComponent implements OnInit {
  adminUserForm!: FormGroup;
  isLoading = false;
  operationDone = false;

  constructor(
    private fb: FormBuilder,
    private countsService: CountsService,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    const currentUser = this.loginService.getCurrentUser();
    if (!currentUser || currentUser.rol.id_rol !== 2) {
      this.router.navigate(['/login']);
    }
  }

  initForm(): void {
    this.adminUserForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      correo: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      fecha_nacimiento: ['', Validators.required],
    });
  }

  submit(): void {
    if (this.adminUserForm.invalid) {
      this.markFormGroupTouched(this.adminUserForm);
      return;
    }

    this.isLoading = true;
    const currentUser = this.loginService.getCurrentUser();
    
    if (!currentUser) {
      this.isLoading = false;
      return;
    }

    const userData = {
      ...this.adminUserForm.value,
      id_rol: 2, 
      id_empresa: currentUser.empresa?.id_empresa || null,
      pais: null,
      telefono: null
    };

    this.countsService.createNewUser(userData).subscribe({
      next: () => {
        this.isLoading = false;
        this.operationDone = true;
        
      },
      error: (error) => {
        console.error('Error al crear administrador:', error);
        this.isLoading = false;
        // Manejar error
      }
    });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}  


