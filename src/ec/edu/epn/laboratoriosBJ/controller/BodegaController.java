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

import ec.edu.epn.laboratorioBJ.beans.BodegaDAO;
import ec.edu.epn.laboratorioBJ.entities.Bodega;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "bodegaController")
@SessionScoped
public class BodegaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/BodegaDAOImplement!ec.edu.epn.laboratorioBJ.beans.BodegaDAO")

	private BodegaDAO bodegaI;

	// variables de la clase
	private Bodega bodega;
	private List<Bodega> bodegas = new ArrayList<>();
	private List<Bodega> filtroBodegas;
	private Bodega nuevoBodega;
	private String nombreB;
	
	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);
			bodega = new Bodega();
			nuevoBodega = new Bodega();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	/****** Nuevo ****/

	public void agregarBodega() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarBodega(nuevoBodega.getNombreBg()) == true) {
				mensajeError("La Bodega (" + nuevoBodega.getNombreBg() + ") ya existe.");

			} else {
				// seteo de las campos
				nuevoBodega.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
				Long iduser = su.id_usuario_log;
				nuevoBodega.setIdUsuario(iduser.intValue());
				bodegaI.save(nuevoBodega);
				bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);
				mensajeInfo("La Bodega (" + nuevoBodega.getNombreBg() + ") registrada exitosamente");
				nuevoBodega = new Bodega();

				context.execute("PF('nuevoB').hide();");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
		}

	}

	public void modificarBodega(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		try {

			if (bodega.getNombreBg().equals(getNombreB())) {
				bodegaI.update(bodega);
				bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);

				mensajeInfo("La Bodega (" + bodega.getNombreBg() + ")se ha actualizado exitosamente");
				context.execute("PF('modificarB').hide();");

			} else if (buscarBodega(bodega.getNombreBg()) == false) {
				bodegaI.update(bodega);
				bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);

				mensajeInfo("La Bodega (" + bodega.getNombreBg() + ")se ha actualizado exitosamente");
				context.execute("PF('modificarB').hide();");

			} else {
				bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);
				mensajeError("La Bodega (" + bodega.getNombreBg() + ") ya existe.");
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	public void eliminarBodega(ActionEvent event) {
		try {

			bodegaI.delete(bodega);
			bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);

			mensajeInfo("La Bodega (" + bodega.getNombreBg() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError("La Tabla Bodega (" + bodega.getNombreBg() + ") tiene relación con otra tabla");
			} else {
				mensajeError("Ha ocurrido un problema");
			}

		}
	}

	private boolean buscarBodega(String valor) {
		try {
			bodegas = bodegaI.listaBodegaUnidad(su.UNIDAD_USUARIO_LOGEADO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean resultado = false;
		for (Bodega tipo : bodegas) {
			if (tipo.getNombreBg().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}
		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreB(nombre);
	}

	/*
	 * get and set
	 */

	public Bodega getBodega() {
		return bodega;
	}

	public void setBodega(Bodega bodega) {
		this.bodega = bodega;
	}

	public List<Bodega> getBodegas() {
		return bodegas;
	}

	public void setBodegas(List<Bodega> bodegas) {
		this.bodegas = bodegas;
	}

	public Bodega getNuevoBodega() {
		return nuevoBodega;
	}

	public void setNuevoBodega(Bodega nuevoBodega) {
		this.nuevoBodega = nuevoBodega;
	}

	public String getNombreB() {
		return nombreB;
	}

	public void setNombreB(String nombreB) {
		this.nombreB = nombreB;
	}

	public List<Bodega> getFiltroBodegas() {
		return filtroBodegas;
	}

	public void setFiltroBodegas(List<Bodega> filtroBodegas) {
		this.filtroBodegas = filtroBodegas;
	}

}
