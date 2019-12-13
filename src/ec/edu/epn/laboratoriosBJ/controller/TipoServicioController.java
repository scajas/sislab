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
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO;
import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoServicioController")
@SessionScoped
public class TipoServicioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO")
	private TipoServicioDAO tipoServicioI;

	// variable de la clase
	private Tiposervicio tipoServicio;
	private List<Tiposervicio> tipoServicios = new ArrayList<>();
	private Tiposervicio nuevoTipoServicio;

	private String nombreTS;

	private List<Tiposervicio> filtroTipoServicio = new ArrayList<>();

	// Mpetodo Init
	@PostConstruct
	public void init() {
		try {

			tipoServicios = tipoServicioI.getListTS();
			tipoServicio = new Tiposervicio();
			nuevoTipoServicio = new Tiposervicio();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	/******* Método Guardar *******/

	public void agregarTipoServicio() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarTipoServicio(nuevoTipoServicio.getNombreTs()) == true) {
				mensajeError("El Tipo de Servicio (" + nuevoTipoServicio.getNombreTs() + ") ya existe.");
				nuevoTipoServicio = new Tiposervicio();

			} else {

				tipoServicioI.save(nuevoTipoServicio);
				tipoServicios = tipoServicioI.getListTS();

				mensajeInfo(
						"El Tipo Servicio  (" + nuevoTipoServicio.getNombreTs() + ") se ha almacenado exitosamente");

				nuevoTipoServicio = new Tiposervicio();

				context.execute("PF('nuevoTipoServicio').hide();");

			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");

		}

	}

	/******* Método Modificar *******/

	public void modificarTipoServicio() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (tipoServicio.getNombreTs().equals(getNombreTS())) {
				tipoServicioI.update(tipoServicio);
				tipoServicios = tipoServicioI.getListTS();

				context.execute("PF('modificarTipoServicio').hide();");

				mensajeInfo("El Tipo Servicio (" + tipoServicio.getNombreTs() + ") se ha actualizado exitosamente");

			} else if (buscarTipoServicio(tipoServicio.getNombreTs()) == false) {

				tipoServicioI.update(tipoServicio);
				tipoServicios = tipoServicioI.getListTS();

				mensajeInfo("El Tipo Servicio (" + tipoServicio.getNombreTs() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarTipoServicio').hide();");

			} else {
				tipoServicios = tipoServicioI.getListTS();
				mensajeError("El Tipo Servicio (" + tipoServicio.getNombreTs() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	/******* Método Eliminar *******/

	public void eliminarTipoServicio(ActionEvent event) {
		try {

			tipoServicioI.delete(tipoServicio);
			tipoServicios = tipoServicioI.getListTS();

			mensajeInfo("El Tipo Servicio (" + tipoServicio.getNombreTs() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError(
						"La tabla Tipo Servicio (" + tipoServicio.getNombreTs() + ") tiene relación con otra tabla");
			} else {

				mensajeError("Ha ocurrido un error");
			}

		}

	}

	/******* Validación *******/

	private boolean buscarTipoServicio(String valor) {

		try {
			tipoServicios = tipoServicioI.getAll(Tiposervicio.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Tiposervicio tipo : tipoServicios) {
			if (tipo.getNombreTs().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreTS(nombre);
	}

	/******* GET and SET *******/

	public List<Tiposervicio> getFiltroTipoServicio() {
		return filtroTipoServicio;
	}

	public Tiposervicio getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(Tiposervicio tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public List<Tiposervicio> getTipoServicios() {
		return tipoServicios;
	}

	public void setTipoServicios(List<Tiposervicio> tipoServicios) {
		this.tipoServicios = tipoServicios;
	}

	public void setFiltroTipoServicio(List<Tiposervicio> filtroTipoServicio) {
		this.filtroTipoServicio = filtroTipoServicio;
	}

	public Tiposervicio getNuevoTipoServicio() {
		return nuevoTipoServicio;
	}

	public void setNuevoTipoServicio(Tiposervicio nuevoTipoServicio) {
		this.nuevoTipoServicio = nuevoTipoServicio;
	}

	public String getNombreTS() {
		return nombreTS;
	}

	public void setNombreTS(String nombreTS) {
		this.nombreTS = nombreTS;
	}

}
