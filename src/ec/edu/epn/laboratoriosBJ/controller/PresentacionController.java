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

import ec.edu.epn.laboratorioBJ.beans.PresentacionDAO;
import ec.edu.epn.laboratorioBJ.entities.Presentacion;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "presentacionController")
@SessionScoped
public class PresentacionController implements Serializable {

	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PresentacionDAOImplement!ec.edu.epn.laboratorioBJ.beans.PresentacionDAO")

	private PresentacionDAO presentacionI;
	private Presentacion presentacion;
	private List<Presentacion> Presentaciones = new ArrayList<>();
	private List<Presentacion> filtroPresentacion = new ArrayList<>();
	private Presentacion nuevoPresentacion;
	private String nombreTP;

	@PostConstruct
	public void init() {
		try {

			Presentaciones = presentacionI.getAll(Presentacion.class);
			presentacion = new Presentacion();
			nuevoPresentacion = new Presentacion();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "¡ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/******* Método Guardar *******/

	public void agregarPresentacion() {

		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (buscarPresentacion(nuevoPresentacion.getNombrePrs()) == true) {

				mensajeError("La presentación (" + nuevoPresentacion.getNombrePrs() + ") ya existe.");

			} else {
				presentacionI.save(nuevoPresentacion);

				Presentaciones = presentacionI.getAll(Presentacion.class);

				mensajeInfo("La Presentación (" + nuevoPresentacion.getNombrePrs() + ") se ha almacenado exitosamente");

				nuevoPresentacion = new Presentacion();

				context.execute("PF('nuevaPresentacion').hide();");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

		}

	}

	/******* Método Modificar *******/

	public void modificarPresentacion(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (presentacion.getNombrePrs().equals(getNombreTP())) {
				presentacionI.update(presentacion);
				Presentaciones = presentacionI.getAll(Presentacion.class);

				mensajeInfo("La Presentación (" + presentacion.getNombrePrs() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarPresentacion').hide();");

			} else if (buscarPresentacion(presentacion.getNombrePrs()) == false) {

				presentacionI.update(presentacion);
				Presentaciones = presentacionI.getAll(Presentacion.class);

				mensajeInfo("La Presentación (" + presentacion.getNombrePrs() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarPresentacion').hide();");

			} else {
				Presentaciones = presentacionI.getAll(Presentacion.class);
				mensajeError("La Presentación (" + presentacion.getNombrePrs() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
		}
	}

	/******* Método Eliminar *******/

	public void eliminarPresentacion(ActionEvent event) {
		try {

			presentacionI.delete(presentacion);
			Presentaciones = presentacionI.getAll(Presentacion.class);

			mensajeInfo("La Presentación (" + presentacion.getNombrePrs() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La Presentación (" + presentacion.getNombrePrs() + ") tiene relación con otra tabla.");

			} else {

				mensajeError("Ha ocurrido un error");
			}

		}

	}

	/******* Validación *******/

	private boolean buscarPresentacion(String valor) {

		try {
			Presentaciones = presentacionI.getAll(Presentacion.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Presentacion tipo : Presentaciones) {
			if (tipo.getNombrePrs().equals(valor)) {
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

	/******* GET and SET *******/
	
	public Presentacion getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}

	public List<Presentacion> getPresentaciones() {
		return Presentaciones;
	}

	public void setPresentaciones(List<Presentacion> presentaciones) {
		Presentaciones = presentaciones;
	}

	public List<Presentacion> getFiltroPresentacion() {
		return filtroPresentacion;
	}

	public void setFiltroPresentacion(List<Presentacion> filtroPresentacion) {
		this.filtroPresentacion = filtroPresentacion;
	}

	public Presentacion getNuevoPresentacion() {
		return nuevoPresentacion;
	}

	public void setNuevoPresentacion(Presentacion nuevoPresentacion) {
		this.nuevoPresentacion = nuevoPresentacion;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

}
