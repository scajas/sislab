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
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.facturacion.entities.Factura;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.entities.Detallemetodo;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Ordeninventario;
import ec.edu.epn.laboratorioBJ.entities.Presentacion;
import ec.edu.epn.laboratorioBJ.entities.Tipordeninv;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "movimientoInventarioController")
@SessionScoped
public class MovimientoInventarioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/*****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MovimientosInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO")
	private MovimientosInventarioDAO movimientoInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoOrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO")
	private TipoOrdenInventarioDAO tipoOrdenInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	/** Variables **/
	private List<Movimientosinventario> movimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> filtroMovimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> nuevoMovimientoInventarios = new ArrayList<>();
	private Movimientosinventario nuevoMovimientoInventario;
	private Movimientosinventario movimientosinventario;

	// select de Tipo Orden Inventario
	private Tipordeninv tipoOrdenSelect;
	private List<Tipordeninv> tipordeninvs = new ArrayList<Tipordeninv>();
	private Tipordeninv tipordeninv;

	// existencias
	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filtrarExistencias = new ArrayList<>();
	private Existencia existencia;
	private Existencia selectExistencia;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {
			// Long idUsuario = su.id_usuario_log;
			movimientoInventarios = movimientoInventarioI.getAll(Movimientosinventario.class);
			nuevoMovimientoInventario = new Movimientosinventario();
			movimientosinventario = new Movimientosinventario();

			System.out.println("Orde de inventarios consultadas: " + movimientoInventarios.size());

			// init de Presentacion
			tipordeninvs = tipoOrdenInventarioI.orderById();
			tipordeninv = new Tipordeninv(); // existenciasI.reemplazarNullPresentacion();
			
			// init de Presentacion
			selectExistencia = new Existencia();
			existencia = new Existencia();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****** Agregar/Editar/Eliminar Movimiento de Inventario a lista temporal ****/

	public void agregarMovimiento() {

		try {
			nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
			nuevoMovimientoInventario = new Movimientosinventario();
			System.out.println("Esta funcionando el registro");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void editarMovimiento() {

		try {
			nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
			nuevoMovimientoInventario = new Movimientosinventario();
			System.out.println("Esta funcionando el registro");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void eliminarMovimiento() {

		try {
			nuevoMovimientoInventarios.remove(movimientosinventario);
			mensajeInfo("Se ha eliminado el movimiento de Inventario ("+ movimientosinventario.getIdExistencia() +")");
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error interno.");
		}
		
	}
	
	public void buscar(){
		
	}
	
	/****** Cambiar valor de tabla ****/
	public Existencia cambiarDatosExistencia(String id){
		Existencia existenciatemp = movimientoInventarioI.buscarExistenciaById(id);
		return existenciatemp;
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

	public void cambiarPanel() {
		
		
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('ingresarMI').show();");
			System.out.println("este es el cambio de combo" + getTipoOrdenSelect().getNombreToi());
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Debe seleccionar un tipo de Inventario");
		}

		
	}

	public void cargarExistencias() {

		try {

			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
			//filtrarExistencias = existencias;
			System.out.println("Existencias consultadas" + existencias.size());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void seleccionarExistencia() {

		try {
			getNuevoMovimientoInventario().setIdExistencia(getSelectExistencia().getIdExistencia());
			mensajeInfo("Se seleccionó la Existencia (" + selectExistencia.getIdExistencia());
			System.out.println("este es el id de existencia: " + selectExistencia.getIdExistencia() + " "
					+ getNuevoMovimientoInventario().getIdExistencia());

			setExistencia(selectExistencia);
			
			selectExistencia = new Existencia();

		} catch (Exception e) {
			mensajeError("No se ha seleccionado ninguna Existencia");
		}

	}

	// Settear valores dentro de tablas i formularios

	/*
	 * get and set
	 */

	public List<Movimientosinventario> getMovimientoInventarios() {
		return movimientoInventarios;
	}

	public void setMovimientoInventarios(List<Movimientosinventario> movimientoInventarios) {
		this.movimientoInventarios = movimientoInventarios;
	}

	public Movimientosinventario getNuevoMovimientoInventario() {
		return nuevoMovimientoInventario;
	}

	public void setNuevoMovimientoInventario(Movimientosinventario nuevoMovimientoInventario) {
		this.nuevoMovimientoInventario = nuevoMovimientoInventario;
	}

	public Tipordeninv getTipoOrdenSelect() {
		return tipoOrdenSelect;
	}

	public void setTipoOrdenSelect(Tipordeninv tipoOrdenSelect) {
		this.tipoOrdenSelect = tipoOrdenSelect;
	}

	public List<Tipordeninv> getTipordeninvs() {
		return tipordeninvs;
	}

	public void setTipordeninvs(List<Tipordeninv> tipordeninvs) {
		this.tipordeninvs = tipordeninvs;
	}

	public Tipordeninv getTipordeninv() {
		return tipordeninv;
	}

	public void setTipordeninv(Tipordeninv tipordeninv) {
		this.tipordeninv = tipordeninv;
	}

	public List<Movimientosinventario> getNuevoMovimientoInventarios() {
		return nuevoMovimientoInventarios;
	}

	public void setNuevoMovimientoInventarios(List<Movimientosinventario> nuevoMovimientoInventarios) {
		this.nuevoMovimientoInventarios = nuevoMovimientoInventarios;
	}

	public Movimientosinventario getMovimientosinventario() {
		return movimientosinventario;
	}

	public void setMovimientosinventario(Movimientosinventario movimientosinventario) {
		this.movimientosinventario = movimientosinventario;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public List<Movimientosinventario> getFiltroMovimientoInventarios() {
		return filtroMovimientoInventarios;
	}

	public void setFiltroMovimientoInventarios(List<Movimientosinventario> filtroMovimientoInventarios) {
		this.filtroMovimientoInventarios = filtroMovimientoInventarios;
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

	public Existencia getExistencia() {
		return existencia;
	}

	public void setExistencia(Existencia existencia) {
		this.existencia = existencia;
	}

}
