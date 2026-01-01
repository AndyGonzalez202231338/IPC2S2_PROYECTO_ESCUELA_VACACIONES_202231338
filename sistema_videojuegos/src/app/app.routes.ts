import { Routes } from '@angular/router';
import { AuthGuard } from '../services/Login/auth.guard';
import { RoleGuard } from '../services/Login/role.guard';
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
import { GruposComponent } from '../components/Grupo/grupos-component/grupos-component';
import { BibliotecaComponent } from '../components/Usuario/biblioteca-component/biblioteca-component';
import { ReportesSistemaComponent } from '../components/Reporte/reportes-sistema-component/reportes-sistema-component';
import { ReportesEmpresaComponent } from '../components/Reporte/reportes-empresa-component/reportes-empresa-component';
import { ReportesUsuarioComponent } from '../components/Reporte/reportes-usuario-component/reportes-usuario-component';

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
        path: 'create-account', 
        component: CreateAccountComponent
    },
    { 
        path: 'create-account-form', 
        redirectTo: 'create-account' 
    },

    // Rutas que requieren autenticación básica
    { 
        path: 'home', 
        component: Home,
        canActivate: [AuthGuard]
    },

    // Rutas exclusivas para ADMINISTRADOR DE SISTEMA
    { 
        path: 'empresas', 
        component: GetAllEmpresasComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'empresas/crear', 
        component: CreateEmpresaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'empresas/:id/comisiones', 
        component: ComisionesEmpresaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'empresas/editar/:id', 
        component: EditEmpresaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'sistema/configuraciones', 
        component: ListarConfiguracionesComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'sistema/configuraciones/editar/:id', 
        component: EditarConfiguracionComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'categorias', 
        component: ListarCategoriasComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'categorias/crear', 
        component: FormCategoriaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    { 
        path: 'categorias/editar/:id', 
        component: FormCategoriaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },
    {
        path: 'reportes-sistema',
        component: ReportesSistemaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE SISTEMA'] }
    },

    // Rutas para ADMINISTRADOR DE EMPRESA
    {
        path: 'empresas/crear-usuario',
        component: CrearUsuarioEmpresaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE EMPRESA'] }
    },
    {
        path: 'empresas/videojuegos',
        component: VideojuegosEmpresaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE EMPRESA'] }
    },
    {
        path: 'empresa/videojuegos/crear',
        component: CrearVideojuegoComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE EMPRESA'] }
    },
    { 
        path: 'empresa/videojuegos/editar/:id', 
        component: EditarVideojuegoComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE EMPRESA'] }
    },
    {
        path: 'reportes-empresa',
        component: ReportesEmpresaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRADOR DE EMPRESA'] }
    },

    // Rutas para USUARIO COMUN
    {
        path: 'usuario-cartera',
        component: CarteraUsuarioComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },
    { 
        path: 'tienda', 
        component: TiendaVideojuegosComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },
    { 
        path: 'videojuego/comprar/:id',
        component: ComprarVideojuegoComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },
    {
        path: 'videojuego/detalle/:id',
        component: DetalleVideojuegoComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },
    {
        path: 'grupos',
        component: GruposComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },
    {
        path: 'biblioteca',
        component: BibliotecaComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },
    {
        path: 'reportes-usuario',
        component: ReportesUsuarioComponent,
        canActivate: [RoleGuard],
        data: { roles: ['COMUN'] }
    },

    // Ruta comodín
    { 
        path: '**', 
        redirectTo: 'login' 
    }
];
