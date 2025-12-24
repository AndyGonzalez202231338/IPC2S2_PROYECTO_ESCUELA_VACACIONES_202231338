import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { UpdateVideojuegoRequest, Videojuego } from '../../models/videojuego/videojuego';



@Injectable({
  providedIn: 'root'
})
export class VideojuegoService {
  restConstants = new RestConstants();

  constructor(private httpClient: HttpClient) { }

  public getVideojuegosByEmpresa(id_empresa: number): Observable<Videojuego[]> {
    return this.httpClient.get<Videojuego[]>(
      `${this.restConstants.getApiURL()}videojuegos/empresa/${id_empresa}`
    );
  }

  
  public getVideojuegoById(id: number): Observable<Videojuego> {
    return this.httpClient.get<Videojuego>(
      `${this.restConstants.getApiURL()}videojuegos/${id}?incluirCategorias=true`
    );
  }

  
  public verificarTituloDisponible(titulo: string, idEmpresa: number): Observable<any> {
    return this.httpClient.get(
      `${this.restConstants.getApiURL()}videojuegos/verificar-titulo?titulo=${titulo}&idEmpresa=${idEmpresa}`
    );
  }

  
  public crearVideojuego(videojuego: any): Observable<Videojuego> {
    return this.httpClient.post<Videojuego>(
      `${this.restConstants.getApiURL()}videojuegos`,
      videojuego
    );
  }


    public actualizarVideojuego(id: number, videojuego: UpdateVideojuegoRequest): Observable<Videojuego> {
    return this.httpClient.put<Videojuego>(
      `${this.restConstants.getApiURL()}videojuegos/${id}`,
      videojuego
    );
  }

  
  public eliminarVideojuego(id: number): Observable<any> {
    return this.httpClient.delete(
      `${this.restConstants.getApiURL()}videojuegos/${id}`
    );
  }

  bloquearComentariosTodosVideojuegosEmpresa(idEmpresa: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}videojuegos/empresa/${idEmpresa}/bloquear-comentarios-todos`;
    return this.httpClient.put(url, {});
  }

  desbloquearComentariosTodosVideojuegosEmpresa(idEmpresa: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}videojuegos/empresa/${idEmpresa}/desbloquear-comentarios-todos`;
    return this.httpClient.put(url, {});
  }
}