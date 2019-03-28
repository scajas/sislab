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

import ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO;
import ec.edu.epn.laboratorioBJ.entities.Caracteristica;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "CaracteristicaController")
@SessionScoped
public class CaracteristicaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS DAOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/CaracteristicaDAOImplement!ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO")
	private CaracteristicaDAO caracteristicaI;

	/** VARIABLES DE LA CLASE **/
	private Caracteristica caracteristica;
	private List<Caracteristica> listarCaracteristicas = new ArrayList<>();
	private Caracteristica nuevaCaracteristica;
	private String nombreTP;

	/** METODO INIT **/
	@PostConstruct
	public void init() {
		try {
			listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
			nuevaCaracteristica = new Caracteristica();
			caracteristica = new Caracteristica();
		} catch (Exception e) {

		}

	}
	/** METODO CREAR CARACTERISTICA **/
	public void agregarCaracteristica(ActionEvent event) {
		try {
			if (buscarCaracteristica(nuevaCaracteristica.getNombreCr()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
								"Ha ocurrido un error, Característica " + nuevaCaracteristica.getNombreCr() + " ya existe."));

			} else {
				caracteristicaI.save(nuevaCaracteristica);
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Característica almacenada exitosamente"));
				nuevaCaracteristica = new Caracteristica();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}



	/** METODO ELIMINAR CARACTERISTICA **/
	public void eliminarCaracteristica(ActionEvent event) {
		try {
			caracteristicaI.delete(caracteristica);
			listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Característica Eliminada exitosamente"));
		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE HA PODIDO ELIMINAR LA CARACTERÍSTICA",
								"La tabla característica tiene una relacion con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}
	}

	/** METODO EDITAR CARACTERISTICA **/
	public void editarCaracteristica(ActionEvent event) {
		try {
			if (caracteristica.getNombreCr().equals(getNombreTP())) {
				caracteristicaI.update(caracteristica);
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarCaracteristica').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Caracteristica actualizado exitosamente"));

			} else if (buscarCaracteristica(caracteristica.getNombreCr()) == false) {
				caracteristicaI.update(caracteristica);
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarCaracteristica').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Caracteristica actualizado exitosamente"));
			} else {
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "La Caracteristica ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	/** METODO BUSCAR CARACTERISTICA **/
	private boolean buscarCaracteristica(String valor) {
		
		try {
			listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean resultado = false;
		for (Caracteristica tipo : listarCaracteristicas) {
			if (tipo.getNombreCr().equals(valor)) {
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

	
	/** GET AND SET CARACTERISTICA **/

	public List<Caracteristica> getListarCaracteristicas() {
		return listarCaracteristicas;
	}

	public void setListarCaracteristicas(List<Caracteristica> listarCaracteristicas) {
		this.listarCaracteristicas = listarCaracteristicas;
	}

	public Caracteristica getNuevaCaracteristica() {
		return nuevaCaracteristica;
	}

	public void setNuevaCaracteristica(Caracteristica nuevaCaracteristica) {
		this.nuevaCaracteristica = nuevaCaracteristica;
	}

	public Caracteristica getCaracteristica() {
		return caracteristica;
	}

	public void setCaracteristica(Caracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}
	public String getNombreTP() {
		return nombreTP;
	}
	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

}
