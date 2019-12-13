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

import ec.edu.epn.laboratorioBJ.beans.ClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.entities.Cliente;
import ec.edu.epn.laboratorioBJ.entities.Tipocliente;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "clienteController")
@SessionScoped

public class ClienteController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/******************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.ClienteDAO")
	private ClienteDAO clienteI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO")
	private TipoClienteDAO tipoClienteI;// I (interface)

	private Cliente cliente;
	private List<Cliente> listaCliente = new ArrayList<Cliente>();
	private Cliente nuevoCliente;

	private Tipocliente tipoClienteSelect;
	private Tipocliente tipoCliente;
	private List<Tipocliente> tipoClientes = new ArrayList<Tipocliente>();

	private List<Cliente> filtroCliente;

	private String nombreC;

	@PostConstruct
	public void init() {
		try {

			listaCliente = clienteI.ListCliente();
			cliente = new Cliente();
			nuevoCliente = new Cliente();

			tipoCliente = new Tipocliente();
			tipoClientes = tipoClienteI.getAll(Tipocliente.class);

		} catch (Exception e) {
			e.printStackTrace();
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

	/****** Agregar Cliente ****/

	public void agregarCliente(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarCliente(nuevoCliente.getNombreCl()) == true) {

				mensajeError("El Cliente (" + nuevoCliente.getNombreCl() + ") ya existe.");

			} else {

				nuevoCliente.setTipocliente(tipoClienteSelect);
				clienteI.save(nuevoCliente);
				listaCliente = clienteI.ListCliente();

				mensajeInfo("El Cliente (" + nuevoCliente.getNombreCl() + ") se ha almacenado exitosamente");

				nuevoCliente = new Cliente();
				tipoCliente = new Tipocliente();
				tipoClienteSelect = new Tipocliente();

				context.execute("PF('nuevoCliente').hide();");

			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");

		}

	}

	// ****** Modificar Cliente ****//*

	public void modificarCliente() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (cliente.getNombreCl().equals(getNombreC())) {

				clienteI.update(cliente);
				listaCliente = clienteI.ListCliente();

				mensajeInfo("El Cliente (" + cliente.getNombreCl() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarCliente').hide();");

			} else if (buscarCliente(cliente.getNombreCl()) == false) {

				clienteI.update(cliente);
				listaCliente = clienteI.ListCliente();

				mensajeInfo("El Cliente (" + cliente.getNombreCl() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarCliente').hide();");

			} else {

				listaCliente = clienteI.ListCliente();
				mensajeError("El Cliente (" + cliente.getNombreCl() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	// ****** Eliminar Cliente ****//*

	public void eliminarCliente() {

		try {

			clienteI.delete(cliente);
			listaCliente = clienteI.ListCliente();

			mensajeInfo("El Cliente (" + cliente.getNombreCl() + ") se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError("La tabla Cliente (" + cliente.getNombreCl() + ") tiene relación con otra tabla.");
			} else {
				mensajeError("Ha ocurrido un error");
			}

		}

	}

	// ****** Busqueda de Cliente ****//*

	private boolean buscarCliente(String valor) {
		try {
			listaCliente = clienteI.getAll(Cliente.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Cliente cli : listaCliente) {
			if (cli.getNombreCl().trim().equals(valor.trim())) {
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

	// ****** Getter y Setters ****//*

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
	}

	public Cliente getNuevoCliente() {
		return nuevoCliente;
	}

	public void setNuevoCliente(Cliente nuevoCliente) {
		this.nuevoCliente = nuevoCliente;
	}

	public String getNombreC() {
		return nombreC;
	}

	public void setNombreC(String nombreC) {
		this.nombreC = nombreC;
	}

	public Tipocliente getTipoClienteSelect() {
		return tipoClienteSelect;
	}

	public void setTipoClienteSelect(Tipocliente tipoClienteSelect) {
		this.tipoClienteSelect = tipoClienteSelect;
	}

	public List<Cliente> getFiltroCliente() {
		return filtroCliente;
	}

	public void setFiltroCliente(List<Cliente> filtroCliente) {
		this.filtroCliente = filtroCliente;
	}

	public List<Tipocliente> getTipoClientes() {
		return tipoClientes;
	}

	public void setTipoClientes(List<Tipocliente> tipoClientes) {
		this.tipoClientes = tipoClientes;
	}

	public Tipocliente getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(Tipocliente tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

}
