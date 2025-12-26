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
  estado: 'COMPLETADA' | 'PENDIENTE' | 'CANCELADA';
  videojuego?: {
    id_videojuego: number;
    titulo: string;
    precio: number;
  };
  usuario?: {
    id_usuario: number;
    nombre: string;
  };
}