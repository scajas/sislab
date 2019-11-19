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

import ec.edu.epn.laboratorioBJ.beans.HidratacionDAO;
import ec.edu.epn.laboratorioBJ.entities.Hidratacion;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "hidratacionController")
@SessionScoped

public class HidratacionController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/HidratacionDAOImplement!ec.edu.epn.laboratorioBJ.beans.HidratacionDAO")
	private HidratacionDAO hidratacionI;// I (interface)

	private Hidratacion hidratacion;
	private List<Hidratacion> listaHidratacion = new ArrayList<Hidratacion>();
	private Hidratacion nuevaHidratacion;
	private String nombreH;
	private List<Hidratacion> filtrarHidratacion;

	@PostConstruct
	public void init() {
		try {

			setListaHidratacion(hidratacionI.getAll(Hidratacion.class));
			setNuevaHidratacion(new Hidratacion());
			hidratacion = new Hidratacion();
			nuevaHidratacion = new Hidratacion();

		} catch (Exception e) {
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

	/****** Agregar Nuevo ****/

	public void agregarHidratacion() {
		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarHidratacion(nuevaHidratacion.getNombreHi()) == true) {

				mensajeError("La Hidratación (" + nuevaHidratacion.getNombreHi() + ") ya existe.");

			} else {
				hidratacionI.save(nuevaHidratacion);
				listaHidratacion = hidratacionI.getAll(Hidratacion.class);

				mensajeInfo("La Hidratación (" + nuevaHidratacion.getNombreHi() + ") se ha almacenado exitosamente.");

				nuevaHidratacion = new Hidratacion();

				context.execute("PF('nuevaHidratacion').hide();");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema.");
		}

	}

	/****** Modificar ****/

	public void modificarHidratacion() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (hidratacion.getNombreHi().equals(getNombreH())) {
				hidratacionI.update(hidratacion);
				listaHidratacion = hidratacionI.getAll(Hidratacion.class);

				mensajeInfo("La Hidratación (" + hidratacion.getNombreHi() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarHidratacion').hide();");

			} else if (buscarHidratacion(hidratacion.getNombreHi()) == false) {
				hidratacionI.update(hidratacion);
				listaHidratacion = hidratacionI.getAll(Hidratacion.class);

				mensajeInfo("La Hidratación (" + hidratacion.getNombreHi() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarHidratacion').hide();");
			} else {

				listaHidratacion = hidratacionI.getAll(Hidratacion.class);
				mensajeError("La Hidratación (" + hidratacion.getNombreHi() + ") ya existe.");

			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
		}
	}

	/****** Eliminar ****/

	public void eliminarHidratacion() {

		try {

			hidratacionI.delete(hidratacion);
			listaHidratacion = hidratacionI.getAll(Hidratacion.class);

			mensajeInfo("La Hidratación (" + hidratacion.getNombreHi() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Hidratación (" + hidratacion.getNombreHi() + ") tiene relación con otra tabla.");

			} else {

				mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	/****** Busqueda ****/

	private boolean buscarHidratacion(String valor) {
		try {
			listaHidratacion = hidratacionI.getAll(Hidratacion.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Hidratacion tipo : listaHidratacion) {
			if (tipo.getNombreHi().trim().equals(valor.trim())) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreH(nombre);
	}

	public List<Hidratacion> getListaHidratacion() {
		return listaHidratacion;
	}

	public void setListaHidratacion(List<Hidratacion> listaHidratacion) {
		this.listaHidratacion = listaHidratacion;
	}

	public Hidratacion getHidratacion() {
		return hidratacion;
	}

	public void setHidratacion(Hidratacion hidratacion) {
		this.hidratacion = hidratacion;
	}

	public String getNombreH() {
		return nombreH;
	}

	public void setNombreH(String nombreH) {
		this.nombreH = nombreH;
	}

	public List<Hidratacion> getFiltrarHidratacion() {
		return filtrarHidratacion;
	}

	public void setFiltrarHidratacion(List<Hidratacion> filtrarHidratacion) {
		this.filtrarHidratacion = filtrarHidratacion;
	}

	public Hidratacion getNuevaHidratacion() {
		return nuevaHidratacion;
	}

	public void setNuevaHidratacion(Hidratacion nuevaHidratacion) {
		this.nuevaHidratacion = nuevaHidratacion;
	}

}