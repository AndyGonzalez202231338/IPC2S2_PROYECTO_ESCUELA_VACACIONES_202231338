import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Videojuego } from '../../../models/videojuego/videojuego';
import { Categoria } from '../../../models/videojuego/videojuego';
import { MultimediaResponse } from '../../../models/videojuego/multimedia';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { CategoriaService } from '../../../services/Videojuego/categoria.service';
import { MultimediaService } from '../../../services/Videojuego/multimedia.service';
import { LoginService } from '../../../services/Login/login.services';
import { Header } from '../../Header/header/header';
import { Footer } from '../../footer/footer';
import { EmpresaService } from '../../../services/Empresa/empresa.service';
import { CalificacionesComentariosComponent } from '../../Comentario/calificaciones-comentarios-component/calificaciones-comentarios-component';

@Component({
  selector: 'app-detalle-videojuego',
  imports: [RouterLink, CommonModule, Header, Footer, CalificacionesComentariosComponent],
  templateUrl: './detalle-videojuego-component.html',
  styleUrls: ['./detalle-videojuego-component.css']
})
export class DetalleVideojuegoComponent implements OnInit {
  videojuego: Videojuego | null = null;
  categorias: Categoria[] = [];
  imagenes: MultimediaResponse[] = [];
  empresa: any = null;

  isLoading = false;
  isLoadingCategorias = false;
  isLoadingImagenes = false;
  isLoadingEmpresa = false;
  errorMessage = '';
  currentUser: any = null;
  videojuegoId: number | null = null;
  
  // Para el carrusel
  imagenActiva = 0;
  carruselAutoPlay = true;
  intervaloAutoPlay: any;

  mostrarCalificaciones = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private videojuegoService: VideojuegoService,
    private categoriaService: CategoriaService,
    private multimediaService: MultimediaService,
    private empresaService: EmpresaService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.loginService.getCurrentUser();
    this.cargarVideojuego();
  }

  ngOnDestroy(): void {
    // Limpiar intervalo del carrusel
    this.detenerAutoPlay();
  }

  private cargarVideojuego(): void {
    this.route.params.subscribe(params => {
      this.videojuegoId = +params['id'];
      if (this.videojuegoId) {
        this.obtenerVideojuego(this.videojuegoId);
        this.obtenerCategorias(this.videojuegoId);
        this.obtenerImagenes(this.videojuegoId);
      }
    });
  }

  private obtenerVideojuego(id: number): void {
    this.isLoading = true;
    
    this.videojuegoService.getVideojuegoById(id).subscribe({
      next: (videojuego) => {
        this.videojuego = videojuego;
        console.log('Videojuego cargado:', videojuego);
        
        // Cargar información de la empresa
        if (videojuego.id_empresa) {
          this.obtenerEmpresa(videojuego.id_empresa);
        }
        
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar videojuego:', error);
        this.errorMessage = 'Error al cargar el videojuego.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private obtenerCategorias(videojuegoId: number): void {
    this.isLoadingCategorias = true;
    
    this.categoriaService.getCategoriasByVideojuegoId(videojuegoId).subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        console.log('Categorías cargadas:', categorias);
        this.isLoadingCategorias = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar categorías:', error);
        this.isLoadingCategorias = false;
        this.cdr.detectChanges();
      }
    });
  }

  private obtenerImagenes(videojuegoId: number): void {
    this.isLoadingImagenes = true;
    
    this.multimediaService.getMultimediaByVideojuego(videojuegoId).subscribe({
      next: (imagenes) => {
        this.imagenes = imagenes;
        console.log('Imágenes cargadas:', imagenes);
        
        // Iniciar auto-play del carrusel si hay imágenes
        if (this.imagenes.length > 0 && this.carruselAutoPlay) {
          this.iniciarAutoPlay();
        }
        
        this.isLoadingImagenes = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar imágenes:', error);
        this.isLoadingImagenes = false;
        this.cdr.detectChanges();
      }
    });
  }

  private obtenerEmpresa(idEmpresa: number): void {
    this.isLoadingEmpresa = true;
    
    this.empresaService.getEmpresaById(idEmpresa).subscribe({
      next: (empresa) => {
        this.empresa = empresa;
        console.log('Empresa cargada:', empresa);
        this.isLoadingEmpresa = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar empresa:', error);
        this.isLoadingEmpresa = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Métodos para el carrusel
  cambiarImagen(index: number): void {
    if (index >= 0 && index < this.imagenes.length) {
      this.imagenActiva = index;
    }
  }

  imagenAnterior(): void {
    if (this.imagenes.length > 0) {
      this.imagenActiva = (this.imagenActiva - 1 + this.imagenes.length) % this.imagenes.length;
    }
  }

  imagenSiguiente(): void {
    if (this.imagenes.length > 0) {
      this.imagenActiva = (this.imagenActiva + 1) % this.imagenes.length;
    }
  }

  iniciarAutoPlay(): void {
    this.detenerAutoPlay(); // Limpiar intervalo previo
    
    this.intervaloAutoPlay = setInterval(() => {
      this.imagenSiguiente();
    }, 3000); // Cambia cada 3 segundos
  }

  detenerAutoPlay(): void {
    if (this.intervaloAutoPlay) {
      clearInterval(this.intervaloAutoPlay);
    }
  }

  toggleAutoPlay(): void {
    this.carruselAutoPlay = !this.carruselAutoPlay;
    
    if (this.carruselAutoPlay && this.imagenes.length > 0) {
      this.iniciarAutoPlay();
    } else {
      this.detenerAutoPlay();
    }
  }

  // Métodos de utilidad
  formatearPrecio(precio: number): string {
    if (!precio) return 'Q0.00';
    return `Q${precio.toFixed(2)}`;
  }

  formatearFecha(fechaString: string): string {
    if (!fechaString) return 'Sin fecha';
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-GT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getImagenUrl(imagenBase64: string): string {
    return `data:image/jpeg;base64,${imagenBase64}`;
  }

  tieneImagenes(): boolean {
    return this.imagenes && this.imagenes.length > 0;
  }

  tieneCategorias(): boolean {
    return this.categorias && this.categorias.length > 0;
  }

  // Navegación
  volverATienda(): void {
    this.router.navigate(['/tienda']);
  }

  comprarVideojuego(): void {
    if (this.videojuegoId) {
      this.router.navigate(['/videojuego/comprar', this.videojuegoId]);
    }
  }

  // Para usuarios COMUN
  /*puedeComprar(): boolean {
    return this.currentUser?.rol?.nombre === 'COMUN' && 
           this.videojuego && 
           !this.videojuego.comentarios_bloqueados;
  }*/
}