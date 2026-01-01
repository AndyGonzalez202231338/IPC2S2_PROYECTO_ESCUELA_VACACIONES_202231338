import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReporteUsuarioService } from '../../../services/Reporte/reporte-usuario.service';
import { Footer } from '../../footer/footer';
import { Header } from '../../Header/header/header';
import { LoginService } from '../../../services/Login/login.services';

@Component({
  selector: 'app-reportes-usuario-component',
  imports: [NgIf, NgFor, FormsModule, ReactiveFormsModule, Header, Footer, DatePipe],
  templateUrl: './reportes-usuario-component.html',
  styleUrl: './reportes-usuario-component.css',
})
export class ReportesUsuarioComponent {
  formularioReporte: FormGroup;
  cargando = false;
  cargandoCategorias = false;
  reporteSeleccionado = 'historial-compras';  
  fechaActual = new Date();

  usuarioActual: any = null;
  idUsuario: number | null = null;

  tiposReporte = [
    { id: 'historial-compras', nombre: 'Historial de Compras', descripcion: 'Reporte de compras realizadas por el usuario', icono: 'fa-chart-line' },
    { id: 'biblioteca', nombre: 'Biblioteca Personal', descripcion: 'Valoraciones del usuario vs valoraciones promedio', icono: 'fa-building' },
    { id: 'biblioteca-familiar', nombre: 'Bibloteca Familiar', descripcion: 'Juegos prestados e instalados', icono: 'fa-users' }
  ];

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteUsuarioService,
    private loginService: LoginService
  ) {
    this.formularioReporte = this.fb.group({
      usarFechas: [false],
      fechaInicio: [''],
      fechaFin: ['']
    });
  }

  ngOnInit(): void {
    console.log('Componente Reportes inicializado');    
    this.usuarioActual = this.loginService.getCurrentUser();

    //si exsite el usuario actual, obtener su id de usuario
    if (this.usuarioActual) {
      this.idUsuario = this.usuarioActual.idUsuario;
      console.log('ID Usuario actual:', this.idUsuario);
    } else {
      console.warn('No se encontró usuario actual');
    }
    
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

    generarReporte(): void {
    console.log('Generando reporte:', this.reporteSeleccionado);
    
    const valores = this.formularioReporte.value;
    
    // Si no se usan fechas, establecerlas como undefined
    const fechaInicio = valores.usarFechas ? valores.fechaInicio : undefined;
    const fechaFin = valores.usarFechas ? valores.fechaFin : undefined;
    
    console.log('Parámetros:', { fechaInicio, fechaFin, valores });
    
    // Validar fechas solo si se están usando
    if (valores.usarFechas) {
      const validacion = this.reporteService.validarFechas(fechaInicio, fechaFin);
      
      if (!validacion.valido) {
        alert(validacion.mensaje);
        return;
      }
    }

    this.cargando = true;

    // Preparar parámetros para el servicio
    const parametros = {
      fechaInicio: fechaInicio,
      fechaFin: fechaFin,
    };

    console.log('Llamando servicio con:', parametros);

    switch (this.reporteSeleccionado) {
      case 'historial-compras':
        this.generarHitorialCompras(parametros);
        break;
        
      case 'biblioteca':
        this.generarBiblioteca(parametros);
        break;
        
      case 'biblioteca-familiar':
        this.generarBibliotecaFamiliar(parametros);
        break;
    }
  }

    private generarHitorialCompras(parametros: any): void {
    this.reporteService.generarReporteHistorialComprasPDF(
      this.idUsuario!,
      parametros.fechaInicio,
      parametros.fechaFin
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.generarNombreArchivo(
          'historial_compras',
          parametros.fechaInicio,
          parametros.fechaFin
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

  private generarBiblioteca(parametros: any): void {
    this.reporteService.generarReporteAnalisisBiblioteca(this.idUsuario!, parametros.fechaInicio, parametros.fechaFin)
      .subscribe({
        next: (blob) => {
          const nombreArchivo = this.generarNombreArchivo('analisis_biblioteca', parametros.fechaInicio, parametros.fechaFin);
          this.reporteService.descargarArchivo(blob, nombreArchivo);
          this.cargando = false;
        },
        error: (error) => {
          alert('Error al generar el reporte: ' + error.message);
          this.cargando = false;
        }
      });
  }

  private generarBibliotecaFamiliar(parametros: any): void {
    null; // Implementar lógica similar para biblioteca familiar
  }

  limpiarFiltros(): void {
    console.log('Limpiando filtros');
    this.formularioReporte.patchValue({
      usarFechas: false,
      fechaInicio: '',
      fechaFin: ''
    });
  }

  private generarNombreArchivo(tipoReporte: string, fechaInicio?: string, fechaFin?: string, extras?: string): string {
    const fecha = new Date();
    const fechaStr = fecha.toISOString().split('T')[0];
    
    let nombre = `reporte_${tipoReporte.toLowerCase().replace(/ /g, '_')}_${fechaStr}`;
    
    if (fechaInicio && fechaFin) {
      nombre += `_${fechaInicio}_a_${fechaFin}`;
    } else {
      nombre += '_historico';
    }
    
    if (extras) {
      nombre += `_${extras}`;
    }
    
    nombre += '.pdf';
    
    console.log('Nombre archivo generado:', nombre);
    return nombre;
  }

}
