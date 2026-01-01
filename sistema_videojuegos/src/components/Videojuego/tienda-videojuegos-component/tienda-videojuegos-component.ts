import { CategoriaService } from './../../../services/Videojuego/categoria.service';
// components/Videojuego/tienda-videojuegos/tienda-videojuegos.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Categoria, Videojuego } from '../../../models/videojuego/videojuego';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { LoginService } from '../../../services/Login/login.services';
import { FormsModule } from '@angular/forms';
import { Footer } from '../../footer/footer';
import { Header } from '../../Header/header/header';


@Component({
  selector: 'app-tienda-videojuegos',
  imports: [CommonModule, RouterModule, FormsModule, Header, Footer],
  templateUrl: './tienda-videojuegos-component.html',
  styleUrls: ['./tienda-videojuegos-component.css']
})
export class TiendaVideojuegosComponent implements OnInit {
  videojuegos: Videojuego[] = [];
  categorias: Categoria[] = [];
  isLoadingCategorias = false;
  isLoading = false;
  errorMessage = '';
  currentUser: any = null;
  filtroTitulo: string = '';
  filtroCategoria: string = '';
  categoriasUnicas: string[] = [];

  constructor(
    private videojuegoService: VideojuegoService,
    private loginService: LoginService,
    private CategoriaService: CategoriaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.loginService.getCurrentUser();
    this.cargarVideojuegos();
    this.cargarCategorias();
  }

  cargarVideojuegos(): void {
    this.isLoading = true;
    
    this.videojuegoService.getAllVideojuegos().subscribe({
      next: (videojuegos) => {
        console.log('Todos los videojuegos cargados:', videojuegos);
        this.videojuegos = videojuegos;
        this.extraerCategoriasUnicas();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar videojuegos:', error);
        this.errorMessage = 'Error al cargar los videojuegos. Intenta nuevamente.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarCategorias(): void {
    this.isLoadingCategorias = true;
    
    this.CategoriaService.getAllCategorias().subscribe({
      next: (categorias) => {
        console.log('Categorías cargadas desde servicio:', categorias);
        this.categorias = categorias;
        
        // Extraer solo los nombres únicos para el filtro
        this.categoriasUnicas = [...new Set(categorias.map(c => c.nombre))].sort();
        
        this.isLoadingCategorias = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar categorías:', error);
        // Si falla, usa las categorías de los videojuegos como respaldo
        this.extraerCategoriasDeVideojuegos();
        this.isLoadingCategorias = false;
        this.cdr.detectChanges();
      }
    });
  }

  private extraerCategoriasDeVideojuegos(): void {
    const categoriasSet = new Set<string>();
    
    this.videojuegos.forEach(videojuego => {
      if (videojuego.categorias && videojuego.categorias.length > 0) {
        videojuego.categorias.forEach(categoria => {
          categoriasSet.add(categoria.nombre);
        });
      }
    });
    
    this.categoriasUnicas = Array.from(categoriasSet).sort();
  }
  private extraerCategoriasUnicas(): void {
    const categoriasSet = new Set<string>();
    
    this.videojuegos.forEach(videojuego => {
      if (videojuego.categorias && videojuego.categorias.length > 0) {
        videojuego.categorias.forEach(categoria => {
          categoriasSet.add(categoria.nombre);
        });
      }
    });
    
    this.categoriasUnicas = Array.from(categoriasSet).sort();
    console.log('Categorías únicas extraídas:', this.categoriasUnicas);
  }

  // Métodos de filtrado
  get videojuegosFiltrados(): Videojuego[] {
    return this.videojuegos.filter(videojuego => {
      // Filtro por título (búsqueda insensible a mayúsculas)
      const coincideTitulo = !this.filtroTitulo || 
        videojuego.titulo.toLowerCase().includes(this.filtroTitulo.toLowerCase().trim());
      
      // Filtro por categoría
      const coincideCategoria = !this.filtroCategoria || 
        (videojuego.categorias && 
         videojuego.categorias.some(c => 
           c.nombre.toLowerCase() === this.filtroCategoria.toLowerCase()
         ));
      
      return coincideTitulo && coincideCategoria;
    });
  }


  // Métodos de utilidad
  formatearPrecio(precio: number): string {
    return `Q${precio.toFixed(2)}`;
  }

  formatearFecha(fechaString: string): string {
    if (!fechaString) return 'Sin fecha';
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-GT');
  }

  tieneCategorias(videojuego: Videojuego): boolean {
    return videojuego.categorias && videojuego.categorias.length > 0;
  }

  // Método para navegar a detalles
  verDetalles(id: number): void {
    this.router.navigate(['videojuego/detalle', id]);
  }

  comprarVideojuego(id: number): void {
    this.router.navigate(['videojuego/comprar', id]);
  }

  // Método para obtener nombres de categorías
  getNombresCategorias(videojuego: Videojuego): string {
    if (!videojuego.categorias || videojuego.categorias.length === 0) {
      return 'Sin categorías';
    }
    return videojuego.categorias.map(c => c.nombre).join(', ');
  }

  get categoriasActivas(): string[] {
    const categorias = new Set<string>();
    this.videojuegosFiltrados.forEach(v => {
      if (v.categorias) {
        v.categorias.forEach(c => categorias.add(c.nombre));
      }
    });
    return Array.from(categorias);
  }

  onFiltroChange(): void {
  // Actualizar categorías disponibles basadas en el filtro de título
  if (this.filtroTitulo) {
    this.actualizarCategoriasDisponibles();
  } else {
    // Si no hay filtro, restaurar todas las categorías
    if (this.categorias.length > 0) {
      this.categoriasUnicas = [...new Set(this.categorias.map(c => c.nombre))].sort();
    }
  }
}

// Método que se llama cuando cambian los filtros
actualizarCategoriasDisponibles(): void {
  if (this.filtroTitulo) {
    // Si hay filtro por título, mostrar solo categorías de esos videojuegos
    const categoriasFiltradas = new Set<string>();
    this.videojuegos.forEach(videojuego => {
      if (videojuego.titulo.toLowerCase().includes(this.filtroTitulo.toLowerCase())) {
        if (videojuego.categorias) {
          videojuego.categorias.forEach(c => categoriasFiltradas.add(c.nombre));
        }
      }
    });
    this.categoriasUnicas = Array.from(categoriasFiltradas).sort();
  } else {
    // Si no hay filtro, mostrar todas las categorías
    if (this.categorias.length > 0) {
      this.categoriasUnicas = [...new Set(this.categorias.map(c => c.nombre))].sort();
    } else {
      this.extraerCategoriasDeVideojuegos();
    }
  }
}

  // Resetear filtros
  resetearFiltros(): void {
    this.filtroTitulo = '';
    this.filtroCategoria = '';
  }
}