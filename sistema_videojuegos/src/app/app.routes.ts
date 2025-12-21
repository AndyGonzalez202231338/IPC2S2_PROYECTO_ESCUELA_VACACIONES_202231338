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

        // Ruta comodín
    { 
        path: '**', 
        redirectTo: 'login' 
    }
];
