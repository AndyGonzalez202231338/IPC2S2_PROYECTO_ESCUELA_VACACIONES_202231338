export interface OperacionSaldoResponse {
  id_usuario: number;
  saldo_cartera: number;
}

export interface OperacionSaldoRequest {
  id_usuario: number;
  monto: number;
}

export interface HistorialTransaccion {
  id_transaccion: number;
  id_usuario: number;
  tipo_transaccion: 'CARGA' | 'COMPRA' | 'DEVOLUCION' | 'RECARGA';
  monto: number;
  fecha_transaccion: string;
  descripcion?: string;
  id_videojuego?: number;
}

export interface RecargaRequest {
  id_usuario: number;
  monto: number;
}