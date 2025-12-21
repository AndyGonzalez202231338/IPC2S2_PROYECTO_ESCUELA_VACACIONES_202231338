import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';
import { Empresa, EmpresaService } from '../../../services/Empresa/empresa.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-get-all-empresas-component',
  imports: [CommonModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './get-all-empresas-component.html',
  styleUrl: './get-all-empresas-component.css',
})
export class GetAllEmpresasComponent implements OnInit {
  protected empresas: Empresa[] = [];
  errorMessage = '';
  successMessage = '';

  constructor(
    private empresaService: EmpresaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadEmpresas();
  }

  loadEmpresas(): void {
    this.errorMessage = '';
    console.log('Iniciando carga de empresas...'); // DEBUG
    
    this.empresaService.getAllEmpresa().subscribe({
      next: (empresasServidor: Empresa[]) => {
        console.log('Datos recibidos:', empresasServidor); // DEBUG
        this.empresas = empresasServidor;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error recibido:', error); // DEBUG
        this.errorMessage = error.message || 'Error al cargar las empresas';
        this.cdr.detectChanges();
      }
    });
  }

  navigateToCreate(): void {
    this.router.navigate(['/empresas/crear']);
  }

  navigateToEdit(id: number): void {
    this.router.navigate(['/empresas/editar', id]);
  }

  navigateToComisiones(id: number): void {
  this.router.navigate(['/empresas', id, 'comisiones']);
}

  deleteEmpresa(id: number, nombre: string): void {
    if (confirm(`¿Está seguro que desea eliminar la empresa "${nombre}"?`)) {
      this.empresaService.deleteEmpresa(id).subscribe({
        next: () => {
          this.successMessage = `Empresa "${nombre}" eliminada exitosamente`;
          this.loadEmpresas(); // Recargar la lista
          
          // Limpiar mensaje después de 5 segundos
          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al eliminar la empresa';
          console.error('Error al eliminar empresa:', error);
        }
      });
    }
  }

  getAdministradorNombre(empresa: Empresa): string {
    return empresa.administrador?.nombre || 'Sin administrador asignado';
  }

  getAdministradorEmail(empresa: Empresa): string {
    return empresa.administrador?.correo || 'N/A';
  }


}