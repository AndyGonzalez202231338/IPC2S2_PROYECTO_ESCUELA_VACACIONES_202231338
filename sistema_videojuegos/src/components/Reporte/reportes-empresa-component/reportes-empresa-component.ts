import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReporteEmpresaService } from '../../../services/Reporte/reporte-empresa.service';

import { Footer } from '../../footer/footer';
import { HeaderAdminEmpresa } from '../../Header/header-admin-empresa/header-admin-empresa';
import { LoginService } from '../../../services/Login/login.services';

@Component({
  selector: 'app-reportes-empresa-component',
  imports: [NgIf, NgFor, FormsModule, ReactiveFormsModule, HeaderAdminEmpresa, Footer, DatePipe],
  templateUrl: './reportes-empresa-component.html',
  styleUrl: './reportes-empresa-component.css',
})
export class ReportesEmpresaComponent implements OnInit {
  formularioReporte: FormGroup;
  cargando = false;
  reporteSeleccionado = 'top5-juegos';
  fechaActual = new Date();
  
  // Usuario actual y su empresa
  usuarioActual: any = null;
  idEmpresaUsuario: number | null = null;
  nombreEmpresaUsuario: string = '';
  
  // Tipos de reportes
  tiposReporte = [
    { 
      id: 'top5-juegos', 
      nombre: 'Top 5 Juegos Más Vendidos', 
      descripcion: 'Los 5 juegos más vendidos de tu empresa', 
      icono: 'fa-trophy' 
    },
    { 
      id: 'ventas-propias', 
      nombre: 'Ventas Propias', 
      descripcion: 'Reporte detallado de ventas de tu empresa', 
      icono: 'fa-chart-line' 
    },
    { 
      id: 'feedback', 
      nombre: 'Feedback de Usuarios', 
      descripcion: 'Calificaciones promedio y peores reseñas', 
      icono: 'fa-star' 
    }
  ];

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteEmpresaService,
    private loginService: LoginService
  ) {
    this.formularioReporte = this.fb.group({
      usarFechas: [false],
      fechaInicio: [''],
      fechaFin: ['']
    });
  }

  ngOnInit(): void {
    console.log('Componente Reportes Empresa inicializado');
    
    // Obtener el usuario actual del LoginService
    this.usuarioActual = this.loginService.getCurrentUser();
    
    if (this.usuarioActual && this.usuarioActual.empresa) {
      this.idEmpresaUsuario = this.usuarioActual.empresa.id_empresa;
      this.nombreEmpresaUsuario = this.usuarioActual.empresa.nombre;
      
      console.log('Empresa del usuario:', this.usuarioActual.empresa);
      console.log('ID Empresa:', this.idEmpresaUsuario);
      console.log('Nombre Empresa:', this.nombreEmpresaUsuario);
    } else {
      console.warn('Usuario no tiene empresa asociada o no está autenticado');
      // Aquí podrías redirigir a login o mostrar un mensaje
    }
    
    // Actualizar fecha actual cada minuto
    setInterval(() => {
      this.fechaActual = new Date();
    }, 60000);
    
    // Suscribirse al cambio del checkbox
    this.formularioReporte.get('usarFechas')?.valueChanges.subscribe((usar) => {
      console.log('usarFechas cambió a:', usar);
      
      if (usar) {
        // Establecer fechas por defecto (último mes)
        const fechaFin = new Date();
        const fechaInicio = new Date();
        fechaInicio.setMonth(fechaInicio.getMonth() - 1);
        
        this.formularioReporte.patchValue({
          fechaInicio: fechaInicio.toISOString().split('T')[0],
          fechaFin: fechaFin.toISOString().split('T')[0]
        }, { emitEvent: false });
      } else {
        // Limpiar fechas si se desactiva el checkbox
        this.formularioReporte.patchValue({
          fechaInicio: '',
          fechaFin: ''
        }, { emitEvent: false });
      }
    });
  }

  seleccionarReporte(tipo: string): void {
    console.log('Seleccionando reporte:', tipo);
    this.reporteSeleccionado = tipo;
  }

  get usarFechas(): boolean {
    return this.formularioReporte.get('usarFechas')?.value || false;
  }

  get puedeGenerarReporte(): boolean {
    // Verificar que el usuario tenga una empresa asignada
    return this.idEmpresaUsuario !== null && this.idEmpresaUsuario > 0;
  }

  generarReporte(): void {
    if (!this.puedeGenerarReporte) {
      alert('No tienes una empresa asignada. Contacta al administrador.');
      return;
    }
    
    console.log('Generando reporte:', this.reporteSeleccionado);
    
    const valores = this.formularioReporte.value;
    
    // Si no se usan fechas, establecerlas como undefined
    const fechaInicio = valores.usarFechas ? valores.fechaInicio : undefined;
    const fechaFin = valores.usarFechas ? valores.fechaFin : undefined;
    
    console.log('Parámetros:', { 
      idEmpresa: this.idEmpresaUsuario,
      fechaInicio, 
      fechaFin 
    });
    
    // Validar fechas solo si se están usando
    if (valores.usarFechas) {
      const validacion = this.reporteService.validarFechas(fechaInicio, fechaFin);
      
      if (!validacion.valido) {
        alert(validacion.mensaje);
        return;
      }
    }

    this.cargando = true;

    // IMPORTANTE: Usar this.idEmpresaUsuario que obtenemos del usuario en sesión
    switch (this.reporteSeleccionado) {
      case 'top5-juegos':
        this.generarTop5Juegos(this.idEmpresaUsuario!, fechaInicio, fechaFin);
        break;
        
      case 'ventas-propias':
        this.generarVentasPropias(this.idEmpresaUsuario!, fechaInicio, fechaFin);
        break;
      case 'feedback':
      this.generarFeedback(this.idEmpresaUsuario!, fechaInicio, fechaFin);
      break;  
      
    }
  }

  private generarTop5Juegos(idEmpresa: number, fechaInicio?: string, fechaFin?: string): void {
    this.reporteService.generarReporteTop5JuegosPDF(
      idEmpresa,
      fechaInicio,
      fechaFin
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.reporteService.generarNombreArchivo(
          'top5_juegos',
          idEmpresa,
          fechaInicio,
          fechaFin
        );
        this.reporteService.descargarArchivo(pdfBlob, nombreArchivo);
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error:', error);
        alert(error.message);
        this.cargando = false;
      }
    });
  }

  private generarFeedback(idEmpresa: number, fechaInicio?: string, fechaFin?: string): void {
    this.reporteService.generarReporteFeedbackPDF(
      idEmpresa,
      fechaInicio,
      fechaFin
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.reporteService.generarNombreArchivo(
          'feedback',
          idEmpresa,
          fechaInicio,
          fechaFin
        );
        this.reporteService.descargarArchivo(pdfBlob, nombreArchivo);
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error:', error);
        alert(error.message);
        this.cargando = false;
      }
    });
  }

  private generarVentasPropias(idEmpresa: number, fechaInicio?: string, fechaFin?: string): void {
    this.reporteService.generarReporteVentasPropiasPDF(
      idEmpresa,
      fechaInicio,
      fechaFin
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.reporteService.generarNombreArchivo(
          'ventas_propias',
          idEmpresa,
          fechaInicio,
          fechaFin
        );
        this.reporteService.descargarArchivo(pdfBlob, nombreArchivo);
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error:', error);
        alert(error.message);
        this.cargando = false;
      }
    });
  }

  limpiarFiltros(): void {
    console.log('Limpiando filtros');
    this.formularioReporte.patchValue({
      usarFechas: false,
      fechaInicio: '',
      fechaFin: ''
    });
  }
}