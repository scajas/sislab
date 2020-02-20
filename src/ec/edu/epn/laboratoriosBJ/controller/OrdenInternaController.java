package ec.edu.epn.laboratoriosBJ.controller;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.RemoveException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jdom.IllegalAddException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.sun.media.sound.DLSInstrument;

import ec.edu.epn.laboratorioBJ.beans.ClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO;
import ec.edu.epn.laboratorioBJ.beans.DetalleProDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO;
import ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO;
import ec.edu.epn.laboratorioBJ.beans.ProformaDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Cliente;
import ec.edu.epn.laboratorioBJ.entities.DetalleProforma;
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Muestra;
import ec.edu.epn.laboratorioBJ.entities.OrdenTrabajo;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.Proforma;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import javax.faces.application.FacesMessage;

@ManagedBean(name = "ordenInternaController")
@SessionScoped

public class OrdenInternaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/
	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenTrabajoDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenTrabajoDAO")
	private OrdenTrabajoDAO ordenTrabajoI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleOrdenDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleOrdenDAO")
	private DetalleOrdenDAO detalleOrdenI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PersonalLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO")
	private PersonalLabDAO personalLabI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratorioDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO")
	private LaboratorioDAO laboratorioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadMedidaDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO")
	private UnidadMedidaDAO unidadMedidaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProformaDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProformaDAO")
	private ProformaDAO proformaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleProDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleProDAO")
	private DetalleProDAO detalleProI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.ClienteDAO")
	private ClienteDAO clienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO")
	private TipoClienteDAO tipoClienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;

	/****************************************************************************/
	/** VARIABLES **/

	/* Orden Interna */
	private OrdenTrabajo ordenTrabajo; // Orden interno
	private OrdenTrabajo nuevoOrdenTrabajo;
	private OrdenTrabajo buscarOrdenTrabajo;
	private List<OrdenTrabajo> listaOrdenTrabajo = new ArrayList<OrdenTrabajo>();
	private List<OrdenTrabajo> filtroListaOrdenTrabajo = new ArrayList<OrdenTrabajo>();

	/* Orden Interna */
	private Detalleorden detalleOrden;
	private Detalleorden nuevoDetalleOrden;
	private Detalleorden buscarDetalleOrden;
	private List<Detalleorden> listaDetalleOrden = new ArrayList<>();
	private List<Detalleorden> listaDetalleOrdenTemp = new ArrayList<>();
	private List<Detalleorden> listaDetalleOrdenAdd = new ArrayList<>();
	private List<Detalleorden> listaDetalleOrdenDelete = new ArrayList<>();

	/* Personal */
	private PersonalLab personalLab;
	private List<PersonalLab> personalLabs = new ArrayList<PersonalLab>();
	private List<PersonalLab> filtroPersonalLabs = new ArrayList<PersonalLab>();

	/* Cliente */
	private Cliente cliente;
	private Cliente selectCliente;
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private List<Cliente> filtroClientes = new ArrayList<Cliente>();

	/* Servicio */
	private Servicio servicio;
	private Servicio nuevoServicio;
	private Servicio selectServicio;
	private List<Servicio> servicios = new ArrayList<Servicio>();
	private List<Servicio> filtroServicios = new ArrayList<Servicio>();
	private List<Servicio> allServicios = new ArrayList<Servicio>();
	private List<Servicio> filtroAllServicios = new ArrayList<Servicio>();

	/* Metodo */
	private List<Metodo> metodos = new ArrayList<Metodo>();
	private Metodo metodoSelect;
	private Metodo metodoNA;

	/* Variables adicionales */
	private Date fechaInicio;
	private Date fechaFinal;
	private int tempId;
	private int tempId2;
	private int tempIdServ;
	private int tempIdPer;
	private UploadedFile file;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {

			/** Orden Trabajo (Interno) **/
			UnidadLabo uni = new UnidadLabo();
			Long iduser = su.id_usuario_log;
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			// proformas = proformaI.listaProformaByUnidadLab(uni.getCodigoU(),
			// iduser.intValue());
			ordenTrabajo = new OrdenTrabajo();
			nuevoOrdenTrabajo = new OrdenTrabajo();
			buscarOrdenTrabajo = new OrdenTrabajo();
			nuevoOrdenTrabajo.setFechaordenOt(new Date());

			/** Detalle Orden Trabajo **/
			nuevoDetalleOrden = new Detalleorden();
			detalleOrden = new Detalleorden();
			listaDetalleOrden.clear();
			filtroListaOrdenTrabajo.clear();

			/** Personal **/
			personalLab = new PersonalLab();

			/** Cliente **/
			cliente = new Cliente();
			selectServicio = new Servicio();
			nuevoServicio = new Servicio();
			setTempIdServ(0);
			setTempIdServ(0);
			metodoNA = ordenTrabajoI.findMetodoById("120");
			System.err.println("Este es el metodo: " + metodoNA.getNombreMt());

			/** Valores Adicionales **/
			fechaInicio = null;
			fechaFinal = null;

			disabledSelectItem();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* Metodos de limpieza de formularios */

	public void limpiarOT() {
		ordenTrabajo = new OrdenTrabajo();
		nuevoOrdenTrabajo = new OrdenTrabajo();
		buscarOrdenTrabajo = new OrdenTrabajo();
		nuevoOrdenTrabajo.setFechaordenOt(new Date());
		
	}

	public void limpiarDetalleOT() {
		nuevoDetalleOrden = new Detalleorden();
		detalleOrden = new Detalleorden();
		nuevoDetalleOrden.setHorasTrabajo(0);
		nuevoServicio = new Servicio();
	}

	public void limpiarTodosCampos() {
		try {

			/** Orden Trabajo **/
			UnidadLabo uni = new UnidadLabo();
			Long iduser = su.id_usuario_log;
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			// proformas = proformaI.listaProformaByUnidadLab(uni.getCodigoU(),
			// iduser.intValue());
			limpiarOT();

			/** Detalle Orden Trabajo **/
			limpiarDetalleOT();
			listaDetalleOrden.clear();
			listaOrdenTrabajo = new ArrayList<OrdenTrabajo>();

			setTempIdServ(0);
			setTempIdPer(0);

			/** Valores Adicionales **/
			fechaInicio = null;
			fechaFinal = null;

			// Servicio
			servicio = new Servicio();
			nuevoServicio = new Servicio();
			servicios = new ArrayList<Servicio>();
			filtroServicios = new ArrayList<Servicio>();
			allServicios = new ArrayList<Servicio>();
			filtroAllServicios = new ArrayList<Servicio>();

			// Cliente
			cliente = new Cliente();
			clientes = new ArrayList<Cliente>();
			filtroClientes = new ArrayList<Cliente>();

			disabledSelectItem();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/****** Metodos de Orden Trabajo ****/

	public void guardarOT() {
		try {
			/** Creacion de ID Proforma **/
			obtenerIDOT();

			/** Seteo de Ids en Detalle Proforma **/
			cambiarIDDetalleOT(nuevoOrdenTrabajo);

			/** GUARDAR EN DB **/
			ordenTrabajoI.save(nuevoOrdenTrabajo);
			guardarDetalleOT(listaDetalleOrden);

			mensajeInfo("Se ha guardado el nuevo orden de trabajo con el id (" + nuevoOrdenTrabajo.getIdOrden() + ")");

			/** Limpiar todos los campos **/
			limpiarTodosCampos();

		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error interno.");
		}
	}

	/****** Metodo Editar Proforma ******/

	public void editarPro() {
		// Settear Id a las lista
		cambiarIDDetalleOT(ordenTrabajo);
		cambiarIDDetalleOTAdd(ordenTrabajo);

		// Eliminar A la base de datos
		eliminarDetallesOT(listaDetalleOrdenDelete);

		// Agregar A la base de datos
		guardarDetalleOT(listaDetalleOrdenAdd);

		// Modificar A la base de datos
		actualizarDetalleOT(listaDetalleOrden);
		actualizarOT(ordenTrabajo);

		mensajeInfo("Se ha modificado la orden de trabajo (" + ordenTrabajo.getIdOrden() + ")");

		limpiarTodosCampos();

	}

	public void abrirProforma() {

		try {

			/*
			 * HttpSession sesion=request.getSession();
			 * session.setAttribute("Hola", "Hola");
			 */

			FacesContext contex = FacesContext.getCurrentInstance();
			// contex.getExternalContext().getSessionMap().put("id",
			// proforma.getIdProforma());

			contex.getExternalContext().redirect("/SisLab/pages/printProforma.jsf");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Me voy al carajo, no funciona esta redireccion");
		}
	}

	public void eliminarProforma() {
		try {
			// cargarDetalleProforma(proforma.getIdProforma());
			//
			// eliminarDetallesPro(detalleProformas);
			//
			// proformaI.delete(getProforma());
			//
			// proformas.remove(proforma);

			mensajeInfo("Se ha eliminado la proforma ()");
			limpiarOT();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mensajeError("Ha ocurrido un error en la eliminacion");
		}
	}

	public void eliminarDetallesOT(List<Detalleorden> dot) {
		System.out.println("Entra al eliminar");
		if (dot.size() == 0) {
			System.out.println("No hay registros que Eliminar");
		} else {
			for (Detalleorden detalleorden : dot) {
				try {
					detalleOrdenI.delete(detalleorden);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void buscarOrdenTrabajos() {

		try {
			UnidadLabo uni = new UnidadLabo();
			buscarOrdenTrabajo.setCliente(getCliente());
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			listaOrdenTrabajo = ordenTrabajoI.listarOTInternaByUnidadLab(uni.getCodigoU(), 1, buscarOrdenTrabajo,
					fechaInicio, fechaFinal);
			// proformas = proformaI.listaProformaByUnidadLab(uni.getCodigoU(),
			// 1, proformaBuscar, fechaInicio,
			// fechaFinal);

			System.out.println("Estos son todos los registros que trae " + listaOrdenTrabajo.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void buscarAllProformas() {

	}

	public void calcularTotalPrecios() {

	}

	public void obtenerIDOT() {
		try {
			/** Creacion del Obtener año **/
			String fecha = cambioFecha(new Date());
			// String fecha = "2020-05-05";
			String[] partsFecha = fecha.split("-");
			String anio = partsFecha[0];

			/** Obtener la unidad **/
			UnidadLabo uni = new UnidadLabo();

			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			/** Creacion del IdOrdenInv **/

			String codigoAux = ordenTrabajoI.maxIdOTInterno(uni.getCodigoU(), fecha);

			/* Validacion en caso cambie de año */
			if (codigoAux == null) {
				codigoAux = (uni.getCodigoU() + "-OTI0000-" + anio);
			}

			System.out.println("Este es el id que trae: " + codigoAux);

			String[] partsId = codigoAux.split("-OTI", 2);

			partsId = partsId[1].split("-");

			String codigoCortado = partsId[0];

			System.out.println("Este es el id convertido en numero: " + codigoCortado);

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			System.out.println("Este es el id oficial: " + codigo);

			Long id = su.id_usuario_log;

			String codigoPro = codigo.toString();
			/* Setteo de valores adicionales */
			nuevoOrdenTrabajo.setEstadoOt("GENERADA");
			nuevoOrdenTrabajo.setTipoOt("Interna");
			nuevoOrdenTrabajo.setIdUsuario(id.intValue());

			// nuevoExistencia.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.getUnidad().setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);

			//
			// unidadLabo =
			// ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.setUnidad(unidadLabo);

			switch (codigoPro.length()) {
			case 1:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OTI" + "000" + codigoPro + "-" + anio);
				break;
			case 2:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OTI" + "00" + codigoPro + "-" + anio);
				break;
			case 3:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OTI" + "0" + codigoPro + "-" + anio);
				break;
			case 4:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OTI" + codigoPro + "-" + anio);
				break;

			default:
				break;
			}

			System.out.println("Este es el nuevo id: " + nuevoOrdenTrabajo.getIdOrden());
		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	public void cargarDetalleOT(String id) {
		try {
			System.out.println("Entra a la cargar de lista de detalleOt: " + id);
			listaDetalleOrdenTemp.clear();
			System.out.println("Lista de detallesPro id: " + id);
			listaDetalleOrdenTemp = ordenTrabajoI.listarDetalleOrdenById(id);
			System.out.println("Lista de detallePro: " + listaDetalleOrdenTemp.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void validarEstadoOT(OrdenTrabajo o, String panel) {
		RequestContext context = RequestContext.getCurrentInstance();
		if (o.getEstadoOt().equals("CLOSED")) {
			context.execute("PF('cdPanelError').show();");
		} else {
			context.execute("PF('" + panel + "').show();");
		}
	}
	
	public void validarFechaEntregaOT(OrdenTrabajo o, String panel) {
		RequestContext context = RequestContext.getCurrentInstance();
		if (o.getEstadoOt().equals("CLOSED")) {
			context.execute("PF('cdPanelError').show();");
		} else {
			if (o.getFechaCierre() == null) {
				context.execute("PF('cdPanelErrorFecha').show();");
			} else {
				context.execute("PF('" + panel + "').show();");
			}
		}
		
	}

	/****** Metodos de Detalle Orden Trabajo ****/

	public void agregarDetalleOT() {
		RequestContext context = RequestContext.getCurrentInstance();

		Muestra muestra = ordenTrabajoI.muestraDefault();
		nuevoDetalleOrden.setMuestra(muestra);
		listaDetalleOrden.add(nuevoDetalleOrden);

		if (getTempId() == 1) {
			System.out.println("Guarda en lista temporal");
			listaDetalleOrdenAdd.add(nuevoDetalleOrden);
		}

		mensajeInfo("Se ha agregado exitosamente.");
		limpiarDetalleOT();

		if (getTempId() == 0) {
			// calcularTotalPrecios();
			context.update("formAgregarOT");

		} else {
			context.update("formEditarOT");
		}
	}

	public void actualizarOT(OrdenTrabajo o) {
		try {
			ordenTrabajoI.update(o);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void editarEstado() {
		try {
			ordenTrabajoI.update(getOrdenTrabajo());
			mensajeInfo("Se ha editado correctamente el estado de (" + ordenTrabajo.getIdOrden() + ")");
			limpiarOT();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mensajeError("Ha ocurrido un error!");
		}
	}

	public void guardarDetalleOT(List<Detalleorden> ots) {
		System.out.println("Entra en la funcion guardar detalleOT");
		if (ots.size() == 0) {
			System.out.println("No hay registros que guardar");
		} else {
			for (Detalleorden detalleOt : ots) {
				try {
					System.out.println("id:_" + detalleOt.getIdMetodo());
					System.out.println("id:_" + detalleOt.getIdServicio());
					System.out.println("id:_" + detalleOt.getMuestra().getIdMuestra());
					detalleOrdenI.save(detalleOt);
				} catch (Exception e) {
					System.out.println(e);
					// e.printStackTrace();
				}

			}
		}

	}

	public void actualizarDetalleOT(List<Detalleorden> dot) {
		System.out.println("Esta entrando a la funcion");
		for (Detalleorden detalleO : dot) {
			try {
				detalleOrdenI.update(detalleO);
			} catch (Exception e) {
				System.out.println(e);
				// e.printStackTrace();
			}

		}
	}

	public void guardarTempDetalleOT() {

	}

	public void editarDetalleOT() {
		RequestContext context = RequestContext.getCurrentInstance();

		if (getTempId() == 1) {

			System.out.println("Esta en el panel editar");

			editarDetalleTemp(detalleOrden);

			context.update("formEditarOT");

			context.execute("PF('editarDetalleOT').hide();");

		} else {

			context.update("formAgregarOT");

			context.execute("PF('editarDetalleOT').hide();");

		}

		mensajeInfo("Se ha editado Correctamente");
	}

	public void editarDetalleTemp(Detalleorden detalleorden) {
		int i = 0;
		for (Detalleorden dot : listaDetalleOrdenAdd) {
			if (dot.getIdServicio().equals(detalleorden.getIdServicio())) {
				listaDetalleOrdenAdd.set(i, detalleorden);
				break;
			} else {
				i++;
			}
		}
	}

	public void validarCantidad(FacesContext ctx, UIComponent component, Integer value) throws ValidatorException {
		// String prueba = value.toString();

	}

	public void eliminarDetalleOT() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();

			// Seteo de listas temporales
			setListasTempDelete();

			listaDetalleOrden.remove(detalleOrden);

			if (getTempId() == 0) {
				context.update("formAgregarPro");
			} else {
				context.update("formEditarPro");
			}

			mensajeInfo("Se ha eliminado el datelle de la proforma ("
					+ servicio(detalleOrden.getIdServicio()).getNombreS() + ")");

		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error interno.");
		}
	}

	public void setListasTempDelete() {
		if (getTempId() == 1) {
			if (buscarDetalleOTAdd(detalleOrden)) {
				listaDetalleOrdenAdd.remove(detalleOrden);
				System.out.println("Le quita del temporal de añadir");
			} else {
				listaDetalleOrdenDelete.add(detalleOrden);
				System.out.println("Entra al else");
			}
		}
	}

	public void cambiarIDDetalleOT(OrdenTrabajo o) {
		System.out.println("Entra al setteo de Ids (cambiarIDDetallePro)");
		int i = 0;

		for (Detalleorden detalleO : listaDetalleOrden) {

			detalleO.setOrdenTrabajo(o);
			detalleO.setEstadoDot("PENDIENTE");
			detalleO.setSizeExistencias(0);
			listaDetalleOrden.set(i, detalleO);

			i++;
		}
	}

	public void cambiarIDDetalleOTAdd(OrdenTrabajo o) {
		System.out.println("entra a la funcion cambiarIDDetalleProADD");
		int i = 0;
		if (listaDetalleOrdenAdd.size() == 0) {
			System.out.println("No hay registros que añadir");
		} else {
			for (Detalleorden detalleO : listaDetalleOrdenAdd) {

				detalleO.setOrdenTrabajo(o);
				detalleO.setEstadoDot("PENDIENTE");
				detalleO.setSizeExistencias(0);
				listaDetalleOrdenAdd.set(i, detalleO);

				i++;
			}
		}
	}

	/* Metodos de servicios */

	public void cargarServicios() {
		try {

			System.out.println("Esta entrando a la funcion");
			servicios = ordenTrabajoI.listarServiciosByLabType(su.UNIDAD_USUARIO_LOGEADO, 5);
			filtroServicios = servicios;

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}

	}

	public void cargarPersonal(int idPer) {
		try {
			setTempIdPer(idPer);
			personalLabs = personalLabI.listarPersonalById(su.UNIDAD_USUARIO_LOGEADO);
			filtroPersonalLabs = personalLabs;
			System.out.println("Estos son los servicios que trae: " + personalLabs.size());

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}

	}

	public void seleccionarPersonal() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (tempIdPer == 0) {
				// formDetalleOT]
				nuevoDetalleOrden.setPersonal(personalLab);
				context.update("formDetalleOT");

			} else {
				detalleOrden.setPersonal(personalLab);
				context.update("formEditarDetalleOT");

			}

			mensajeInfo("Se ha seleccionado al cliente: " + personalLab.getNombresPe());
			personalLab = new PersonalLab();

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}
	}

	public boolean buscarServicio(Servicio servicio) {

		return false;
	}

	public void validarAllDetallePro() {

	}

	public void validarDetalleOT() {
		RequestContext context = RequestContext.getCurrentInstance();
		nuevoDetalleOrden.setIdServicio(nuevoServicio.getIdServicio());
		if (buscarDetalleOT(nuevoDetalleOrden) == true) {
			mensajeError("El servicio " + nuevoServicio.getNombreS() + " ya esta agregado.");
		} else {
			cargarMetodo(nuevoDetalleOrden);
			System.out.println("Esta entrando al else");
			setTempIdServ(0);
			context.update("formDetalleOT");
		}

	}

	public boolean buscarDetalleOT(Detalleorden detalleorden) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;
		System.out.println("Este es el del formulario: " + detalleorden.getIdServicio());
		System.out.println("Este es el detallePro: " + listaDetalleOrden.size());

		for (Detalleorden dc : listaDetalleOrden) {
			System.out.println("Este es el que encontro: " + dc.getIdServicio());

			if (dc.getIdServicio().equals(detalleorden.getIdServicio())) {
				System.out.println("Encajan " + dc.getIdServicio() + " y " + detalleorden.getIdServicio());
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public boolean buscarDetalleOTAdd(Detalleorden detalleorden) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;
		System.out.println("Este es el del formulario: " + detalleorden.getIdServicio());
		System.out.println("Este es el detallePro: " + listaDetalleOrden.size());

		for (Detalleorden dc : listaDetalleOrdenAdd) {
			System.out.println("Este es el que encontro: " + dc.getIdServicio());

			if (dc.getIdServicio().equals(detalleorden.getIdServicio())) {
				System.out.println("Encajan " + dc.getIdServicio() + " y " + detalleorden.getIdServicio());
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public boolean validarCantidadPro() {

		return false;
	}

	public void cargarMetodo(Detalleorden detalleOrden) {
		try {

			System.out.println("Esta entrando a la funcion de metodo: " + detalleOrden.getIdServicio());
			metodos = ordenTrabajoI.listarMetodosByIdServicio(detalleOrden.getIdServicio());

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}
	}

	public void calcularTotal() {

	}

	public void calcularTotalEdit() {

	}

	public void cargarDetalleProformaEdit(String id) {
		listaDetalleOrden.clear();
		// cambiarIdTempPanel(1);
		System.out.println("Lista de detallesPro id: " + id);
		listaDetalleOrden = ordenTrabajoI.listarDetalleOrdenById(id);
		System.out.println("Lista de detallePro: " + listaDetalleOrden.size());
	}

	/* Metodos Detalle Pro */

	public void buscarClientes() {
		try {

			clientes = ordenTrabajoI.listarClienteBY(getCliente());
			filtroClientes = clientes;
			System.out.println("Registros de clientes: " + clientes.size());

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}
	}

	public void seleccionarCliente() {

		try {

			nuevoOrdenTrabajo.setCliente(selectCliente);
			mensajeInfo("Se ha seleccionado al cliente: " + selectCliente.getNombreCl());
			selectCliente = new Cliente();

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			e.printStackTrace();
		}
	}

	public void validarCliente() {

		RequestContext context = RequestContext.getCurrentInstance();
		if (nuevoOrdenTrabajo.getCliente() == null) {
			mensajeError("Debe buscar un cliente y seleccionarlo");
		} else {
			cargarServicios();
			cambiarIdTempPanel(0, 0);
			context.execute("PF('detalleOT').show();");
		}
	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext contextM = FacesContext.getCurrentInstance();
		contextM.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {
		FacesContext contextM = FacesContext.getCurrentInstance();

		contextM.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Reemplazar valores de la tabla o formulario ****/

	public String producto(String id) {
		String nombre = "";

		Existencia existenciaPro = new Existencia();

		existenciaPro = existenciasI.buscarExistenciaById(id);

		if (existenciaPro == null) {
			nombre = "N/A";
		} else {
			nombre = existenciaPro.getProducto().getNombrePr();
		}

		return nombre;
	}

	public String metodo(String id) {
		String nombre = "";

		Metodo m = new Metodo();

		m = detalleOrdenI.findMetodoById(id);

		if (m == null) {
			nombre = "N/A";
		} else {
			nombre = m.getNombreMt();
		}

		return nombre;
	}

	public Servicio servicio(String idServicio) {

		Servicio s = new Servicio();

		s = detalleOrdenI.findServicioById(idServicio);

		if (s == null) {
			s.setNombreS("N/A");
		}

		return s;
	}

	/** Calcular IVA **/
	public double obtenerIva(double base, double porcentaje) {
		return base * porcentaje / 100;
	}

	/****** Manejo de fechas ****/
	public String cambioFecha(Date fecha) {
		if (fecha == null) {
			return "No registrada";
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			String fechaFinal = format.format(fecha);

			return fechaFinal;
		}

	}

	public boolean disabledSelectItem() {
		if (fechaInicio == null) {
			return false;
		} else {
			return true;
		}

	}

	public boolean disabledDate() {
		if (nuevoDetalleOrden.getFechaInicioAnalisis() == null) {
			return false;
		} else {
			return true;
		}

	}

	public boolean disabledDateEdit() {
		if (detalleOrden.getFechaInicioAnalisis() == null) {
			return false;
		} else {
			return true;
		}

	}

	public boolean disabledButton() {

		System.out.println("Este es el numero actual de registros: " + listaDetalleOrden.size());
		if (listaDetalleOrden.size() == 0) {
			return false;
		} else {
			return true;
		}

	}

	public void cambiarIdTempPanel(int a, int b) {
		setTempId(a);
		if (a == 1 && b == 1) {
			cargarServicios();
		}
		System.out.println("Se ha cambiado a: " + a);
	}

	/****** Getter y Setter de Estado Producto ****/

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getFiltroClientes() {
		return filtroClientes;
	}

	public void setFiltroClientes(List<Cliente> filtroClientes) {
		this.filtroClientes = filtroClientes;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public Cliente getSelectCliente() {
		return selectCliente;
	}

	public void setSelectCliente(Cliente selectCliente) {
		this.selectCliente = selectCliente;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public Servicio getNuevoServicio() {
		return nuevoServicio;
	}

	public void setNuevoServicio(Servicio nuevoServicio) {
		this.nuevoServicio = nuevoServicio;
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio> servicios) {
		this.servicios = servicios;
	}

	public List<Servicio> getAllServicios() {
		return allServicios;
	}

	public void setAllServicios(List<Servicio> allServicios) {
		this.allServicios = allServicios;
	}

	public Servicio getSelectServicio() {
		return selectServicio;
	}

	public void setSelectServicio(Servicio selectServicio) {
		this.selectServicio = selectServicio;
	}

	public List<Metodo> getMetodos() {
		return metodos;
	}

	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	}

	public List<Servicio> getFiltroServicios() {
		return filtroServicios;
	}

	public void setFiltroServicios(List<Servicio> filtroServicios) {
		this.filtroServicios = filtroServicios;
	}

	public List<Servicio> getFiltroAllServicios() {
		return filtroAllServicios;
	}

	public void setFiltroAllServicios(List<Servicio> filtroAllServicios) {
		this.filtroAllServicios = filtroAllServicios;
	}

	public int getTempId() {
		return tempId;
	}

	public void setTempId(int tempId) {
		this.tempId = tempId;
	}

	public int getTempId2() {
		return tempId2;
	}

	public void setTempId2(int tempId2) {
		this.tempId2 = tempId2;
	}

	public int getTempIdServ() {
		return tempIdServ;
	}

	public void setTempIdServ(int tempIdServ) {
		this.tempIdServ = tempIdServ;
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

	public List<OrdenTrabajo> getListaOrdenTrabajo() {
		return listaOrdenTrabajo;
	}

	public void setListaOrdenTrabajo(List<OrdenTrabajo> listaOrdenTrabajo) {
		this.listaOrdenTrabajo = listaOrdenTrabajo;
	}

	public OrdenTrabajo getNuevoOrdenTrabajo() {
		return nuevoOrdenTrabajo;
	}

	public void setNuevoOrdenTrabajo(OrdenTrabajo nuevoOrdenTrabajo) {
		this.nuevoOrdenTrabajo = nuevoOrdenTrabajo;
	}

	public List<OrdenTrabajo> getFiltroListaOrdenTrabajo() {
		return filtroListaOrdenTrabajo;
	}

	public void setFiltroListaOrdenTrabajo(List<OrdenTrabajo> filtroListaOrdenTrabajo) {
		this.filtroListaOrdenTrabajo = filtroListaOrdenTrabajo;
	}

	public Detalleorden getDetalleOrden() {
		return detalleOrden;
	}

	public void setDetalleOrden(Detalleorden detalleOrden) {
		this.detalleOrden = detalleOrden;
	}

	public List<Detalleorden> getListaDetalleOrden() {
		return listaDetalleOrden;
	}

	public void setListaDetalleOrden(List<Detalleorden> listaDetalleOrden) {
		this.listaDetalleOrden = listaDetalleOrden;
	}

	public Detalleorden getNuevoDetalleOrden() {
		return nuevoDetalleOrden;
	}

	public void setNuevoDetalleOrden(Detalleorden nuevoDetalleOrden) {
		this.nuevoDetalleOrden = nuevoDetalleOrden;
	}

	public PersonalLab getPersonalLab() {
		return personalLab;
	}

	public void setPersonalLab(PersonalLab personalLab) {
		this.personalLab = personalLab;
	}

	public Detalleorden getBuscarDetalleOrden() {
		return buscarDetalleOrden;
	}

	public void setBuscarDetalleOrden(Detalleorden buscarDetalleOrden) {
		this.buscarDetalleOrden = buscarDetalleOrden;
	}

	public OrdenTrabajo getBuscarOrdenTrabajo() {
		return buscarOrdenTrabajo;
	}

	public void setBuscarOrdenTrabajo(OrdenTrabajo buscarOrdenTrabajo) {
		this.buscarOrdenTrabajo = buscarOrdenTrabajo;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<PersonalLab> getPersonalLabs() {
		return personalLabs;
	}

	public void setPersonalLabs(List<PersonalLab> personalLabs) {
		this.personalLabs = personalLabs;
	}

	public List<PersonalLab> getFiltroPersonalLabs() {
		return filtroPersonalLabs;
	}

	public void setFiltroPersonalLabs(List<PersonalLab> filtroPersonalLabs) {
		this.filtroPersonalLabs = filtroPersonalLabs;
	}

	public List<Detalleorden> getListaDetalleOrdenAdd() {
		return listaDetalleOrdenAdd;
	}

	public void setListaDetalleOrdenAdd(List<Detalleorden> listaDetalleOrdenAdd) {
		this.listaDetalleOrdenAdd = listaDetalleOrdenAdd;
	}

	public List<Detalleorden> getListaDetalleOrdenDelete() {
		return listaDetalleOrdenDelete;
	}

	public void setListaDetalleOrdenDelete(List<Detalleorden> listaDetalleOrdenDelete) {
		this.listaDetalleOrdenDelete = listaDetalleOrdenDelete;
	}

	public int getTempIdPer() {
		return tempIdPer;
	}

	public void setTempIdPer(int tempIdPer) {
		this.tempIdPer = tempIdPer;
	}

	public List<Detalleorden> getListaDetalleOrdenTemp() {
		return listaDetalleOrdenTemp;
	}

	public void setListaDetalleOrdenTemp(List<Detalleorden> listaDetalleOrdenTemp) {
		this.listaDetalleOrdenTemp = listaDetalleOrdenTemp;
	}

}
