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

import ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "estadoProductoController")
@SessionScoped

public class EstadoProductoController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/EstadoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO")
	private EstadoProductoDAO estadoProductoI;

	/****************************************************************************/

	// Método init
	@PostConstruct
	public void init() {
		try {
			setListaEstadoProducto(estadoProductoI.getAll(Estadoproducto.class));
			setNuevoEstadoProducto(new Estadoproducto());
			estadoproducto = new Estadoproducto();

		} catch (Exception e) {

		}

	}

	// Variables de la clase
	private List<Estadoproducto> listaEstadoProducto = new ArrayList<>();
	private Estadoproducto nuevoEstadoProducto;
	private Estadoproducto estadoproducto;
	private String nombreTP;

	/****** Agregar Estado Producto ****/

	public void agregarEstadoProducto(ActionEvent event) {

		try {
			if (buscarEstadoProducto(nuevoEstadoProducto.getNombreEstp()) == true) {

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "Ha ocurrido un error, El estado del producto ("+ nuevoEstadoProducto.getNombreEstp() +")ya existe."));
				
				nuevoEstadoProducto= new Estadoproducto();

			} else {
				estadoProductoI.save(nuevoEstadoProducto);
				listaEstadoProducto = estadoProductoI.getAll(Estadoproducto.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"El Estado Producto se ha almacenado exitosamente"));

				nuevoEstadoProducto = new Estadoproducto();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}

	}

	/****** Modificar Estado Producto ****/

	public void modificarEstadoProducto(ActionEvent event) {
		try {
			if (estadoproducto.getNombreEstp().equals(getNombreTP())) {
				estadoProductoI.update(estadoproducto);
				listaEstadoProducto = estadoProductoI.getAll(Estadoproducto.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarEstadoProducto').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Estado Producto actualizado exitosamente"));

			} else if (buscarEstadoProducto(estadoproducto.getNombreEstp()) == false) {
				estadoProductoI.update(estadoproducto);
				listaEstadoProducto = estadoProductoI.getAll(Estadoproducto.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarEstadoProducto').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Concentración actualizado exitosamente"));
			} else {
				listaEstadoProducto = estadoProductoI.getAll(Estadoproducto.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Estado Producto ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	/****** Eliminar Estado Producto ****/

	public void eliminarEstadoProducto(ActionEvent event) {

		try {

			estadoProductoI.delete(estadoproducto);
			listaEstadoProducto = estadoProductoI.getAll(Estadoproducto.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "",
							"El Estado producto se ha eliminado correctamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Estado Producto tiene relación con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR!", ""));
			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarEstadoProducto(String valor) {
		
		try {
			listaEstadoProducto = estadoProductoI.getAll(Estadoproducto.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean resultado = false;
		for (Estadoproducto tipo : listaEstadoProducto) {
			if (tipo.getNombreEstp().equals(valor)) {
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

	
	/****** Getter y Setter de Estado Producto ****/

	public Estadoproducto getNuevoEstadoProducto() {
		return nuevoEstadoProducto;
	}

	public void setNuevoEstadoProducto(Estadoproducto nuevoEstadoProducto) {
		this.nuevoEstadoProducto = nuevoEstadoProducto;
	}

	public List<Estadoproducto> getListaEstadoProducto() {
		return listaEstadoProducto;
	}

	public void setListaEstadoProducto(List<Estadoproducto> listaEstadoProducto) {
		this.listaEstadoProducto = listaEstadoProducto;
	}

	public Estadoproducto getEstadoproducto() {
		return estadoproducto;
	}

	public void setEstadoproducto(Estadoproducto estadoproducto) {
		this.estadoproducto = estadoproducto;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

}