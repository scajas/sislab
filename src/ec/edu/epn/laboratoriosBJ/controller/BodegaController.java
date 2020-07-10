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

import ec.edu.epn.laboratorioBJ.beans.LaboratoryDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.laboratorioBJ.entities.laboratory;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
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

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratoryDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratoryDAO")
	private LaboratoryDAO laboratoryI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	// variables de la clase
	private laboratory bodega;
	private List<laboratory> bodegas = new ArrayList<>();
	private List<laboratory> filtroBodegas;

	private laboratory nuevoBodega;
	private String nombreB;
	private Utilidades utilidades;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			updateTable();
			bodega = new laboratory();
			nuevoBodega = new laboratory();
			utilidades = new Utilidades();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateTable() {

		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			System.out.println("unidad obtenida" + uni.getIdUnidad());
			bodegas = laboratoryI.ListarBodegaById(uni.getIdUnidad());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/****** Nuevo ****/

	public void agregarBodega() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarBodega(nuevoBodega.getNombreBg()) == true) {
				utilidades.mensajeError("La Bodega (" + nuevoBodega.getNombreBg() + ") ya existe.");

			} else {
				// seteo de las campos
				nuevoBodega.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
				Long iduser = su.id_usuario_log;
				nuevoBodega.setIdUsuario(iduser.intValue());
				laboratoryI.save(nuevoBodega);
				updateTable();
				utilidades.mensajeInfo("La Bodega (" + nuevoBodega.getNombreBg() + ") se ha almacenado exitosamente");
				nuevoBodega = new laboratory();
				context.execute("PF('nuevoB').hide();");
			}

		} catch (Exception e) {

			utilidades.mensajeError("Ha ocurrido un error");
		}

	}

	public void modificarBodega(ActionEvent event) {

		RequestContext context = RequestContext.getCurrentInstance();
		try {

			if (bodega.getNombreBg().equals(getNombreB())) {
				laboratoryI.update(bodega);
				updateTable();

				utilidades.mensajeInfo("La Bodega (" + bodega.getNombreBg() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarB').hide();");

			} else if (buscarBodega(bodega.getNombreBg()) == false) {
				laboratoryI.update(bodega);
				updateTable();
				utilidades.mensajeInfo("La Bodega (" + bodega.getNombreBg() + ") se ha actualizado exitosamente");
				context.execute("PF('modificarB').hide();");

			} else {

				utilidades.mensajeError("La Bodega (" + bodega.getNombreBg() + ") ya existe.");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		}
	}

	public void eliminarBodega(ActionEvent event) {
		try {

			laboratoryI.delete(bodega);
			updateTable();
			utilidades.mensajeInfo("La Bodega (" + bodega.getNombreBg() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				utilidades.mensajeError("La Tabla Bodega (" + bodega.getNombreBg() + ") tiene relación con otra tabla");
			} else {
				utilidades.mensajeError("Ha ocurrido un problema");
			}

		}
	}

	private boolean buscarBodega(String valor) {
		try {
			updateTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean resultado = false;
		for (laboratory tipo : bodegas) {
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

	// SETT Y GETT
	public laboratory getBodega() {
		return bodega;
	}

	public void setBodega(laboratory bodega) {
		this.bodega = bodega;
	}

	public List<laboratory> getBodegas() {
		return bodegas;
	}

	public void setBodegas(List<laboratory> bodegas) {
		this.bodegas = bodegas;
	}

	public laboratory getNuevoBodega() {
		return nuevoBodega;
	}

	public void setNuevoBodega(laboratory nuevoBodega) {
		this.nuevoBodega = nuevoBodega;
	}

	public String getNombreB() {
		return nombreB;
	}

	public void setNombreB(String nombreB) {
		this.nombreB = nombreB;
	}

	public List<laboratory> getFiltroBodegas() {
		return filtroBodegas;
	}

	public void setFiltroBodegas(List<laboratory> filtroBodegas) {
		this.filtroBodegas = filtroBodegas;
	}

	public Utilidades getUtilidades() {
		return utilidades;
	}

	public void setUtilidades(Utilidades utilidades) {
		this.utilidades = utilidades;
	}

}
