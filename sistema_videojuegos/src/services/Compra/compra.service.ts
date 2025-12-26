import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface NewCompraRequest {
  id_usuario: number;
  id_videojuego: number;
  fecha_compra: string; // Formato YYYY-MM-DD
}

export interface CompraResponse {
  id_compra: number;
  id_usuario: number;
  id_videojuego: number;
  monto: number;
  fecha_compra: string;
  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class CompraService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Registrar una nueva compra con manejo de errores
   */
  registrarCompra(compra: NewCompraRequest): Observable<CompraResponse> {
    const url = `${this.restConstants.getApiURL()}compras`;
    return this.http.post<CompraResponse>(url, compra)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log('Error completo recibido:', error);
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
        errorMessage = serverError || 'Datos de compra inválidos';
      } else if (error.status === 402 || error.status === 402) {
        errorMessage = serverError || 'Saldo insuficiente';
      } else if (error.status === 403) {
        errorMessage = serverError || 'No tienes permiso para realizar esta acción';
      } else if (error.status === 404) {
        errorMessage = serverError || 'Recurso no encontrado';
      } else if (error.status === 409) {
        errorMessage = serverError || 'Ya posees este videojuego';
      } else if (error.status === 500) {
        // Para errores 500, mostrar el mensaje específico del backend
        errorMessage = serverError || 'Error interno del servidor';
        
        // Analizar el mensaje de error para casos específicos
        if (serverError && serverError.includes('EdadNoValidaException')) {
          errorMessage = 'No cumples con la edad requerida para comprar este videojuego.';
        } else if (serverError && serverError.includes('El usuario tiene')) {
          errorMessage = serverError; // Mostrar el mensaje completo del backend
        }
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
      console.log('Extrayendo mensaje de error:', error);
      
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
      console.error('Error al extraer mensaje de error:', e);
    }
    
    return null;
  }

  /**
   * Obtener compras por usuario
   */
  obtenerComprasPorUsuario(idUsuario: number): Observable<CompraResponse[]> {
    const url = `${this.restConstants.getApiURL()}compras/usuario/${idUsuario}`;
    return this.http.get<CompraResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener compras por videojuego
   */
  obtenerComprasPorVideojuego(idVideojuego: number): Observable<CompraResponse[]> {
    const url = `${this.restConstants.getApiURL()}compras/videojuego/${idVideojuego}`;
    return this.http.get<CompraResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener una compra por ID
   */
  obtenerCompraPorId(idCompra: number): Observable<CompraResponse> {
    const url = `${this.restConstants.getApiURL()}compras/${idCompra}`;
    return this.http.get<CompraResponse>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }
}