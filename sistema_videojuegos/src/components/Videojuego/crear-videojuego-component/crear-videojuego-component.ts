import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HeaderAdminEmpresa } from '../../Header/header-admin-empresa/header-admin-empresa';
import { Footer } from '../../footer/footer';
import { Categoria } from '../../../models/videojuego/videojuego';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { CategoriaService } from '../../../services/Videojuego/categoria.service';
import { LoginService } from '../../../services/Login/login.services';
import { Router } from '@angular/router';
import { MultimediaService } from '../../../services/Videojuego/multimedia.service';

@Component({
  selector: 'app-crear-videojuego-component',
  imports: [CommonModule, ReactiveFormsModule, HeaderAdminEmpresa, Footer],
  templateUrl: './crear-videojuego-component.html',
  styleUrl: './crear-videojuego-component.css',
})
export class CrearVideojuegoComponent implements OnInit {
  videojuegoForm: FormGroup;
  categorias: Categoria[] = [];
  isLoading = false;
  isLoadingCategorias = false;
  errorMessage = '';
  successMessage = '';
  empresaId: number | null = null;
  

  imagenesBase64: string[] = [];
  archivosSeleccionados: File[] = [];
  maxImagenes = 10; // Máximo de imágenes permitidas

  constructor(
    private fb: FormBuilder,
    private videojuegoService: VideojuegoService,
    private categoriaService: CategoriaService,
    private multimediaService: MultimediaService,
    private loginService: LoginService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.videojuegoForm = this.createForm();
  }

  ngOnInit(): void {
    this.cargarEmpresaId();
    this.cargarCategorias();
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
      categorias_ids: [[]] // Array de IDs de categorías
    });
  }

  private cargarEmpresaId(): void {
    const currentUser = this.loginService.getCurrentUser();
    if (currentUser && currentUser.empresa?.id_empresa) {
      this.empresaId = currentUser.empresa.id_empresa;
    } else {
      this.errorMessage = 'No se encontró la información de la empresa.';
      this.router.navigate(['/empresa/videojuegos']);
    }
  }

  private cargarCategorias(): void {
    this.isLoadingCategorias = true;
    this.categoriaService.getAllCategorias().subscribe({
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

  getCategoriaPorId(id: number): Categoria | undefined {
    return this.categorias?.find(c => c.id_categoria === id);
  }
  // Métodos para manejar categorías seleccionadas
  toggleCategoria(categoriaId: number): void {
    const categoriasControl = this.videojuegoForm.get('categorias_ids');
    if (!categoriasControl) return;

    const currentCategorias: number[] = categoriasControl.value || [];
    
    if (currentCategorias.includes(categoriaId)) {
      // Remover si ya está seleccionada
      const index = currentCategorias.indexOf(categoriaId);
      currentCategorias.splice(index, 1);
    } else {
      // Agregar si no está seleccionada
      currentCategorias.push(categoriaId);
    }
    
    categoriasControl.setValue([...currentCategorias]);
  }

  isCategoriaSelected(categoriaId: number): boolean {
    const categorias: number[] = this.videojuegoForm.get('categorias_ids')?.value || [];
    return categorias.includes(categoriaId);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    
    if (!input.files || input.files.length === 0) return;
    
    const archivos = Array.from(input.files);
    
    if (this.archivosSeleccionados.length + archivos.length > this.maxImagenes) {
      this.errorMessage = `Solo puedes subir un máximo de ${this.maxImagenes} imágenes.`;
      return;
    }
    
    archivos.forEach(file => {
      if (!file.type.match('image.*')) {
        this.errorMessage = 'Solo se permiten archivos de imagen.';
        return;
      }
      
      // Validar tamaño (ej: 5MB máximo)
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = 'La imagen no debe superar los 5MB.';
        return;
      }
      
      this.archivosSeleccionados.push(file);
      this.convertirABase64(file);
    });
    
    input.value = '';
  }

  convertirABase64(file: File): void {
    const reader = new FileReader();
    
    reader.onload = (e: ProgressEvent<FileReader>) => {
      if (e.target?.result) {
        // Extraer solo la parte base64 (sin el prefijo data:image/...)
        const base64String = e.target.result as string;
        const base64Data = base64String.split(',')[1]; // Obtener solo los datos base64
        this.imagenesBase64.push(base64Data);
      }
    };
    
    reader.readAsDataURL(file);
  }

  eliminarImagen(index: number): void {
    this.archivosSeleccionados.splice(index, 1);
    this.imagenesBase64.splice(index, 1);
  }

  getFileName(index: number): string {
    if (this.archivosSeleccionados[index]) {
      return this.archivosSeleccionados[index].name;
    }
    return `Imagen ${index + 1}`;
  }

  // Envío del formulario
  onSubmit(): void {
    if (this.videojuegoForm.invalid || !this.empresaId) {
      this.marcarCamposComoSucios();
      return;
    }
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const videojuegoData = {
      ...this.videojuegoForm.value,
      id_empresa: this.empresaId,
      precio: Number(this.videojuegoForm.value.precio)
    };
    
    console.log('Datos del videojuego a enviar:', videojuegoData);
    this.videojuegoService.crearVideojuego(videojuegoData).subscribe({
      next: (videojuegoCreado) => {
        console.log('Videojuego creado:', videojuegoCreado);
        
        //Si hay imágenes, subirlas
        if (this.imagenesBase64.length > 0) {
          this.subirImagenes(videojuegoCreado.id_videojuego);
        } else {
          // Si no hay imágenes, redirigir
          this.successMessage = 'Videojuego creado exitosamente.';
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Error al crear videojuego:', error);
        this.errorMessage = error.message || 'Error al crear el videojuego.';
        this.isLoading = false;
      }
    });
  }

  private subirImagenes(idVideojuego: number): void {
    const requests = this.imagenesBase64.map(imagenBase64 => ({
      id_videojuego: idVideojuego,
      imagenBase64: imagenBase64
    }));
    
    this.multimediaService.crearMultiplesImagenes(idVideojuego, requests).subscribe({
      next: (response) => {
        console.log('Imágenes subidas:', response);
        this.successMessage = 'Videojuego creado con imágenes exitosamente.';
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al subir imágenes:', error);
        // Aún así mostrar éxito con el videojuego creado
        this.successMessage = 'Videojuego creado, pero hubo un error al subir algunas imágenes.';
        this.isLoading = false;
        
        setTimeout(() => {
          this.router.navigate(['/empresa/videojuegos']);
        }, 3000);
      }
    });
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
    this.router.navigate(['/empresas/videojuegos']);
  }
}
