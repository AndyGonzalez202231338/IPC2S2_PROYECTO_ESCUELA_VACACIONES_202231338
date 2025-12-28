import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface RespuestaComentarioRequest {
  id_comentario_padre: number;
  id_usuario: number;
  comentario: string;
}

export interface RespuestaComentarioResponse {
  id_respuesta: number;
  id_comentario_padre: number;
  id_usuario: number;
  comentario: string;
  fecha_hora: string;
}

@Injectable({
  providedIn: 'root'
})
export class RespuestaComentarioService {
  
  constructor(
    private http: HttpClient,
    private restConstants: RestConstants
  ) { }

  crearRespuesta(request: RespuestaComentarioRequest): Observable<RespuestaComentarioResponse> {
    const url = `${this.restConstants.getApiURL()}respuestas-comentarios`;
    return this.http.post<RespuestaComentarioResponse>(url, request)
      .pipe(catchError(this.handleError));
  }

  getRespuestasPorComentarioPadre(idComentarioPadre: number): Observable<RespuestaComentarioResponse[]> {
    const url = `${this.restConstants.getApiURL()}respuestas-comentarios/comentario/${idComentarioPadre}`;
    return this.http.get<RespuestaComentarioResponse[]>(url)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error desconocido';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error ${error.status}: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}