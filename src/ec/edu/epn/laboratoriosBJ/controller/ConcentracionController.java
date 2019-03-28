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

import ec.edu.epn.laboratorioBJ.beans.ConcentracionDAO;
import ec.edu.epn.laboratorioBJ.entities.Concentracion;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
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

	// Variables de la clase
	private List<Concentracion> listaConcentracion = new ArrayList<>();
	private Concentracion nuevaConcentracion;
	private Concentracion concentracion;
	private List<Concentracion> filtroConcentracion;
	private String nombreTP;
	// private List<Concentracion> concentracionFiltro = new ArrayList<>();

	/****** Agregar Estado Producto ****/

	public void agregarConcentracion(ActionEvent event) {

		try {
			if (buscarConcentracion(nuevaConcentracion.getNombreCon()) == true) {

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "La concentración ya existe."));

			} else {

				concentracionI.save(nuevaConcentracion);
				listaConcentracion = concentracionI.getAll(Concentracion.class);
				filtroConcentracion =concentracionI.getAll(Concentracion.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Concentración almacenado exitosamente"));

				nuevaConcentracion = new Concentracion();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}

	}

	/****** Modificar Estado Producto ****/

	public void modificarConcentracion(ActionEvent event) {
		try {
			if (concentracion.getNombreCon().equals(getNombreTP())) {
				concentracionI.update(concentracion);
				listaConcentracion= concentracionI.getAll(Concentracion.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarConcentracion').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Concentración actualizado exitosamente"));

			} else if (buscarConcentracion(concentracion.getNombreCon()) == false) {
				concentracionI.update(concentracion);
				listaConcentracion= concentracionI.getAll(Concentracion.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarConcentracion').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Concentración actualizado exitosamente"));
			} else {
				listaConcentracion= concentracionI.getAll(Concentracion.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La concentración ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}


	/****** Eliminar Estado Producto ****/

	public void eliminarConcentracion(ActionEvent event) {

		try {

			concentracionI.delete(concentracion);
			listaConcentracion = concentracionI.getAll(Concentracion.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "La concentración se ha eliminado correctamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Concentración tiene relación con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}

	}
	/****** Pasar Nombre ****/

	public void pasarNombre(String nombre) {
		setNombreTP(nombre);
	}

	/****** Busqueda de Estado Producto ****/

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
	

	/****** Getter y Setter de Estado Producto ****/
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

	public List<Concentracion> getFiltroConcentracion() {
		return filtroConcentracion;
	}

	public void setFiltroConcentracion(List<Concentracion> filtroConcentracion) {
		this.filtroConcentracion = filtroConcentracion;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

}