import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';
import { Comision } from '../../../models/comision/comision';
import { ComisionService, NuevaComisionRequest } from '../../../services/Comision/comision.service';
import { EmpresaService } from '../../../services/Empresa/empresa.service';

@Component({
  selector: 'app-comisiones-empresa-component',
  imports: [CommonModule, RouterModule, FormsModule, HeaderAdminSistema, Footer],
  templateUrl: './comisiones-empresa-component.html',
  styleUrl: './comisiones-empresa-component.css',
})
export class ComisionesEmpresaComponent implements OnInit {
  comisiones: Comision[] = [];
  empresaId!: number;
  empresaNombre: string = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Variables para crear nueva comisión
  nuevaComision: NuevaComisionRequest = {
    id_empresa: 0,
    porcentaje: 0,
    fecha_inicio: this.getTodayDate(),
    fecha_final: null,
    tipo_comision: 'especifica'
  };

  showForm = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comisionService: ComisionService,
    private empresaService: EmpresaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.empresaId = +params['id'];
      this.nuevaComision.id_empresa = this.empresaId;
      this.loadEmpresaInfo();
      this.loadComisiones();
    });
  }

  loadEmpresaInfo(): void {
    this.empresaService.getEmpresaById(this.empresaId).subscribe({
      next: (empresa) => {
        this.empresaNombre = empresa.nombre;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar información de la empresa:', error);
      }
    });
  }

  loadComisiones(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.comisionService.getComisionesByEmpresa(this.empresaId).subscribe({
      next: (comisiones) => {
        this.comisiones = comisiones;
        this.loading = false;
        this.cdr.detectChanges();
        
        console.log('Comisiones cargadas:', comisiones);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar las comisiones';
        this.loading = false;
        this.cdr.detectChanges();
        console.error('Error al cargar comisiones:', error);
      }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (this.showForm) {
      // Resetear formulario
      this.nuevaComision = {
        id_empresa: this.empresaId,
        porcentaje: 0,
        fecha_inicio: this.getTodayDate(),
        fecha_final: null,
        tipo_comision: 'especifica'
      };
    }
  }

  crearComision(): void {
    if (!this.validarComision()) {
      return;
    }

    this.loading = true;
    
    this.comisionService.createComision(this.nuevaComision).subscribe({
      next: (comisionCreada) => {
        this.successMessage = 'Comisión creada exitosamente';
        this.loadComisiones();
        this.showForm = false;
        this.cdr.detectChanges();
        // Limpiar mensaje después de 5 segundos
        setTimeout(() => {
          this.successMessage = '';
        }, 5000);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al crear la comisión';
        this.loading = false;
        this.cdr.detectChanges();
        console.error('Error al crear comisión:', error);
      }
    });
  }

  validarComision(): boolean {
    if (this.nuevaComision.porcentaje <= 0 || this.nuevaComision.porcentaje > 100) {
      this.errorMessage = 'El porcentaje debe estar entre 0.01 y 100';
      return false;
    }

    if (!this.nuevaComision.fecha_inicio) {
      this.errorMessage = 'La fecha de inicio es requerida';
      return false;
    }

    if (this.nuevaComision.fecha_final && 
        new Date(this.nuevaComision.fecha_final) <= new Date(this.nuevaComision.fecha_inicio)) {
      this.errorMessage = 'La fecha final debe ser posterior a la fecha de inicio';
      return false;
    }

    return true;
  }

  eliminarComision(id: number, porcentaje: number): void {
    if (confirm(`¿Está seguro que desea eliminar la comisión del ${porcentaje}%?`)) {
      this.comisionService.deleteComision(id).subscribe({
        next: () => {
          this.successMessage = 'Comisión eliminada exitosamente';
          this.loadComisiones();
          
          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al eliminar la comisión';
          console.error('Error al eliminar comisión:', error);
          this.cdr.detectChanges();
        }
        
      });
    }
  }

  esComisionActiva(comision: Comision): boolean {
    const hoy = new Date();
    const fechaInicio = new Date(comision.fecha_inicio);
    const fechaFinal = comision.fecha_final ? new Date(comision.fecha_final) : null;
    
    return fechaInicio <= hoy && (!fechaFinal || fechaFinal >= hoy);
  }

  getTipoComisionBadge(tipo: string): string {
    return tipo === 'global' ? 'Global' : 'Específica';
  }

  getTipoComisionClass(tipo: string): string {
    return tipo === 'global' ? 'bg-primary' : 'bg-info';
  }

  getTodayDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  formatFecha(fecha: string | null): string {
    if (!fecha) return 'Sin fecha final';
    return new Date(fecha).toLocaleDateString('es-ES');
  }

  volverAListaEmpresas(): void {
    this.router.navigate(['/empresas']);
  }
}
