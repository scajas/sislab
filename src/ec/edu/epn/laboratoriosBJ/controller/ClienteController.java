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
	private String nombreTP;
	private Tipocliente tipoClienteSelect;
	private List<Tipocliente> tipo = new ArrayList<Tipocliente>();
	private List<Cliente> filtroCliente;

	@PostConstruct
	public void init() {
		try {

			setListaCliente(clienteI.getAll(Cliente.class));
			setNuevoCliente(new Cliente());
			cliente = new Cliente();
			nuevoCliente = new Cliente();
			setTipo(tipoClienteI.getAll(Tipocliente.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ****** Modificar Cliente ****//*

	public void modificarCliente(ActionEvent event) {
		try {
			if (cliente.getNombreCl().equals(getNombreTP())) {
				
				cliente.setTipocliente(tipoClienteSelect);
				clienteI.update(cliente);
				listaCliente = clienteI.getAll(Cliente.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarCliente').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Cliente actualizado exitosamente"));

			} else if (buscarCliente(cliente.getNombreCl()) == false) {
				cliente.setTipocliente(tipoClienteSelect);
				clienteI.update(cliente);
				listaCliente = clienteI.getAll(Cliente.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarCliente').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Cliente actualizado exitosamente"));
			} else {

				listaCliente = clienteI.getAll(Cliente.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Cliente ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	// ************Agregar Cliente************//

	public void agregarCliente(ActionEvent event) {

		try {
			if (buscarCliente(nuevoCliente.getNombreCl()) == true) {		
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "Este Cliente ya existe."));

				nuevoCliente = new Cliente();
				
			} else {
				
				
				nuevoCliente.setTipocliente(tipoClienteSelect);
				
				clienteI.save(nuevoCliente);
				listaCliente = clienteI.getAll(Cliente.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El Cliente se ha almacenado exitosamente"));

				nuevoCliente = new Cliente();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
			
		}

	}

	// ****** Eliminar Cliente ****//*

	public void eliminarCliente(ActionEvent event) {

		try {

			clienteI.delete(cliente);
			listaCliente = clienteI.getAll(Cliente.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El Cliente se ha eliminado correctamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"Ha ocurrido un error interno, comuniquese con el personal DGIP"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR!", ""));
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
		setNombreTP(nombre);
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

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public Tipocliente getTipoClienteSelect() {
		return tipoClienteSelect;
	}

	public void setTipoClienteSelect(Tipocliente tipoClienteSelect) {
		this.tipoClienteSelect = tipoClienteSelect;
	}


	public List<Tipocliente> getTipo() {
		return tipo;
	}

	public void setTipo(List<Tipocliente> tipo) {
		this.tipo = tipo;
	}

	public List<Cliente> getFiltroCliente() {
		return filtroCliente;
	}

	public void setFiltroCliente(List<Cliente> filtroCliente) {
		this.filtroCliente = filtroCliente;
	}

}
