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

import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "unidadLaboController")
@SessionScoped

public class UnidadLabController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;// I (interface)

	private UnidadLabo unidad;
	private List<UnidadLabo> unidades = new ArrayList<UnidadLabo>();
	private UnidadLabo nuevoUnidad;
	private String nombreTP;
	private List<UnidadLabo> filtroUnidad;
	private Utilidades utilidades;
	
	@PostConstruct
	public void init() {
		try {

			unidades = unidadI.getAll(UnidadLabo.class);
			nuevoUnidad = new UnidadLabo();
			unidad = new UnidadLabo();
			utilidades = new Utilidades();

		} catch (Exception e) {
			
		}
	}

	// ************Agregar Unidad************//

	public void nuevoUnidad() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarUnidad(nuevoUnidad.getNombreU()) == true) {

				utilidades.mensajeError("La Unidad (" + nuevoUnidad.getNombreU() + ") ya existe.");
		
			} else {

				unidadI.save(nuevoUnidad);
				utilidades.mensajeInfo("La Unidad (" + nuevoUnidad.getNombreU() + ") se ha almacenado correctamente.");
				nuevoUnidad = new UnidadLabo();
				unidades = unidadI.getAll(UnidadLabo.class);
				context.execute("PF('nuevoUnidad').hide();");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");
		}

	}

	// ****** Modificar Unidad********//
	public void modificarUnidad() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (unidad.getNombreU().equals(getNombreTP())) {

				unidadI.update(unidad);
				unidades = unidadI.getAll(UnidadLabo.class);
				utilidades.mensajeInfo("La Unidad (" + unidad.getNombreU() + ") se ha actualizado exitosamente.");
				context.execute("PF('modificarUnidad')");

			} else if (buscarUnidad(unidad.getNombreU()) == false) {

				unidadI.update(unidad);
				unidades = unidadI.getAll(UnidadLabo.class);
				utilidades.mensajeInfo("La Unidad (" + unidad.getNombreU() + ") se ha actualizado exitosamente.");
				context.execute("PF('modificarUnidad')");

			} else {

				unidades = unidadI.getAll(UnidadLabo.class);
				utilidades.mensajeError("La Unidad (" + unidad.getNombreU() + ") ya existe.");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");
		}
	}

	// ****** Eliminar Unidad ****//*

	public void eliminarUnidad() {

		try {

			unidadI.delete(unidad);
			unidades = unidadI.getAll(UnidadLabo.class);
			utilidades.mensajeInfo("La Unidad (" + unidad.getNombreU() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				utilidades.mensajeError("La tabla Unidad (" + unidad.getNombreU() + ") tiene relación con otra tabla.");
			} else {
				utilidades.mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	// ****** Busqueda de Unidad ****//*

	private boolean buscarUnidad(String valor) {
		try {
			unidades = unidadI.getAll(UnidadLabo.class);
		} catch (Exception e) {
		
		}

		boolean resultado = false;
		for (UnidadLabo uni : unidades) {
			if (uni.getNombreU().equals(valor)) {
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

	public UnidadLabo getNuevoUnidad() {
		return nuevoUnidad;
	}

	public void setNuevoUnidad(UnidadLabo nuevoUnidad) {
		this.nuevoUnidad = nuevoUnidad;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public List<UnidadLabo> getFiltroUnidad() {
		return filtroUnidad;
	}

	public void setFiltroUnidad(List<UnidadLabo> filtroUnidad) {
		this.filtroUnidad = filtroUnidad;
	}

	public List<UnidadLabo> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<UnidadLabo> unidades) {
		this.unidades = unidades;
	}

}
