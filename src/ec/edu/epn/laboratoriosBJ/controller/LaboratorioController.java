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
	private LaboratorioLab laboratorioLab;
	private String nombreTP;
	private UnidadLabo unidadSelect;
	private List<UnidadLabo> unidades = new ArrayList<UnidadLabo>();
	private UnidadLabo unidad;

	/** M�TODOS **/
	@PostConstruct
	public void init() {
		try {
			setListaLaboratorioLab(laboratorioI.getAll(LaboratorioLab.class));
			setNuevoLaboratorioLab(new LaboratorioLab());
			laboratorioLab = new LaboratorioLab();
			unidades = unidadI.getAll(UnidadLabo.class);
			unidad = new UnidadLabo();

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

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACI�N", mensaje));

	}

	/****** Agregar Laboratorio ****/

	public void agregarLaboratorioLab() {

		try {
			if (buscarLaboratorioLab(nuevoLaboratorioLab.getNombreL()) == true) {

				mensajeError(
						"Ha ocurrido un error, El Laboratorio ( " + nuevoLaboratorioLab.getNombreL() + " ) ya existe.");
				nuevoLaboratorioLab = new LaboratorioLab();

			} else {

				//UnidadLabo uni = new UnidadLabo();
				//uni.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);

				nuevoLaboratorioLab.setUnidad(unidadSelect);
				laboratorioI.save(nuevoLaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

				mensajeInfo(
						"El Laboratorio ( " + nuevoLaboratorioLab.getNombreL() + " ) se ha almacenado exitosamente");

				nuevoLaboratorioLab = new LaboratorioLab();
				unidadSelect = new UnidadLabo();
				// nuevoLaboratorioLab.setUnidad(uni);
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
		}

	}

	/****** Modificar Laboratorio ****/

	public void modificarLaboratorioLab() {
		try {
			if (laboratorioLab.getNombreL().equals(getNombreTP())) {

				laboratorioI.update(laboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

				mensajeInfo("El Laboratorio ( " + laboratorioLab.getNombreL() + " ) se ha actualizado exitosamente");
				laboratorioLab.setUnidad(unidadSelect);

			} else if (buscarLaboratorioLab(laboratorioLab.getNombreL()) == false) {

				laboratorioI.update(laboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

				mensajeInfo("El Laboratorio ( " + laboratorioLab.getNombreL() + " ) se ha actualizado exitosamente");

			} else {
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				mensajeError("El Laboratorio ( " + laboratorioLab.getNombreL() + " ) ya existe");
				
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
		}
	}

	/****** Eliminar Laboratorio ****/

	public void eliminarLaboratorioLab() {

		try {

			laboratorioI.delete(laboratorioLab);
			listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

			mensajeInfo("El Laboratorio (" + laboratorioLab.getNombreL() + ") se ha eliminado correctamente");


		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				
				mensajeError("El Laboratorio (" + laboratorioLab.getNombreL() + ") se ha eliminado correctamente");

			} else {
				mensajeError("Ha ocurrido un error");

			}
		}
	}

	/****** Busqueda de Laboratorios ****/

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

	/****** Getter y Setter de Laboratorio ****/

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
		return laboratorioLab;
	}

	public void setLaboratorioLab(LaboratorioLab LaboratorioLab) {
		this.laboratorioLab = LaboratorioLab;
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