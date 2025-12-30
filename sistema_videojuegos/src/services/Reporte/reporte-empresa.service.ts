import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface ReporteEmpresaParametros {
  fechaInicio?: string; // Formato: YYYY-MM-DD
  fechaFin?: string;    // Formato: YYYY-MM-DD
  idEmpresa?: number;
  limite?: number;
}

export interface ErrorResponse {
  error: string;
  timestamp?: string;
  path?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteEmpresaService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  // ==================== REPORTE 1: TOP 5 JUEGOS MÁS VENDIDOS ====================
  
  /**
   * Generar reporte PDF de Top 5 Juegos Más Vendidos
   * Endpoint: GET /reports/top5-juegos/pdf
   */
  generarReporteTop5JuegosPDF(
    idEmpresa: number,
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/top5-juegos/pdf`;
    let params = new HttpParams();
    
    // Parámetro obligatorio
    params = params.set('idEmpresa', idEmpresa.toString());
    
    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }
    
    console.log('Generando Top 5 Juegos con parámetros:', { idEmpresa, fechaInicio, fechaFin });
    
    return this.http.get(url, {
      params: params,
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Top 5 Juegos Más Vendidos'))
    );
  }

  /**
   * Obtener datos en JSON de Top 5 Juegos
   * Endpoint: GET /reports/top5-juegos/datos
   */
  obtenerDatosTop5Juegos(
    idEmpresa: number,
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<any> {
    const url = `${this.restConstants.getApiURL()}reports/top5-juegos/datos`;
    let params = new HttpParams();
    
    params = params.set('idEmpresa', idEmpresa.toString());
    
    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }
    
    return this.http.get(url, { params: params }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Datos Top 5 Juegos'))
    );
  }

  // ==================== REPORTE 2: VENTAS PROPIAS ====================
  
  /**
   * Generar reporte PDF de Ventas Propias
   * Endpoint: GET /reports/ventas-propias/pdf
   */
  generarReporteVentasPropiasPDF(
    idEmpresa: number,
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/ventas-propias/pdf`;
    let params = new HttpParams();
    
    params = params.set('idEmpresa', idEmpresa.toString());
    
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
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Ventas Propias'))
    );
  }

  generarReporteFeedbackPDF(
    idEmpresa: number,
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/feedback/pdf`;
    let params = new HttpParams();
    
    params = params.set('idEmpresa', idEmpresa.toString());
    
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
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Feedback'))
    );
  }

  /**
   * Obtener datos en JSON de Ventas Propias
   * Endpoint: GET /reports/ventas-propias/datos
   */
  obtenerDatosVentasPropias(
    idEmpresa: number,
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<any> {
    const url = `${this.restConstants.getApiURL()}reports/ventas-propias/datos`;
    let params = new HttpParams();
    
    params = params.set('idEmpresa', idEmpresa.toString());
    
    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }
    
    return this.http.get(url, { params: params }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Datos Ventas Propias'))
    );
  }

  // ==================== REPORTE 3: (AGREGA AQUÍ EL TERCER REPORTE) ====================
  
  /**
   * Ejemplo para un tercer reporte
   * Endpoint: GET /reports/tercer-reporte/pdf
   */
  generarTercerReportePDF(
    idEmpresa: number,
    fechaInicio?: string, 
    fechaFin?: string
  ): Observable<Blob> {
    // Implementa según tu tercer reporte
    const url = `${this.restConstants.getApiURL()}reports/tercer-reporte/pdf`;
    let params = new HttpParams();
    
    params = params.set('idEmpresa', idEmpresa.toString());
    
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
      catchError((error: HttpErrorResponse) => this.handleError(error, 'Tercer Reporte'))
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
  generarNombreArchivo(tipoReporte: string, idEmpresa: number, fechaInicio?: string, fechaFin?: string): string {
    const fecha = new Date();
    const fechaStr = fecha.toISOString().split('T')[0];
    
    let nombre = `reporte_${tipoReporte.toLowerCase().replace(/ /g, '_')}_empresa_${idEmpresa}_${fechaStr}`;
    
    if (fechaInicio && fechaFin) {
      nombre += `_${fechaInicio}_a_${fechaFin}`;
    } else {
      nombre += '_historico';
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

  /**
   * Validar empresa
   */
  validarEmpresa(idEmpresa?: number): { valido: boolean, mensaje?: string } {
    if (!idEmpresa || idEmpresa <= 0) {
      return { 
        valido: false, 
        mensaje: 'Debe seleccionar una empresa válida' 
      };
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