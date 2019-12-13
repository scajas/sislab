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
	private String nombreL;
	private UnidadLabo unidadSelect;
	private List<UnidadLabo> unidades = new ArrayList<UnidadLabo>();
	private UnidadLabo unidad;

	private List<LaboratorioLab> filtrarLaboratorios;
	private List<UnidadLabo> filtrarUnidades;

	/** MÉTODOS **/
	@PostConstruct
	public void init() {
		try {

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			listaLaboratorioLab = laboratorioI.getListLabById(uni.getCodigoU());
			setNuevoLaboratorioLab(new LaboratorioLab());

			unidades = unidadI.getAll(UnidadLabo.class);
			unidad = new UnidadLabo();

		} catch (Exception e) {

		}

	}

	public void updateTable() {

		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			listaLaboratorioLab = laboratorioI.getListLabById(uni.getCodigoU());
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

	/****** Agregar Laboratorio ****/

	public void agregarLaboratorioLab() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarLaboratorioLab(nuevoLaboratorioLab.getNombreL()) == true) {

				mensajeError("El Laboratorio (" + nuevoLaboratorioLab.getNombreL() + ") ya existe.");

			} else {

				nuevoLaboratorioLab.setUnidad(unidadSelect);
				laboratorioI.save(nuevoLaboratorioLab);
				updateTable();

				mensajeInfo("El Laboratorio (" + nuevoLaboratorioLab.getNombreL() + ") se ha almacenado exitosamente");

				nuevoLaboratorioLab = new LaboratorioLab();
				unidadSelect = new UnidadLabo();
				context.execute("PF('nuevoLaboratorio').hide();");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

		}

	}

	/****** Modificar Laboratorio ****/

	public void modificarLaboratorioLab() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (LaboratorioLab.getNombreL().equals(getNombreL())) {

				laboratorioI.update(LaboratorioLab);
				updateTable();
				mensajeInfo("El Laboratorio (" + LaboratorioLab.getNombreL() + ") se actualizado exitosamente");

				context.execute("PF('modificarLaboratorio').hide();");

			} else if (buscarLaboratorioLab(LaboratorioLab.getNombreL()) == false) {

				laboratorioI.update(LaboratorioLab);
				updateTable();
				mensajeInfo("El Laboratorio (" + LaboratorioLab.getNombreL() + ") se actualizado exitosamente");

				context.execute("PF('modificarLaboratorio').hide();");

			} else {
				updateTable();
				mensajeError("El Laboratorio (" + LaboratorioLab.getNombreL() + ") ya existe");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

		}
	}

	/****** Eliminar Laboratorio ****/

	public void eliminarLaboratorioLab() {

		try {

			laboratorioI.delete(LaboratorioLab);
			updateTable();
			mensajeInfo("El Laboratorio (" + LaboratorioLab.getNombreL() + ") se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError(
						"La tabla Laboratorio (" + LaboratorioLab.getNombreL() + ") tiene relación con otra tabla");

			} else {

				mensajeError("Ha ocurrido un problema");

			}

		}

	}

	/****** Busqueda de Laboratorios ****/

	private boolean buscarLaboratorioLab(String valor) {

		try {
			updateTable();
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (LaboratorioLab lab : listaLaboratorioLab) {
			if (lab.getNombreL().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre, UnidadLabo uni) {
		setNombreL(nombre);
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

	public String getNombreL() {
		return nombreL;
	}

	public void setNombreL(String nombreL) {
		this.nombreL = nombreL;
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