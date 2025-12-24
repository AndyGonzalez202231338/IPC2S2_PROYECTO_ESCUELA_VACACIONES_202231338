import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderAdminEmpresa } from '../../Header/header-admin-empresa/header-admin-empresa';
import { Footer } from '../../footer/footer';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { CategoriaService } from '../../../services/Videojuego/categoria.service';
import { MultimediaService } from '../../../services/Videojuego/multimedia.service';
import { LoginService } from '../../../services/Login/login.services';
import { Categoria } from '../../../models/videojuego/videojuego';
import { MultimediaResponse } from '../../../models/videojuego/multimedia';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-editar-videojuego-component',
  imports: [CommonModule, ReactiveFormsModule, HeaderAdminEmpresa, Footer],
  templateUrl: './editar-videojuego-component.html',
  styleUrl: './editar-videojuego-component.css',
})
export class EditarVideojuegoComponent implements OnInit, OnDestroy {
  videojuegoForm: FormGroup;
  categorias: Categoria[] = [];
  imagenesExistentes: MultimediaResponse[] = [];
  
  // Nuevas imágenes a agregar
  nuevasImagenesBase64: string[] = [];
  nuevosArchivos: File[] = [];
  maxImagenes = 10;
  
  // Estados
  isLoading = false;
  isLoadingCategorias = false;
  isLoadingVideojuego = false;
  errorMessage = '';
  successMessage = '';
  empresaId: number | null = null;
  videojuegoId: number | null = null;
  videojuegoOriginal: any = null;
  
  private routeSub: Subscription | null = null;

  constructor(
    private fb: FormBuilder,
    private videojuegoService: VideojuegoService,
    private categoriaService: CategoriaService,
    private multimediaService: MultimediaService,
    private loginService: LoginService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.videojuegoForm = this.createForm();
  }

