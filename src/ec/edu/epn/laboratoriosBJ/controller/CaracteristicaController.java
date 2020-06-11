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

import ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO;
import ec.edu.epn.laboratorioBJ.entities.Caracteristica;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "caracteristicaController")
@SessionScoped
public class CaracteristicaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/CaracteristicaDAOImplement!ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO")
	private CaracteristicaDAO caracteristicaI;

	/****************************************************************************/

	private Caracteristica caracteristica;
	private List<Caracteristica> listarCaracteristicas = new ArrayList<>();
	private Caracteristica nuevaCaracteristica;
	private String nombreC;
	private List<Caracteristica> filtrarCaracteristicas;
	private Utilidades utilidades;

	@PostConstruct
	public void init() {
		try {
			listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
			nuevaCaracteristica = new Caracteristica();
			caracteristica = new Caracteristica();
			setUtilidades(new Utilidades());
		} catch (Exception e) {

		}

	}

	public void agregarCaracteristica() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (buscarCaracteristica(nuevaCaracteristica.getNombreCr()) == true) {
				utilidades.mensajeError("La Característica (" + nuevaCaracteristica.getNombreCr() + ") ya existe.");

			} else {
				caracteristicaI.save(nuevaCaracteristica);
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
				utilidades.mensajeInfo(
						"La Característica (" + nuevaCaracteristica.getNombreCr() + ") se ha almacenado exitosamente");
				nuevaCaracteristica = new Caracteristica();
				context.execute("PF('nuevaCaracteristica').hide();");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		}

	}

	/** Modificar **/

	public void modificarCaracteristica() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (caracteristica.getNombreCr().equals(getNombreC())) {
				caracteristicaI.update(caracteristica);
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);

				utilidades.mensajeInfo("La Caracteristica (" + caracteristica.getNombreCr() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarCaracteristica').hide();");

			} else if (buscarCaracteristica(caracteristica.getNombreCr()) == false) {
				caracteristicaI.update(caracteristica);
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);

				utilidades.mensajeInfo("La Caracteristica (" + caracteristica.getNombreCr() + ") se ha actualizado exitosamente");

				context.execute("PF('modificarCaracteristica').hide();");

			} else {
				listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
				utilidades.mensajeError("La Caracteristica (" + caracteristica.getNombreCr() + ") ya existe.");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");
		}
	}

	/****** Eliminar ****/
	
	public void eliminarCaracteristica() {
		try {
			caracteristicaI.delete(caracteristica);
			listarCaracteristicas = caracteristicaI.getAll(Caracteristica.class);
			utilidades.mensajeInfo("La Característica (" + caracteristica.getNombreCr() + ") se ha eliminado exitosamente");
		} 
		
		catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla característica (" + caracteristica.getNombreCr()
						+ ") tiene una relacion con otra tabla");
			} else {
				utilidades.mensajeError("Ha ocurrido un error");
			}

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
		setNombreC(nombre);
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

	public String getNombreC() {
		return nombreC;
	}

	public void setNombreC(String nombreC) {
		this.nombreC = nombreC;
	}

	public List<Caracteristica> getFiltrarCaracteristicas() {
		return filtrarCaracteristicas;
	}

	public void setFiltrarCaracteristicas(List<Caracteristica> filtrarCaracteristicas) {
		this.filtrarCaracteristicas = filtrarCaracteristicas;
	}

	public Utilidades getUtilidades() {
		return utilidades;
	}

	public void setUtilidades(Utilidades utilidades) {
		this.utilidades = utilidades;
	}

}
