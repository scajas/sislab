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

import ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoProductoController")
@SessionScoped

public class TipoProductoController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO")
	private TipoProductoDAO tipoProductoI;

	/****************************************************************************/

	// Método init
	@PostConstruct
	public void init() {
		try {
			setListaTipoProducto(tipoProductoI.getAll(Tipoproducto.class));
			setNuevoTipoProducto(new Tipoproducto());
			tipoProducto = new Tipoproducto();

		} catch (Exception e) {

		}

	}

	// Variables de la clase
	private List<Tipoproducto> listaTipoProducto= new ArrayList<>();
	private Tipoproducto nuevoTipoProducto;
	private Tipoproducto tipoProducto;
	
	/****** Agregar Estado Producto ****/

	public void agregarTipoProducto(ActionEvent event) {

		try {
			if (buscarTipoProducto(nuevoTipoProducto.getNombreTprod()) == true) {

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", "El tipo producto ya existe."));

			} else {

				tipoProductoI.save(nuevoTipoProducto);
				listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo Producto almacenado exitosamente"));

				nuevoTipoProducto = new Tipoproducto();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}

	}

	/****** Modificar Estado Producto ****/

	public void modificarTipoProducto(ActionEvent event) {
		
		try {

				tipoProductoI.update(tipoProducto);
				listaTipoProducto= tipoProductoI.getAll(Tipoproducto.class);
			
				
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"La concentración se ha modificado exitosamente"));
				
				tipoProducto = new Tipoproducto();
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}
	}

	/****** Eliminar Estado Producto ****/

	public void eliminarTipoProducto(ActionEvent event) {

		try {

			tipoProductoI.delete(tipoProducto);
			listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El tipo producto se ha eliminado correctamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Tipo Producto tiene relación con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarTipoProducto(String valor) {
		boolean resultado = false;
		for (Tipoproducto estadoTP : listaTipoProducto) {
			if (estadoTP.getNombreTprod().trim().equals(valor.trim())) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}
		return resultado;
	}

	/****** Getter y Setter de Estado Producto ****/
	
	public List<Tipoproducto> getListaTipoProducto() {
		return listaTipoProducto;
	}

	public void setListaTipoProducto(List<Tipoproducto> listaTipoProducto) {
		this.listaTipoProducto = listaTipoProducto;
	}

	public Tipoproducto getNuevoTipoProducto() {
		return nuevoTipoProducto;
	}

	public void setNuevoTipoProducto(Tipoproducto nuevoTipoProducto) {
		this.nuevoTipoProducto = nuevoTipoProducto;
	}

	public Tipoproducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(Tipoproducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

}