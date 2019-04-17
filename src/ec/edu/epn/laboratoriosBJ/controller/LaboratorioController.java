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
	private LaboratorioLab LaboratorioLab;
	private String nombreTP;
	private UnidadLabo unidadSelect;
	private List<UnidadLabo> unidades = new ArrayList<UnidadLabo>();
	private UnidadLabo unidad;
	
	private List<LaboratorioLab> filtrarLaboratorios;
	private List<UnidadLabo> filtrarUnidades;

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

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Agregar Laboratorio ****/

	public void agregarLaboratorioLab() {

		try {
			if (buscarLaboratorioLab(nuevoLaboratorioLab.getNombreL()) == true) {

				mensajeError(
						"Ha ocurrido un error, El Laboratorio ( " + nuevoLaboratorioLab.getNombreL() + " ) ya existe.");

			} else {

				nuevoLaboratorioLab.setUnidad(unidadSelect);
				laboratorioI.save(nuevoLaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

				mensajeInfo("El Laboratorio ( " + nuevoLaboratorioLab.getNombreL() + " ) se ha almacenado exitosamente");

				nuevoLaboratorioLab = new LaboratorioLab();
				unidadSelect = new UnidadLabo();
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

		}

	}

	/****** Modificar Laboratorio ****/

	public void modificarLaboratorioLab() {
		try {
			if (LaboratorioLab.getNombreL().equals(getNombreTP())) {

				laboratorioI.update(LaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				mensajeInfo("Laboratorio ( " + LaboratorioLab.getNombreL() + " ) se actualizado exitosamente");

			} else if (buscarLaboratorioLab(LaboratorioLab.getNombreL()) == false) {

				laboratorioI.update(LaboratorioLab);
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				mensajeInfo("Laboratorio ( " + LaboratorioLab.getNombreL() + " ) se actualizado exitosamente");

			} else {
				listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);
				mensajeError("El Laboratorio (" + LaboratorioLab.getNombreL() + ") ya existe");

			}

		} catch (Exception e) {

				mensajeError("Ha ocurrido un error");

		}
	}

	/****** Eliminar Laboratorio ****/

	public void eliminarLaboratorioLab() {

		try {

			laboratorioI.delete(LaboratorioLab);
			listaLaboratorioLab = laboratorioI.getAll(LaboratorioLab.class);

			mensajeInfo("El Laboratorio ( " + LaboratorioLab.getNombreL() + " ) se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				
				mensajeError("La tabla Laboratorio tiene relación con otra tabla");

			} else {
				
				mensajeError("Ha ocurrido un error");;

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

	public List<LaboratorioLab> getFiltrarLaboratorios() {
		return filtrarLaboratorios;
	}

	public void setFiltrarLaboratorios(List<LaboratorioLab> filtrarLaboratorios) {
		this.filtrarLaboratorios = filtrarLaboratorios;
	}

	public List<UnidadLabo> getFiltrarUnidades() {
		return filtrarUnidades;
	}

	public void setFiltrarUnidades(List<UnidadLabo> filtrarUnidades) {
		this.filtrarUnidades = filtrarUnidades;
	}

}