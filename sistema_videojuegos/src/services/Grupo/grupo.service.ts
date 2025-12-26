import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface NewGrupoRequest {
  id_creador: number;
  nombre: string;
}

export interface GrupoResponse {
  id_grupo: number;
  id_creador: number;
  nombre: string;
  cantidad_participantes: number;
}

export interface IntegranteResponse {
  id_usuario: number;
  nombre: string;
  correo: string;
  pais?: string;
  telefono?: string;
  es_creador: boolean;
}

export interface EspaciosDisponiblesResponse {
  espacios_disponibles: number;
}

export interface ErrorResponse {
  error: string;
}

@Injectable({
  providedIn: 'root'
})
export class GrupoService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Crear un nuevo grupo
   */
  crearGrupo(grupoRequest: NewGrupoRequest): Observable<GrupoResponse> {
    const url = `${this.restConstants.getApiURL()}grupos`;
    return this.http.post<GrupoResponse>(url, grupoRequest)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Agregar participante a un grupo
   */
  agregarParticipante(idGrupo: number, idUsuario: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}grupos/${idGrupo}/participantes/${idUsuario}`;
    return this.http.post<any>(url, {})
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Eliminar participante de un grupo
   */
  eliminarParticipante(idGrupo: number, idUsuario: number, idUsuarioSolicitante: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}grupos/${idGrupo}/participantes/${idUsuario}`;
    
    console.log('Eliminar participante - URL:', url);
    console.log('ID Usuario Solicitante:', idUsuarioSolicitante);
    
    // Opción 1: Usar headers personalizados
    const headers = new HttpHeaders({
      'X-User-ID': idUsuarioSolicitante.toString(),
      'Content-Type': 'application/json'
    });
    
    // Opción 2: Usar parámetros de consulta (si el backend lo permite)
    // const params = new HttpParams().set('idUsuarioSolicitante', idUsuarioSolicitante.toString());
    
    return this.http.delete<any>(url, { 
      headers: headers
      // params: params // Descomentar si usas parámetros de consulta
    }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error en DELETE participante:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error name:', error.name);
        console.error('Error URL:', error.url);
        
        return this.handleError(error);
      })
    );
  }

  /**
   * Eliminar un grupo
   */
  eliminarGrupo(idGrupo: number, idUsuarioSolicitante: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}grupos/${idGrupo}`;
    
    const headers = new HttpHeaders({
      'X-User-ID': idUsuarioSolicitante.toString()
    });
    
    return this.http.delete<any>(url, { headers })
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener grupos de un usuario
   */
  obtenerGruposDeUsuario(idUsuario: number): Observable<GrupoResponse[]> {
    const url = `${this.restConstants.getApiURL()}grupos/usuario/${idUsuario}`;
    return this.http.get<GrupoResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener espacios disponibles en un grupo
   */
  obtenerEspaciosDisponibles(idGrupo: number): Observable<EspaciosDisponiblesResponse> {
    const url = `${this.restConstants.getApiURL()}grupos/${idGrupo}/espacios-disponibles`;
    return this.http.get<EspaciosDisponiblesResponse>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener integrantes de un grupo
   */
  obtenerIntegrantes(idGrupo: number): Observable<IntegranteResponse[]> {
    const url = `${this.restConstants.getApiURL()}grupos/${idGrupo}/integrantes`;
    return this.http.get<IntegranteResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log('Error completo recibido en GrupoService:', error);
    console.log('Error body:', error.error);
    console.log('Error status:', error.status);
    
    let errorMessage = 'Error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      const serverError = this.extractErrorMessage(error);
      
      if (error.status === 400) {
        errorMessage = serverError || 'Datos inválidos';
      } else if (error.status === 403) {
        errorMessage = serverError || 'No tienes permiso para realizar esta acción';
      } else if (error.status === 404) {
        errorMessage = serverError || 'Recurso no encontrado';
      } else if (error.status === 409) {
        errorMessage = serverError || 'Conflicto al procesar la solicitud';
      } else if (error.status === 500) {
        errorMessage = serverError || 'Error interno del servidor';
      } else {
        errorMessage = serverError || `Error ${error.status}: ${error.message}`;
      }
    }
    
    console.log('Mensaje de error procesado en GrupoService:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Extraer mensaje de error del cuerpo de la respuesta
   */
  private extractErrorMessage(error: HttpErrorResponse): string | null {
    try {
      console.log('Extrayendo mensaje de error en GrupoService:', error);
      
      // Caso 1: El error ya es una cadena que contiene JSON
      if (typeof error.error === 'string') {
        console.log('Error es string:', error.error);
        try {
          const parsedError = JSON.parse(error.error);
          console.log('Error parseado:', parsedError);
          if (parsedError && parsedError.error) {
            return parsedError.error;
          }
        } catch (e) {
          // Si no es JSON válido, devolver la cadena original
          console.log('No es JSON válido, usando string original');
          return error.error;
        }
      }
      
      // Caso 2: El error es un objeto
      if (error.error && typeof error.error === 'object') {
        console.log('Error es objeto:', error.error);
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
        console.log('Usando error.message:', error.message);
        return error.message;
      }
      
    } catch (e) {
      console.error('Error al extraer mensaje de error en GrupoService:', e);
    }
    
    return null;
  }
}