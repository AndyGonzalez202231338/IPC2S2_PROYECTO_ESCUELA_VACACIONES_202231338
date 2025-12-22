// listar-categorias-component.ts
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';
import { CategoriaService } from '../../../services/Videojuego/categoria.service';
import { Categoria } from '../../../models/videojuego/categoria';

@Component({
  selector: 'app-listar-categorias-component',
  imports: [CommonModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './listar-categorias-component.html',
  styleUrl: './listar-categorias-component.css',
})
export class ListarCategoriasComponent implements OnInit {
  categorias: Categoria[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private categoriaService: CategoriaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCategorias();
    this.cdr.detectChanges();
  }

  loadCategorias(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.categoriaService.getAllCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar categorías';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  navigateToCreate(): void {
    this.router.navigate(['/categorias/crear']);
  }

  navigateToEdit(id: number): void {
    this.router.navigate(['/categorias/editar', id]);
  }

  deleteCategoria(id: number, nombre: string): void {
    if (confirm(`¿Eliminar categoría "${nombre}"?`)) {
      this.categoriaService.deleteCategoria(id).subscribe({
        next: () => {
          this.successMessage = `Categoría "${nombre}" eliminada`;
          this.loadCategorias();
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al eliminar';
        }
      });
    }
  }
}