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

import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.entities.Tipocliente;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoClienteController")
@SessionScoped
public class TipoClienteController implements Serializable{

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
	private String nombreTP;
	

	/** METODO INIT **/
	@PostConstruct
	public void init() {
		try {
			listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);
			nuevoTipoCliente = new Tipocliente();
			tipoCliente = new Tipocliente();
		} catch (Exception e) {

		}
	}

	/** METODO CREAR POSGIRO **/
	public void agregarTipoCliente(ActionEvent event) {
		try {
			if (buscarTipoCliente(nuevoTipoCliente.getTipoTcl()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
								"Ha ocurrido un error, Tipo Cliente " + nuevoTipoCliente.getTipoTcl() + " ya existe."));

			} else {
				tipoClienteI.save(nuevoTipoCliente);
				listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Cliente almacenado exitosamente"));
				nuevoTipoCliente = new Tipocliente();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	/** METODO ELIMINAR POSGIRO **/
	public void eliminarTipoCliente(ActionEvent event) {
		try {

			tipoClienteI.delete(tipoCliente);
			listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Cliente Eliminado exitosamente"));
		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE HA PODIDO ELIMINAR EL REGISTRO",
								"La tabla Tipo Cliente tiene una relacion con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}
	}

	/** METODO EDITAR POSGIRO **/
	public void editarTipoCliente(ActionEvent event) {
		try {
			if (tipoCliente.getTipoTcl().equals(getNombreTP())) {
				tipoClienteI.update(tipoCliente);
				listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarTipoCliente').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Cliente actualizado exitosamente"));

			} else if (buscarTipoCliente(tipoCliente.getTipoTcl()) == false) {
				tipoClienteI.update(tipoCliente);
				listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarTipoCliente').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Cliente actualizado exitosamente"));
			} else {
				listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Tipo Cliente ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	/** METODO BUSCAR POSGIRO **/
	/****** Busqueda de Estado Producto ****/

	private boolean buscarTipoCliente(String valor) {
		
		try {
			listarTipoClientes = tipoClienteI.getAll(Tipocliente.class);
		} catch (Exception e) {
			e.printStackTrace();
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
		setNombreTP(nombre);
	}

	/*
	 * get and set
	 */
	
	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
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

}
