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

import ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.entities.Tipordeninv;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "tipoOrdenInventarioController")
@SessionScoped

public class TipoOrdenInventarioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoOrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO")
	private TipoOrdenInventarioDAO tipoOrdenInventarioI;

	/** VARIABLES DE LA CLASE **/

	private List<Tipordeninv> listTipoInventario = new ArrayList<Tipordeninv>();
	private Tipordeninv nuevoTipoInventario;
	private Tipordeninv tipoOrdenInv;
	private String nombreTOI;
	private List<Tipordeninv> filtrarTOI;

	/** METODO INIT **/
	@PostConstruct
	public void init() {

		try {
			
			setListTipoInventario(tipoOrdenInventarioI.getAll(Tipordeninv.class));
			setNuevoTipoInventario(new Tipordeninv());
			tipoOrdenInv = new Tipordeninv();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "¡ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Guardar ****/
	public void agregarTipoInventario() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {

			if (buscarTipoOrdenInventario(nuevoTipoInventario.getNombreToi()) == true) {
				mensajeError("El Tipo Orden Producto (" + nuevoTipoInventario.getNombreToi() + ") ya existe.");
				

			} else {

				tipoOrdenInventarioI.save(nuevoTipoInventario);
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				mensajeInfo("El Tipo Orden Inventario (" + nuevoTipoInventario.getNombreToi()
						+ ") se ha almacenado exitosamente.");

				nuevoTipoInventario = new Tipordeninv();

				context.execute("PF('nuevoTOI').hide();");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

		}

	}

	public void modificarTipoInventario() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (tipoOrdenInv.getNombreToi().equals(getNombreTOI())) {
				tipoOrdenInventarioI.update(tipoOrdenInv);
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				mensajeInfo("El Tipo Orden Inventario (" + tipoOrdenInv.getNombreToi()
						+ ") se ha actualizado exitosamente.");

				context.execute("PF('modificarTOI').hide();");

			} else if (buscarTipoOrdenInventario(tipoOrdenInv.getNombreToi()) == false) {
				tipoOrdenInventarioI.update(tipoOrdenInv);
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				mensajeInfo("El Tipo Orden Inventario (" + tipoOrdenInv.getNombreToi()
						+ ") se ha actualizado exitosamente.");

				context.execute("PF('modificarTOI').hide();");
			} else {
				listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

				mensajeError("El Tipo Orden Inventario (" + tipoOrdenInv.getNombreToi() + ") ya existe.");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

		}
	}

	public void eliminarTipoInventario() {
		try {

			tipoOrdenInventarioI.delete(tipoOrdenInv);
			listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);

			mensajeInfo("El Tipo Orden Inventario (" + tipoOrdenInv.getNombreToi()
					+ ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Tipo Orden Inventario (" + tipoOrdenInv.getNombreToi()
						+ ") tiene relación con otra tabla.");

			} else {
				mensajeError("Ha ocurrido un error");

			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarTipoOrdenInventario(String valor) {
		try {
			listTipoInventario = tipoOrdenInventarioI.getAll(Tipordeninv.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Tipordeninv tipo : listTipoInventario) {
			if (tipo.getNombreToi().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreTOI(nombre);
	}

	/** GET AND SET **/

	public List<Tipordeninv> getListTipoInventario() {
		return listTipoInventario;
	}

	public void setListTipoInventario(List<Tipordeninv> listTipoInventario) {
		this.listTipoInventario = listTipoInventario;
	}

	public Tipordeninv getNuevoTipoInventario() {
		return nuevoTipoInventario;
	}

	public void setNuevoTipoInventario(Tipordeninv nuevoTipoInventario) {
		this.nuevoTipoInventario = nuevoTipoInventario;
	}

	public List<Tipordeninv> getFiltrarTOI() {
		return filtrarTOI;
	}

	public void setFiltrarTOI(List<Tipordeninv> filtrarTOI) {
		this.filtrarTOI = filtrarTOI;
	}

	public String getNombreTOI() {
		return nombreTOI;
	}

	public void setNombreTOI(String nombreTOI) {
		this.nombreTOI = nombreTOI;
	}

	public Tipordeninv getTipoOrdenInv() {
		return tipoOrdenInv;
	}

	public void setTipoOrdenInv(Tipordeninv tipoOrdenInv) {
		this.tipoOrdenInv = tipoOrdenInv;
	}

}
