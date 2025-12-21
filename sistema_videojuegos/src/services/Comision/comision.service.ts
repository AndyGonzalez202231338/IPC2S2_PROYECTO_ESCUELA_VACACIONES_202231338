
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { Comision } from '../../models/comision/comision';

export interface NuevaComisionRequest {
  id_empresa: number;
  porcentaje: number;
  fecha_inicio: string;
  fecha_final: string | null;
  tipo_comision: 'global' | 'especifica';
}

@Injectable({
  providedIn: 'root'
})
export class ComisionService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    const restConstants = new RestConstants();
    this.apiUrl = restConstants.getApiURL();
  }

  /**
   * Obtiene todas las comisiones de una empresa
   */
  getComisionesByEmpresa(idEmpresa: number): Observable<Comision[]> {
    const url = `${this.apiUrl}empresas/${idEmpresa}/comisiones`;
    console.log('URL para obtener comisiones:', url);
    
    return this.http.get<Comision[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Crea una nueva comisión
   */
  createComision(comisionData: NuevaComisionRequest): Observable<any> {
    const url = `${this.apiUrl}empresas/${comisionData.id_empresa}/comisiones`;
    console.log('URL para crear comisión:', url);
    console.log('Datos a enviar:', comisionData);
    
    return this.http.post<any>(url, comisionData).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Actualiza una comisión existente
   */
  updateComision(idComision: number, comisionData: Partial<NuevaComisionRequest>): Observable<Comision> {
    const url = `${this.apiUrl}comisiones/${idComision}`;
    return this.http.put<Comision>(url, comisionData).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Elimina una comisión
   */
  deleteComision(idComision: number): Observable<void> {
    const url = `${this.apiUrl}comisiones/${idComision}`;
    return this.http.delete<void>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Obtiene la comisión activa actual de una empresa
   */
  getComisionActiva(idEmpresa: number): Observable<Comision> {
    const url = `${this.apiUrl}empresas/${idEmpresa}/comision-activa`;
    return this.http.get<Comision>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Manejo de errores
   */
  private handleError(error: HttpErrorResponse) {
    console.error('Error en ComisionService:', error);
    
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
        } else if (error.status === 409) {
          errorMessage = 'Conflicto: La comisión ya existe o hay solapamiento de fechas.';
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