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

import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "laboratorioController")
@SessionScoped

public class LaboratorioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratorioDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO")
	private LaboratorioDAO laboratorioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	/****************************************************************************/
	/** VARIABLES **/
	private List<LaboratorioLab> listaLaboratorioLab = new ArrayList<>();
	private LaboratorioLab nuevoLaboratorioLab;
	private LaboratorioLab LaboratorioLab;
	private String nombreTP;
	private UnidadLabo unidadSelect;
	private List<UnidadLabo> unidades = new ArrayList<UnidadLabo>();
	private UnidadLabo unidad;
	
	
	/** MÉTODOS **/
	@PostConstruct
	public void init() {
		try {
			setListaLaboratorioLab(laboratorioI.getAll(LaboratorioLab.class));
			setNuevoLaboratorioLab(new LaboratorioLab());
			LaboratorioLab = new LaboratorioLab();
			unidades = unidadI.getAll(UnidadLabo.class);
			unidad = new UnidadLabo();

		} catch (Exception e) {

		}

	}



	/****** Agregar Estado Producto ****/

	public void agregarLaboratorioLab(ActionEvent event) {

		try {
			if (buscarLaboratorioLab(nuevoLaboratorioLab.getNombreL()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "ERROR!",
						"Ha ocurrido un error, El Laboratorio (" + nuevoLaboratorioLab.getNombreL() + ")ya existe."));

				nuevoLaboratorioLab = new LaboratorioLab();

			} else {

				// UnidadLabo uni = new UnidadLabo();
				// uni.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
				// nuevoLaboratorioLab.setUnidad(uni);

				nuevoLaboratorioLab.setUnidad(unidadSelect);
				laboratorioI.save(nuevoLaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), new FacesMessage(
						FacesMessage.SEVERITY_INFO, "", "El Laboratorio se ha almacenado exitosamente"));

				nuevoLaboratorioLab = new LaboratorioLab();
				unidadSelect= new UnidadLabo();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}

	}

	/****** Modificar Estado Producto ****/

	public void modificarLaboratorioLab(ActionEvent event) {
		try {
			if (LaboratorioLab.getNombreL().equals(getNombreTP())) {

				laboratorioI.update(LaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				
				//LaboratorioLab = new LaboratorioLab();
				// RequestContext context = RequestContext.getCurrentInstance();
				// context.execute("PF('modificarLaboratorio');");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Laboratorio ("+ LaboratorioLab.getNombreL()+") se actualizado exitosamente"));

				//LaboratorioLab.setUnidad(unidadSelect);

			} else if (buscarLaboratorioLab(LaboratorioLab.getNombreL()) == false) {

				laboratorioI.update(LaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				//LaboratorioLab = new LaboratorioLab();
				// RequestContext context = RequestContext.getCurrentInstance();
				// context.execute("PF('modificarLaboratorio');");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Laboratorio ("+ LaboratorioLab.getNombreL()+") se actualizado exitosamente"));

				//LaboratorioLab.setUnidad(unidadSelect);
			} else {
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Laboratorio ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	/****** Eliminar Estado Producto ****/

	public void eliminarLaboratorioLab(ActionEvent event) {

		try {

			laboratorioI.delete(LaboratorioLab);
			listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El Laboratorio ("+ LaboratorioLab.getNombreL()+") se ha eliminado correctamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Laboratorio tiene relación con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR!", ""));
			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarLaboratorioLab(String valor) {

		try {
			listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (LaboratorioLab tipo : listaLaboratorioLab) {
			if (tipo.getNombreL().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre, UnidadLabo uni) {
		setNombreTP(nombre);
		unidad = uni;
		// System.out.println("Unidad"+unidadSelect.getNombreU());
	}

	/****** Getter y Setter de Estado Producto ****/

	public LaboratorioLab getNuevoLaboratorioLab() {
		return nuevoLaboratorioLab;
	}

	public void setNuevoLaboratorioLab(LaboratorioLab nuevoLaboratorioLab) {
		this.nuevoLaboratorioLab = nuevoLaboratorioLab;
	}

	public List<LaboratorioLab> getListaLaboratorioLab() {
		return listaLaboratorioLab;
	}

	public void setListaLaboratorioLab(List<LaboratorioLab> listaLaboratorioLab) {
		this.listaLaboratorioLab = listaLaboratorioLab;
	}

	public LaboratorioLab getLaboratorioLab() {
		return LaboratorioLab;
	}

	public void setLaboratorioLab(LaboratorioLab LaboratorioLab) {
		this.LaboratorioLab = LaboratorioLab;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public UnidadLabo getUnidadSelect() {
		return unidadSelect;
	}

	public void setUnidadSelect(UnidadLabo unidadSelect) {
		this.unidadSelect = unidadSelect;
	}

	public List<UnidadLabo> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<UnidadLabo> unidades) {
		this.unidades = unidades;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

}