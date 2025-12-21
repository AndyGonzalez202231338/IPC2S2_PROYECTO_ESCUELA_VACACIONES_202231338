import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Configuracion } from '../../../models/configuracion/configuracion';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';
import { ConfiguracionService } from '../../../services/Configuracion/configuracion.service';

@Component({
  selector: 'app-listar-configuraciones-component',
  imports: [CommonModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './listar-configuraciones-component.html',
  styleUrl: './listar-configuraciones-component.css',
})
export class ListarConfiguracionesComponent implements OnInit {
  configuraciones: Configuracion[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private configuracionService: ConfiguracionService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadConfiguraciones();
  }

  loadConfiguraciones(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.configuracionService.getAllConfiguraciones().subscribe({
      next: (configs) => {
        this.configuraciones = configs;
        this.loading = false;
        this.cdr.detectChanges();
        console.log('Configuraciones cargadas:', configs);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar las configuraciones';
        this.loading = false;
        this.cdr.detectChanges();
        console.error('Error al cargar configuraciones:', error);
      }
    });
  }

  navigateToEdit(id: number): void {
    this.router.navigate(['/sistema/configuraciones/editar', id]);
  }

  getConfiguracionTipo(configuracion: string): string {
    switch(configuracion) {
      case 'COMISION_GLOBAL':
        return 'Comisión';
      case 'EDAD_ADOLESCENTES':
        return 'Edad';
      case 'MAX_MIEMBROS_GRUPO':
        return 'Límite';
      default:
        return 'General';
    }
  }

  getConfiguracionIcon(configuracion: string): string {
    switch(configuracion) {
      case 'COMISION_GLOBAL':
        return 'fa-percent';
      case 'EDAD_ADOLESCENTES':
        return 'fa-user';
      case 'MAX_MIEMBROS_GRUPO':
        return 'fa-users';
      default:
        return 'fa-cog';
    }
  }

  getBadgeClass(configuracion: string): string {
    switch(configuracion) {
      case 'COMISION_GLOBAL':
        return 'bg-warning text-dark';
      case 'EDAD_ADOLESCENTES':
        return 'bg-info';
      case 'MAX_MIEMBROS_GRUPO':
        return 'bg-success';
      default:
        return 'bg-secondary';
    }
  }

  isConfiguracionActiva(config: Configuracion): boolean {
    if (!config.fecha_final) return true;
    const fechaFinal = new Date(config.fecha_final);
    const hoy = new Date();
    return fechaFinal >= hoy;
  }

  formatFecha(fecha: string | null): string {
    if (!fecha) return 'Permanente';
    return new Date(fecha).toLocaleDateString('es-ES');
  }
}