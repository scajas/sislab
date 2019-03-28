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

import ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO;
import ec.edu.epn.laboratorioBJ.entities.Tipoproveedor;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoProveedorController")
@SessionScoped
public class TipoProveedorController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProveedorDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO")

	private TipoProveedorDAO tipoProveedorI;

	// variables de la clase
	private Tipoproveedor tipoproveedor;
	private List<Tipoproveedor> tiposProveedor = new ArrayList<>();
	private List<Tipoproveedor> filtroTiposProveedor = new ArrayList<>();
	private Tipoproveedor nuevoTipoProveedor;
	private String nombreTP;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);
			tipoproveedor = new Tipoproveedor();
			nuevoTipoProveedor = new Tipoproveedor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void agregarTipoProveedor(ActionEvent event) {
		try {
			if (buscarTipoProveedor(nuevoTipoProveedor.getNombreTpv()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error, el tipo de proveedor ("
								+ nuevoTipoProveedor.getNombreTpv() + ") ya existe."));
				nuevoTipoProveedor = new Tipoproveedor();
			} else {
				tipoProveedorI.save(nuevoTipoProveedor);

				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Proveedor almacenado exitosamente"));

				nuevoTipoProveedor = new Tipoproveedor();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	public void modificarTipoProveedor(ActionEvent event) {
		try {
			if (tipoproveedor.getNombreTpv().equals(getNombreTP())) {
				tipoProveedorI.update(tipoproveedor);
				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarTP').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Proveedor actualizado exitosamente"));

			} else if (buscarTipoProveedor(tipoproveedor.getNombreTpv()) == false) {
				tipoProveedorI.update(tipoproveedor);
				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarTP').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Proveedor actualizado exitosamente"));
			} else {
				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El tipo de proveedor ("
								+ tipoproveedor.getNombreTpv() + ") ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	public void eliminarrTipoProveedor(ActionEvent event) {
		try {

			tipoProveedorI.delete(tipoproveedor);
			tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Tipo de Proveedor eliminado exitosamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "",
								"Ha ocurrido un error interno, comuniquese con el personal DGIP"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}
	}

	private boolean buscarTipoProveedor(String valor) {
		
		try {
			tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean resultado = false;
		for (Tipoproveedor tipo : tiposProveedor) {
			if (tipo.getNombreTpv().equals(valor)) {
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
	public Tipoproveedor getTipoproveedor() {
		return tipoproveedor;
	}

	public void setTipoproveedor(Tipoproveedor tipoproveedor) {
		this.tipoproveedor = tipoproveedor;
	}

	public List<Tipoproveedor> getTiposProveedor() {
		return tiposProveedor;
	}

	public void setTiposProveedor(List<Tipoproveedor> tiposProveedor) {
		this.tiposProveedor = tiposProveedor;
	}

	public List<Tipoproveedor> getFiltroTiposProveedor() {
		return filtroTiposProveedor;
	}

	public void setFiltroTiposProveedor(List<Tipoproveedor> filtroTiposProveedor) {
		this.filtroTiposProveedor = filtroTiposProveedor;
	}

	public Tipoproveedor getNuevoTipoProveedor() {
		return nuevoTipoProveedor;
	}

	public void setNuevoTipoProveedor(Tipoproveedor nuevoTipoProveedor) {
		this.nuevoTipoProveedor = nuevoTipoProveedor;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = tipoproveedor.getNombreTpv();
	}

}
