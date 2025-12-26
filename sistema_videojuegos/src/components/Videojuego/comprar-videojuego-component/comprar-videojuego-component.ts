import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Videojuego } from '../../../models/videojuego/videojuego';
import { Empresa } from '../../../models/counts/count';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { SaldoService } from '../../../services/Usuario/saldo.service';
import { LoginService } from '../../../services/Login/login.services';
import { Header } from '../../Header/header/header';
import { Footer } from '../../footer/footer';
import { EmpresaService } from '../../../services/Empresa/empresa.service';
import { CompraService } from '../../../services/Compra/compra.service';
import { NewCompraRequest } from '../../../models/compra/compra';

@Component({
  selector: 'app-comprar-videojuego-component',
  imports: [CommonModule, ReactiveFormsModule, Header, Footer],
  templateUrl: './comprar-videojuego-component.html',
  styleUrl: './comprar-videojuego-component.css',
})
export class ComprarVideojuegoComponent implements OnInit {
  videojuego: Videojuego | null = null;
  empresa: Empresa | null = null;
  compraForm: FormGroup;
  
  // Estados
  isLoading = false;
  isLoadingEmpresa = false;
  isComprando = false;
  errorMessage = '';
  successMessage = '';
  currentUser: any = null;
  videojuegoId: number | null = null;
  saldoUsuario: number = 0;

