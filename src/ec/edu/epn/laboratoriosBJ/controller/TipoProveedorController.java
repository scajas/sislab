package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.TipoProveedorDAO;
import ec.edu.epn.laboratorioBJ.entities.Tipoproveedor;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
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
	private Utilidades utilidades;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);
			tipoproveedor = new Tipoproveedor();
			nuevoTipoProveedor = new Tipoproveedor();
			utilidades = new Utilidades();
		} catch (Exception e) {
		
		}
	}

	public void agregarTipoProveedor() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarTipoProveedor(nuevoTipoProveedor.getNombreTpv()) == true) {

				utilidades.mensajeError("El Tipo de proveedor (" + nuevoTipoProveedor.getNombreTpv()
						+ ") ya existe.");

			} else {
				tipoProveedorI.save(nuevoTipoProveedor);

				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

				utilidades.mensajeInfo(
						"El Tipo proveedor (" + nuevoTipoProveedor.getNombreTpv() + ") se ha almacenado exitosamente.");

				nuevoTipoProveedor = new Tipoproveedor();

				context.execute("PF('nuevoTP').hide();");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");

		}

	}

	public void modificarTipoProveedor() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (tipoproveedor.getNombreTpv().equals(getNombreTP())) {
				tipoProveedorI.update(tipoproveedor);
				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

				utilidades.mensajeInfo("El Tipo Proveedor (" + tipoproveedor.getNombreTpv() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarTP').hide();");

			} else if (buscarTipoProveedor(tipoproveedor.getNombreTpv()) == false) {
				tipoProveedorI.update(tipoproveedor);
				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

				utilidades.mensajeInfo("El Tipo Proveedor (" + tipoproveedor.getNombreTpv() + ") se ha actualizado exitosamente.");
				context.execute("PF('modificarTP').hide();");

			} else {
				tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);
				utilidades.mensajeError("El tipo de proveedor (" + tipoproveedor.getNombreTpv() + ") ya existe.");
			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un error");
		}
	}

	public void eliminarTipoProveedor(ActionEvent event) {
		try {

			tipoProveedorI.delete(tipoproveedor);
			tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);

			utilidades.mensajeInfo("El Tipo Proveedor (" + tipoproveedor.getNombreTpv() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla Tipo Proveedor (" + tipoproveedor.getNombreTpv()
						+ ") tiene relación con otra tabla.");
			} else {
				utilidades.mensajeError("Ha ocurrido un error");
			}

		}
	}

	private boolean buscarTipoProveedor(String valor) {

		try {
			tiposProveedor = tipoProveedorI.getAll(Tipoproveedor.class);
		} catch (Exception e) {
			
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
