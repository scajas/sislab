package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Detallemetodo;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Ordeninventario;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "ordenInventarioController")
@SessionScoped
public class OrdenInventarioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/*****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO")
	private OrdenInventarioDAO ordenInventarioI;
	
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	// variables de la clase
	private Ordeninventario ordenInventario;
	private Ordeninventario verOrdenInventario;
	private List<Existencia> existencias = new ArrayList<>();
	private List<Ordeninventario> ordenInventarios = new ArrayList<>();
	private List<Ordeninventario> filterOrdenInventarios = new ArrayList<>();
	private List<Detallemetodo> detallemetodos = new ArrayList<>();
	private List<Movimientosinventario> movimientoInventarios = new ArrayList<>();
	private List<String> listOrdenI;


	// Metodo Init
	@PostConstruct
	public void init() {
		try {
			// Long idUsuario = su.id_usuario_log;

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			ordenInventarios = ordenInventarioI.getListOTById(uni.getCodigoU());
		
			filterOrdenInventarios = ordenInventarios;
			ordenInventario = new Ordeninventario();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Settear valores dentro de tablas i formularios

	public String metodo(String idMetodo) {
		String nombre = "";

		Metodo metodo = new Metodo();

		metodo = ordenInventarioI.findMetodoById(idMetodo);

		if (metodo == null) {
			nombre = "N/A";
		} else {
			nombre = metodo.getNombreMt();
		}

		return nombre;
	}

	public String servicio(String idMetodo) {
		String nombre = "";

		Metodo metodo = new Metodo();

		metodo = ordenInventarioI.findMetodoById(idMetodo);

		if (metodo == null) {
			nombre = "N/A";
		} else {
			nombre = metodo.getServicio().getNombreS();
		}

		return nombre;
	}

	public String existencia(String idExistncia) {
		String nombre = "";

		Existencia existencia = new Existencia();

		existencia = ordenInventarioI.findExistenciaById(idExistncia);

		if (existencia == null) {
			nombre = "N/A";
		} else {
			nombre = existencia.getProducto().getNombrePr();
		}

		return nombre;
	}

	public void pasarDetalleMetodo(String id) {
		setMovimientoInventarios(ordenInventarioI.listaMovimientoI(id));
	}

	/*
	 * get and set
	 */

	public Ordeninventario getOrdenInventario() {
		return ordenInventario;
	}

	public void setOrdenInventario(Ordeninventario ordenInventario) {
		this.ordenInventario = ordenInventario;
	}

	public List<Ordeninventario> getOrdenInventarios() {
		return ordenInventarios;
	}

	public void setOrdenInventarios(List<Ordeninventario> ordenInventarios) {
		this.ordenInventarios = ordenInventarios;
	}

	public Ordeninventario getVerOrdenInventario() {
		return verOrdenInventario;
	}

	public void setVerOrdenInventario(Ordeninventario verOrdenInventario) {
		this.verOrdenInventario = verOrdenInventario;
	}

	public List<Detallemetodo> getDetallemetodos() {
		return detallemetodos;
	}

	public void setDetallemetodos(List<Detallemetodo> detallemetodos) {
		this.detallemetodos = detallemetodos;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public List<String> getListOrdenI() {
		return listOrdenI;
	}

	public void setListOrdenI(List<String> listOrdenI) {
		this.listOrdenI = listOrdenI;
	}

	public List<Movimientosinventario> getMovimientoInventarios() {
		return movimientoInventarios;
	}

	public void setMovimientoInventarios(List<Movimientosinventario> movimientoInventarios) {
		this.movimientoInventarios = movimientoInventarios;
	}

	public List<Ordeninventario> getFilterOrdenInventarios() {
		return filterOrdenInventarios;
	}

	public void setFilterOrdenInventarios(List<Ordeninventario> filterOrdenInventarios) {
		this.filterOrdenInventarios = filterOrdenInventarios;
	}

}