  // Fecha mínima y máxima
  fechaMinima: string;
  fechaMaxima: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private videojuegoService: VideojuegoService,
    private empresaService: EmpresaService,
    private saldoService: SaldoService,
    private compraService: CompraService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef
  ) {
    // Configurar fechas límite
    const hoy = new Date();
    const fechaMin = new Date(2000, 0, 1); // 01/01/2000
    const fechaMax = new Date(); // Hoy (no fechas futuras)
    
    this.fechaMinima = this.formatearFechaParaInput(fechaMin);
    this.fechaMaxima = this.formatearFechaParaInput(fechaMax);
    
    this.compraForm = this.createForm();
  }

  ngOnInit(): void {
    this.currentUser = this.loginService.getCurrentUser();
    
    // Verificar que el usuario esté logueado y sea COMUN
    if (!this.currentUser || this.currentUser.rol?.nombre !== 'COMUN') {
      this.router.navigate(['/login']);
      return;
    }
    
    this.cargarSaldoUsuario();
    this.cargarVideojuego();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      fecha_compra: ['', [
        Validators.required,
        this.fechaNoFuturaValidator.bind(this),
        this.fechaValidaValidator.bind(this)
      ]],
      aceptarTerminos: [false, [Validators.requiredTrue]],
      confirmarCompra: [false, [Validators.requiredTrue]]
    });
  }

  // Validador personalizado para evitar fechas futuras
  private fechaNoFuturaValidator(control: any): { [key: string]: boolean } | null {
    if (!control.value) return null;
    
    const fechaSeleccionada = new Date(control.value);
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0); // Establecer a inicio del día
    
    if (fechaSeleccionada > hoy) {
      return { 'fechaFutura': true };
    }
    
    return null;
  }

  // Validador personalizado para fecha válida
  private fechaValidaValidator(control: any): { [key: string]: boolean } | null {
    if (!control.value) return null;
    
    const fechaSeleccionada = new Date(control.value);
    const fechaMin = new Date(2000, 0, 1);
    
    if (fechaSeleccionada < fechaMin) {
      return { 'fechaMuyAntigua': true };
    }
    
    return null;
  }

  // Formatear fecha para input type="date"
  private formatearFechaParaInput(fecha: Date): string {
    const year = fecha.getFullYear();
    const month = (fecha.getMonth() + 1).toString().padStart(2, '0');
    const day = fecha.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private cargarSaldoUsuario(): void {
    this.saldoService.obtenerSaldo(this.currentUser.id_usuario || this.currentUser.idUsuario).subscribe({
      next: (response) => {
        this.saldoUsuario = response.saldo_cartera || 0;
      },
      error: (error) => {
        console.error('Error al cargar saldo:', error);
        this.saldoUsuario = 0;
      }
    });
  }

  private cargarVideojuego(): void {
    this.route.params.subscribe(params => {
      this.videojuegoId = +params['id'];
      if (this.videojuegoId) {
        this.obtenerVideojuego(this.videojuegoId);
      }
    });
  }

  private obtenerVideojuego(id: number): void {
    this.isLoading = true;
    
    this.videojuegoService.getVideojuegoById(id).subscribe({
      next: (videojuego) => {
        this.videojuego = videojuego;
        this.videojuegoId = videojuego.id_videojuego;
        console.log('Videojuego cargado:', videojuego);
        
        // Cargar información de la empresa
        if (videojuego.id_empresa) {
          this.obtenerEmpresa(videojuego.id_empresa);
        }
        
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar videojuego:', error);
        this.errorMessage = 'Error al cargar el videojuego.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private obtenerEmpresa(idEmpresa: number): void {
    this.isLoadingEmpresa = true;
    
    this.empresaService.getEmpresaById(idEmpresa).subscribe({
      next: (empresa) => {
        this.empresa = empresa;
        console.log('Empresa cargada:', empresa);
        this.isLoadingEmpresa = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar empresa:', error);
        this.isLoadingEmpresa = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Métodos de utilidad
  formatearPrecio(precio: number): string {
    if (!precio) return 'Q0.00';
    return `Q${precio.toFixed(2)}`;
  }

  formatearFecha(fechaString: string): string {
    if (!fechaString) return 'Sin fecha';
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-GT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  tieneSaldoSuficiente(): boolean {
    if (!this.videojuego) return false;
    return this.saldoUsuario >= this.videojuego.precio;
  }

  get saldoFaltante(): number {
    if (!this.videojuego) return 0;
    return Math.max(0, this.videojuego.precio - this.saldoUsuario);
  }

  // Método para realizar la compra
  realizarCompra(): void {
    if (!this.videojuego || !this.currentUser) return;
    
    if (this.compraForm.invalid) {
      this.marcarCamposComoSucios();
      return;
    }
    
    if (!this.tieneSaldoSuficiente()) {
      this.errorMessage = `Saldo insuficiente. Te faltan ${this.formatearPrecio(this.saldoFaltante)} para completar la compra.`;
      return;
    }
    
    if (this.videojuego.comentarios_bloqueados) {
      this.errorMessage = 'Este videojuego no está disponible para compra en este momento.';
      return;
    }
    
    this.isComprando = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const usuarioId = this.currentUser.id_usuario || this.currentUser.idUsuario;
    const fechaSeleccionada = this.compraForm.value.fecha_compra;
    
    const compraRequest: NewCompraRequest = {
      id_usuario: usuarioId,
      id_videojuego: this.videojuegoId ? this.videojuegoId : 0,
      fecha_compra: fechaSeleccionada
    };
    
    this.compraService.registrarCompra(compraRequest).subscribe({
      next: (compraResponse) => {
        console.log('Compra registrada exitosamente:', compraResponse);
        
        // Actualizar saldo después de la compra exitosa
        this.cargarSaldoUsuario();
        
        this.successMessage = `¡Compra exitosa! Has adquirido "${this.videojuego?.titulo}" por ${this.formatearPrecio(this.videojuego?.precio || 0)}.`;
        
        this.isComprando = false;
        this.compraForm.reset();
        this.cdr.detectChanges();
      },
      error: (error: Error) => {
        console.error('Error al registrar compra:', error);
        
        // Usar directamente el mensaje de error del servicio
        this.errorMessage = error.message;
        
        this.isComprando = false;
        this.cdr.detectChanges();
      }
    });
  }

  private marcarCamposComoSucios(): void {
    Object.keys(this.compraForm.controls).forEach(key => {
      const control = this.compraForm.get(key);
      control?.markAsTouched();
    });
  }

  // Seleccionar fecha por defecto (hoy)
  seleccionarFechaHoy(): void {
    const hoy = new Date();
    const fechaHoy = this.formatearFechaParaInput(hoy);
    this.compraForm.patchValue({ fecha_compra: fechaHoy });
  }

  // Seleccionar fecha de ayer
  seleccionarFechaAyer(): void {
    const ayer = new Date();
    ayer.setDate(ayer.getDate() - 1);
    const fechaAyer = this.formatearFechaParaInput(ayer);
    this.compraForm.patchValue({ fecha_compra: fechaAyer });
  }

  recargarSaldo(): void {
    this.router.navigate(['/usuario-cartera']);
  }

  volverATienda(): void {
    this.router.navigate(['/tienda']);
  }

  // Getters para el formulario
  get f() {
    return this.compraForm.controls;
  }

  // Para mostrar mensajes de error específicos
  get fechaError(): string {
    const errors = this.compraForm.get('fecha_compra')?.errors;
    
    if (!errors) return '';
    
    if (errors['required']) {
      return 'La fecha de compra es requerida';
    } else if (errors['fechaFutura']) {
      return 'No puedes seleccionar una fecha futura';
    } else if (errors['fechaMuyAntigua']) {
      return 'La fecha debe ser posterior al año 2000';
    }
    
    return 'Fecha inválida';
  }
}