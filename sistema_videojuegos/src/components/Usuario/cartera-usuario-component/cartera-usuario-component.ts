import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Footer } from '../../footer/footer';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Header } from '../../Header/header/header';
import { CommonModule } from '@angular/common';
import { SaldoService } from '../../../services/Usuario/saldo.service';
import { LoginService } from '../../../services/Login/login.services';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cartera-usuario-component',
  imports: [CommonModule, ReactiveFormsModule, Header, Footer],
  templateUrl: './cartera-usuario-component.html',
  styleUrl: './cartera-usuario-component.css',
})
export class CarteraUsuarioComponent implements OnInit {
  recargaForm: FormGroup;
  saldoActual: number = 0;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  usuarioId: number | null = null;
  usuarioNombre: string = '';

  // Montos predefinidos para recarga rápida
  montosPredefinidos = [10, 20, 50, 100, 200, 500];

  constructor(
    private fb: FormBuilder,
    private saldoService: SaldoService,
    private loginService: LoginService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.recargaForm = this.createForm();
  }

  ngOnInit(): void {
    this.cargarUsuario();
    this.cargarSaldo();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      monto: ['', [Validators.required, Validators.min(1), Validators.max(10000)]]
    });
  }

  private cargarUsuario(): void {
    const currentUser = this.loginService.getCurrentUser();
    if (currentUser && currentUser.idUsuario) {
      console.log('Usuario actual cargado cartera:', currentUser);
      this.usuarioId = currentUser.idUsuario;
      this.usuarioNombre = currentUser.nombre || 'Usuario';
      this.cdr.detectChanges();
    } else {
      this.router.navigate(['/login']);
    }
  }

  cargarSaldo(): void {
    if (!this.usuarioId) return;

    this.isLoading = true;
    this.saldoService.obtenerSaldo(this.usuarioId).subscribe({
      next: (response) => {
        this.saldoActual = response.saldo_cartera;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar saldo:', error);
        this.errorMessage = 'Error al cargar el saldo. Intenta nuevamente.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Método para usar montos predefinidos
  seleccionarMontoPredefinido(monto: number): void {
    this.recargaForm.patchValue({ monto: monto });
  }

  // Enviar recarga
  onSubmit(): void {
    if (this.recargaForm.invalid || !this.usuarioId) return;

    const monto = Number(this.recargaForm.value.monto);

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.saldoService.recargarSaldo(this.usuarioId, monto).subscribe({
      next: (response) => {
        this.saldoActual = response.saldo_cartera;
        this.successMessage = `¡Recarga exitosa! Se acreditaron Q${monto.toFixed(2)} a tu cuenta. Saldo actual: Q${response.saldo_cartera.toFixed(2)}`;
        this.recargaForm.reset();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al recargar saldo:', error);
        const errorMessage = error.error?.error || error.message || 'Error al procesar la recarga.';
        this.errorMessage = this.parsearError(errorMessage);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Parsear mensajes de error del backend
  private parsearError(errorMessage: string): string {
    if (errorMessage.includes('Saldo insuficiente')) {
      return 'Saldo insuficiente para realizar la operación.';
    } else if (errorMessage.includes('Usuario no encontrado')) {
      return 'Usuario no encontrado. Por favor, inicia sesión nuevamente.';
    } else if (errorMessage.includes('El monto debe ser mayor a 0')) {
      return 'El monto debe ser mayor a Q0.00.';
    } else {
      return 'Error al procesar la recarga. Verifica tu conexión.';
    }
  }

  // Formatear precio
  formatearPrecio(precio: number): string {
    return `Q${precio.toFixed(2)}`;
  }

  // Navegar a compras
  navegarACompras(): void {
    this.router.navigate(['/usuario/tienda']);
  }

  get f() {
    return this.recargaForm.controls;
  }
}
