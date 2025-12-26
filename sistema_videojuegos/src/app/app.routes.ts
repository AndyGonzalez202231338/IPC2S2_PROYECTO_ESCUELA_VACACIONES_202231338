import { Routes } from '@angular/router';
import { LoginForm } from '../components/login/login-form/login-form';
import { Home } from '../components/Home/home/home';
import { CreateAccountComponent } from '../components/login/create-account-form/create-account-form';
import { CreateEmpresaComponent } from '../components/Empresa/create-empresa-component/create-empresa-component';
import { GetAllEmpresasComponent } from '../components/Empresa/get-all-empresas-component/get-all-empresas-component';
import { ComisionesEmpresaComponent } from '../components/Empresa/comisiones-empresa-component/comisiones-empresa-component';
import { EditEmpresaComponent } from '../components/Empresa/edit-empresa-component/edit-empresa-component';
import { ListarConfiguracionesComponent } from '../components/Configuracion/listar-configuraciones-component/listar-configuraciones-component';
import { EditarConfiguracionComponent } from '../components/Configuracion/editar-configuracion-component/editar-configuracion-component';
import { ListarCategoriasComponent } from '../components/Videojuego/listar-categorias-component/listar-categorias-component';
import { FormCategoriaComponent } from '../components/Videojuego/form-categoria-component/form-categoria-component';
import { CrearUsuarioEmpresaComponent } from '../components/Empresa/crear-usuario-empresa-component/crear-usuario-empresa-component';
import { VideojuegosEmpresaComponent } from '../components/Videojuego/videojuegos-empresa-component/videojuegos-empresa-component';
import { CrearVideojuegoComponent } from '../components/Videojuego/crear-videojuego-component/crear-videojuego-component';
import { EditarVideojuegoComponent } from '../components/Videojuego/editar-videojuego-component/editar-videojuego-component';
import { CarteraUsuarioComponent } from '../components/Usuario/cartera-usuario-component/cartera-usuario-component';
import { ComprarVideojuegoComponent } from '../components/Videojuego/comprar-videojuego-component/comprar-videojuego-component';
import { DetalleVideojuegoComponent } from '../components/Videojuego/detalle-videojuego-component/detalle-videojuego-component';
import { TiendaVideojuegosComponent } from '../components/Videojuego/tienda-videojuegos-component/tienda-videojuegos-component';

export const routes: Routes = [

    { 
        path: '', 
        redirectTo: 'login', 
        pathMatch: 'full' 
    },
    { 
        path: 'login', 
        component: LoginForm 
    },

    { 
        path: 'home', 
        component: Home 
    },
    { 
        path: 'create-account', 
        component: CreateAccountComponent
    },
    { 
        path: 'create-account-form', 
        redirectTo: 'create-account' 
    },
    { 
        path: 'empresas', 
        component: GetAllEmpresasComponent
    },
    { 
        path: 'empresas/crear', 
        component: CreateEmpresaComponent
    },
    { 
    path: 'empresas/:id/comisiones', 
    component: ComisionesEmpresaComponent 
    },
    { 
    path: 'empresas/editar/:id', 
    component: EditEmpresaComponent 
    },

      { 
    path: 'sistema/configuraciones', 
    component: ListarConfiguracionesComponent 
  },
  { 
    path: 'sistema/configuraciones/editar/:id', 
    component: EditarConfiguracionComponent 
  },
  { 
    path: 'categorias', 
    component: ListarCategoriasComponent 
  },
  { 
    path: 'categorias/crear', 
    component: FormCategoriaComponent 
  },
  { 
    path: 'categorias/editar/:id', 
    component: FormCategoriaComponent 
  },
  {
    path: 'empresas/crear-usuario',
    component: CrearUsuarioEmpresaComponent
  },
    {
    path: 'empresas/videojuegos',
    component: VideojuegosEmpresaComponent
  },
  {
    path: 'empresa/videojuegos/crear',
    component: CrearVideojuegoComponent
  },
  { 
    path: 'empresa/videojuegos/editar/:id', 
    component: EditarVideojuegoComponent 
  },
  //usuario header
  {
    path: 'usuario-cartera',
    component: CarteraUsuarioComponent
  },
  { 
    path: 'tienda', 
    component: TiendaVideojuegosComponent 
  },
  { 
    path: 'videojuego/comprar/:id', // Ruta alternativa
    component: ComprarVideojuegoComponent 
  },
  {
    path: 'videojuego/detalle/:id',
    component: DetalleVideojuegoComponent
  },


        // Ruta comodín
    { 
        path: '**', 
        redirectTo: 'login' 
    }
];
