import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, map, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';


@Injectable({
  providedIn: 'root'
})
export class ReporteUsuarioService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  // ==================== REPORTE DE GASTOS ====================
  
  /**
   * Genera reporte PDF de gastos del usuario
   */
  generarReporteHistorialComprasPDF(
    idUsuario: number,
    fechaInicio?: string, 
    fechaFin?: string,
  ): Observable<Blob> {
    const url = `${this.restConstants.getApiURL()}reports/gastos-usuario/pdf`;
    let params = new HttpParams();
    
    params = params.set('idUsuario', idUsuario.toString());
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
      catchError(this.handleError)
    );
  }

  

  // ==================== REPORTE DE ANÁLISIS DE BIBLIOTECA ====================

  /**
   * Genera reporte PDF de análisis de biblioteca
   */
  generarReporteAnalisisBiblioteca(
    idUsuario: number,
    fechaInicio?: string,
    fechaFin?: string
  ): Observable<Blob> {
    let params = new HttpParams()
      .set('idUsuario', idUsuario.toString());

    params = params.set('idUsuario', idUsuario.toString());

    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }
    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }

    return this.http.get(
      `${this.restConstants.getApiURL()}reports/analisis-biblioteca/pdf`,
      {
        params: params,
        responseType: 'blob',
        headers: new HttpHeaders().set('Accept', 'application/pdf')
      }
    ).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Previsualiza el reporte en una nueva pestaña
   */
  previsualizarReporteAnalisisBiblioteca(
    idUsuario: number,
    fechaInicio?: string,
    fechaFin?: string
  ): Observable<void> {
    return this.generarReporteAnalisisBiblioteca(idUsuario, fechaInicio, fechaFin)
      .pipe(
        map(pdfBlob => {
          this.previsualizarArchivo(pdfBlob);
        }),
        catchError(error => {
          console.error('Error al previsualizar reporte:', error);
          return throwError(() => error);
        })
      );
  }

  // ==================== MÉTODOS AUXILIARES ====================

  /**
   * Genera nombre de archivo para el reporte
   */
  generarNombreArchivo(
    tipoReporte: string, 
    fechaInicio?: string, 
    fechaFin?: string, 
    extras?: string
  ): string {
    const fecha = new Date();
    const fechaStr = fecha.toISOString().split('T')[0];
    
    let nombre = `reporte_${tipoReporte.toLowerCase().replace(/ /g, '_')}_${fechaStr}`;
    
    if (fechaInicio && fechaFin) {
      // Formatear fechas para nombre de archivo
      const fechaInicioFormateada = fechaInicio.replace(/-/g, '');
      const fechaFinFormateada = fechaFin.replace(/-/g, '');
      nombre += `_${fechaInicioFormateada}_a_${fechaFinFormateada}`;
    } else if (fechaInicio) {
      const fechaInicioFormateada = fechaInicio.replace(/-/g, '');
      nombre += `_desde_${fechaInicioFormateada}`;
    } else if (fechaFin) {
      const fechaFinFormateada = fechaFin.replace(/-/g, '');
      nombre += `_hasta_${fechaFinFormateada}`;
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

  /**
   * Descarga un archivo Blob
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
   * Previsualiza un archivo PDF en nueva pestaña
   */
  private previsualizarArchivo(blob: Blob): void {
    const url = window.URL.createObjectURL(blob);
    const nuevaVentana = window.open(url, '_blank');
    
    if (!nuevaVentana) {
      console.warn('El navegador bloqueó la ventana emergente. Por favor, habilite popups.');
      // Si el popup está bloqueado, descargar el archivo
      const fecha = new Date();
      const nombre = `previsualizacion_${fecha.getTime()}.pdf`;
      this.descargarArchivo(blob, nombre);
      return;
    }
    
    // Liberar memoria después de un tiempo
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
    }, 60000); // 60 segundos
  }

  /**
   * Maneja errores HTTP
   */
  private handleError(error: any): Observable<never> {
    let errorMessage = 'Error desconocido al procesar el reporte';
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error de conexión: ${error.error.message}`;
    } else if (error.status === 0) {
      errorMessage = 'No se pudo conectar con el servidor. Verifique su conexión.';
    } else if (error.status === 400) {
      errorMessage = 'Parámetros inválidos. Verifique los datos ingresados.';
    } else if (error.status === 404) {
      errorMessage = 'El servicio de reportes no está disponible.';
    } else if (error.status === 500) {
      errorMessage = 'Error interno del servidor al generar el reporte.';
    } else if (error.error && typeof error.error === 'string') {
      try {
        // Intentar extraer mensaje del error
        errorMessage = error.error;
      } catch {
        errorMessage = `Error ${error.status}: ${error.statusText}`;
      }
    }
    
    console.error('Error en ReporteUsuarioService:', {
      message: errorMessage,
      error: error
    });
    
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Valida parámetros de fecha
   */
  validarFechas(fechaInicio?: string, fechaFin?: string): { valido: boolean, mensaje?: string } {
    if (fechaInicio && fechaFin) {
      const inicio = new Date(fechaInicio);
      const fin = new Date(fechaFin);
      
      if (isNaN(inicio.getTime()) || isNaN(fin.getTime())) {
        return { 
          valido: false, 
          mensaje: 'Formato de fecha inválido. Use formato YYYY-MM-DD.' 
        };
      }
      
      if (fin < inicio) {
        return { 
          valido: false, 
          mensaje: 'La fecha fin no puede ser anterior a la fecha inicio.' 
        };
      }
    } else if (fechaInicio) {
      const inicio = new Date(fechaInicio);
      if (isNaN(inicio.getTime())) {
        return { 
          valido: false, 
          mensaje: 'Formato de fecha inicio inválido.' 
        };
      }
    } else if (fechaFin) {
      const fin = new Date(fechaFin);
      if (isNaN(fin.getTime())) {
        return { 
          valido: false, 
          mensaje: 'Formato de fecha fin inválido.' 
        };
      }
    }
    
    return { valido: true };
  }

  /**
   * Valida ID de usuario
   */
  validarIdUsuario(idUsuario: number): { valido: boolean, mensaje?: string } {
    if (!idUsuario || idUsuario <= 0) {
      return { 
        valido: false, 
        mensaje: 'El ID de usuario debe ser un número positivo.' 
      };
    }
    
    return { valido: true };
  }
}