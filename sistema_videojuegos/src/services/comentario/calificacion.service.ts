import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { DateUtilsService } from './date-utils.service';

export interface CalificacionRequest {
  id_usuario: number;
  id_biblioteca: number;
  calificacion: number;
}

export interface CalificacionResponse {
  id_calificacion: number;
  id_usuario: number;
  id_biblioteca: number;
  calificacion: number;
  fecha_hora: string;
}

export interface VerificarCalificacionResponse {
  yaCalifico: boolean;
  idUsuario: number;
  idBiblioteca: number;
}

export interface CalificacionesVideojuegoResponse {
  id_calificacion: number;
  id_usuario: number;
  id_biblioteca: number;
  calificacion: number;
  fecha_hora: string;
  nombre_usuario?: string;
  comentario_asociado?: string;
}

export interface EstadisticasCalificacion {
  promedio: number;
  total: number;
  distribucion: {
    1: number;
    2: number;
    3: number;
    4: number;
    5: number;
  };
}

export interface NotaFinalResponse {
  idVideojuego: number;
  notaFinal: number;
  totalCalificaciones: number;
}

@Injectable({
  providedIn: 'root'
})
export class CalificacionService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Crear una nueva calificación
   */
  crearCalificacion(calificacionData: CalificacionRequest): Observable<CalificacionResponse> {
    const url = `${this.restConstants.getApiURL()}calificaciones`;
    
    console.log('Crear calificación - URL:', url);
    console.log('Datos enviados:', calificacionData);
    
    return this.http.post<CalificacionResponse>(url, calificacionData)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Verificar si usuario ya calificó una biblioteca
   */
  verificarCalificacion(idUsuario: number, idBiblioteca: number): Observable<VerificarCalificacionResponse> {
    const url = `${this.restConstants.getApiURL()}calificaciones/verificar`;
    
    const params = new URLSearchParams({
      idUsuario: idUsuario.toString(),
      idBiblioteca: idBiblioteca.toString()
    });
    
    const urlConParams = `${url}?${params.toString()}`;
    
    console.log('Verificar calificación - URL:', urlConParams);
    
    return this.http.get<VerificarCalificacionResponse>(urlConParams)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener calificación específica por usuario y biblioteca
   */
  obtenerCalificacionUsuario(idUsuario: number, idBiblioteca: number): Observable<CalificacionResponse> {
    const url = `${this.restConstants.getApiURL()}calificaciones/usuario/${idUsuario}/biblioteca/${idBiblioteca}`;
    
    console.log('Obtener calificación usuario - URL:', url);
    
    return this.http.get<CalificacionResponse>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener todas las calificaciones de un videojuego
   */
  obtenerCalificacionesVideojuego(idVideojuego: number): Observable<any[]> {
    const url = `${this.restConstants.getApiURL()}calificaciones/videojuego/${idVideojuego}`;
    
    console.log('Obtener calificaciones videojuego - URL:', url);
    
    return this.http.get<any[]>(url)
      .pipe(
        map(response => this.normalizarFecha(response)), // Normaliza fechas
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener calificaciones por biblioteca
   */
  obtenerCalificacionesBiblioteca(idBiblioteca: number): Observable<CalificacionResponse[]> {
    const url = `${this.restConstants.getApiURL()}calificaciones/biblioteca/${idBiblioteca}`;
    
    console.log('Obtener calificaciones biblioteca - URL:', url);
    
    return this.http.get<CalificacionResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener estadísticas de calificaciones de un videojuego
   */
  obtenerEstadisticasVideojuego(idVideojuego: number): Observable<EstadisticasCalificacion> {
    // Si tu backend tiene un endpoint específico para estadísticas
    const url = `${this.restConstants.getApiURL()}calificaciones/estadisticas/videojuego/${idVideojuego}`;
    
    console.log('Obtener estadísticas videojuego - URL:', url);
    
    return this.http.get<EstadisticasCalificacion>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          // Si no existe el endpoint, calcular las estadísticas localmente
          return this.calcularEstadisticasLocalmente(idVideojuego);
        })
      );
  }

  /**
   * Calcular estadísticas localmente si no hay endpoint
   */
  private calcularEstadisticasLocalmente(idVideojuego: number): Observable<EstadisticasCalificacion> {
    return new Observable(observer => {
      this.obtenerCalificacionesVideojuego(idVideojuego)
        .subscribe({
          next: (calificaciones) => {
            if (calificaciones.length === 0) {
              observer.next({
                promedio: 0,
                total: 0,
                distribucion: { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }
              });
              observer.complete();
              return;
            }
            
            const suma = calificaciones.reduce((total, cal) => total + cal.calificacion, 0);
            const promedio = suma / calificaciones.length;
            const distribucion = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
            
            calificaciones.forEach(cal => {
              if (cal.calificacion >= 1 && cal.calificacion <= 5) {
                distribucion[cal.calificacion as keyof typeof distribucion]++;
              }
            });
            
            observer.next({
              promedio,
              total: calificaciones.length,
              distribucion
            });
            observer.complete();
          },
          error: (error) => observer.error(error)
        });
    });
  }

  /**
   * Actualizar una calificación existente
   * (Si tu backend lo permite)
   */
  actualizarCalificacion(idCalificacion: number, nuevaCalificacion: number): Observable<CalificacionResponse> {
    const url = `${this.restConstants.getApiURL()}calificaciones/${idCalificacion}`;
    
    console.log('Actualizar calificación - URL:', url);
    console.log('Nueva calificación:', nuevaCalificacion);
    
    const updateData = { calificacion: nuevaCalificacion };
    
    return this.http.put<CalificacionResponse>(url, updateData)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Eliminar una calificación
   * (Si tu backend lo permite)
   */
  eliminarCalificacion(idCalificacion: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}calificaciones/${idCalificacion}`;
    
    console.log('Eliminar calificación - URL:', url);
    
    return this.http.delete<any>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener nota final de un videojuego
   */
  obtenerNotaFinalVideojuego(idVideojuego: number): Observable<NotaFinalResponse> {
    const url = `${this.restConstants.getApiURL()}calificaciones/videojuego/${idVideojuego}/nota-final`;
    
    console.log('Obtener nota final videojuego - URL:', url);
    
    return this.http.get<NotaFinalResponse>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error obteniendo nota final:', error);
          // Si hay error, devolver datos por defecto
          return throwError(() => new Error(`Error obteniendo nota final: ${error.message}`));
        })
      );
  }


  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log('Error completo recibido en CalificacionService:', error);
    console.log('Error status:', error.status);
    console.log('Error URL:', error.url);
    
    let errorMessage = 'Error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      const serverError = this.extractErrorMessage(error);
      
      if (error.status === 0) {
        errorMessage = 'Error de conexión. Verifica que el servidor esté corriendo.';
      } else if (error.status === 400) {
        errorMessage = serverError || 'Datos inválidos para la calificación';
      } else if (error.status === 404) {
        errorMessage = serverError || 'Recurso no encontrado';
      } else if (error.status === 409) {
        errorMessage = serverError || 'El usuario ya calificó este juego';
      } else if (error.status === 500) {
        errorMessage = serverError || 'Error interno del servidor';
      } else {
        errorMessage = serverError || `Error ${error.status}: ${error.message}`;
      }
    }
    
    console.log('Mensaje de error procesado en CalificacionService:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Extraer mensaje de error del cuerpo de la respuesta
   */
  private extractErrorMessage(error: HttpErrorResponse): string | null {
    try {
      // Caso 1: El error ya es una cadena que contiene JSON
      if (typeof error.error === 'string') {
        try {
          const parsedError = JSON.parse(error.error);
          if (parsedError && parsedError.error) {
            return parsedError.error;
          }
        } catch (e) {
          // Si no es JSON válido, devolver la cadena original
          return error.error;
        }
      }
      
      // Caso 2: El error es un objeto
      if (error.error && typeof error.error === 'object') {
        if (error.error.error) {
          return error.error.error;
        } else if (error.error.message) {
          return error.error.message;
        } else if (error.error.title) {
          return error.error.title;
        }
      }
      
      // Caso 3: Usar el mensaje de error estándar
      if (error.message) {
        return error.message;
      }
      
    } catch (e) {
      console.error('Error al extraer mensaje de error en CalificacionService:', e);
    }
    
    return null;
  }

  private normalizarFecha(datos: any): any {
    if (Array.isArray(datos)) {
      return datos.map(item => this.normalizarFecha(item));
    }
    
    if (datos && typeof datos === 'object') {
      const normalizado = { ...datos };
      
      // Convertir fecha_hora de array a string ISO
      if (normalizado.fecha_hora && Array.isArray(normalizado.fecha_hora)) {
        const dateUtils = new DateUtilsService(); // O inyectar si prefieres
        normalizado.fecha_hora_iso = dateUtils.fechaToISO(normalizado.fecha_hora);
        normalizado.fecha_hora_formateada = dateUtils.formatearFecha(normalizado.fecha_hora);
      }
      
      return normalizado;
    }
    
    return datos;
  }
}