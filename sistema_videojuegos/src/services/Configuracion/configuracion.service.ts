
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { Configuracion, UpdateConfiguracionRequest } from '../../models/configuracion/configuracion';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    const restConstants = new RestConstants();
    this.apiUrl = restConstants.getApiURL();
  }

  /**
   * Obtiene todas las configuraciones del sistema
   */
  getAllConfiguraciones(): Observable<Configuracion[]> {
    const url = `${this.apiUrl}sistema/configuraciones`;
    console.log('URL para obtener configuraciones:', url);
    
    return this.http.get<Configuracion[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Actualiza una configuración
   */
  updateConfiguracion(id: number, configData: UpdateConfiguracionRequest): Observable<Configuracion> {
    const url = `${this.apiUrl}sistema/configuraciones/${id}`;
    console.log('URL para actualizar configuración:', url);
    console.log('Datos a enviar:', configData);
    
    return this.http.put<Configuracion>(url, configData).pipe(
      catchError(this.handleError)
    );
  }


  /**
   * Manejo de errores personalizado
   */
  private handleError(error: HttpErrorResponse) {
    console.error('Error en ConfiguracionService:', error);
    
    let errorMessage = 'Ocurrió un error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Manejar string JSON del backend
      if (error.error && typeof error.error === 'string') {
        try {
          const errorObj = JSON.parse(error.error);
          errorMessage = errorObj.error || 'Error del servidor';
        } catch (e) {
          errorMessage = error.error;
        }
      } else if (error.error && error.error.error) {
        errorMessage = error.error.error;
      } else {
        // Mensajes por defecto según status
        if (error.status === 0) {
          errorMessage = 'No se pudo conectar con el servidor.';
        } else if (error.status === 400) {
          errorMessage = 'Datos inválidos enviados al servidor.';
        } else if (error.status === 404) {
          errorMessage = 'Recurso no encontrado.';
        } else if (error.status === 500) {
          errorMessage = 'Error interno del servidor. Por favor intente más tarde.';
        } else {
          errorMessage = `Error ${error.status}: ${error.statusText}`;
        }
      }
    }
    
    console.log('Mensaje de error generado:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}