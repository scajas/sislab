package ec.edu.epn.laboratorios.reportes;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "reporteMovInvController")
@SessionScoped

public class ReporteMovimientoInventarioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MovimientosInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO")
	private MovimientosInventarioDAO movimientoInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;
	/****************************************************************************/
	private List<Movimientosinventario> movimientoInventarios = new ArrayList<>();
	private Movimientosinventario movimientoInventario;
	private List<Existencia> filtrarMovimientos;

	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filtrarExistencias;

	private Existencia selectExistencia;
	private Existencia existencia;
	/* Variables adicionales */
	private Date fechaInicio;
	private Date fechaFinal;
	private Utilidades utilidades;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
			selectExistencia = new Existencia();
			existencia = new Existencia();
			utilidades = new Utilidades();

		} catch (Exception e) {

		}
	}

	public void buscar() {

		try {

			if (existencia.getIdExistencia().equals("")) {

				utilidades.mensajeError("Debe seleccionar una Existencia.");
			}

			else if (fechaInicio == null) {

				utilidades.mensajeError("Ingrese la Fecha de Inicio.");

			} else if (fechaFinal == null) {

				utilidades.mensajeError("Ingrese la Fecha Final.");
			}

			else {

				movimientoInventarios = movimientoInventarioI.parametrosMovInv(existencia.getIdExistencia(),
						cambioFecha(fechaInicio), cambioFecha(fechaFinal));

				utilidades.mensajeInfo("Registros Obtenidos: " + movimientoInventarios.size());

			}
		} catch (Exception e) {

		}

	}

	/****** Metodo de Seleccion de Existencia ****/
	public void seleccionarExistencia() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {

			utilidades.mensajeInfo("Se seleccionó la Existencia (" + selectExistencia.getIdExistencia());
			setExistencia(selectExistencia);
			selectExistencia = new Existencia();
			filtrarExistencias = new ArrayList<Existencia>();
			context.execute("PF('listadoEx').hide();");

		} catch (Exception e) {
			utilidades.mensajeError("No se ha seleccionado ninguna Existencia");

		}

	}

	/****** Metodo para setear la fecha ****/
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
	}

	public boolean disabledSelectItem() {
		if (fechaInicio == null) {
			return false;
		} else {
			return true;
		}

	}

	public void limpiarCampos() {

		try {

			movimientoInventarios.clear();
			filtrarMovimientos.clear();
			movimientoInventario = new Movimientosinventario();

			existencias = new ArrayList<Existencia>();
			filtrarExistencias = new ArrayList<Existencia>();
			selectExistencia = new Existencia();
			existencia = new Existencia();

		} catch (Exception e) {

		}

	}

	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}

	/****** Busqueda de Existencias ******/
	public Existencia cambiarDatosExistencia(String id) {
		Existencia existenciatemp = movimientoInventarioI.buscarExistenciaById(id);
		return existenciatemp;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public List<Existencia> getFiltrarExistencias() {
		return filtrarExistencias;
	}

	public void setFiltrarExistencias(List<Existencia> filtrarExistencias) {
		this.filtrarExistencias = filtrarExistencias;
	}

	public Existencia getSelectExistencia() {
		return selectExistencia;
	}

	public void setSelectExistencia(Existencia selectExistencia) {
		this.selectExistencia = selectExistencia;
	}

	public List<Movimientosinventario> getMovimientoInventarios() {
		return movimientoInventarios;
	}

	public void setMovimientoInventarios(List<Movimientosinventario> movimientoInventarios) {
		this.movimientoInventarios = movimientoInventarios;
	}

	public Existencia getExistencia() {
		return existencia;
	}

	public void setExistencia(Existencia existencia) {
		this.existencia = existencia;
	}

	public Movimientosinventario getMovimientoInventario() {
		return movimientoInventario;
	}

	public void setMovimientoInventario(Movimientosinventario movimientoInventario) {
		this.movimientoInventario = movimientoInventario;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public List<Existencia> getFiltrarMovimientos() {
		return filtrarMovimientos;
	}

	public void setFiltrarMovimientos(List<Existencia> filtrarMovimientos) {
		this.filtrarMovimientos = filtrarMovimientos;
	}

}
