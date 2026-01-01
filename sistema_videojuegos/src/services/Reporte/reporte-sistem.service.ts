import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface ReporteParametros {
  fechaInicio?: string;
  fechaFin?: string;
  limite?: number;
  clasificacion?: string;
  categoria?: string;
  incluirCalificaciones?: boolean;
}

export interface ErrorResponse {
  error: string;
  timestamp?: string;
  path?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  // ==================== REPORTE 1: GANANCIAS GLOBALES ====================
  
  /**
   * Generar reporte PDF de Ganancias Globales
   * Endpoint: GET /reports/ganancias-globales-pdf
   */
  generarReporteGananciasGlobalesPDF(
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/ganancias-globales-pdf`;
    let params = new HttpParams();
    
    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }
    
    return this.http.get(url, {
      params: params,
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Ganancias Globales'))
    );
  }

  // ==================== REPORTE 2: INGRESOS POR EMPRESA ====================
  
  /**
   * Generar reporte PDF de Ingresos por Empresa
   * Endpoint: GET /reports/ingresos-empresas-pdf
   */
  generarReporteIngresosEmpresasPDF(
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/ingresos-empresas-pdf`;
    let params = new HttpParams();
    
    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }
    
    return this.http.get(url, {
      params: params,
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Ingresos por Empresa'))
    );
  }

  // ==================== REPORTE 3: RANKING DE USUARIOS ====================
  
  /**
   * Generar reporte PDF de Ranking de Usuarios
   * Endpoint: GET /reports/ranking-usuarios-pdf
   */
  generarReporteRankingUsuariosPDF(
    fechaInicio?: string, 
    fechaFin?: string,
    limite: number = 10
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/ranking-usuarios-pdf`;
    let params = new HttpParams();
    
    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }
    
    if (limite && limite > 0) {
      params = params.set('limite', limite.toString());
    }
    
    return this.http.get(url, {
      params: params,
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Ranking de Usuarios'))
    );
  }

  // ==================== REPORTE 4: TOP VENTAS POR CALIDAD ====================
  
  /**
   * Generar reporte PDF de Top Ventas por Calidad
   * Endpoint: GET /reportsventas/top-ventas-calidad-pdf
   */
  generarReporteTopVentasCalidadPDF(
    parametros: ReporteParametros
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reportsventas/top-ventas-calidad-pdf`;
    let params = new HttpParams();
    
    if (parametros.fechaInicio) {
      params = params.set('fechaInicio', parametros.fechaInicio);
    }
    
    if (parametros.fechaFin) {
      params = params.set('fechaFin', parametros.fechaFin);
    }
    
    if (parametros.limite && parametros.limite > 0) {
      params = params.set('limite', parametros.limite.toString());
    }
    
    if (parametros.clasificacion) {
      params = params.set('clasificacion', parametros.clasificacion);
    }
    
    if (parametros.categoria) {
      params = params.set('categoria', parametros.categoria);
    }
    
    if (parametros.incluirCalificaciones !== undefined) {
      params = params.set('incluirCalificaciones', parametros.incluirCalificaciones.toString());
    }
    
    return this.http.get(url, {
      params: params,
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Top Ventas por Calidad'))
    );
  }

  // ==================== MÉTODOS UTILITARIOS ====================

  /**
   * Descargar archivo generado
   */
  descargarArchivo(blob: Blob, nombreArchivo: string): void {
    try {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = nombreArchivo;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error al descargar archivo:', error);
      throw new Error('No se pudo descargar el archivo');
    }
  }

  /**
   * Generar nombre de archivo automático
   */
  generarNombreArchivo(tipoReporte: string, fechaInicio?: string, fechaFin?: string, extras?: string): string {
    const fecha = new Date();
    const fechaStr = fecha.toISOString().split('T')[0];
    
    let nombre = `reporte_${tipoReporte.toLowerCase().replace(/ /g, '_')}_${fechaStr}`;
    
    if (fechaInicio && fechaFin) {
      nombre += `_${fechaInicio}_a_${fechaFin}`;
    } else if (fechaInicio || fechaFin) {
      nombre += `_${fechaInicio || fechaFin}`;
    } else {
      nombre += '_historico';
    }
    
    if (extras) {
      nombre += `_${extras}`;
    }
    
    nombre += '.pdf';
    
    return nombre;
  }

  /**
   * Validar fechas
   */
  validarFechas(fechaInicio?: string, fechaFin?: string): { valido: boolean, mensaje?: string } {
    if ((fechaInicio && !fechaFin) || (!fechaInicio && fechaFin)) {
      return { 
        valido: false, 
        mensaje: 'Debe especificar ambas fechas o ninguna' 
      };
    }
    
    if (fechaInicio && fechaFin) {
      const inicio = new Date(fechaInicio);
      const fin = new Date(fechaFin);
      
      if (inicio > fin) {
        return { 
          valido: false, 
          mensaje: 'Fecha inicio no puede ser después de fecha fin' 
        };
      }
    }
    
    return { valido: true };
  }

  // ==================== MANEJO DE ERRORES ====================

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse, tipoReporte: string = 'Reporte'): Observable<never> {
    console.log(`Error en reporte ${tipoReporte}:`, error);
    console.log('Error status:', error.status);
    
    let errorMessage = `Error desconocido al generar el reporte de ${tipoReporte}`;
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error de conexión: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      const serverError = this.extractErrorMessage(error);
      
      if (error.status === 400) {
        errorMessage = serverError || `Parámetros inválidos para ${tipoReporte}`;
      } else if (error.status === 403) {
        errorMessage = serverError || `No tienes permiso para generar el reporte de ${tipoReporte}`;
      } else if (error.status === 404) {
        errorMessage = serverError || `El endpoint del reporte ${tipoReporte} no fue encontrado`;
      } else if (error.status === 500) {
        errorMessage = serverError || `Error interno del servidor al generar ${tipoReporte}`;
      } else if (error.status === 0) {
        errorMessage = `No se pudo conectar con el servidor para ${tipoReporte}`;
      } else {
        errorMessage = serverError || `Error ${error.status}: ${error.message}`;
      }
    }
    
    console.log('Mensaje de error procesado:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Extraer mensaje de error del cuerpo de la respuesta
   */
  private extractErrorMessage(error: HttpErrorResponse): string | null {
    try {
      // Caso 1: El error es una cadena de texto
      if (typeof error.error === 'string') {
        return error.error;
      }
      
      // Caso 2: El error es un objeto
      if (error.error && typeof error.error === 'object') {
        if (error.error.error) {
          return error.error.error;
        } else if (error.error.message) {
          return error.error.message;
        } else if (error.error.detail) {
          return error.error.detail;
        }
      }
      
      // Caso 3: El error es un Blob (posible PDF con error)
      if (error.error instanceof Blob) {
        // Intentar leer como texto
        const reader = new FileReader();
        let errorText = 'Error al generar documento';
        
        reader.onload = () => {
          try {
            const result = JSON.parse(reader.result as string);
            if (result && result.error) {
              errorText = result.error;
            }
          } catch (e) {
            // No es JSON, usar texto genérico
          }
        };
        
        reader.readAsText(error.error);
        return errorText;
      }
      
      // Caso 4: Usar el mensaje estándar
      if (error.message) {
        return error.message;
      }
      
    } catch (e) {
      console.error('Error al extraer mensaje:', e);
    }
    
    return null;
  }
}