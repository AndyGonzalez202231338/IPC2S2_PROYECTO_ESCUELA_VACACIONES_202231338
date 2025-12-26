// services/Usuario/saldo.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { OperacionSaldoResponse } from '../../models/user/saldo';


@Injectable({
  providedIn: 'root'
})
export class SaldoService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Obtener saldo actual del usuario
   */
  obtenerSaldo(idUsuario: number): Observable<OperacionSaldoResponse> {
    const url = `${this.restConstants.getApiURL()}saldo/${idUsuario}`;
    return this.http.get<OperacionSaldoResponse>(url);
  }

  /**
   * Recargar saldo (acreditar)
   */
  recargarSaldo(idUsuario: number, monto: number): Observable<OperacionSaldoResponse> {
    const url = `${this.restConstants.getApiURL()}saldo/acreditar`;
    const request = { 
      id_usuario: idUsuario,
      monto: monto
    };
    return this.http.post<OperacionSaldoResponse>(url, request);
  }

  /**
   * Debitar saldo (para compras)
   */
  debitarSaldo(idUsuario: number, monto: number): Observable<OperacionSaldoResponse> {
    const url = `${this.restConstants.getApiURL()}saldo/debitar`;
    const request = { 
      id_usuario: idUsuario,
      monto: monto
    };
    return this.http.post<OperacionSaldoResponse>(url, request);
  }


}