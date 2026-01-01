import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree 
} from '@angular/router';
import { Observable } from 'rxjs';
import { LoginService } from './login.services';


@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private loginService: LoginService,
    private router: Router
  ) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    
    const currentUser = this.loginService.getCurrentUser();
    
    if (!currentUser) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: state.url } 
      });
      return false;
    }
    const requiredRoles = next.data['roles'] as Array<string>;
    
    if (requiredRoles && requiredRoles.length > 0) {
      const hasRole = requiredRoles.includes(currentUser.rol.nombre);
      
      if (!hasRole) {
        // Redirigir a home o página de no autorizado
        this.router.navigate(['/home']);
        return false;
      }
    }

    return true;
  }
}