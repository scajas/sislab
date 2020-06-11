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
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.entities.Tipocliente;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoClienteController")
@SessionScoped
public class TipoClienteController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS DAOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO")
	private TipoClienteDAO tipoClienteI;

	/** VARIABLES DE LA CLASE **/
	private Tipocliente tipoCliente;
	private List<Tipocliente> listarTipoClientes = new ArrayList<>();
	private Tipocliente nuevoTipoCliente;
	private String nombreTC;

	private List<Tipocliente> filtrarTC;
	private Utilidades utilidades;

	/** METODO INIT **/
	@PostConstruct
	public void init() {
		try {
			listarTipoClientes = tipoClienteI.getLisTC();
			nuevoTipoCliente = new Tipocliente();
			tipoCliente = new Tipocliente();
			utilidades = new Utilidades();
		} catch (Exception e) {

		}
	}

	/****** Agregar Tipo Cliente ****/

	public void agregarTipoCliente(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarTipoCliente(nuevoTipoCliente.getTipoTcl()) == true) {
				utilidades.mensajeError("El Tipo Cliente (" + nuevoTipoCliente.getTipoTcl() + ") ya existe.");

			} else {
				tipoClienteI.save(nuevoTipoCliente);
				listarTipoClientes = tipoClienteI.getLisTC();
				utilidades.mensajeInfo("El Tipo Cliente (" + nuevoTipoCliente.getTipoTcl() + ") se ha almacenado exitosamente");
				nuevoTipoCliente = new Tipocliente();
				context.execute("PF('nuevoTipoCliente').hide();");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		}

	}

	/****** Editar Tipo Cliente ****/

	public void eliminarTipoCliente(ActionEvent event) {
		try {

			tipoClienteI.delete(tipoCliente);
			listarTipoClientes = tipoClienteI.getLisTC();
			utilidades.mensajeInfo("El Tipo Cliente (" + tipoCliente.getTipoTcl() + ") se ha eliminado exitosamente");
		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				utilidades.mensajeError(
						"La tabla Tipo Cliente (" + tipoCliente.getTipoTcl() + ") tiene una relación con otra tabla");
			} else {
				utilidades.mensajeError("Ha ocurrido un problema");
			}

		}
	}

	/** METODO EDITAR POSGIRO **/
	public void modificarTipoCliente(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (tipoCliente.getTipoTcl().equals(getNombreTC())) {
				tipoClienteI.update(tipoCliente);
				listarTipoClientes = tipoClienteI.getLisTC();
				utilidades.mensajeInfo("El Tipo Cliente (" + tipoCliente.getTipoTcl() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarTipoCliente').hide();");

			} else if (buscarTipoCliente(tipoCliente.getTipoTcl()) == false) {
				tipoClienteI.update(tipoCliente);
				listarTipoClientes = tipoClienteI.getLisTC();
				utilidades.mensajeInfo("El Tipo Cliente (" + tipoCliente.getTipoTcl() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarTipoCliente').hide();");

			} else {
				listarTipoClientes = tipoClienteI.getLisTC();
				utilidades.mensajeError("El Tipo Cliente (" + tipoCliente.getTipoTcl() + ") ya existe.");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		}
	}

	/****** Busqueda de tipo Cliente ****/

	private boolean buscarTipoCliente(String valor) {

		try {
			listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);
		} catch (Exception e) {
		
		}

		boolean resultado = false;
		for (Tipocliente tipo : listarTipoClientes) {
			if (tipo.getTipoTcl().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreTC(nombre);
	}

	/****** Getter y Setter de Tipo Cliente ****/

	public String getNombreTC() {
		return nombreTC;
	}

	public void setNombreTC(String nombreTC) {
		this.nombreTC = nombreTC;
	}

	public Tipocliente getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(Tipocliente tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public Tipocliente getNuevoTipoCliente() {
		return nuevoTipoCliente;
	}

	public void setNuevoTipoCliente(Tipocliente nuevoTipoCliente) {
		this.nuevoTipoCliente = nuevoTipoCliente;
	}

	public List<Tipocliente> getListarTipoClientes() {
		return listarTipoClientes;
	}

	public void setListarTipoClientes(List<Tipocliente> listarTipoClientes) {
		this.listarTipoClientes = listarTipoClientes;
	}

	public List<Tipocliente> getFiltrarTC() {
		return filtrarTC;
	}

	public void setFiltrarTC(List<Tipocliente> filtrarTC) {
		this.filtrarTC = filtrarTC;
	}

}
