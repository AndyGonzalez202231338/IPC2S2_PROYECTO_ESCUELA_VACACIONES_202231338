// components/Videojuego/tienda-videojuegos/tienda-videojuegos.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Videojuego } from '../../../models/videojuego/videojuego';
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
  isLoading = false;
  errorMessage = '';
  currentUser: any = null;
  filtroTitulo: string = '';
  filtroCategoria: string = '';
  categoriasUnicas: string[] = [];

  constructor(
    private videojuegoService: VideojuegoService,
    private loginService: LoginService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.loginService.getCurrentUser();
    this.cargarVideojuegos();
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
  }

  // Métodos de filtrado
  get videojuegosFiltrados(): Videojuego[] {
    return this.videojuegos.filter(videojuego => {
      const coincideTitulo = this.filtroTitulo === '' || 
        videojuego.titulo.toLowerCase().includes(this.filtroTitulo.toLowerCase());
      
      const coincideCategoria = this.filtroCategoria === '' || 
        (videojuego.categorias && videojuego.categorias.some(c => c.nombre === this.filtroCategoria));
      
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

  // Resetear filtros
  resetearFiltros(): void {
    this.filtroTitulo = '';
    this.filtroCategoria = '';
  }
}