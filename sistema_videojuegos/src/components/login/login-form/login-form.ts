import { LoginResponse, LoginService } from './../../../services/Login/login.services';
import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-login-form',
  imports: [RouterLink, FormsModule, NgIf, ReactiveFormsModule],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css'
})
export class LoginForm {
  loginForm: FormGroup;
  isLoading: boolean = false;

  constructor(
    private loginService: LoginService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  // Getters para acceder fácilmente a los controles del formulario
  get email(): AbstractControl | null { return this.loginForm.get('email'); }
  get password(): AbstractControl | null { return this.loginForm.get('password'); }

  login() {
    // Marcar todos los campos como touched para mostrar errores
    this.loginForm.markAllAsTouched();

    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;

    this.loginService.login(this.email?.value, this.password?.value).subscribe({
      next: (response: LoginResponse) => {
        this.isLoading = false;
        if (response.success && response.user) {
          console.log('Inicio de sesión exitoso', response.user);

          // Redirigir a Home para todos los usuarios
          this.router.navigate(['/home']);
        } else {
          alert(response.message || 'Usuario o contraseña incorrectos');
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error en login:', error);
        
        if (error.status === 0) {
          alert('Error de conexión. Verifique su conexión a internet.');
        } else if (error.status === 401) {
          alert('Credenciales incorrectas');
        } else if (error.status === 403) {
          alert('Usuario inactivo. Contacte al administrador.');
        } else {
          alert('Error en el servidor. Intente nuevamente.');
        }
      }
    });
  }

  // Métodos de utilidad para mostrar errores en el template
  getEmailErrorMessage(): string {
    if (this.email?.errors?.['required']) {
      return 'El email es requerido';
    }
    if (this.email?.errors?.['email']) {
      return 'Ingrese un email válido';
    }
    return '';
  }

  getPasswordErrorMessage(): string {
    if (this.password?.errors?.['required']) {
      return 'La contraseña es requerida';
    }
    if (this.password?.errors?.['minlength']) {
      return `Mínimo ${this.password.errors?.['minlength'].requiredLength} caracteres`;
    }
    return '';
  }
}