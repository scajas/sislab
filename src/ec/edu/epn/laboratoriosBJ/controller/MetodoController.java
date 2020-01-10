package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
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
import javax.faces.validator.ValidatorException;
import javax.faces.component.UIComponent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
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

@ManagedBean(name = "metodoController")
@SessionScoped
public class MetodoController implements Serializable {

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
	private List<Movimientosinventario> tempMovimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> filtroMovimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> nuevoMovimientoInventarios = new ArrayList<>();
	private Movimientosinventario nuevoMovimientoInventario;
	private Movimientosinventario nuevoMovimientoInventarioAux;
	private Movimientosinventario movInventario;

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
	private List<Existencia> transExistencias = new ArrayList<>();
	private List<Existencia> tempExistencias = new ArrayList<>();
	private List<Existencia> filtrarTransExistencias = new ArrayList<>();
	private List<Existencia> filtrarExistencias = new ArrayList<>();
	private Existencia existencia;
	private Existencia existenciaAux;
	private Existencia selectExistencia;

	// saldo Existencia
	private SaldoExistencia saldoExistencia;
	private List<SaldoExistencia> saldoExistencias = new ArrayList<>();

	// unidad
	UnidadLabo unidadLabo;

	// id temporal
	private String idTemporal;

	// Num Aux
	private int aux;

