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

import ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "estadoProductoController")
@SessionScoped

public class EstadoProductoController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/
	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/EstadoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO")
	private EstadoProductoDAO estadoProductoI;

	/****************************************************************************/

	// Variables de la clase
	private List<Estadoproducto> estadoProductos = new ArrayList<>();
	private Estadoproducto nuevoEstadoProducto;
	private Estadoproducto estadoproducto;
	private String nombreTP;
	private List<Estadoproducto> filtrarEstados;
	private List<Estadoproducto> selectEP;
	private Utilidades utilidades;
	// Método init
	@PostConstruct
	public void init() {
		try {

			estadoProductos = estadoProductoI.getAll(Estadoproducto.class);
			setNuevoEstadoProducto(new Estadoproducto());
			estadoproducto = new Estadoproducto();
			utilidades = new Utilidades();

		} catch (Exception e) {

		}

	}

	/****** Nuevo ****/

	public void agregarEstadoProducto() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (buscarEstadoProducto(nuevoEstadoProducto.getNombreEstp()) == true) {

				utilidades.mensajeError("El Estado Producto (" + nuevoEstadoProducto.getNombreEstp() + ") ya existe.");

			} else {
				estadoProductoI.save(nuevoEstadoProducto);
				estadoProductos = estadoProductoI.getAll(Estadoproducto.class);

				utilidades.mensajeInfo("El Estado producto (" + nuevoEstadoProducto.getNombreEstp()
						+ ") se ha almacenado exitosamente.");

				nuevoEstadoProducto = new Estadoproducto();

				context.execute("PF('nuevoEstadoPro').hide();");
			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un problema.");
	
		}

	}

	/****** Modificar ****/

	public void modificarEstadoProducto() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (estadoproducto.getNombreEstp().equals(getNombreTP())) {
				estadoProductoI.update(estadoproducto);
				estadoProductos = estadoProductoI.getAll(Estadoproducto.class);

				utilidades.mensajeInfo(
						"El Estado Producto (" + estadoproducto.getNombreEstp() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarEstadoPro').hide();");

			} else if (buscarEstadoProducto(estadoproducto.getNombreEstp()) == false) {
				estadoProductoI.update(estadoproducto);
				estadoProductos = estadoProductoI.getAll(Estadoproducto.class);

				utilidades.mensajeInfo(
						"El Estado Producto (" + estadoproducto.getNombreEstp() + ") se ha actualizado exitosamente.");
				context.execute("PF('modificarEstadoPro').hide();");
			} else {
				estadoProductos = estadoProductoI.getAll(Estadoproducto.class);
				utilidades.mensajeError("El Estado Producto (" + estadoproducto.getNombreEstp() + ") ya existe.");
			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un problema");

		}
	}

	/****** Eliminar ****/

	public void eliminarEstadoProducto() {

		try {

			estadoProductoI.delete(estadoproducto);
			estadoProductos = estadoProductoI.getAll(Estadoproducto.class);
			utilidades.mensajeInfo("El Estado Producto (" + estadoproducto.getNombreEstp() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla Estado Producto (" + estadoproducto.getNombreEstp()
						+ ") tiene relación con otra tabla.");

			} else {

				utilidades.mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarEstadoProducto(String valor) {

		try {
			estadoProductos = estadoProductoI.getAll(Estadoproducto.class);
		} catch (Exception e) {

		}

		boolean resultado = false;
		for (Estadoproducto tipo : estadoProductos) {
			if (tipo.getNombreEstp().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreTP(nombre);
	}

	/****** Getter y Setter de Estado Producto ****/

	public Estadoproducto getNuevoEstadoProducto() {
		return nuevoEstadoProducto;
	}

	public void setNuevoEstadoProducto(Estadoproducto nuevoEstadoProducto) {
		this.nuevoEstadoProducto = nuevoEstadoProducto;
	}

	public List<Estadoproducto> getEstadoProductos() {
		return estadoProductos;
	}

	public void setEstadoProductos(List<Estadoproducto> estadoProductos) {
		this.estadoProductos = estadoProductos;
	}

	public Estadoproducto getEstadoproducto() {
		return estadoproducto;
	}

	public void setEstadoproducto(Estadoproducto estadoproducto) {
		this.estadoproducto = estadoproducto;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public List<Estadoproducto> getFiltrarEstados() {
		return filtrarEstados;
	}

	public void setFiltrarEstados(List<Estadoproducto> filtrarEstados) {
		this.filtrarEstados = filtrarEstados;
	}

	public List<Estadoproducto> getSelectEP() {
		return selectEP;
	}

	public void setSelectEP(List<Estadoproducto> selectEP) {
		this.selectEP = selectEP;
	}

}