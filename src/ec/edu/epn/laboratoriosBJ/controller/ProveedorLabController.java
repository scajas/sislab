package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO;
import ec.edu.epn.laboratorioBJ.entities.ProveedorLab;
import ec.edu.epn.laboratorioBJ.entities.Tipoproveedor;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "proveedorController")
@SessionScoped

public class ProveedorLabController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProveedorLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO")
	private ProveedorLabDAO proveedorI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProveedorDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO")
	private TipoProveedorDAO tipoProveedorI;// I (interface)

	/** VARIABLES **/
	private ProveedorLab proveedor;
	private List<ProveedorLab> proveedores = new ArrayList<>();
	private ProveedorLab nuevoProveedor;
	private String nombreP;

	private List<Tipoproveedor> tipoProveedores = new ArrayList<>();
	private Tipoproveedor tipoProveedor;
	private Tipoproveedor tipoProveedorSelect;

	private List<ProveedorLab> filtroProveedor;
	private Utilidades utilidades;

	@PostConstruct
	public void init() {
		try {

			proveedores = proveedorI.getListProveedor();
			nuevoProveedor = new ProveedorLab();
			proveedor = new ProveedorLab();

			tipoProveedores = tipoProveedorI.getAll(Tipoproveedor.class);
			tipoProveedor = new Tipoproveedor();
			utilidades = new Utilidades();
		
		} catch (Exception e) {
		
		}
	}

	/****** Agregar Proveedor ****/

	public void agregarProveedor() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarProveedor(nuevoProveedor.getNombrePv()) == true) {

				utilidades.mensajeError("El Proveedor (" + nuevoProveedor.getNombrePv() + ") ya existe.");

			} else {

				nuevoProveedor.setTipoproveedor(tipoProveedorSelect);
				proveedorI.save(nuevoProveedor);

				proveedores = proveedorI.getListProveedor();

				utilidades.mensajeInfo("El Proveedor (" + nuevoProveedor.getNombrePv() + ") se ha almacenado exitosamente");

				nuevoProveedor = new ProveedorLab();
				tipoProveedorSelect = new Tipoproveedor();

				context.execute("PF('nuevoProveedor').hide();");

			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un problema");
		
		}
	}

	// ****** Modificar Proveedor ****//*

	public void modificarProveedor() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (proveedor.getNombrePv().equals(getNombreP())) {

				proveedorI.update(proveedor);
				proveedores = proveedorI.getListProveedor();

				utilidades.mensajeInfo("El Proveedor (" + proveedor.getNombrePv() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarProveedor').hide();");

			} else if (buscarProveedor(proveedor.getNombrePv()) == false) {

				proveedorI.update(proveedor);
				proveedores = proveedorI.getListProveedor();

				utilidades.mensajeInfo("El Proveedor (" + proveedor.getNombrePv() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarProveedor').hide();");

			} else {
				proveedores = proveedorI.getListProveedor();
				utilidades.mensajeError("El Proveedor (" + proveedor.getNombrePv() + ") ya existe.");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		
		}
	}

	// ****** Eliminar Proveedor ****//*

	public void eliminarProveedorLab() {

		try {

			proveedorI.delete(proveedor);
			proveedores = proveedorI.getListProveedor();
			utilidades.mensajeInfo("El Proveedor (" + proveedor.getNombrePv() + ") se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				utilidades.mensajeError("La tabla Proveedor (" + proveedor.getNombrePv() + ") tiene relación con otra tabla");
			} else {
				utilidades.mensajeError("Ha ocurrido un problema");
			}

		}

	}

	// ****** Busqueda Proveedor ****//*

	private boolean buscarProveedor(String valor) {

		try {

			proveedores = proveedorI.getListProveedor();

		} catch (Exception e) {

		}

		boolean resultado = false;

		for (ProveedorLab pro : proveedores) {
			if (pro.getNombrePv().equals(valor)) {

				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreP(nombre);
	}

	// ****** Getter y Setters ****//*

	public ProveedorLab getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorLab proveedor) {
		this.proveedor = proveedor;
	}

	public ProveedorLab getNuevoProveedor() {
		return nuevoProveedor;
	}

	public void setNuevoProveedor(ProveedorLab nuevoProveedor) {
		this.nuevoProveedor = nuevoProveedor;
	}

	public String getNombreP() {
		return nombreP;
	}

	public void setNombreP(String nombreP) {
		this.nombreP = nombreP;
	}

	public List<Tipoproveedor> getTipoProveedores() {
		return tipoProveedores;
	}

	public void setTipoProveedores(List<Tipoproveedor> tipoProveedores) {
		this.tipoProveedores = tipoProveedores;
	}

	public Tipoproveedor getTipoProveedor() {
		return tipoProveedor;
	}

	public void setTipoProveedor(Tipoproveedor tipoProveedor) {
		this.tipoProveedor = tipoProveedor;
	}

	public List<ProveedorLab> getFiltroProveedor() {
		return filtroProveedor;
	}

	public void setFiltroProveedor(List<ProveedorLab> filtroProveedor) {
		this.filtroProveedor = filtroProveedor;
	}

	public List<ProveedorLab> getProveedores() {
		return proveedores;
	}

	public void setProveedores(List<ProveedorLab> proveedores) {
		this.proveedores = proveedores;
	}

	public Tipoproveedor getTipoProveedorSelect() {
		return tipoProveedorSelect;
	}

	public void setTipoProveedorSelect(Tipoproveedor tipoProveedorSelect) {
		this.tipoProveedorSelect = tipoProveedorSelect;
	}

}