	// fechaAuxFiltro
	private String fechaFiltro;
	private Date fechaFiltroAux;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {
			// Long idUsuario = su.id_usuario_log;
			// init movimiento inventario
			// movimientoInventarioI.getAll(Movimientosinventario.class);
			nuevoMovimientoInventarios = new ArrayList<>();
			tempMovimientoInventarios = new ArrayList<>();
			nuevoMovimientoInventario = new Movimientosinventario();
			nuevoMovimientoInventarioAux = new Movimientosinventario();
			movInventario = new Movimientosinventario();
			nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));
			nuevoMovimientoInventarioAux.setCantidadMov(new BigDecimal(0));

			// System.out.println("Orde de inventarios consultadas: " +
			// movimientoInventarios.size());

			// init Orden inventario
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			ordenInventarios = ordenInventarioI.getListOIById(uni.getCodigoU());
			filterOrdenInventarios = ordenInventarios;
			ordeninventario = new Ordeninventario();
			nuevoOrdeninventario = new Ordeninventario();
			System.out.println("Orde de inventarios consultadas: " + ordenInventarios.size());
			nuevoOrdeninventario.setFechaingresoOi(new Date());
			System.out.println("Fecha Orden: " + nuevoOrdeninventario.getFechaingresoOi());

			// init de Tipo Orden Inventario
			temptipordeninvs = new ArrayList<Tipordeninv>();
			tipordeninvs = tipoOrdenInventarioI.orderById();
			llenarCombo();
			tipordeninv = new Tipordeninv(); // existenciasI.reemplazarNullPresentacion();
			tipoOrdenSelect = new Tipordeninv();

			// init de Existencia
			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
			System.out.println("Existencias consultadas: " + existencias.size());
			transExistencias = existencias;
			System.out.println("Existencias transferencia consultadas: " + transExistencias.size());
			// filtrarTransExistencias = existencias;
			// filtrarExistencias = new ArrayList<Existencia>();
			tempExistencias = new ArrayList<Existencia>();
			selectExistencia = new Existencia();
			existencia = new Existencia();
			existenciaAux = new Existencia();

			// init de Saldo Existencia
			saldoExistencia = new SaldoExistencia();
			saldoExistencias = saldoExistenciaI.listaSaldoExistenciaAñoActual();
			System.out.println("Saldo Existencias consultadas: " + saldoExistencias.size());

			// init Unidad
			unidadLabo = ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			nuevoOrdeninventario.setUnidad(unidadLabo);

			// init id temporal
			idTemporal = "1";

			// init aux
			// aux = 0;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/******
	 * Agregar/Editar/Eliminar Movimiento de Inventario/Existencias a lista
	 * temporal
	 ****/

	public void holaMundo() {
		RequestContext context = RequestContext.getCurrentInstance();
		for (Movimientosinventario m : nuevoMovimientoInventarios) {
			if (m.getIdExistencia().equals(movInventario.getIdExistencia())) {
				
				System.out.println("Obeservaciones: " + m.getObservacionesMov());
				System.out.println("Envases: " + m.getEnvaseMov());
				System.out.println("Este es el editar: " + movInventario.getEnvaseMov());
				
				context.execute("PF('editarMI').hide();");
	
				break;
			} else {

			}

		}

	}

	public void validarSaldoEdit(FacesContext ctx, UIComponent component, BigDecimal value) throws ValidatorException {
		// String prueba = value.toString();
		Existencia e = cambiarDatosExistencia(movInventario.getIdExistencia());
		double resultado;
		double cantidadMo = value.doubleValue();
		double cantidadE = e.getCantidadE().doubleValue();

		if (getIdTemporal().equals("2") || getIdTemporal().equals("3")) {

			if (cantidadMo <= cantidadE) {
				// Ajuste negativo a la existencia/Mov
				resultado = cantidadE - cantidadMo;

				e.setCantidadE(BigDecimal.valueOf(resultado));

				movInventario.setSaldoE(e.getCantidadE());
				movInventario.setDism(BigDecimal.valueOf(cantidadMo));
				movInventario.setIncrem(new BigDecimal(0));

				editarExistenciaTemporal(e);
				return;

			} else {
				// mensajeError("La cantidad es mayor que el saldo existente");
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error",
						"La cantidad es mayor que el saldo existente"));
			}

		} else {

			resultado = cantidadE + cantidadMo;

			e.setCantidadE(BigDecimal.valueOf(resultado));

			movInventario.setSaldoE(e.getCantidadE());
			movInventario.setIncrem(BigDecimal.valueOf(cantidadMo));
			movInventario.setDism(new BigDecimal(0));

			editarExistenciaTemporal(e);

			return;

		}

	}

	public void editarExistenciaTemporal(Existencia value) {
		int i = 0;
		for (Existencia e : tempExistencias) {
			if (e.getIdExistencia().equals(value.getIdExistencia())) {

				// System.out.println("este es el saldo existencia sin editar: "
				// + e.getCantidadE().doubleValue());
				//
				// System.out.println("este es la saldo modificado existencia: "
				// + value.getCantidadE().doubleValue());

				tempExistencias.set(i, value);

				break;
			} else {
				i++;
			}

		}
	}

	public void limpiarCampos() {

		try {

			nuevoMovimientoInventarios.clear();
			tempMovimientoInventarios.clear();
			nuevoMovimientoInventario = new Movimientosinventario();
			movInventario = new Movimientosinventario();
			nuevoMovimientoInventarioAux = new Movimientosinventario();
			nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));
			nuevoMovimientoInventarioAux.setCantidadMov(new BigDecimal(0));

			// System.out.println("Orde de inventarios consultadas: " +
			// movimientoInventarios.size());

			// init Orden inventario
			ordeninventario = new Ordeninventario();
			nuevoOrdeninventario = new Ordeninventario();

			// init de Tipo Orden Inventario
			tipordeninv = new Tipordeninv();
			tipoOrdenSelect = new Tipordeninv();

			// init de Existencia
			/*
			 * existencias = movimientoInventarioI.listarExistenciaById(su.
			 * UNIDAD_USUARIO_LOGEADO);
			 * System.out.println("Existencias consultadas: " +
			 * existencias.size()); transExistencias = existencias;
			 * System.out.println("Existencias transferencia consultadas: " +
			 * transExistencias.size());
			 */
			filtrarExistencias = new ArrayList<Existencia>();
			filtrarTransExistencias = new ArrayList<Existencia>();
			tempExistencias = new ArrayList<Existencia>();
			selectExistencia = new Existencia();
			existencia = new Existencia();
			existenciaAux = new Existencia();

			// init Unidad
			unidadLabo = ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			nuevoOrdeninventario.setUnidad(unidadLabo);

			// aux = 1;

			// init id temporal
			// idTemporal = "1";

			mensajeInfo("Se ha limpiado todo el formulario");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void limpiarMI() {
		System.out.println("Esta entrando a la funcion MO");
		/*
		 * nuevoMovimientoInventario = new Movimientosinventario();
		 * movimientosinventario = new Movimientosinventario();
		 * nuevoMovimientoInventario.setSaldoE(new BigDecimal(0));
		 * nuevoMovimientoInventarioAux.setSaldoE(new BigDecimal(0));
		 */
	}

	public void cargarMI(String id) {
		tempMovimientoInventarios.clear();
		System.out.println("Lista de MI id: " + id);
		tempMovimientoInventarios = ordenInventarioI.listaMovimientoI(id);
		System.out.println("Lista de MI: " + tempMovimientoInventarios.size());
	}

	public void agregarMovimiento() {

		try {

			RequestContext context = RequestContext.getCurrentInstance();

			double resultado;
			double cantidadMo = nuevoMovimientoInventario.getCantidadMov().doubleValue();
			double cantidadE = existencia.getCantidadE().doubleValue();

			if (getIdTemporal().equals("2") || getIdTemporal().equals("3")) {

				if (cantidadMo <= cantidadE) {

					// Ajuste negativo a la existencia/Mov
					resultado = cantidadE - cantidadMo;

					existencia.setCantidadE(BigDecimal.valueOf(resultado));
					nuevoMovimientoInventario.setSaldoE(getExistencia().getCantidadE());
					nuevoMovimientoInventario.setDism(BigDecimal.valueOf(cantidadMo));
					nuevoMovimientoInventario.setIncrem(new BigDecimal(0));

					System.out.println("Este es la nueva cantidad ajustada (Existencia): "
							+ existencia.getCantidadE().doubleValue());
					System.out.println("Este es la nueva cantidad ajustada (Mov Inventario): "
							+ existencia.getCantidadE().doubleValue());

					// Añadir a la lista temporal de Existenciass
					tempExistencias.add(getExistencia());

					nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
					setMovimientoInventarios(nuevoMovimientoInventarios);
					mensajeInfo(
							"Se ha almacenado (" + nuevoMovimientoInventario.getIdExistencia() + ") correctamente.");

					nuevoMovimientoInventario = new Movimientosinventario();
					existencia = new Existencia();
					context.execute("PF('ingresarMI').hide();");

				} else {
					mensajeError("La cantidad es mayor que el saldo existente");
				}
			} else {
				// Ajuste positivo a la existencia/Mov

				resultado = cantidadMo + cantidadE;
				existencia.setCantidadE(BigDecimal.valueOf(resultado));
				nuevoMovimientoInventario.setSaldoE(getExistencia().getCantidadE());
				nuevoMovimientoInventario.setIncrem(BigDecimal.valueOf(cantidadMo));
				nuevoMovimientoInventario.setDism(new BigDecimal(0));

				System.out.println(
						"Este es la nueva cantidad ajustada (Existencia): " + existencia.getCantidadE().doubleValue());
				System.out.println("Este es la nueva cantidad ajustada (Mov Inventario): "
						+ existencia.getCantidadE().doubleValue());

				// Añadir a la lista temporal de Existencias
				tempExistencias.add(getExistencia());

				nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
				setMovimientoInventarios(nuevoMovimientoInventarios);

				mensajeInfo("Se ha almacenado (" + nuevoMovimientoInventario.getIdExistencia() + ") correctamente.");

				nuevoMovimientoInventario = new Movimientosinventario();
				existencia = new Existencia();

				context.execute("PF('ingresarMI').hide();");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void agregarMovimientoTransf() {

		RequestContext context = RequestContext.getCurrentInstance();

		/** Ajuste del saldo existencia **/

		double resultado;
		double resultadoAux;
		double cantidadMo = nuevoMovimientoInventario.getCantidadMov().doubleValue();
		double cantidadMoAux = nuevoMovimientoInventarioAux.getCantidadMov().doubleValue();
		double cantidadE = existencia.getCantidadE().doubleValue();
		double cantidadEAux = existenciaAux.getCantidadE().doubleValue();

		if (existencia.getUnidadmedida().getIdUmedida() == existenciaAux.getUnidadmedida().getIdUmedida()) {
			System.out.println("Entra a la validacion");
			cantidadMoAux = nuevoMovimientoInventario.getCantidadMov().doubleValue();
			nuevoMovimientoInventarioAux.setCantidadMov(nuevoMovimientoInventario.getCantidadMov());

		}

		resultado = cantidadE - cantidadMo;
		resultadoAux = cantidadEAux + cantidadMoAux;

		// Setteo de objetos y listas

		existencia.setCantidadE(BigDecimal.valueOf(resultado));
		existenciaAux.setCantidadE(BigDecimal.valueOf(resultadoAux));

		tempExistencias.add(getExistencia());
		tempExistencias.add(getExistenciaAux());

		nuevoMovimientoInventario.setSaldoE(getExistencia().getCantidadE());
		nuevoMovimientoInventario.setIncrem(BigDecimal.valueOf(cantidadMo));
		nuevoMovimientoInventario.setDism(new BigDecimal(0));

		nuevoMovimientoInventarioAux.setSaldoE(getExistenciaAux().getCantidadE());
		nuevoMovimientoInventarioAux.setIncrem(new BigDecimal(0));
		nuevoMovimientoInventarioAux.setDism(BigDecimal.valueOf(cantidadMoAux));

		nuevoMovimientoInventarios.add(nuevoMovimientoInventario);

		nuevoMovimientoInventarios.add(nuevoMovimientoInventarioAux);

		setMovimientoInventarios(nuevoMovimientoInventarios);

		mensajeInfo("Se han almacenado (" + nuevoMovimientoInventario.getIdExistencia() + ") y ("
				+ nuevoMovimientoInventarioAux.getIdExistencia() + ") correctamente.");

		nuevoMovimientoInventarioAux = new Movimientosinventario();
		nuevoMovimientoInventario = new Movimientosinventario();

		existencia = new Existencia();
		existenciaAux = new Existencia();

		nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));
		nuevoMovimientoInventarioAux.setCantidadMov(new BigDecimal(0));

		context.execute("PF('ingresarMITrans').hide();");

	}

	/** Guarda MI en la base de datos **/
	public void guardarMovimientosInv() {
		for (Movimientosinventario m : nuevoMovimientoInventarios) {
			try {
				movimientoInventarioI.save(m);
			} catch (Exception e) {
				System.out.println(e);
				// e.printStackTrace();
			}

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

	public void editarMovimiento() {

		try {
			double cantidadMo = movInventario.getCantidadMov().doubleValue();
			double cantidadE = cambiarDatosExistencia(movInventario.getIdExistencia()).getCantidadE().doubleValue();
			if (getIdTemporal().equals("2") || getIdTemporal().equals("3")) {

				if (cantidadMo <= cantidadE) {
					System.out.println("Entra a");
					editarMovimientoTemporal();
					// setMovimientoInventarios(nuevoMovimientoInventarios);

				} else {
					mensajeError("La cantidad es mayor que el saldo existente");
				}
			} else {
				System.out.println("Entra a else");
				editarMovimientoTemporal();
				// setMovimientoInventarios(nuevoMovimientoInventarios);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void editarMovimientoTemporal() {
		int i = 0;
		RequestContext context = RequestContext.getCurrentInstance();
		System.out.println("Entra aL EDITAR");

		for (Movimientosinventario m : nuevoMovimientoInventarios) {
			if (m.getIdExistencia().equals(getMovInventario().getIdExistencia())) {
				nuevoMovimientoInventarios.set(i, getMovInventario());

				mensajeInfo("Se ha editado (" + getMovInventario().getIdExistencia() + ") correctamente.");

				setMovInventario(new Movimientosinventario());

				context.execute("PF('editarMI').hide();");
				// context.update("formAgregarMI");
				break;
			} else {
				i++;
			}
		}
	}

	public void eliminarMovimiento() {

		try {
			nuevoMovimientoInventarios.remove(movInventario);
			setMovimientoInventarios(nuevoMovimientoInventarios);
			mensajeInfo("Se ha eliminado el movimiento de Inventario (" + movInventario.getIdExistencia() + ")");
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

	public long cambiarFecha(long session) {
		// nuevoOrdeninventario.setFechaingresoOi(session);
		Date fecha = new Date(session);
		System.out.println("Si es long :v " + session);
		System.out.println("Esta es la fecha: " + fecha);

		return session;
	}

	public void llenarCombo() {

		for (Tipordeninv tipordeninv : tipordeninvs) {
			if (tipordeninv.getIdTipordeninv() == 1 || tipordeninv.getIdTipordeninv() == 2
					|| tipordeninv.getIdTipordeninv() == 3 || tipordeninv.getIdTipordeninv() == 6
					|| tipordeninv.getIdTipordeninv() == 7) {

				temptipordeninvs.add(tipordeninv);
				System.out.println("Se ha agregado: " + tipordeninv.getNombreToi());

			}
		}

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
			actualizarExistencias();

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			ordenInventarios = ordenInventarioI.getListOIById(uni.getCodigoU());

			mensajeInfo("El orden de inventario ( " + nuevoOrdeninventario.getIdOrdeninventario()
					+ " ) se ha almacenado exitosamente");

			nuevoOrdeninventario = new Ordeninventario();
			movimientoInventarios.clear();

			// Llenar las listas con los nuevos registros
			ordenInventarios = ordenInventarioI.getListOIById(uni.getCodigoU());
			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/* PENDIENTE */
	public void buscar() {
		System.out.println("Esta entrando al buscar");
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
			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/****** Cambiar Id de Movimientos Inventarios ****/
	public void cambiarIdMov(Ordeninventario ordeninventario) {
		int i = 0;
		for (Movimientosinventario movimientosinventario : nuevoMovimientoInventarios) {

			movimientosinventario.setOrdeninventario(ordeninventario);
			movimientosinventario.setFechaMi(ordeninventario.getFechaingresoOi());
			movimientosinventario.setCantidadDmt(0);

			nuevoMovimientoInventarios.set(i, movimientosinventario);
			// movimientoInventarios = nuevoMovimientoInventarios;

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

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Manejo de paneles ****/
	public void cambiarPanel() {

		RequestContext context = RequestContext.getCurrentInstance();
		if (getIdTemporal().equals("1")) {
			mensajeError("Debe seleccionar un tipo de Inventario");
		} else if (getIdTemporal().equals("7")) {

			context.execute("PF('ingresarMITrans').show();");
			System.out.println("Este es tranferencia");
		} else {
			try {
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

	public boolean buscarExistencia(String id) {
		boolean resultado = false;

		for (Movimientosinventario movimientosinventario : nuevoMovimientoInventarios) {
			if (movimientosinventario.getIdExistencia().equals(id)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}

		}

		return resultado;
	}

	public boolean buscarMovInv() {
		return true;
	}

	/****** Metodos de existencias ****/
	public void auxPanel(int a) {
		try {

			setAux(a);
			System.out.println("Este es el valor que trae: " + getAux());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void cargarExistencias(String idPanel) {

		try {

			if (idPanel.equals("7")) {
				transExistencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
				filtrarTransExistencias = transExistencias;
				System.out.println("Existencias transferencia consultadas: " + transExistencias.size());

			} else {
				existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
				filtrarExistencias = existencias;
				System.out.println("Existencias consultadas: " + existencias.size());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void seleccionarExistencia() {

		try {
			RequestContext context = RequestContext.getCurrentInstance();

			if (buscarExistencia(selectExistencia.getIdExistencia()) == false) {

				System.out.println("Este es el auxiliar de panel: " + getAux());

				if (getIdTemporal().equals("7")) {
					if (getAux() == 2) {

						if (validarExistenciaTransf(existencia, selectExistencia) == true) {
							getNuevoMovimientoInventarioAux().setIdExistencia(getSelectExistencia().getIdExistencia());
							mensajeInfo("Se seleccionó la Existencia de bodega de destino ("
									+ selectExistencia.getIdExistencia() + ")");

							setExistenciaAux(selectExistencia);
							selectExistencia = new Existencia();
							filtrarTransExistencias = new ArrayList<Existencia>();
							setAux(0);
							context.execute("PF('listadoExTransf').hide();");
						} else {
							mensajeError("La existencia seleccionada (" + selectExistencia.getIdExistencia()
									+ ") no coincide con la existencia de origen (" + existencia.getIdExistencia()
									+ ")");
						}

					} else if (getAux() == 1) {

						if (validarExistenciaTransf(existenciaAux, selectExistencia) == true) {
							getNuevoMovimientoInventario().setIdExistencia(getSelectExistencia().getIdExistencia());
							mensajeInfo("Se seleccionó la Existencia de bodega de origen ("
									+ selectExistencia.getIdExistencia());

							setExistencia(selectExistencia);
							selectExistencia = new Existencia();
							filtrarTransExistencias = new ArrayList<Existencia>();
							setAux(0);
							context.execute("PF('listadoExTransf').hide();");
						} else {
							mensajeError("La existencia seleccionada (" + selectExistencia.getIdExistencia()
									+ ") no coincide con la existencia de origen (" + existenciaAux.getIdExistencia()
									+ ")");
						}

					}
				} else {
					getNuevoMovimientoInventario().setIdExistencia(getSelectExistencia().getIdExistencia());
					mensajeInfo("Se seleccionó la Existencia (" + selectExistencia.getIdExistencia());

					setExistencia(selectExistencia);
					selectExistencia = new Existencia();
					filtrarExistencias = new ArrayList<Existencia>();
					context.execute("PF('listadoEx').hide();");
				}

			} else {
				mensajeError("La existencia (" + selectExistencia.getIdExistencia() + ") ya ha sido seleccionada");
				selectExistencia = new Existencia();
			}

		} catch (Exception e) {
			mensajeError("No se ha seleccionado ninguna Existencia");
			e.printStackTrace();
		}

	}

	public boolean validarExistenciaTransf(Existencia e, Existencia seleccionE) {

		boolean resultado;

		if (e.getIdExistencia() == null) {

			resultado = true;

		} else if (e.getPresentacion().getNombrePrs().equals(seleccionE.getPresentacion().getNombrePrs())
				&& e.getEstadoproducto().getNombreEstp().equals(seleccionE.getEstadoproducto().getNombreEstp())
				&& e.getGrado().getNombreGr().equals(seleccionE.getGrado().getNombreGr())
				&& e.getPosgiro().getNombrePg().equals(seleccionE.getPosgiro().getNombrePg())
				&& e.getConcentracion().getNombreCon().equals(seleccionE.getConcentracion().getNombreCon())) {

			resultado = true;

		} else {
			resultado = false;
		}

		return resultado;
	}

	public void cargarExistenciasTemp() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (existencia.getIdExistencia() == null) {
			mensajeError("Debe seleccionar una existencia");
		} else {
			tempExistencias.clear();
			tempExistencias.add(getExistencia());
			System.out.println("Este es el id de existencia: " + getExistencia().getIdExistencia());
			System.out.println("Este es el id de existencia: " + tempExistencias.size());
			// PF('tblexis')
			// context.execute("PF('tblexis')");
			context.execute("PF('verEx').show();");

		}

	}

	public void prueba() {
		mensajeInfo("esto funca");
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

	public void cambiarFechaFiltro() {
		String fecha = new SimpleDateFormat("dd-MM-yyyy").format(getFechaFiltroAux());
		setFechaFiltro(fecha);
		System.out.println("Esta es la fecha del filtro: " + fecha);
	}
	

    public boolean filterByDate(Object value, Object filter, Locale locale) {

        if( filter == null ) {
            return true;
        }

        if( value == null ) {
            return false;
        }

        return DateUtils.truncatedEquals((Date) filter, (Date) value, Calendar.DATE);
    }

	public boolean disabledSelectItem() {
		if (nuevoMovimientoInventarios.size() == 0) {
			return true;
		} else {
			return false;
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

	public List<Movimientosinventario> getTempMovimientoInventarios() {
		return tempMovimientoInventarios;
	}

	public void setTempMovimientoInventarios(List<Movimientosinventario> tempMovimientoInventarios) {
		this.tempMovimientoInventarios = tempMovimientoInventarios;
	}

	public List<Existencia> getTempExistencias() {
		return tempExistencias;
	}

	public void setTempExistencias(List<Existencia> tempExistencias) {
		this.tempExistencias = tempExistencias;
	}

	public Movimientosinventario getNuevoMovimientoInventarioAux() {
		return nuevoMovimientoInventarioAux;
	}

	public void setNuevoMovimientoInventarioAux(Movimientosinventario nuevoMovimientoInventarioAux) {
		this.nuevoMovimientoInventarioAux = nuevoMovimientoInventarioAux;
	}

	public Existencia getExistenciaAux() {
		return existenciaAux;
	}

	public void setExistenciaAux(Existencia existenciaAux) {
		this.existenciaAux = existenciaAux;
	}

	public int getAux() {
		return aux;
	}

	public void setAux(int aux) {
		this.aux = aux;
	}

	public String getFechaFiltro() {
		return fechaFiltro;
	}

	public void setFechaFiltro(String fechaFiltro) {
		this.fechaFiltro = fechaFiltro;
	}

	public Date getFechaFiltroAux() {
		return fechaFiltroAux;
	}

	public void setFechaFiltroAux(Date fechaFiltroAux) {
		this.fechaFiltroAux = fechaFiltroAux;
	}

	public List<Existencia> getTransExistencias() {
		return transExistencias;
	}

	public void setTransExistencias(List<Existencia> transExistencias) {
		this.transExistencias = transExistencias;
	}

	public List<Existencia> getFiltrarTransExistencias() {
		return filtrarTransExistencias;
	}

	public void setFiltrarTransExistencias(List<Existencia> filtrarTransExistencias) {
		this.filtrarTransExistencias = filtrarTransExistencias;
	}

	public Movimientosinventario getMovInventario() {
		return movInventario;
	}

	public void setMovInventario(Movimientosinventario movInventario) {
		this.movInventario = movInventario;
	}

}
