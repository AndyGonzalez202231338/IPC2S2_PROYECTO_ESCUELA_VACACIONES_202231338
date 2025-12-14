package empresa.dtos;

import empresa.models.Empresa;
import user.dtos.UserResponse;
import user.services.UsersCrudService;

public class EmpresaResponse {
    private int id_empresa;
    private String nombre;
    private String descripcion;
    private UserResponse administrador;

    public EmpresaResponse(Empresa empresa) {
        this.id_empresa = empresa.getId_empresa();
        this.nombre = empresa.getNombre();
        this.descripcion = empresa.getDescripcion();
    }
    
    public EmpresaResponse(Empresa empresa, UserResponse administrador) {
        this.id_empresa = empresa.getId_empresa();
        this.nombre = empresa.getNombre();
        this.descripcion = empresa.getDescripcion();
        this.administrador = administrador;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UserResponse getAdministrador() {
        return administrador;
    }

    public void setAdministrador(UserResponse administrador) {
        this.administrador = administrador;
    }

    
}