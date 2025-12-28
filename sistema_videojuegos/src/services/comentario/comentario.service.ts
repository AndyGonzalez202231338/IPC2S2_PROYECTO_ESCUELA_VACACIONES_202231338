import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { DateUtilsService } from './date-utils.service';

export interface ComentarioRequest {
  id_usuario: number;
  id_biblioteca: number;
  comentario: string;
}

export interface ComentarioResponse {
  id_comentario: number;
  id_usuario: number;
  id_biblioteca: number;
  comentario: string;
  fecha_hora: string;
}

export interface ComentarioConUsuario extends ComentarioResponse {
  nombre_usuario?: string;
  avatar_usuario?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ComentarioService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Crear un nuevo comentario
   */
  crearComentario(comentarioData: ComentarioRequest): Observable<ComentarioResponse> {
    const url = `${this.restConstants.getApiURL()}comentarios`;
    
    console.log('Crear comentario - URL:', url);
    console.log('Datos enviados:', comentarioData);
    
    return this.http.post<ComentarioResponse>(url, comentarioData)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener comentarios por videojuego
   */
  obtenerComentariosVideojuego(idVideojuego: number): Observable<any[]> {
    const url = `${this.restConstants.getApiURL()}comentarios/videojuego/${idVideojuego}`;
    
    console.log('Obtener comentarios videojuego - URL:', url);
    
    return this.http.get<any[]>(url)
      .pipe(
        map(response => this.normalizarFecha(response)), // Normaliza fechas
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener comentarios por biblioteca
   */
  obtenerComentariosBiblioteca(idBiblioteca: number): Observable<ComentarioResponse[]> {
    const url = `${this.restConstants.getApiURL()}comentarios/biblioteca/${idBiblioteca}`;
    
    console.log('Obtener comentarios biblioteca - URL:', url);
    
    return this.http.get<ComentarioResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener comentarios por usuario
   */
  obtenerComentariosUsuario(idUsuario: number): Observable<ComentarioResponse[]> {
    const url = `${this.restConstants.getApiURL()}comentarios/usuario/${idUsuario}`;
    
    console.log('Obtener comentarios usuario - URL:', url);
    
    return this.http.get<ComentarioResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener comentarios por usuario y videojuego
   */
  obtenerComentariosUsuarioVideojuego(idUsuario: number, idVideojuego: number): Observable<ComentarioResponse[]> {
    const url = `${this.restConstants.getApiURL()}comentarios/usuario/${idUsuario}/videojuego/${idVideojuego}`;
    
    console.log('Obtener comentarios usuario-videojuego - URL:', url);
    
    return this.http.get<ComentarioResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener total de comentarios por videojuego
   */
  obtenerTotalComentariosVideojuego(idVideojuego: number): Observable<{ totalComentarios: number }> {
    const url = `${this.restConstants.getApiURL()}comentarios/estadisticas/videojuego/${idVideojuego}`;
    
    console.log('Obtener total comentarios videojuego - URL:', url);
    
    return this.http.get<{ totalComentarios: number }>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          // Si no existe el endpoint, calcular localmente
          return this.calcularTotalComentariosLocalmente(idVideojuego);
        })
      );
  }

  /**
   * Calcular total de comentarios localmente
   */
  private calcularTotalComentariosLocalmente(idVideojuego: number): Observable<{ totalComentarios: number }> {
    return new Observable(observer => {
      this.obtenerComentariosVideojuego(idVideojuego)
        .subscribe({
          next: (comentarios) => {
            observer.next({ totalComentarios: comentarios.length });
            observer.complete();
          },
          error: (error) => observer.error(error)
        });
    });
  }

  /**
   * Actualizar un comentario existente
   */
  actualizarComentario(idComentario: number, nuevoComentario: string): Observable<ComentarioResponse> {
    const url = `${this.restConstants.getApiURL()}comentarios/${idComentario}`;
    
    console.log('Actualizar comentario - URL:', url);
    console.log('Nuevo comentario:', nuevoComentario);
    
    const updateData = { comentario: nuevoComentario };
    
    return this.http.put<ComentarioResponse>(url, updateData)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Eliminar un comentario
   */
  eliminarComentario(idComentario: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}comentarios/${idComentario}`;
    
    console.log('Eliminar comentario - URL:', url);
    
    return this.http.delete<any>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener comentario por ID
   */
  obtenerComentarioPorId(idComentario: number): Observable<ComentarioResponse> {
    const url = `${this.restConstants.getApiURL()}comentarios/${idComentario}`;
    
    console.log('Obtener comentario por ID - URL:', url);
    
    return this.http.get<ComentarioResponse>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log('Error completo recibido en ComentarioService:', error);
    
    let errorMessage = 'Error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      const serverError = this.extractErrorMessage(error);
      
      if (error.status === 0) {
        errorMessage = 'Error de conexión. Verifica que el servidor esté corriendo.';
      } else if (error.status === 400) {
        errorMessage = serverError || 'Datos inválidos para el comentario';
      } else if (error.status === 404) {
        errorMessage = serverError || 'Comentario no encontrado';
      } else if (error.status === 409) {
        errorMessage = serverError || 'El usuario ya comentó este juego';
      } else if (error.status === 500) {
        errorMessage = serverError || 'Error interno del servidor';
      } else {
        errorMessage = serverError || `Error ${error.status}: ${error.message}`;
      }
    }
    
    console.log('Mensaje de error procesado en ComentarioService:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Extraer mensaje de error del cuerpo de la respuesta
   */
  private extractErrorMessage(error: HttpErrorResponse): string | null {
    try {
      if (typeof error.error === 'string') {
        try {
          const parsedError = JSON.parse(error.error);
          if (parsedError && parsedError.error) {
            return parsedError.error;
          }
        } catch (e) {
          return error.error;
        }
      }
      
      if (error.error && typeof error.error === 'object') {
        if (error.error.error) {
          return error.error.error;
        } else if (error.error.message) {
          return error.error.message;
        }
      }
      
      if (error.message) {
        return error.message;
      }
      
    } catch (e) {
      console.error('Error al extraer mensaje de error en ComentarioService:', e);
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