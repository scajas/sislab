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
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Unidadmedida;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "unidadMedidaController")
@SessionScoped

public class UnidadmedidaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadMedidaDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO")
	private UnidadMedidaDAO unidadMedidaI;

	/** VARIABLES DE LA CLASE **/

	private List<Unidadmedida> listUnidadMedida = new ArrayList<Unidadmedida>();
	private Unidadmedida nuevoUnidadMedida;
	private Unidadmedida unidadMedida; // select
	private String nombreUM;
	private List<Unidadmedida> filtrarUM;

	/** METODO INIT **/
	@PostConstruct
	public void init() {
		try {

			listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);
			nuevoUnidadMedida = new Unidadmedida();
			unidadMedida = new Unidadmedida();

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
	public void agregarUnidadMedida() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			if (buscarUnidadMedida(nuevoUnidadMedida.getMedidaUm()) == true) {

				mensajeError("La Unidad de Medida (" + nuevoUnidadMedida.getMedidaUm() + ") ya existe.");

			} else {

				unidadMedidaI.save(nuevoUnidadMedida);
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

				mensajeInfo(
						"La Unidad de Medida (" + nuevoUnidadMedida.getMedidaUm() + ") se ha almacenado exitosamente.");

				nuevoUnidadMedida = new Unidadmedida();
				context.execute("PF('nuevoUnidadMedida').hide();");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

		}

	}

	public void modificarUnidadMedida() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (unidadMedida.getMedidaUm().equals(getNombreUM())) {
				unidadMedidaI.update(unidadMedida);
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

				mensajeInfo("La Unidad de Medida (" + unidadMedida.getMedidaUm() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarUnidadMedida').hide();");

			} else if (buscarUnidadMedida(unidadMedida.getMedidaUm()) == false) {
				unidadMedidaI.update(unidadMedida);
				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

				mensajeInfo("La unidad de Medida (" + unidadMedida.getMedidaUm() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarUnidadMedida').hide();");

			} else {

				listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

				mensajeError("La Unidad de Medida (" + unidadMedida.getMedidaUm() + ") ya existe.");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
		}
	}

	public void eliminarUnidadMedida() {
		try {

			unidadMedidaI.delete(unidadMedida);
			listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);

			mensajeInfo("La Unidad de Medida (" + unidadMedida.getMedidaUm() + ")  se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Unidad de Medida (" + unidadMedida.getMedidaUm()
						+ ") tiene relación con otra tabla.");

			} else {

				mensajeError("Ha ocurrido un error");

			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarUnidadMedida(String valor) {
		try {
			listUnidadMedida = unidadMedidaI.getAll(Unidadmedida.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Unidadmedida tipo : listUnidadMedida) {
			if (tipo.getMedidaUm().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreUM(nombre);
	}

	/** GET AND SET **/

	public List<Unidadmedida> getListUnidadMedida() {
		return listUnidadMedida;
	}

	public void setListUnidadMedida(List<Unidadmedida> listUnidadMedida) {
		this.listUnidadMedida = listUnidadMedida;
	}

	public Unidadmedida getNuevoUnidadMedida() {
		return nuevoUnidadMedida;
	}

	public void setNuevoUnidadMedida(Unidadmedida nuevoUnidadMedida) {
		this.nuevoUnidadMedida = nuevoUnidadMedida;
	}

	public String getNombreUM() {
		return nombreUM;
	}

	public void setNombreUM(String nombreUM) {
		this.nombreUM = nombreUM;
	}

	public List<Unidadmedida> getFiltrarUM() {
		return filtrarUM;
	}

	public void setFiltrarUM(List<Unidadmedida> filtrarUM) {
		this.filtrarUM = filtrarUM;
	}

	public Unidadmedida getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(Unidadmedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

}
