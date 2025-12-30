import { DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReporteService } from '../../../services/Reporte/reporte-sistem.service';

import { Categoria } from '../../../models/videojuego/categoria'; // Importa la interfaz
import { Footer } from '../../footer/footer';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { CategoriaService } from '../../../services/Videojuego/categoria.service';

@Component({
  selector: 'app-reportes-sistema-component',
  imports: [NgIf, NgFor, FormsModule, ReactiveFormsModule, HeaderAdminSistema, Footer, DatePipe],
  templateUrl: './reportes-sistema-component.html',
  styleUrl: './reportes-sistema-component.css',
})
export class ReportesSistemaComponent implements OnInit {
  formularioReporte: FormGroup;
  cargando = false;
  cargandoCategorias = false;
  reporteSeleccionado = 'ganancias-globales';
  fechaActual = new Date();
  
  // Tipos de reportes
  tiposReporte = [
    { id: 'ganancias-globales', nombre: 'Ganancias Globales', descripcion: 'Reporte de ganancias totales del sistema', icono: 'fa-chart-line' },
    { id: 'ingresos-empresas', nombre: 'Ingresos por Empresa', descripcion: 'Distribución de ingresos por empresa', icono: 'fa-building' },
    { id: 'ranking-usuarios', nombre: 'Ranking de Usuarios', descripcion: 'Top usuarios por compras y calificaciones', icono: 'fa-users' },
    { id: 'top-ventas-calidad', nombre: 'Top Ventas por Calidad', descripcion: 'Videojuegos más vendidos por calidad', icono: 'fa-trophy' }
  ];

  // Opciones para filtros
  clasificaciones = [
    { valor: '', etiqueta: 'Todas las clasificaciones' },
    { valor: 'E', etiqueta: 'Everyone (E)' },
    { valor: 'T', etiqueta: 'Teen (T)' },
    { valor: 'M', etiqueta: 'Mature (M)' },
  ];

  // Categorías dinámicas desde el servicio
  categorias: Categoria[] = [];
  opcionesCategorias: { valor: string, etiqueta: string }[] = [];

  limites = [5, 10, 15];

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteService,
    private categoriaService: CategoriaService
  ) {
    this.formularioReporte = this.fb.group({
      usarFechas: [false],
      fechaInicio: [''],
      fechaFin: [''],
      limite: [10],
      clasificacion: [''],
      categoria: [''],
      incluirCalificaciones: [true]
    });
  }

  ngOnInit(): void {
    console.log('Componente Reportes inicializado');
    
    // Cargar categorías desde el servicio
    this.cargarCategorias();
    
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

  /**
   * Cargar categorías desde el servicio
   */
  private cargarCategorias(): void {
    this.cargandoCategorias = true;
    console.log('Cargando categorías desde el servicio...');
    
    this.categoriaService.getAllCategorias().subscribe({
      next: (categorias: Categoria[]) => {
        console.log('Categorías cargadas:', categorias);
        this.categorias = categorias;
        
        // Transformar las categorías al formato esperado por el select
        this.opcionesCategorias = [
          { valor: '', etiqueta: 'Todas las categorías' },
          ...categorias.map(cat => ({
            valor: cat.nombre, // o cat.id_categoria.toString() si prefieres usar ID
            etiqueta: cat.nombre
          }))
        ];
        
        this.cargandoCategorias = false;
        console.log('Opciones de categorías transformadas:', this.opcionesCategorias);
      },
      error: (error) => {
        console.error('Error al cargar categorías:', error);
        this.cargandoCategorias = false;
        
        // Opción de respaldo en caso de error
        this.opcionesCategorias = [
          { valor: '', etiqueta: 'Todas las categorías' },
          { valor: 'Accion', etiqueta: 'Acción' },
          { valor: 'Aventura', etiqueta: 'Aventura' },
          { valor: 'Estrategia', etiqueta: 'Estrategia' },
          { valor: 'RPG', etiqueta: 'RPG' },
          { valor: 'Deportes', etiqueta: 'Deportes' },
          { valor: 'Simulacion', etiqueta: 'Simulación' }
        ];
        
        alert('No se pudieron cargar las categorías. Se usarán valores por defecto.');
      }
    });
  }

  seleccionarReporte(tipo: string): void {
    console.log('Seleccionando reporte:', tipo);
    this.reporteSeleccionado = tipo;
  }

  get mostrarFiltrosAvanzados(): boolean {
    return this.reporteSeleccionado === 'top-ventas-calidad';
  }

  get mostrarLimite(): boolean {
    return this.reporteSeleccionado === 'ranking-usuarios' || 
           this.reporteSeleccionado === 'top-ventas-calidad';
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
      limite: valores.limite,
      clasificacion: valores.clasificacion,
      categoria: valores.categoria,
      incluirCalificaciones: valores.incluirCalificaciones
    };

    console.log('Llamando servicio con:', parametros);

    switch (this.reporteSeleccionado) {
      case 'ganancias-globales':
        this.generarGananciasGlobales(parametros);
        break;
        
      case 'ingresos-empresas':
        this.generarIngresosEmpresas(parametros);
        break;
        
      case 'ranking-usuarios':
        this.generarRankingUsuarios(parametros);
        break;
        
      case 'top-ventas-calidad':
        this.generarTopVentasCalidad(parametros);
        break;
    }
  }

  private generarGananciasGlobales(parametros: any): void {
    this.reporteService.generarReporteGananciasGlobalesPDF(
      parametros.fechaInicio,
      parametros.fechaFin
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.generarNombreArchivo(
          'ganancias_globales',
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

  private generarIngresosEmpresas(parametros: any): void {
    this.reporteService.generarReporteIngresosEmpresasPDF(
      parametros.fechaInicio,
      parametros.fechaFin
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.generarNombreArchivo(
          'ingresos_empresas',
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

  private generarRankingUsuarios(parametros: any): void {
    this.reporteService.generarReporteRankingUsuariosPDF(
      parametros.fechaInicio,
      parametros.fechaFin,
      parametros.limite
    ).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.generarNombreArchivo(
          'ranking_usuarios',
          parametros.fechaInicio,
          parametros.fechaFin,
          `limite_${parametros.limite}`
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

  private generarTopVentasCalidad(parametros: any): void {
    this.reporteService.generarReporteTopVentasCalidadPDF(parametros).subscribe({
      next: (pdfBlob) => {
        const nombreArchivo = this.generarNombreArchivo(
          'top_ventas_calidad',
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

  limpiarFiltros(): void {
    console.log('Limpiando filtros');
    this.formularioReporte.patchValue({
      usarFechas: false,
      fechaInicio: '',
      fechaFin: '',
      limite: 10,
      clasificacion: '',
      categoria: '',
      incluirCalificaciones: true
    });
  }

  // Método para generar nombre de archivo
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

  // Método para refrescar categorías
  refrescarCategorias(): void {
    console.log('Refrescando categorías...');
    this.cargarCategorias();
  }
}