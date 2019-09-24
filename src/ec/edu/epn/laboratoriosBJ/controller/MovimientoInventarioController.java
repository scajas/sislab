package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.SaldoExistenciaDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoOrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Hidratacion;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Ordeninventario;
import ec.edu.epn.laboratorioBJ.entities.SaldoExistencia;
import ec.edu.epn.laboratorioBJ.entities.Tipordeninv;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.CambioClave;
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

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/SaldoExistenciaDAOImplement!ec.edu.epn.laboratorioBJ.beans.SaldoExistenciaDAO")
	private SaldoExistenciaDAO saldoExistenciaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO")
	private OrdenInventarioDAO ordenInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	/** Variables **/
	private List<Movimientosinventario> movimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> filtroMovimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> nuevoMovimientoInventarios = new ArrayList<>();
	private Movimientosinventario nuevoMovimientoInventario;
	private Movimientosinventario movimientosinventario;

	/** Variables OrdenInv **/
	private Ordeninventario ordeninventario;
	private Ordeninventario nuevoOrdeninventario;
	private List<Ordeninventario> ordenInventarios = new ArrayList<>();
	private List<Ordeninventario> filterOrdenInventarios = new ArrayList<>();

	// select de Tipo Orden Inventario
	private Tipordeninv tipoOrdenSelect;
	private List<Tipordeninv> tipordeninvs = new ArrayList<Tipordeninv>();
	private List<Tipordeninv> temptipordeninvs = new ArrayList<Tipordeninv>();
	private Tipordeninv tipordeninv;

	// existencias
	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filtrarExistencias = new ArrayList<>();
	private Existencia existencia;
	private Existencia selectExistencia;

	// saldo Existencia
	private SaldoExistencia saldoExistencia;
	private List<SaldoExistencia> saldoExistencias = new ArrayList<>();

	// unidad
	UnidadLabo unidadLabo;

	// id temporal
	private String idTemporal;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {
			// Long idUsuario = su.id_usuario_log;
			// init movimiento inventario
			// movimientoInventarioI.getAll(Movimientosinventario.class);
			nuevoMovimientoInventarios = new ArrayList<>();
			nuevoMovimientoInventario = new Movimientosinventario();
			movimientosinventario = new Movimientosinventario();

			// System.out.println("Orde de inventarios consultadas: " +
			// movimientoInventarios.size());

			// init Orden inventario
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			ordenInventarios = ordenInventarioI.getListOIById(uni.getCodigoU());
			ordeninventario = new Ordeninventario();
			nuevoOrdeninventario = new Ordeninventario();
			System.out.println("Orde de inventarios consultadas: " + ordenInventarios.size());

			// init de Tipo Orden Inventario
			temptipordeninvs = new ArrayList<Tipordeninv>();
			tipordeninvs = tipoOrdenInventarioI.orderById();
			llenarCombo();
			tipordeninv = new Tipordeninv(); // existenciasI.reemplazarNullPresentacion();
			tipoOrdenSelect = new Tipordeninv();

			// init de Existencia
			filtrarExistencias = new ArrayList<Existencia>();
			existencias = new ArrayList<Existencia>();
			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
			selectExistencia = new Existencia();
			existencia = new Existencia();

			// init de Saldo Existencia
			saldoExistencia = new SaldoExistencia();
			saldoExistencias = saldoExistenciaI.getAll(SaldoExistencia.class);

			// init Unidad
			unidadLabo = ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			nuevoOrdeninventario.setUnidad(unidadLabo);

			// init id temporal
			idTemporal = "";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/******
	 * Agregar/Editar/Eliminar Movimiento de Inventario a lista temporal
	 ****/

	public void agregarMovimiento() {

		try {
			if (getIdTemporal().equals("2") || getIdTemporal().equals("3")) {
				double saldoE = nuevoMovimientoInventario.getSaldoE().doubleValue();
				double cantidadE = existencia.getCantidadE().doubleValue();

				if (saldoE <= cantidadE) {
					nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
					// nuevoOrdeninventario.addMovimientosinventario(nuevoMovimientoInventario);
					mensajeInfo("Se ha almacenado (" + nuevoMovimientoInventario.getIdExistencia() + ") correctamente."
							+ nuevoMovimientoInventario.getIdMovimiento());

					nuevoMovimientoInventario = new Movimientosinventario();
					System.out.println("Esta funcionando el registro");

				} else {
					mensajeError("La cantidad es mayor que el saldo existente");
				}
			} else {
				nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
				nuevoMovimientoInventario = new Movimientosinventario();
				System.out.println("Esta funcionando el registro");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void guardarMovimientosInv() {
		for (Movimientosinventario movimientosinventario : nuevoMovimientoInventarios) {
			try {
				movimientoInventarioI.save(movimientosinventario);
			} catch (Exception e) {
				System.out.println(e);
				// e.printStackTrace();
			}

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
			mensajeInfo(
					"Se ha eliminado el movimiento de Inventario (" + movimientosinventario.getIdExistencia() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error interno.");
		}

	}

	public String cambiarResponsable(String session) {
		String nombre = "";

		nuevoOrdeninventario.setResponsableOi(session);

		if (nuevoOrdeninventario == null) {
			nombre = "N/A";
		} else {
			nombre = nuevoOrdeninventario.getResponsableOi();
		}

		return nombre;
	}

	public void llenarCombo() {
		System.out.println("Chica combo");

		for (Tipordeninv tipordeninv : tipordeninvs) {
			if (tipordeninv.getIdTipordeninv() == 1 || tipordeninv.getIdTipordeninv() == 2
					|| tipordeninv.getIdTipordeninv() == 3 || tipordeninv.getIdTipordeninv() == 6
					|| tipordeninv.getIdTipordeninv() == 7) {
				
				temptipordeninvs.add(tipordeninv);
				System.out.println("Se ha agregado: " + tipordeninv.getNombreToi());
				
			}
		}

		System.out.println("Este es el tama�o de la lista: " + temptipordeninvs.size());

	}

	/******
	 * Agregar/Editar/Eliminar en lista real
	 ****/
	public void agregarOrdenI() {
		try {

			/** Creacion del IdOrdenInv **/
			obtenerIdOrdenId();

			/** Seteo de Selects (ComboBox) **/
			OrdenISelect();

			/** Seteo de Ids en la lista de Movimientos Inventarios **/
			cambiarIdMov(nuevoOrdeninventario);

			/** GUARDAR **/
			ordenInventarioI.save(nuevoOrdeninventario);

			guardarMovimientosInv();

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			ordenInventarios = ordenInventarioI.getListOIById(uni.getCodigoU());

			mensajeInfo("El orden de inventario ( " + nuevoOrdeninventario.getIdOrdeninventario()
					+ " ) se ha almacenado exitosamente");

			nuevoOrdeninventario = new Ordeninventario();

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/* PENDIENTE */
	public void buscar() {

	}

	public void OrdenISelect() {
		// set de TipoOrdenInventario
		nuevoOrdeninventario.setTipordeninv(tipoOrdenSelect);

	}

	/****** Metodo para construir el id ****/
	public void obtenerIdOrdenId() {
		try {
			/** Creacion del Obtener fecha **/
			String fecha = obtenerFecha();
			// String fecha = "2020-05-05";
			String[] partsFecha = fecha.split("-");
			String anio = partsFecha[0];

			/** Creacion del IdOrdenInv **/
			String codigoAux = ordenInventarioI.maxIdOrdenI(su.UNIDAD_USUARIO_LOGEADO, fecha);

			if (codigoAux == null) {
				codigoAux = ("DC-OI-0000-" + anio);
			} else {
				System.out.println("Problema resolvido");
			}

			System.out.println("Este es el id que trae: " + codigoAux);

			String[] partsId = codigoAux.split("-");

			String codigoCortado = partsId[2];
			System.out.println("Este es el id convertido en numero: " + codigoCortado);

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;
			System.out.println("Este es el id oficial: " + codigo);

			String codigoOrden = codigo.toString();
			// nuevoExistencia.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.getUnidad().setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
			UnidadLabo uni = new UnidadLabo();

			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			switch (codigoOrden.length()) {
			case 1:
				nuevoOrdeninventario
						.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + "0000" + codigoOrden + "-" + anio);
				break;
			case 2:
				nuevoOrdeninventario.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + "000" + codigoOrden + "-" + anio);
				break;
			case 3:
				nuevoOrdeninventario.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + "00" + codigoOrden + "-" + anio);
				break;
			case 4:
				nuevoOrdeninventario.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + "0" + codigoOrden + "-" + anio); // DC-E-2217
				break;
			case 5:
				nuevoOrdeninventario.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + codigoOrden + "-" + anio); // DC-E-2217
				break;

			default:
				break;
			}

			System.out.println("Este es el nuevo id: " + nuevoOrdeninventario.getIdOrdeninventario());
		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/****** Cambiar Id de Movimientos Inventarios ****/
	public void cambiarIdMov(Ordeninventario ordeninventario) {
		int i = 0;
		for (Movimientosinventario movimientosinventario : nuevoMovimientoInventarios) {

			movimientosinventario.setOrdeninventario(ordeninventario);
			movimientosinventario.setCantidadMov(movimientosinventario.getSaldoE());
			movimientosinventario.setCantidadDmt(0);

			nuevoMovimientoInventarios.set(i, movimientosinventario);

			i++;
		}
	}

	/****** Cambiar valor de tabla ****/
	public Existencia cambiarDatosExistencia(String id) {
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

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACI�N", mensaje));

	}

	/****** Manejo de paneles ****/
	public void cambiarPanel() {

		if (getIdTemporal().equals("1")) {
			mensajeError("Debe seleccionar un tipo de Inventario");
		} else if (getIdTemporal().equals("7")) {
			RequestContext context = RequestContext.getCurrentInstance();
			/*
			 * context.execute("PF('ingresarMI').show();");
			 * System.out.println("este es el cambio de combo" +
			 * getTipoOrdenSelect().getNombreToi());
			 */
			System.out.println("Este es tranferencia");
		} else {
			try {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('ingresarMI').show();");
				// System.out.println("este es el cambio de combo" +
				// getTipoOrdenSelect().getNombreToi());
			} catch (Exception e) {
				e.printStackTrace();
				mensajeError("Debe seleccionar un tipo de Inventario");
			}

		}

	}

	public void validarPanelRegistro() {
		try {
			String fecha = obtenerFecha();
			String mes = obtenerMes();

			String[] parts = fecha.split("-");

			String anio = parts[0];
			String m = parts[1];

			if (buscarFechaSaldoEx(anio, m) == false) {

				mensajeError("No estan registrado el mes (" + mes + ") Cierre Saldo existecia mensual");
				System.out.println("Esta entrando a la funcion");

			} else {

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('nuevoMI').show();");

			}

		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error");
		}
	}

	/****** Metodos de existencias ****/

	public void cargarExistencias() {

		try {

			// filtrarExistencias =
			// movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);

			System.out.println("Existencias consultadas: " + getExistencias().size());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void seleccionarExistencia() {

		try {
			getNuevoMovimientoInventario().setIdExistencia(getSelectExistencia().getIdExistencia());
			mensajeInfo("Se seleccion� la Existencia (" + selectExistencia.getIdExistencia());
			System.out.println("este es el id de existencia: " + selectExistencia.getIdExistencia() + " "
					+ getNuevoMovimientoInventario().getIdExistencia());

			setExistencia(selectExistencia);

			selectExistencia = new Existencia();

		} catch (Exception e) {
			mensajeError("No se ha seleccionado ninguna Existencia");
		}

	}

	/****** Setear valos de combo para validacion ****/
	public void cambiarCombo(String id) {
		// System.out.println("Este es valor que trae: " + id);
		setIdTemporal(id);
		// System.out.println("Este es valor setteado: " + getIdTemporal());
	}

	/****** Metodos de manejo de fechas ****/

	public String obtenerFecha() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
		// System.out.println("Fecha: " + sdf.format(calendar.getTime()));

		String fecha = sdf.format(calendar.getTime());

		return fecha;

	}

	public String obtenerMes() {
		Calendar calendar = Calendar.getInstance();

		SimpleDateFormat mes = new SimpleDateFormat("MMMM");

		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

		// System.out.println("Fecha: " + mes.format(calendar.getTime()));

		String fecha = mes.format(calendar.getTime());

		return fecha;
	}

	private boolean buscarFechaSaldoEx(String anio, String mes) {

		/*
		 * try { saldoExistencias =
		 * saldoExistenciaI.getAll(SaldoExistencia.class); } catch (Exception e)
		 * {
		 * 
		 * e.printStackTrace(); }
		 */

		boolean resultado = false;

		int a = Integer.parseInt(anio);
		int m = Integer.parseInt(mes);

		for (SaldoExistencia saldoExistencia : saldoExistencias) {
			if (saldoExistencia.getId().getAnio() == a && saldoExistencia.getId().getMes() == m) {
				resultado = true;
				saldoExistencia = new SaldoExistencia();
				break;
			} else {
				resultado = false;
			}

		}

		return resultado;
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

	public SaldoExistencia getSaldoExistencia() {
		return saldoExistencia;
	}

	public void setSaldoExistencia(SaldoExistencia saldoExistencia) {
		this.saldoExistencia = saldoExistencia;
	}

	public List<SaldoExistencia> getSaldoExistencias() {
		return saldoExistencias;
	}

	public void setSaldoExistencias(List<SaldoExistencia> saldoExistencias) {
		this.saldoExistencias = saldoExistencias;
	}

	public String getIdTemporal() {
		return idTemporal;
	}

	public void setIdTemporal(String idTemporal) {
		this.idTemporal = idTemporal;
	}

	public Ordeninventario getNuevoOrdeninventario() {
		return nuevoOrdeninventario;
	}

	public void setNuevoOrdeninventario(Ordeninventario nuevoOrdeninventario) {
		this.nuevoOrdeninventario = nuevoOrdeninventario;
	}

	public Ordeninventario getOrdeninventario() {
		return ordeninventario;
	}

	public void setOrdeninventario(Ordeninventario ordeninventario) {
		this.ordeninventario = ordeninventario;
	}

	public List<Ordeninventario> getOrdenInventarios() {
		return ordenInventarios;
	}

	public void setOrdenInventarios(List<Ordeninventario> ordenInventarios) {
		this.ordenInventarios = ordenInventarios;
	}

	public List<Ordeninventario> getFilterOrdenInventarios() {
		return filterOrdenInventarios;
	}

	public void setFilterOrdenInventarios(List<Ordeninventario> filterOrdenInventarios) {
		this.filterOrdenInventarios = filterOrdenInventarios;
	}

	public UnidadLabo getUnidadLabo() {
		return unidadLabo;
	}

	public void setUnidadLabo(UnidadLabo unidadLabo) {
		this.unidadLabo = unidadLabo;
	}

	public List<Tipordeninv> getTemptipordeninvs() {
		return temptipordeninvs;
	}

	public void setTemptipordeninvs(List<Tipordeninv> temptipordeninvs) {
		this.temptipordeninvs = temptipordeninvs;
	}

}
