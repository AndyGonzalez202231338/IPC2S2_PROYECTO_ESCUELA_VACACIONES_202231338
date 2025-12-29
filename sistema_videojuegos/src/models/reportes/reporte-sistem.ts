export interface ReporteGenericoRequest {
  fechaInicio?: string;
  fechaFin?: string;
  limite?: number;
  formato?: 'pdf' | 'excel' | 'json';
}

export interface ReporteResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp: string;
  metadata?: {
    totalRegistros: number;
    tiempoProcesamiento: number;
    formato: string;
  };
}

export interface ReporteGeneracionResponse {
  idReporte: string;
  estado: 'PENDIENTE' | 'PROCESANDO' | 'COMPLETADO' | 'ERROR';
  mensaje?: string;
  urlDescarga?: string;
  fechaGeneracion: string;
}

export interface ReporteHistoricoResponse {
  idReporte: string;
  tipo: string;
  fechaGeneracion: string;
  parametros: any;
  estado: string;
  tamanioBytes?: number;
  urlDescarga?: string;
}