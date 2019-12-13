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
	// Variables de la clase
	private List<Tipoproducto> listaTipoProducto = new ArrayList<>();
	private Tipoproducto nuevoTipoProducto;
	private Tipoproducto tipoProducto;
	private String nombreTP;

	// filtro
	private List<Tipoproducto> filtrarTipoProducto;

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

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Agregar Tipo Producto ****/

	public void agregarTipoProducto() {
		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarTipoProducto(nuevoTipoProducto.getNombreTprod()) == true) {

				mensajeError("El Tipo Producto (" + nuevoTipoProducto.getNombreTprod() + ") ya existe ");

			} else {

				tipoProductoI.save(nuevoTipoProducto);
				listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);

				mensajeInfo("El Tipo Producto (" + nuevoTipoProducto.getNombreTprod() + ") se ha almacenado exitosamente ");
				nuevoTipoProducto = new Tipoproducto();
				context.execute("PF('nuevoTipoProducto').hide();");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
		}

	}

	/****** Modificar Tipo Producto ****/

	public void modificarTipoProducto() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (tipoProducto.getNombreTprod().equals(getNombreTP())) {
				tipoProductoI.update(tipoProducto);
				listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);

				mensajeInfo("El Tipo Producto (" + tipoProducto.getNombreTprod() + ") se ha actualizado exitosamente.");
				context.execute("PF('modificarTipoProducto').hide();");

			} else if (buscarTipoProducto(tipoProducto.getNombreTprod()) == false) {
				tipoProductoI.update(tipoProducto);
				listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);

				mensajeInfo("El Tipo Producto (" + tipoProducto.getNombreTprod() + ") se ha actualizado exitosamente.");
				context.execute("PF('modificarTipoProducto').hide();");

			} else {
				listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);
				mensajeError("El Tipo Producto (" + tipoProducto.getNombreTprod() + ") ya existe.");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

		}
	}

	/****** Eliminar Tipo Producto ****/

	public void eliminarTipoProducto() {

		try {

			tipoProductoI.delete(tipoProducto);
			listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);

			mensajeInfo("El Tipo Producto (" + tipoProducto.getNombreTprod() + ") se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError(
						"La tabla Tipo Producto (" + tipoProducto.getNombreTprod() + ") tiene relación con otra tabla");

			} else {
				mensajeError("Ha ocurrido un error");
			}

		}

	}

	/****** Busqueda de Tipo Producto ****/

	private boolean buscarTipoProducto(String valor) {

		try {
			listaTipoProducto = tipoProductoI.getAll(Tipoproducto.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Tipoproducto tipo : listaTipoProducto) {
			if (tipo.getNombreTprod().equals(valor)) {
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

	/****** Getter y Setter ****/

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

	public List<Tipoproducto> getFiltrarTipoProducto() {
		return filtrarTipoProducto;
	}

	public void setFiltrarTipoProducto(List<Tipoproducto> filtrarTipoProducto) {
		this.filtrarTipoProducto = filtrarTipoProducto;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}
}