  ngOnInit(): void {
    this.cargarEmpresaId();
    this.cargarVideojuego();
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      recursos_minimos: ['', [Validators.required]],
      precio: ['', [Validators.required, Validators.min(0)]],
      clasificacion_edad: ['E', Validators.required],
      fecha_lanzamiento: ['', Validators.required],
      comentarios_bloqueados: [false],
      categorias_ids: [[]]
    });
  }

  private cargarEmpresaId(): void {
    const currentUser = this.loginService.getCurrentUser();
    if (currentUser && currentUser.empresa?.id_empresa) {
      this.empresaId = currentUser.empresa.id_empresa;
      this.cdr.detectChanges();
    } else {
      this.errorMessage = 'No se encontró la información de la empresa.';
      this.router.navigate(['/empresa/videojuegos']);
      this.cdr.detectChanges();
    }
  }

  private cargarVideojuego(): void {
    this.routeSub = this.route.params.subscribe(params => {
      this.videojuegoId = +params['id'];
      if (this.videojuegoId) {
        this.cargarDatosVideojuego(this.videojuegoId);
        this.cargarCategorias(this.videojuegoId);
        this.cargarImagenesVideojuego(this.videojuegoId);
      }
    });
  }

    private cargarCategorias(id: number): void {
    this.isLoadingCategorias = true;
    this.categoriaService.getCategoriasByVideojuegoId(id).subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        console.log('Categorías cargadas:', categorias);
        this.isLoadingCategorias = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar categorías:', error);
        this.errorMessage = 'Error al cargar las categorías.';
        this.isLoadingCategorias = false;
        this.cdr.detectChanges();
      }
    });
  }

  private cargarDatosVideojuego(id: number): void {
    this.isLoadingVideojuego = true;
    this.videojuegoService.getVideojuegoById(id).subscribe({
      next: (videojuego) => {
        this.videojuegoOriginal = videojuego;
        console.log('Videojuego cargado para edición:', videojuego);
        this.cargarFormulario(videojuego);
        this.isLoadingVideojuego = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar videojuego:', error);
        this.errorMessage = 'Error al cargar el videojuego.';
        this.isLoadingVideojuego = false;
        this.router.navigate(['/empresa/videojuegos']);
        this.cdr.detectChanges();
      }
    });
  }

  private cargarImagenesVideojuego(id: number): void {
    this.multimediaService.getMultimediaByVideojuego(id).subscribe({
      next: (imagenes) => {
        this.imagenesExistentes = imagenes;
        console.log('Imágenes cargadas:', imagenes.length);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar imágenes:', error);
        this.cdr.detectChanges();
        // No mostrar error fatal, solo log
      }
    });
  }

  private cargarFormulario(videojuego: any): void {
    this.videojuegoForm.patchValue({
      titulo: videojuego.titulo,
      descripcion: videojuego.descripcion,
      recursos_minimos: videojuego.recursos_minimos,
      precio: videojuego.precio,
      clasificacion_edad: videojuego.clasificacion_edad,
      fecha_lanzamiento: this.formatearFechaParaInput(videojuego.fecha_lanzamiento),
      comentarios_bloqueados: videojuego.comentarios_bloqueados,
      categorias_ids: videojuego.categorias?.map((c: any) => c.id_categoria) || []
    });
    this.cdr.detectChanges();
  }



  // Métodos para categorías
  toggleCategoria(categoriaId: number): void {
    const categoriasControl = this.videojuegoForm.get('categorias_ids');
    if (!categoriasControl) return;

    const currentCategorias: number[] = categoriasControl.value || [];
    
    if (currentCategorias.includes(categoriaId)) {
      const index = currentCategorias.indexOf(categoriaId);
      currentCategorias.splice(index, 1);
    } else {
      currentCategorias.push(categoriaId);
    }
    
    categoriasControl.setValue([...currentCategorias]);
  }

  isCategoriaSelected(categoriaId: number): boolean {
    const categorias: number[] = this.videojuegoForm.get('categorias_ids')?.value || [];
    return categorias.includes(categoriaId);
  }

  getNombreCategoria(id: number): string {
    const categoria = this.categorias.find(c => c.id_categoria === id);
    return categoria ? categoria.nombre : 'Desconocida';
  }

  // Métodos para imágenes existentes
  eliminarImagenExistente(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta imagen?')) {
      this.multimediaService.deleteMultimedia(id).subscribe({
        next: () => {
          // Remover de la lista local
          this.imagenesExistentes = this.imagenesExistentes.filter(img => img.id_multimedia !== id);
          this.successMessage = 'Imagen eliminada exitosamente.';
        },
        error: (error) => {
          console.error('Error al eliminar imagen:', error);
          this.errorMessage = 'Error al eliminar la imagen.';
        }
      });
    }
  }

  eliminarTodasLasImagenes(): void {
    if (!this.videojuegoId) return;
    
    if (confirm('¿Estás seguro de que deseas eliminar TODAS las imágenes del videojuego?')) {
      this.multimediaService.deleteMultimediaByVideojuego(this.videojuegoId).subscribe({
        next: () => {
          this.imagenesExistentes = [];
          this.successMessage = 'Todas las imágenes han sido eliminadas.';
        },
        error: (error) => {
          console.error('Error al eliminar imágenes:', error);
          this.errorMessage = 'Error al eliminar las imágenes.';
        }
      });
    }
  }

  // Métodos para nuevas imágenes
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    
    if (!input.files || input.files.length === 0) return;
    
    const archivos = Array.from(input.files);
    const totalImagenes = this.imagenesExistentes.length + this.nuevasImagenesBase64.length + archivos.length;
    
    if (totalImagenes > this.maxImagenes) {
      this.errorMessage = `Solo puedes tener un máximo de ${this.maxImagenes} imágenes en total.`;
      return;
    }
    
    archivos.forEach(file => {
      if (!file.type.match('image.*')) {
        this.errorMessage = 'Solo se permiten archivos de imagen.';
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = 'La imagen no debe superar los 5MB.';
        return;
      }
      
      this.nuevosArchivos.push(file);
      this.convertirABase64(file);
    });
    
    input.value = '';
  }

  convertirABase64(file: File): void {
    const reader = new FileReader();
    
    reader.onload = (e: ProgressEvent<FileReader>) => {
      if (e.target?.result) {
        const base64String = e.target.result as string;
        const base64Data = base64String.split(',')[1];
        this.nuevasImagenesBase64.push(base64Data);
      }
    };
    
    reader.readAsDataURL(file);
  }

  eliminarNuevaImagen(index: number): void {
    this.nuevosArchivos.splice(index, 1);
    this.nuevasImagenesBase64.splice(index, 1);
  }

  // Método para subir nuevas imágenes
  private subirNuevasImagenes(): void {
    if (!this.videojuegoId || this.nuevasImagenesBase64.length === 0) {
      this.redirigirConExito();
      return;
    }
    
    const requests = this.nuevasImagenesBase64.map(imagenBase64 => ({
      id_videojuego: this.videojuegoId!,
      imagenBase64: imagenBase64
    }));
    
    // Usar el endpoint de múltiples imágenes si está disponible
    this.multimediaService.crearMultiplesImagenes(this.videojuegoId, requests).subscribe({
      next: () => {
        this.successMessage += ' Nuevas imágenes agregadas exitosamente.';
        this.redirigirConExito();
      },
      error: (error) => {
        console.error('Error al subir nuevas imágenes:', error);
        // Intentar subir una por una como fallback
        this.subirImagenesIndividualmente(requests);
      }
    });
  }

  private subirImagenesIndividualmente(requests: any[]): void {
    const observables = requests.map(request => 
      this.multimediaService.createMultimedia(request)
    );
    
    // Usar forkJoin para esperar a que todas terminen
    forkJoin(observables).subscribe({
      next: () => {
        this.successMessage += ' Nuevas imágenes agregadas exitosamente.';
        this.redirigirConExito();
      },
      error: (error) => {
        console.error('Error al subir algunas imágenes:', error);
        this.successMessage += ' Videojuego actualizado, pero hubo problemas al agregar algunas imágenes.';
        this.redirigirConExito();
      }
    });
  }

  private redirigirConExito(): void {
    setTimeout(() => {
      this.router.navigate(['/empresa/videojuegos']);
    }, 2000);
  }

  // Envío del formulario
  onSubmit(): void {
    if (this.videojuegoForm.invalid || !this.videojuegoId) {
      this.marcarCamposComoSucios();
      return;
    }
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const videojuegoData = {
      ...this.videojuegoForm.value,
      precio: Number(this.videojuegoForm.value.precio)
    };
    
    console.log('Actualizando videojuego:', this.videojuegoId, videojuegoData);
    
    this.videojuegoService.actualizarVideojuego(this.videojuegoId, videojuegoData).subscribe({
      next: (videojuegoActualizado) => {
        console.log('Videojuego actualizado:', videojuegoActualizado);
        this.successMessage = 'Videojuego actualizado exitosamente.';
        
        // Subir nuevas imágenes si hay
        this.subirNuevasImagenes();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al actualizar videojuego:', error);
        this.errorMessage = error.message || 'Error al actualizar el videojuego.';
        this.isLoading = false;
      }
    });
  }

  private formatearFechaParaInput(fecha: any): string {
  if (!fecha) return '';
  
  let date: Date;
  
  // Si es un número (timestamp), crear Date desde el timestamp
  if (typeof fecha === 'number') {
    date = new Date(fecha);
  } 
  // Si es un string, intentar parsearlo
  else if (typeof fecha === 'string') {
    // Si ya está en formato YYYY-MM-DD, devolverlo tal cual
    if (fecha.match(/^\d{4}-\d{2}-\d{2}$/)) {
      return fecha;
    }
    date = new Date(fecha);
  }
  // Si ya es un objeto Date
  else if (fecha instanceof Date) {
    date = fecha;
  } else {
    return '';
  }
  
  // Verificar si la fecha es válida
  if (isNaN(date.getTime())) {
    return '';
  }
  
  // Formatear a YYYY-MM-DD para el input type="date"
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  
  return `${year}-${month}-${day}`;
}

  private marcarCamposComoSucios(): void {
    Object.keys(this.videojuegoForm.controls).forEach(key => {
      const control = this.videojuegoForm.get(key);
      control?.markAsTouched();
    });
  }

  get f() {
    return this.videojuegoForm.controls;
  }

  volver(): void {
    this.router.navigate(['/empresa/videojuegos']);
  }

  getFileName(index: number): string {
    if (this.nuevosArchivos[index]) {
      return this.nuevosArchivos[index].name;
    }
    return `Imagen ${index + 1}`;
  }

  // Método para obtener URL de imagen
  getImagenUrl(imagenBase64: string): string {
    return `data:image/jpeg;base64,${imagenBase64}`;
  }
}

// Necesitas importar forkJoin
import { forkJoin } from 'rxjs';
