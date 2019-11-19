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

import javax.faces.application.FacesMessage;

import ec.edu.epn.laboratorioBJ.beans.PosgiroDAO;
import ec.edu.epn.laboratorioBJ.entities.Posgiro;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "posgiroController")
@SessionScoped
public class PosgiroController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS DAOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PosgiroDAOImplement!ec.edu.epn.laboratorioBJ.beans.PosgiroDAO")
	private PosgiroDAO posgiroI;

	/** VARIABLES DE LA CLASE **/
	private Posgiro posgiro;
	private List<Posgiro> listarPosgiros = new ArrayList<>();
	private Posgiro nuevoPosgiro;
	private String nombreP;
	private List<Posgiro> filtrarPosgiros;

	/** METODO INIT **/
	@PostConstruct
	public void init() {
		try {
			listarPosgiros = posgiroI.getAll(Posgiro.class);
			nuevoPosgiro = new Posgiro();
			posgiro = new Posgiro();
		} catch (Exception e) {

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

	/** METODO CREAR POSGIRO **/
	public void agregarPosgiro() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarPosgiro(nuevoPosgiro.getNombrePg()) == true) {
				mensajeError("El Posgiro (" + nuevoPosgiro.getNombrePg() + ") ya existe.");

			} else {
				posgiroI.save(nuevoPosgiro);
				listarPosgiros = posgiroI.getAll(Posgiro.class);

				mensajeInfo("El Posgiro (" + nuevoPosgiro.getNombrePg() + ") se ha almacenado exitosamente");
				nuevoPosgiro = new Posgiro();
				context.execute("PF('nuevoPosgiro').hide();");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}

	}

	/****** Modificar ****/

	public void modificarPosgiro() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (posgiro.getNombrePg().equals(getNombreP())) {
				posgiroI.update(posgiro);
				listarPosgiros = posgiroI.getAll(Posgiro.class);

				mensajeInfo("El Posgiro (" + posgiro.getNombrePg() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarPosgiro').hide();");

			} else if (buscarPosgiro(posgiro.getNombrePg()) == false) {
				posgiroI.update(posgiro);
				listarPosgiros = posgiroI.getAll(Posgiro.class);

				mensajeInfo("El Posgiro (" + posgiro.getNombrePg() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarPosgiro').hide();");

			} else {
				listarPosgiros = posgiroI.getAll(Posgiro.class);
				mensajeError("El Posgiro (" + posgiro.getNombrePg() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	/****** Eliminar ****/

	public void eliminarPosgiro() {
		try {

			posgiroI.delete(posgiro);
			listarPosgiros = posgiroI.getAll(Posgiro.class);

			mensajeInfo("El Posgiro (" + posgiro.getNombrePg() + ") se ha eliminado exitosamente");
		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError("La tabla posgiro (" + posgiro.getNombrePg() + ") tiene una relacion con otra tabla");
			} else {
				mensajeError("Ha ocurrido un error");
			}

		}
	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarPosgiro(String valor) {

		try {
			listarPosgiros = posgiroI.getAll(Posgiro.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Posgiro tipo : listarPosgiros) {
			if (tipo.getNombrePg().equals(valor)) {
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

	/** GET AND SET POSGIRO **/

	public List<Posgiro> getListarPosgiros() {
		return listarPosgiros;
	}

	public void setListarPosgiros(List<Posgiro> listarPosgiros) {
		this.listarPosgiros = listarPosgiros;
	}

	public Posgiro getNuevoPosgiro() {
		return nuevoPosgiro;
	}

	public void setNuevoPosgiro(Posgiro nuevoPosgiro) {
		this.nuevoPosgiro = nuevoPosgiro;
	}

	public Posgiro getPosgiro() {
		return posgiro;
	}

	public void setPosgiro(Posgiro posgiro) {
		this.posgiro = posgiro;
	}

	public String getNombreP() {
		return nombreP;
	}

	public void setNombreP(String nombreP) {
		this.nombreP = nombreP;
	}

	public List<Posgiro> getFiltrarPosgiros() {
		return filtrarPosgiros;
	}

	public void setFiltrarPosgiros(List<Posgiro> filtrarPosgiros) {
		this.filtrarPosgiros = filtrarPosgiros;
	}

}
