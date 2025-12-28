import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateUtilsService {
  
  /**
   * Convierte un array de fecha [año, mes, día, hora, minuto, segundo] a Date
   */
  arrayToDate(fechaArray: number[] | string | Date): Date {
    if (!fechaArray) {
      return new Date();
    }
    
    // Si ya es un objeto Date
    if (fechaArray instanceof Date) {
      return fechaArray;
    }
    
    // Si es un string de fecha
    if (typeof fechaArray === 'string') {
      // Intentar parsear como string primero
      const dateFromString = new Date(fechaArray);
      if (!isNaN(dateFromString.getTime())) {
        return dateFromString;
      }
    }
    
    // Si es un array [año, mes, día, hora, minuto, segundo]
    if (Array.isArray(fechaArray) && fechaArray.length >= 6) {
      // Nota: Los meses en JavaScript son 0-indexed (0 = Enero, 11 = Diciembre)
      // El backend probablemente envía meses como 1-12, así que restamos 1
      const año = fechaArray[0];
      const mes = fechaArray[1] - 1; // Ajustar mes (restar 1)
      const día = fechaArray[2];
      const hora = fechaArray[3] || 0;
      const minuto = fechaArray[4] || 0;
      const segundo = fechaArray[5] || 0;
      
      return new Date(año, mes, día, hora, minuto, segundo);
    }
    
    // Si no podemos determinar, devolver fecha actual
    console.warn('Formato de fecha no reconocido:', fechaArray);
    return new Date();
  }
  
  /**
   * Formatea fecha a string legible
   */
  formatearFecha(fecha: any, incluirHora: boolean = true): string {
    try {
      const fechaObj = this.arrayToDate(fecha);
      
      if (isNaN(fechaObj.getTime())) {
        return 'Fecha inválida';
      }
      
      const opciones: Intl.DateTimeFormatOptions = {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      };
      
      if (incluirHora) {
        opciones.hour = '2-digit';
        opciones.minute = '2-digit';
      }
      
      return fechaObj.toLocaleDateString('es-GT', opciones);
    } catch (error) {
      console.error('Error formateando fecha:', error, fecha);
      return 'Fecha inválida';
    }
  }
  
  /**
   * Formatea fecha para mostrar tiempo relativo (ej: "Hace 2 horas")
   */
  formatearFechaRelativa(fecha: any): string {
    try {
      const fechaObj = this.arrayToDate(fecha);
      const ahora = new Date();
      const diferencia = ahora.getTime() - fechaObj.getTime();
      
      const segundos = Math.floor(diferencia / 1000);
      const minutos = Math.floor(segundos / 60);
      const horas = Math.floor(minutos / 60);
      const días = Math.floor(horas / 24);
      
      if (días > 7) {
        return this.formatearFecha(fecha, false);
      } else if (días > 0) {
        return `Hace ${días} ${días === 1 ? 'día' : 'días'}`;
      } else if (horas > 0) {
        return `Hace ${horas} ${horas === 1 ? 'hora' : 'horas'}`;
      } else if (minutos > 0) {
        return `Hace ${minutos} ${minutos === 1 ? 'minuto' : 'minutos'}`;
      } else {
        return 'Hace unos momentos';
      }
    } catch (error) {
      return this.formatearFecha(fecha);
    }
  }
  
  /**
   * Convierte fecha a string ISO para APIs
   */
  fechaToISO(fecha: any): string {
    try {
      const fechaObj = this.arrayToDate(fecha);
      return fechaObj.toISOString();
    } catch (error) {
      return new Date().toISOString();
    }
  }
}