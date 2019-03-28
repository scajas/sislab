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
import javax.faces.event.ActionEvent;

import ec.edu.epn.laboratorioBJ.beans.PosgiroDAO;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.laboratorioBJ.entities.Posgiro;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "PosgiroController")
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
	private String nombreTP;
	

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

	/** METODO CREAR POSGIRO **/
	public void agregarPosgiro(ActionEvent event) {
		try {
			if (buscarPosgiro(nuevoPosgiro.getNombrePg()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
								"Ha ocurrido un error, Posgiro " + nuevoPosgiro.getNombrePg() + " ya existe."));

			} else {
				posgiroI.save(nuevoPosgiro);
				listarPosgiros = posgiroI.getAll(Posgiro.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Posgiro almacenado exitosamente"));
				nuevoPosgiro = new Posgiro();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	/** METODO ELIMINAR POSGIRO **/
	public void eliminarPosgiro(ActionEvent event) {
		try {

			posgiroI.delete(posgiro);
			listarPosgiros = posgiroI.getAll(Posgiro.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Posgiro Eliminado exitosamente"));
		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE HA PODIDO ELIMINAR EL REGISTRO",
								"La tabla posgiro tiene una relacion con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}
	}

	/** METODO EDITAR POSGIRO **/
	public void editarPosgiro(ActionEvent event) {
		try {
			if (posgiro.getNombrePg().equals(getNombreTP())) {
				posgiroI.update(posgiro);
				listarPosgiros = posgiroI.getAll(Posgiro.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarPosgiro').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Posgiro actualizado exitosamente"));

			} else if (buscarPosgiro(posgiro.getNombrePg()) == false) {
				posgiroI.update(posgiro);
				listarPosgiros = posgiroI.getAll(Posgiro.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarPosgiro').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Proveedor actualizado exitosamente"));
			} else {
				listarPosgiros = posgiroI.getAll(Posgiro.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Estado Producto ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	/** METODO BUSCAR POSGIRO **/
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
		setNombreTP(nombre);
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

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

}
