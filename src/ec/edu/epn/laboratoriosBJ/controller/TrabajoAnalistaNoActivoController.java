package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
//import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
//import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

//import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO;
import ec.edu.epn.laboratorioBJ.beans.SaldoExistenciaDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Detallemetodo;
//import ec.edu.epn.laboratorioBJ.beans.NormaDAO;
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.OrdenTrabajo;
import ec.edu.epn.laboratorioBJ.entities.Ordeninventario;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.SaldoExistencia;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.TipoJustificacion;
import ec.edu.epn.laboratorioBJ.entities.Tipordeninv;
//import ec.edu.epn.laboratorioBJ.entities.Norma;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "trabajoAnalistaNoActivoController")
@SessionScoped
public class TrabajoAnalistaNoActivoController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/*****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleOrdenDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO")
	private DetalleOrdenDAO detalleOrdenI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MovimientosInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO")
	private MovimientosInventarioDAO movimientoInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenTrabajoDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO")
	private OrdenTrabajoDAO ordenTrabajoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/SaldoExistenciaDAOImplement!ec.edu.epn.laboratorioBJ.beans.SaldoExistenciaDAO")
	private SaldoExistenciaDAO saldoExistenciaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO")
	private OrdenInventarioDAO ordenInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;
	/****************************************************************************/
	/** VARIABLES DE LA CLASE **/
	Utilidades utilidades = new Utilidades();

	/** Orden Trabajo **/
	private OrdenTrabajo ordenTrabajo; // Orden interno

	/** Detalle Orden Interna **/
	private Detalleorden detalleOrden;
	private List<Detalleorden> detallesOrden = new ArrayList<Detalleorden>();

	/** Metodo **/
	private List<Metodo> metodos = new ArrayList<Metodo>();
	private Metodo metodoNA = new Metodo();

	/** Variables **/
	private List<Movimientosinventario> movimientoInventarios = new ArrayList<>();
	private Movimientosinventario nuevoMovimientoInventario;
	private Movimientosinventario movimientoInventario;

	/** Variables OrdenInv **/
	private Ordeninventario nuevoOrdeninventario;

	// select de Tipo Orden Inventario (8)
	private Tipordeninv tipordeninv;

	// existencias
	private Existencia existencia;
	private List<Existencia> tempExistencias = new ArrayList<>();

	// detalle Metodo
	private List<Detallemetodo> listaDetalleMetodo = new ArrayList<>();
	private List<Detallemetodo> filtroDetalleMetodo = new ArrayList<>();
	private List<Detallemetodo> listaDetalleMetodoTemp = new ArrayList<>();
	private Detallemetodo nuevoDetallemetodo = new Detallemetodo();
	private Detallemetodo detallemetodo = new Detallemetodo();

	// saldo Existencia
	private SaldoExistencia saldoExistencia;
	private List<SaldoExistencia> saldoExistencias = new ArrayList<>();

	// justificacion
	private List<TipoJustificacion> tipoJustificacions = new ArrayList<>();
	private TipoJustificacion tipoJustificacion = new TipoJustificacion();

	private int tempAdd;
	private int numDetalleMetodos;

	// unidad
	UnidadLabo unidadLabo;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			cargarDetalleOT();
			metodoNA = ordenTrabajoI.findMetodoById("120");

			// init movimiento inventario
			// movimientoInventarioI.getAll(Movimientosinventario.class);

			nuevoMovimientoInventario = new Movimientosinventario();
			movimientoInventario = new Movimientosinventario();
			nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));

			// init Orden inventario
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			nuevoOrdeninventario = new Ordeninventario();
			nuevoOrdeninventario.setFechaingresoOi(new Date());

			// init de Tipo Orden Inventario
			tipordeninv = ordenTrabajoI.findTipoInvById("8");

			// init de Existencia
			existencia = new Existencia();

			// init de Saldo Existencia
			saldoExistencia = new SaldoExistencia();
			saldoExistencias = saldoExistenciaI.listaSaldoExistenciaAñoActual();
			System.out.println("Saldo Existencias consultadas: " + saldoExistencias.size());

			// init Unidad
			unidadLabo = ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			nuevoOrdeninventario.setUnidad(unidadLabo);

			nuevoDetallemetodo = new Detallemetodo();
			detallemetodo = new Detallemetodo();

			tempAdd = 0;
			numDetalleMetodos = 999;

			setDetalleOrden(new Detalleorden());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validarPanelSaldo() {
		try {
			String fecha = obtenerFecha();
			String mes = obtenerMes();

			String[] parts = fecha.split("-");

			String anio = parts[0];
			String m = parts[1];

			if (buscarFechaSaldoEx(anio, m) == false) {

				utilidades.mensajeError("No estan registrado el mes (" + mes + ") Cierre Saldo existecia mensual");

			} else {

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('descargaOT').show();");

			}

		} catch (Exception e) {
			e.printStackTrace();
			utilidades.mensajeError("Ha ocurrido un error");
		}
	}

	private boolean buscarFechaSaldoEx(String anio, String mes) {

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

	// metodos limpiar datos
	public void limpiarTodosCampos() {
		limpiarMovInventario();
		listaDetalleMetodo.clear();
		listaDetalleMetodoTemp.clear();
		tipoJustificacions.clear();
		movimientoInventarios.clear();
		metodos.clear();
		tipordeninv = ordenTrabajoI.findTipoInvById("8");
		tempAdd = 0;
		numDetalleMetodos = 999;
		saldoExistencias = saldoExistenciaI.listaSaldoExistenciaAñoActual();

	}

	public void limpiarMovInventario() {
		nuevoMovimientoInventario = new Movimientosinventario();
		movimientoInventario = new Movimientosinventario();
		nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));

	}

	public void validarDetalleM() {
		RequestContext context = RequestContext.getCurrentInstance();
		nuevoMovimientoInventario.setIdExistencia(nuevoDetallemetodo.getIdExistencia());
		nuevoMovimientoInventario.setCantidadDmt(nuevoDetallemetodo.getCantidadDmt());
		nuevoMovimientoInventario
				.setSaldoE(cambiarDatosExistencia(nuevoDetallemetodo.getIdExistencia()).getCantidadE());
		tipoJustificacions = detalleOrdenI.listarTipoJustificacion();

		context.update("formNuevoDescarga");

	}

	// Metodos de Cargar Datos

	public void cargarDetalleOT() {

		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			detallesOrden = detalleOrdenI.listarDetalleOrdenByUsuarioNoActivo((int) su.id_usuario_log, uni.getCodigoU());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void cargarDetalleMetodo() {

		try {
			RequestContext context = RequestContext.getCurrentInstance();
			if (listaDetalleMetodo.size() == 0) {
				listaDetalleMetodo = detalleOrdenI.listarDetalleMetodoById(detalleOrden.getIdMetodo());
				filtroDetalleMetodo = listaDetalleMetodo;
				numDetalleMetodos = filtroDetalleMetodo.size();
				context.update("formNuevoDescarga");
				if (listaDetalleMetodo.size() == 0) {
					utilidades.mensajeInfo("No hay registros.");
				}
			} else {
				System.out.println("Ya hay registros previos");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void cargarMetodo(Detalleorden detalleO) {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			System.out.println("Esta entrando a la funcion de metodo: " + detalleOrden.getIdServicio());
			metodos = ordenTrabajoI.listarMetodosByIdServicio(detalleO.getIdServicio());
			context.update("formEditarMetodoOT");

			if (metodos.size() == 0) {
				metodos.add(metodoNA);
			}

		} catch (Exception e) {
			// utilidades.mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}
	}

	public void cargarJustificacion() {
		tipoJustificacions = detalleOrdenI.listarTipoJustificacion();
	}

	/*** Metodos CRUD ***/

	// Metodos CRUD ordenInventario
	public void agregarOrdenI() {
		try {

			/** Creacion del IdOrdenInv **/
			obtenerIdOrdenId();

			/** Seteo de Selects (ComboBox) **/
			nuevoOrdeninventario.setTipordeninv(tipordeninv);
			nuevoOrdeninventario.setEstadoOi("PENDIENTE");
			nuevoOrdeninventario.setResponsableOi(su.nombre_usuario_logeado);

			/** Seteo de Ids en la lista de Movimientos Inventarios **/
			cambiarIdMov(nuevoOrdeninventario);
			nuevoOrdeninventario.setDetalleorden(detalleOrden);

			/** GUARDAR **/
			ordenInventarioI.save(nuevoOrdeninventario);
			guardarMovimientosInv();
			actualizarExistencias();

			utilidades.mensajeInfo("La nueva descarga ( " + nuevoOrdeninventario.getIdOrdeninventario()
					+ " ) se ha almacenado exitosamente");

			// cambiar estado de detalleorden
			detalleOrden.setEstadoDot("DOWNLOAD");

			modificarDetalleOt();

			nuevoOrdeninventario = new Ordeninventario();
			limpiarTodosCampos();
			cargarDetalleOT();

		} catch (Exception e) {

			// utilidades.mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/****** Metodo para construir el id ****/
	public void obtenerIdOrdenId() {
		try {
			/** Consulta de COdigoUnidad **/
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			/** Creacion del Obtener fecha **/
			String fecha = obtenerFechaActual();
			// String fecha = "2020-05-05";
			String[] partsFecha = fecha.split("-");
			String anio = partsFecha[0];

			/** Creacion del IdOrdenInv **/
			String codigoAux = ordenInventarioI.maxIdOrdenI(su.UNIDAD_USUARIO_LOGEADO, fecha);

			if (codigoAux == null) {
				codigoAux = (uni.getCodigoU() + "-OI-0000-" + anio);
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

			unidadLabo = ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			nuevoOrdeninventario.setUnidad(unidadLabo);

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
			// utilidades.mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/****** Cambiar Id de Movimientos Inventarios ****/
	public void cambiarIdMov(Ordeninventario ordeninventario) {
		int i = 0;
		for (Movimientosinventario movimientosinventario : movimientoInventarios) {

			movimientosinventario.setOrdeninventario(ordeninventario);
			movimientosinventario.setFechaMi(ordeninventario.getFechaingresoOi());

			movimientoInventarios.set(i, movimientosinventario);
			i++;
		}
	}

	/** Actualizar Existencias en la base de datos **/
	public void actualizarExistencias() {
		System.out.println(tempExistencias.size());
		for (Existencia existencia : tempExistencias) {
			try {
				existenciasI.update(existencia);
			} catch (Exception e) {
				System.out.println(e);
				// e.printStackTrace();
			}

		}
	}

	// Metodos CRUD movimientosInventario

	/** Guarda MI en la base de datos **/
	public void guardarMovimientosInv() {
		for (Movimientosinventario m : movimientoInventarios) {
			try {
				movimientoInventarioI.save(m);
			} catch (Exception e) {
				System.out.println(e);
				// e.printStackTrace();
			}

		}
	}

	public void agregarMovimientoInv() {

		existencia = cambiarDatosExistencia(nuevoMovimientoInventario.getIdExistencia());
		double resultado;
		double cantidadMo = nuevoMovimientoInventario.getCantidadMov().doubleValue();
		double cantidadE = existencia.getCantidadE().doubleValue();

		if (cantidadMo == 0) {
			nuevoMovimientoInventario.setCantidadMov(BigDecimal.valueOf(movimientoInventario.getCantidadDmt()));
			cantidadMo = nuevoMovimientoInventario.getCantidadDmt();

		}

		if (cantidadMo <= cantidadE) {
			resultado = cantidadE - cantidadMo;
			// resultado =
			// Double.parseDouble(utilidades.cambiarFormatoDouble(resultado));
			existencia.setCantidadE(BigDecimal.valueOf(resultado));
			nuevoMovimientoInventario.setSaldoE(getExistencia().getCantidadE());
			nuevoMovimientoInventario.setDism(BigDecimal.valueOf(cantidadMo));
			nuevoMovimientoInventario.setIncrem(new BigDecimal(0));
			nuevoMovimientoInventario.setCantidadMov(BigDecimal.valueOf(cantidadMo));

			// System.out
			// .println("Este es la nueva cantidad ajustada (Existencia): " +
			// existencia.getCantidadE().doubleValue());
			// System.out.println(
			// "Este es la nueva cantidad ajustada (Mov Inventario): " +
			// existencia.getCantidadE().doubleValue());

			// Añadir a la lista temporal de Existenciass
			tempExistencias.add(getExistencia());

			movimientoInventarios.add(nuevoMovimientoInventario);

			if (getTempAdd() == 0) {
				eliminarDetalleMetodoById(nuevoDetallemetodo);
			}

			limpiarMovInventario();
			existencia = new Existencia();

			// utilidades.mensajeInfo("Se ha registrado.");
		} else if (cantidadE <= 0) {
			utilidades.mensajeError(
					"Ya no hay saldo para ejecutar este registro, haga un ajuste de inventario positivo en la interfaz (Movimientos de Inventario)");
			System.out.println("ayudaa2" + nuevoDetallemetodo.getIdExistencia());
			listaDetalleMetodoTemp.add(nuevoDetallemetodo);
			System.out.println("dasd");
		} else {
			utilidades.mensajeError("La cantidad es mayor al saldo");
			System.out.println("ayudaa1");
			listaDetalleMetodoTemp.add(nuevoDetallemetodo);
			System.out.println("asdasd");
		}

		// context.execute("PF('ingresarMI').hide();");

	}

	public void modificarMovimientoInv() {

		eliminarExistenciaTemp(movimientoInventario.getIdExistencia());
		eliminarMovimientoInv(movimientoInventario);

		nuevoMovimientoInventario = movimientoInventario;

		setTempAdd(1);

		agregarMovimientoInv();

		utilidades.mensajeInfo("Se ha modificado correctamente");

	}

	public void eliminarExistenciaTemp(String id) {
		System.out.println("Este la lista de existencia inicial: " + tempExistencias.size());

		for (Existencia existencia : tempExistencias) {
			if (existencia.getIdExistencia().equals(id)) {
				tempExistencias.remove(existencia);
				break;
			}
		}

		System.out.println("Este la lista de existencia final: " + tempExistencias.size());
	}

	public void eliminarMovimientoInv(Movimientosinventario m) {
		System.out.println("Este la lista de mov inicial: " + movimientoInventarios.size());

		for (Movimientosinventario movimientosinventario : movimientoInventarios) {
			if (movimientosinventario.getIdExistencia().equals(m.getIdExistencia())) {
				movimientoInventarios.remove(movimientosinventario);
				break;
			}
		}

		System.out.println("Este la lista de mov final: " + movimientoInventarios.size());
	}

	public void agregarTodosMovimientos() {
		RequestContext context = RequestContext.getCurrentInstance();
		setTempAdd(1);

		for (Detallemetodo detallemetodo : listaDetalleMetodo) {
			limpiarMovInventario(); // este va en caso el formulario ya cuente
									// con datos
			nuevoMovimientoInventario.setIdExistencia(detallemetodo.getIdExistencia());
			nuevoMovimientoInventario.setCantidadDmt(detallemetodo.getCantidadDmt());
			nuevoMovimientoInventario.setSaldoE(cambiarDatosExistencia(detallemetodo.getIdExistencia()).getCantidadE());
			nuevoMovimientoInventario.setIdTipojust("1");
			nuevoDetallemetodo = detallemetodo;
			agregarMovimientoInv();
		}

		System.out.println("este:" + listaDetalleMetodoTemp.size());

		if (listaDetalleMetodoTemp.size() == 0) {
			listaDetalleMetodo.clear();
		} else {

			listaDetalleMetodo.clear();
			listaDetalleMetodo = listaDetalleMetodoTemp;
			System.out.println("estes: " + listaDetalleMetodo.size());
		}

		context.update("formNuevoDescarga");
		context.update("formDescargarOT");
		utilidades.mensajeInfo("Se han agregado todas las descargas.");

	}

	// metodos de fecha
	public String obtenerFechaActual() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String fecha = sdf.format(calendar.getTime());

		return fecha;

	}

	public String obtenerFecha() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String date = sdf.format(calendar.getTime());

		String[] parts = date.split("-");

		String dia = parts[2];

		System.out.println("Fecha de hoy: " + date);
		System.out.println("Este es el dia: " + dia);

		if (dia.equals("31")) {
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
			System.out.println("Fecha de hoy restada un dia: " + sdf.format(calendar.getTime()));

			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
			System.out.println("Fecha de hoy restado el mes: " + sdf.format(calendar.getTime()));

		} else {
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
			System.out.println(
					"Fecha de hoy restado el mes sin tomar en cuenta el dia: " + sdf.format(calendar.getTime()));
		}

		System.out.println("Fecha restada: " + sdf.format(calendar.getTime()));

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

	// Metodos CRUD para otros modulos

	public void eliminarDetalleMetodoById(Detallemetodo d) {
		for (Detallemetodo detallemetodo : listaDetalleMetodo) {
			if (detallemetodo.getIdDetallemetodo() == d.getIdDetallemetodo()) {
				listaDetalleMetodo.remove(detallemetodo);
				break;
			}
		}
	}

	public void modificarDetalleOt() {
		try {
			detalleOrdenI.update(detalleOrden);
			cargarDetalleOT();
			utilidades.mensajeInfo("Se ha modificado correctamente.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****** Cambiar valor de tabla ****/
	public Existencia cambiarDatosExistencia(String id) {
		Existencia existenciatemp = movimientoInventarioI.buscarExistenciaById(id);
		return existenciatemp;
	}

	public void modificarEstadoOrden() {
		try {

			detalleOrden.setEstadoDot("CLOSED");

			detalleOrdenI.update(detalleOrden);
			cargarDetalleOT();
			utilidades.mensajeInfo("Se ha cerrado con exito.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validarCantEmpl(FacesContext ctx, UIComponent component, BigDecimal value) throws ValidatorException {
		// String prueba = value.toString();

		double cantidadMo = value.doubleValue();
		double cantidadE = nuevoMovimientoInventario.getSaldoE().doubleValue();

		if (cantidadMo <= cantidadE) {
			// Ajuste negativo a la existencia/Mov
			agregarMovimientoInv();
			return;

		} else {
			// utilidades.mensajeError("La cantidad es mayor que el saldo
			// existente");
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error",
					"La cantidad es mayor que el saldo existente"));
		}

	}

	/** Reemplazar valores de tabla **/

	public String metodo(String idMetodo) {
		String nombre = "";
		
		System.out.println("Nomnbre: " + idMetodo );
		
		Metodo metodo = new Metodo();

		metodo = detalleOrdenI.findMetodoById(idMetodo);

		if (metodo == null) {
			nombre = "N/A";
		} else {
			nombre = metodo.getNombreMt();
		}

		return nombre;
	}

	public String justificacion(String idJustificacion) {
		String nombre = "";

		TipoJustificacion tipoJustificacion = new TipoJustificacion();

		tipoJustificacion = detalleOrdenI.findTipoJustificacionById(idJustificacion);

		if (tipoJustificacion == null) {
			nombre = "N/A";
		} else {
			nombre = tipoJustificacion.getNombreJust();
		}

		return nombre;
	}

	public String reemplazarStringVacios(String value) {

		if (value == null || value.equals("                     ") || value.equals("")) {
			value = "N/A";
		}

		return value;
	}

	public String servicio(String idServicio) {
		String nombre = "";

		Servicio servicio = new Servicio();

		servicio = detalleOrdenI.findServicioById(idServicio);

		if (servicio == null) {
			nombre = "N/A";
		} else {
			nombre = servicio.getNombreS();
			if (nombre.length() >= 25) {
				nombre = nombre.substring(0, 25);
			}
		}

		return nombre;
	}

	// diable buttons
	public boolean disabledButton() {
		System.out.println(movimientoInventarios.size() + " " + numDetalleMetodos);

		if (movimientoInventarios.size() == numDetalleMetodos) {
			return true;
		} else {
			return false;
		}

	}

	public boolean disabledButtonAgregarTodoDescarga() {

		if (listaDetalleMetodo.size() == 0) {
			return false;
		} else {
			return true;
		}

	}

	public boolean disabledButtonAgregarDescarga() {

		if (listaDetalleMetodo.size() == 0) {
			return false;
		} else {
			if (nuevoMovimientoInventario.getIdExistencia() == null) {
				return false;
			} else {
				return true;
			}

		}

	}

	/****** Getter y Setter ****/
	public Detalleorden getDetalleOrden() {
		return detalleOrden;
	}

	public void setDetalleOrden(Detalleorden detalleOrden) {
		this.detalleOrden = detalleOrden;
	}

	public List<Detalleorden> getDetallesOrden() {
		return detallesOrden;
	}

	public void setDetallesOrden(List<Detalleorden> detallesOrden) {
		this.detallesOrden = detallesOrden;
	}

	public List<Metodo> getMetodos() {
		return metodos;
	}

	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	}

	public Metodo getMetodoNA() {
		return metodoNA;
	}

	public void setMetodoNA(Metodo metodoNA) {
		this.metodoNA = metodoNA;
	}

	public OrdenTrabajo getOrdenTrabajo() {
		return ordenTrabajo;
	}

	public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
		this.ordenTrabajo = ordenTrabajo;
	}

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

	public Movimientosinventario getMovimientoInventario() {
		return movimientoInventario;
	}

	public void setMovimientoInventario(Movimientosinventario movimientoInventario) {
		this.movimientoInventario = movimientoInventario;
	}

	public Ordeninventario getNuevoOrdeninventario() {
		return nuevoOrdeninventario;
	}

	public void setNuevoOrdeninventario(Ordeninventario nuevoOrdeninventario) {
		this.nuevoOrdeninventario = nuevoOrdeninventario;
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

	public Tipordeninv getTipordeninv() {
		return tipordeninv;
	}

	public void setTipordeninv(Tipordeninv tipordeninv) {
		this.tipordeninv = tipordeninv;
	}

	public List<Detallemetodo> getListaDetalleMetodo() {
		return listaDetalleMetodo;
	}

	public void setListaDetalleMetodo(List<Detallemetodo> listaDetalleMetodo) {
		this.listaDetalleMetodo = listaDetalleMetodo;
	}

	public Detallemetodo getNuevoDetallemetodo() {
		return nuevoDetallemetodo;
	}

	public void setNuevoDetallemetodo(Detallemetodo nuevoDetallemetodo) {
		this.nuevoDetallemetodo = nuevoDetallemetodo;
	}

	public List<Detallemetodo> getFiltroDetalleMetodo() {
		return filtroDetalleMetodo;
	}

	public void setFiltroDetalleMetodo(List<Detallemetodo> filtroDetalleMetodo) {
		this.filtroDetalleMetodo = filtroDetalleMetodo;
	}

	public List<TipoJustificacion> getTipoJustificacions() {
		return tipoJustificacions;
	}

	public void setTipoJustificacions(List<TipoJustificacion> tipoJustificacions) {
		this.tipoJustificacions = tipoJustificacions;
	}

	public TipoJustificacion getTipoJustificacion() {
		return tipoJustificacion;
	}

	public void setTipoJustificacion(TipoJustificacion tipoJustificacion) {
		this.tipoJustificacion = tipoJustificacion;
	}

	public List<Existencia> getTempExistencias() {
		return tempExistencias;
	}

	public void setTempExistencias(List<Existencia> tempExistencias) {
		this.tempExistencias = tempExistencias;
	}

	public int getTempAdd() {
		return tempAdd;
	}

	public void setTempAdd(int tempAdd) {
		this.tempAdd = tempAdd;
	}

	public int getNumDetalleMetodos() {
		return numDetalleMetodos;
	}

	public void setNumDetalleMetodos(int numDetalleMetodos) {
		this.numDetalleMetodos = numDetalleMetodos;
	}

	public Detallemetodo getDetallemetodo() {
		return detallemetodo;
	}

	public void setDetallemetodo(Detallemetodo detallemetodo) {
		this.detallemetodo = detallemetodo;
	}

	public List<Detallemetodo> getListaDetalleMetodoTemp() {
		return listaDetalleMetodoTemp;
	}

	public void setListaDetalleMetodoTemp(List<Detallemetodo> listaDetalleMetodoTemp) {
		this.listaDetalleMetodoTemp = listaDetalleMetodoTemp;
	}

}
