package ec.edu.epn.laboratorios.controller;

import java.io.Serializable;
import java.sql.SQLException;
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

import ec.edu.epn.laboratorios.beans.TipoProductoDAO;
import ec.edu.epn.laboratorios.entities.Tipoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoProductoController")
@SessionScoped
public class TipoProductoController implements Serializable {

	// ****************************************************************/
	// ********************* VARIABLES MANEJO SESION **********************/
	// ****************************************************************/

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	// ****************************************************************/
	// ********************* DAOS **********************/
	// ****************************************************************/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProductoDAOImplement!ec.edu.epn.laboratorios.beans.TipoProductoDAO")
	private TipoProductoDAO tipoProductoI;

	// Variables de la clase
	private List<Tipoproducto> tiposProducto = new ArrayList<>();
	private List<Tipoproducto> tiposProductoFiltro = new ArrayList<>();
	private Tipoproducto nuevoTipoProducto;
	private Tipoproducto tipoProductoSelect;

	// Mètodo init
	@PostConstruct
	public void init() {
		try {
			tiposProducto = tipoProductoI.getAll(Tipoproducto.class);
			nuevoTipoProducto = new Tipoproducto();
			tipoProductoSelect = new Tipoproducto();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void agregarTipoProducto(ActionEvent event) {
		try {

			tipoProductoI.save(nuevoTipoProducto);
			tiposProducto = tipoProductoI.getAll(Tipoproducto.class);
			tiposProductoFiltro = tipoProductoI.getAll(Tipoproducto.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Producto almacenado exitosamente"));

			nuevoTipoProducto = new Tipoproducto();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	public void editarTipoProducto(ActionEvent event) {
		try {

			tipoProductoI.update(tipoProductoSelect);
			tiposProducto = tipoProductoI.getAll(Tipoproducto.class);
			tiposProductoFiltro = tipoProductoI.getAll(Tipoproducto.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Producto editado exitosamente"));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	public void eliminarTipoProducto(ActionEvent event) {
		try {

			tipoProductoI.delete(tipoProductoSelect);
			tiposProducto = tipoProductoI.getAll(Tipoproducto.class);
			tiposProductoFiltro = tipoProductoI.getAll(Tipoproducto.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Producto eliminado exitosamente"));

		}

		catch (SQLException p) {

			System.out.println("estado del error" + p.getSQLState());

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

		catch (Exception e) {

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	public List<Tipoproducto> getTiposProducto() {
		return tiposProducto;
	}

	public void setTiposProducto(List<Tipoproducto> tiposProducto) {
		this.tiposProducto = tiposProducto;
	}

	public Tipoproducto getNuevoTipoProducto() {
		return nuevoTipoProducto;
	}

	public void setNuevoTipoProducto(Tipoproducto nuevoTipoProducto) {
		this.nuevoTipoProducto = nuevoTipoProducto;
	}

	public Tipoproducto getTipoProductoSelect() {
		return tipoProductoSelect;
	}

	public void setTipoProductoSelect(Tipoproducto tipoProductoSelect) {
		this.tipoProductoSelect = tipoProductoSelect;
	}

	public List<Tipoproducto> getTiposProductoFiltro() {
		return tiposProductoFiltro;
	}

	public void setTiposProductoFiltro(List<Tipoproducto> tiposProductoFiltro) {
		this.tiposProductoFiltro = tiposProductoFiltro;
	}

}
