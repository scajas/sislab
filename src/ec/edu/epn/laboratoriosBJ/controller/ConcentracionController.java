package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.ConcentracionDAO;
import ec.edu.epn.laboratorioBJ.entities.Concentracion;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "concentracionController")
@SessionScoped

public class ConcentracionController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ConcentracionDAOImplement!ec.edu.epn.laboratorioBJ.beans.ConcentracionDAO")
	private ConcentracionDAO concentracionI;

	/****************************************************************************/

	// Variables de la clase
	private List<Concentracion> listaConcentracion = new ArrayList<>();
	private Concentracion nuevaConcentracion;
	private Concentracion concentracion;
	private String nombreConcentracion;
	private List<Concentracion> filtrarConcentraciones;

	// Método init
	@PostConstruct
	public void init() {
		try {
			setListaConcentracion(concentracionI.getAll(Concentracion.class));
			setNuevaConcentracion(new Concentracion());
			concentracion = new Concentracion();

		} catch (Exception e) {

		}

	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Agregar nueva Concentración ****/

	public void agregarConcentracion() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarConcentracion(nuevaConcentracion.getNombreCon()) == true) {

				mensajeError("La concentración (" + nuevaConcentracion.getNombreCon() + ") ya existe.");

				nuevaConcentracion = new Concentracion();
			} else {

				concentracionI.save(nuevaConcentracion);
				listaConcentracion = concentracionI.getAll(Concentracion.class);

				mensajeInfo("La concentración (" + nuevaConcentracion.getNombreCon()
						+ ") se ha almacenado exitosamente.");

				nuevaConcentracion = new Concentracion();

				context.execute("PF('nuevaConcentracion').hide();");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error.");
		}

	}

	/****** Modificar Concentración ****/

	public void modificarConcentracion() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (concentracion.getNombreCon().equals(getNombreConcentracion())) {
				concentracionI.update(concentracion);
				listaConcentracion = concentracionI.getAll(Concentracion.class);

				mensajeInfo("La Concentración (" + concentracion.getNombreCon() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarConcentracion').hide();");

			} else if (buscarConcentracion(concentracion.getNombreCon()) == false) {
				concentracionI.update(concentracion);
				listaConcentracion = concentracionI.getAll(Concentracion.class);

				mensajeInfo(
						"La Concentración (" + concentracion.getNombreCon() + "" + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarConcentracion').hide();");

			} else {
				listaConcentracion = concentracionI.getAll(Concentracion.class);
				mensajeError("La Concentración (" + concentracion.getNombreCon() + ") ya existe.");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema.");

		}
	}

	/****** Eliminar Concentración ****/

	public void eliminarConcentracion() {

		try {

			concentracionI.delete(concentracion);
			listaConcentracion = concentracionI.getAll(Concentracion.class);

			mensajeInfo("La concentración (" + concentracion.getNombreCon() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Concentración tiene relación con otra tabla.");

			} else {

				mensajeError("Ha ocurrido un error.");

			}

		}

	}

	/****** Busqueda de Concentración ****/

	private boolean buscarConcentracion(String valor) {

		try {
			listaConcentracion = concentracionI.getAll(Concentracion.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Concentracion tipo : listaConcentracion) {
			if (tipo.getNombreCon().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreConcentracion(nombre);
	}

	/****** Getter y Setter de Concentración ****/
	public List<Concentracion> getListaConcentracion() {
		return listaConcentracion;
	}

	public void setListaConcentracion(List<Concentracion> listaConcentracion) {
		this.listaConcentracion = listaConcentracion;
	}

	public Concentracion getConcentracion() {
		return concentracion;
	}

	public void setConcentracion(Concentracion concentracion) {
		this.concentracion = concentracion;
	}

	public Concentracion getNuevaConcentracion() {
		return nuevaConcentracion;
	}

	public void setNuevaConcentracion(Concentracion nuevaConcentracion) {
		this.nuevaConcentracion = nuevaConcentracion;
	}

	public String getNombreConcentracion() {
		return nombreConcentracion;
	}

	public void setNombreConcentracion(String nombreConcentracion) {
		this.nombreConcentracion = nombreConcentracion;
	}

	public List<Concentracion> getFiltrarConcentraciones() {
		return filtrarConcentraciones;
	}

	public void setFiltrarConcentraciones(List<Concentracion> filtrarConcentraciones) {
		this.filtrarConcentraciones = filtrarConcentraciones;
	}
}