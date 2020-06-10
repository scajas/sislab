package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;

import ec.edu.epn.facturacion.entities.EstadoFactura;
import ec.edu.epn.facturacion.entities.Factura;
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
import ec.edu.epn.laboratorioBJ.entities.Detalleorden;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Muestra;
import ec.edu.epn.laboratorioBJ.entities.OrdenTrabajo;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import javax.faces.application.FacesMessage;

@ManagedBean(name = "ordenFacturaController")
@SessionScoped

public class OrdenTrabajoFacturaController implements Serializable {

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
	private List<Detalleorden> listaDetalleOrdenMuestra = new ArrayList<>();

	/* Personal */
	private PersonalLab personalLab;
	private List<PersonalLab> personalLabs = new ArrayList<PersonalLab>();
	private List<PersonalLab> tempPersonalLabs = new ArrayList<PersonalLab>();
	private List<PersonalLab> filtroPersonalLabs = new ArrayList<PersonalLab>();

	/* Cliente */
	private Cliente cliente;
	private Cliente selectCliente;
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private List<Cliente> filtroClientes = new ArrayList<Cliente>();

	/* Factura */
	private Factura factura;
	private Factura selectFactura;
	private List<Factura> facturas = new ArrayList<Factura>();
	private List<Factura> filtroFacturas = new ArrayList<Factura>();
	private LazyDataModel<Factura> lazyModel;

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

	private Metodo metodoNA;

	/* Muestra */
	private List<Muestra> muestras = new ArrayList<Muestra>();
	private List<Muestra> filtroMuestras = new ArrayList<Muestra>();
	private Muestra muestra;
	private Muestra muestraNA;

	/* Variables adicionales */
	private Date fechaInicio;
	private Date fechaFinal;
	private int tempId;
	private int tempId2;
	private int tempIdServ;
	private int tempIdPer;
	private int tempIdM;
	private int totalRegistros;
	private UploadedFile file;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {

			/** Orden Trabajo (Interno) **/
			UnidadLabo uni = new UnidadLabo();

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

			/** Factura **/
			factura = new Factura();
			selectFactura = new Factura();

			/** Cliente **/
			cliente = new Cliente();
			selectServicio = new Servicio();
			nuevoServicio = new Servicio();
			setTempIdServ(0);
			setTempIdServ(0);
			metodoNA = ordenTrabajoI.findMetodoById("120");
	

			/** Valores Adicionales **/
			fechaInicio = null;
			fechaFinal = null;

			disabledSelectItem();

		} catch (Exception e) {

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
		muestra = new Muestra();
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
			// listaOrdenTrabajo = new ArrayList<OrdenTrabajo>();

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

			// factura
			factura = new Factura();
			selectFactura = new Factura();
			facturas = new ArrayList<Factura>();
			filtroFacturas = new ArrayList<Factura>();

			// Muestra
			muestras.clear();

			// Personal
			personalLabs.clear();
			personalLab = new PersonalLab();
			
			//muestraDT
			listaDetalleOrdenMuestra.clear();

			disabledSelectItem();

		} catch (Exception e) {
	
		}

	}

	public void limpiarFormularios() {
		limpiarTodosCampos();
		mensajeInfo("Se han limpiado todos los campos");
	}

	/****** Metodos de Orden Trabajo ****/

	public void guardarOT() {
		try {
			
			if (getTotalRegistros() == listaDetalleOrden.size()) {
				/** Creacion de ID Proforma **/
				obtenerIDOT();

				/** Seteo de Ids en Detalle Proforma **/
				cambiarIDDetalleOT(nuevoOrdenTrabajo);

				/** GUARDAR EN DB **/
				ordenTrabajoI.save(nuevoOrdenTrabajo);
				guardarDetalleOT(listaDetalleOrden);

				/** agregar a la vista **/
				listaOrdenTrabajo.add(nuevoOrdenTrabajo);

				mensajeInfo("Se ha guardado el nuevo orden de trabajo con el id (" + nuevoOrdenTrabajo.getIdOrden() + ")");

				/** Limpiar todos los campos **/
				limpiarTodosCampos();
			}else{
				mensajeError("Aun quedan servicios pendientes que registrar");
			}
			
		} catch (Exception e) {
		
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

	public void abrirOT() {

		try {

			/*
			 * HttpSession sesion=request.getSession();
			 * session.setAttribute("Hola", "Hola");
			 */

			FacesContext contex = FacesContext.getCurrentInstance();
			contex.getExternalContext().getSessionMap().put("idOT", ordenTrabajo.getIdOrden());

			contex.getExternalContext().redirect("/SisLab/pages/printOrdenTrabajoFactura.jsf");
		} catch (Exception e) {
	
		}
	}

	public void eliminarProforma() {
		try {

			mensajeInfo("Se ha eliminado la proforma ()");
			limpiarOT();

		} catch (Exception e) {
		
			mensajeError("Ha ocurrido un error en la eliminacion");
		}
	}

	public void eliminarDetallesOT(List<Detalleorden> dot) {

		if (dot.size() == 0) {
	
		} else {
			for (Detalleorden detalleorden : dot) {
				try {
					detalleOrdenI.delete(detalleorden);
				} catch (Exception e) {
				
				}
			}
		}

	}

	public void buscarOrdenTrabajos() {

		try {
			UnidadLabo uni = new UnidadLabo();
			buscarOrdenTrabajo.setCliente(getCliente());
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			listaOrdenTrabajo = ordenTrabajoI.listarOTFacturaByUnidadLab(uni.getCodigoU(), 1, buscarOrdenTrabajo,
					fechaInicio, fechaFinal);

		} catch (Exception e) {
	
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

			String codigoAux = ordenTrabajoI.maxIdOT(uni.getCodigoU(), fecha);

			/* Validacion en caso cambie de año */
			if (codigoAux == null) {
				codigoAux = (uni.getCodigoU() + "-OT0000-" + anio);
			}

			String[] partsId = codigoAux.split("-OT", 2);

			partsId = partsId[1].split("-");

			String codigoCortado = partsId[0];

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			Long id = su.id_usuario_log;

			String codigoPro = codigo.toString();
			/* Setteo de valores adicionales */
			nuevoOrdenTrabajo.setEstadoOt("GENERADA");
			nuevoOrdenTrabajo.setTipoOt("Externa Factura");
			nuevoOrdenTrabajo.setIdUsuario(id.intValue());

			// nuevoExistencia.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.getUnidad().setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);

			//
			// unidadLabo =
			// ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.setUnidad(unidadLabo);

			switch (codigoPro.length()) {
			case 1:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OT" + "000" + codigoPro + "-" + anio);
				break;
			case 2:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OT" + "00" + codigoPro + "-" + anio);
				break;
			case 3:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OT" + "0" + codigoPro + "-" + anio);
				break;
			case 4:
				nuevoOrdenTrabajo.setIdOrden(uni.getCodigoU() + "-OT" + codigoPro + "-" + anio);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");

		}
	}

	public void cargarDetalleOT(String id) {
		try {
		
			listaDetalleOrdenTemp.clear();
	
			listaDetalleOrdenTemp = ordenTrabajoI.listarDetalleOrdenById(id);
			llenarPersonalTemp(listaDetalleOrdenTemp);

			llenarlistaTempM();
		} catch (Exception e) {
		
		}

	}

	public void llenarlistaTempM() {
		listaDetalleOrdenMuestra.clear();
		boolean prueba = true;

		for (Detalleorden detalleo : listaDetalleOrdenTemp) {

			if (listaDetalleOrdenMuestra.size() == 0) {
				listaDetalleOrdenMuestra.add(detalleo);
			} else {
				for (Detalleorden d : listaDetalleOrdenMuestra) {

					if (detalleo.getMuestra().getIdMuestra().equals(d.getMuestra().getIdMuestra())) {
						prueba = true;
						break;
					} else {
						prueba = false;
					}
				}

				if (prueba == false) {

					listaDetalleOrdenMuestra.add(detalleo);
				} else {
		
				}

			}

		}
	}

	public int listarNumeroM(String idMuestra) {
		int i = 1;

		for (Detalleorden detalleorden : listaDetalleOrdenMuestra) {
			if (detalleorden.getMuestra().getIdMuestra().equals(idMuestra)) {
				break;
			} else {
				i++;
			}

		}

		return i;
	}

	public void llenarPersonalTemp(List<Detalleorden> detalleordens) {
		tempPersonalLabs.clear();
		boolean prueba = true;

		for (Detalleorden detalleo : detalleordens) {


			if (tempPersonalLabs.size() == 0) {
				tempPersonalLabs.add(detalleo.getPersonal());
			
			} else {
				for (PersonalLab p : tempPersonalLabs) {

					if (detalleo.getPersonal().getIdPersonal().equals(p.getIdPersonal())) {
						prueba = true;
						break;
					} else {
						prueba = false;
					}
				}

				if (prueba == false) {
	
					tempPersonalLabs.add(detalleo.getPersonal());
				} else {
				
				}

			}

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

		listaDetalleOrden.add(nuevoDetalleOrden);

		if (getTempId() == 1) {
			
			listaDetalleOrdenAdd.add(nuevoDetalleOrden);
		}

		mensajeInfo("Se ha agregado exitosamente.");
		limpiarDetalleOT();

		if (nuevoOrdenTrabajo.getNumeromuestraOt() == 0) {
			nuevoDetalleOrden.setMuestra(ordenTrabajoI.muestraDefault());
		}

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
	
		}
	}

	public void editarEstado() {
		try {
			ordenTrabajoI.update(getOrdenTrabajo());
			mensajeInfo("Se ha editado correctamente el estado de (" + ordenTrabajo.getIdOrden() + ")");
			limpiarOT();
		} catch (Exception e) {
			
			mensajeError("Ha ocurrido un error!");
		}
	}

	public void guardarDetalleOT(List<Detalleorden> ots) {

		if (ots.size() == 0) {
	
		} else {
			for (Detalleorden detalleOt : ots) {
				try {
				
					detalleOrdenI.save(detalleOt);
				} catch (Exception e) {
			
				}

			}
		}

	}

	public void actualizarDetalleOT(List<Detalleorden> dot) {

		for (Detalleorden detalleO : dot) {
			try {
				detalleOrdenI.update(detalleO);
			} catch (Exception e) {
		
			}

		}
	}

	public void guardarTempDetalleOT() {

	}

	public void editarDetalleOT() {
		RequestContext context = RequestContext.getCurrentInstance();

		if (getTempId() == 1) {

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
	
			mensajeError("Ha ocurrido un error interno.");
		}
	}

	public void setListasTempDelete() {
		if (getTempId() == 1) {
			if (buscarDetalleOTAdd(detalleOrden)) {
				listaDetalleOrdenAdd.remove(detalleOrden);
		
			} else {
				listaDetalleOrdenDelete.add(detalleOrden);
			
			}
		}
	}

	public void cambiarIDDetalleOT(OrdenTrabajo o) {

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
	
		int i = 0;
		if (listaDetalleOrdenAdd.size() == 0) {
		
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

			servicios = ordenTrabajoI.listarServiciosByPro(getFactura().getIdProforma());
			filtroServicios = servicios;

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
		
		}

	}

	public void cargarPersonal(int idPer) {
		try {
			setTempIdPer(idPer);
			personalLabs = personalLabI.listarPersonalByServ(nuevoServicio, su.UNIDAD_USUARIO_LOGEADO);
			filtroPersonalLabs = personalLabs;

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
		
		}

	}

	public void cargarMuestras(int idM) {
		try {
			setTempIdM(idM);
			muestras = ordenTrabajoI.listarMuestraByFactura(nuevoOrdenTrabajo.getIdFactura());
			filtroMuestras = muestras;

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
	
		}

	}

	public void cambiarFecha() {
		// TODO Auto-generated method stub
		setFechaFinal(fechaInicio);

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

			mensajeInfo("Se ha seleccionado al analista: " + personalLab.getNombresPe());
			personalLab = new PersonalLab();

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
		
		}
	}

	public void seleccionarMuestra() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (tempIdM == 0) {
			
				nuevoDetalleOrden.setMuestra(muestra);
				context.update("formDetalleOT");

			} else {
				detalleOrden.setMuestra(muestra);
				context.update("formEditarDetalleOT");

			}

			mensajeInfo("Se ha seleccionado al Muestra: " + muestra.getCodigoMCliente());
			muestra = new Muestra();

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			
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
			mensajeError("El servicio " + nuevoServicio.getNombreS() + " ya esta registrado con todas las muestras.");
			limpiarDetalleOT();
			if (nuevoOrdenTrabajo.getNumeromuestraOt() == 0) {
				nuevoDetalleOrden.setMuestra(ordenTrabajoI.muestraDefault());
			}
		} else {
			cargarMetodo(nuevoDetalleOrden);
			nuevoDetalleOrden.setPersonal(new PersonalLab());
			setTempIdServ(0);
			context.update("formDetalleOT");
		}

	}

	public void validarDetalleOTM() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (buscarDetalleOTM(nuevoDetalleOrden, muestra.getIdMuestra()) == true) {
			mensajeError("La muestra " + muestra.getCodigoMCliente() + " ya esta registrado con el servicio "
					+ nuevoDetalleOrden.getIdServicio() + ".");
			muestra = new Muestra();
		}

	}

	public boolean buscarDetalleOTM(Detalleorden detalleorden, String idMuestra) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;

		for (Detalleorden dc : listaDetalleOrden) {
		
			if (dc.getIdServicio().equals(detalleorden.getIdServicio())
					&& dc.getMuestra().getIdMuestra().equals(idMuestra)) {
			
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public boolean buscarDetalleOT(Detalleorden detalleorden) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		int i = 0;

		for (Detalleorden dc : listaDetalleOrden) {

			if (dc.getIdServicio().equals(detalleorden.getIdServicio())) {
				i++;
			}
		}

		if (nuevoOrdenTrabajo.getNumeromuestraOt() == 0) {
			if (i == 1) {
				return true;
			} else {
				return false;
			}
		} else if (nuevoOrdenTrabajo.getNumeromuestraOt() == i) {
			return true;
		} else {
			return false;
		}
	}

	public boolean buscarDetalleOTAdd(Detalleorden detalleorden) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;

		for (Detalleorden dc : listaDetalleOrdenAdd) {

			if (dc.getIdServicio().equals(detalleorden.getIdServicio())) {
	
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

			metodos = ordenTrabajoI.listarMetodosByIdServicio(detalleOrden.getIdServicio());

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
	
		}
	}

	public void calcularTotal() {

	}

	public void calcularTotalEdit() {

	}

	public void cargarDetalleProformaEdit(OrdenTrabajo o, String panel) {

		RequestContext context = RequestContext.getCurrentInstance();
		if (o.getEstadoOt().equals("CLOSED")) {
			context.execute("PF('cdPanelError').show();");
		} else {
			listaDetalleOrden.clear();
			// cambiarIdTempPanel(1); editarOT
			listaDetalleOrden = ordenTrabajoI.listarDetalleOrdenById(o.getIdOrden());
			context.execute("PF('" + panel + "').show();");
		}

	}

	/* Metodos Detalle OT */

	public void buscarClientes() {
		try {

			clientes = ordenTrabajoI.listarClienteBY(getCliente());
			filtroClientes = clientes;

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
	
		}
	}

	public void seleccionarCliente() {

		try {

			nuevoOrdenTrabajo.setCliente(selectCliente);
			mensajeInfo("Se ha seleccionado al cliente: " + selectCliente.getNombreCl());
			selectCliente = new Cliente();

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");

		}
	}

	public void validarSeleccionFactura() {

		RequestContext context = RequestContext.getCurrentInstance();
		if (nuevoOrdenTrabajo.getIdFactura() == null) {
			mensajeError("Debe de buscar una factura para generar el Orden de Trabajo");
		} else {
			if (nuevoOrdenTrabajo.getNumeromuestraOt() == 0) {
				nuevoDetalleOrden.setMuestra(ordenTrabajoI.muestraDefault());
				setTotalRegistros(servicios.size());
			}else{
				setTotalRegistros(servicios.size() * nuevoOrdenTrabajo.getNumeromuestraOt());
			}
			cargarServicios();
			cambiarIdTempPanel(0, 0);
			context.execute("PF('detalleOT').show();");
			context.update("formDetalleOT");
		}
	}

	/*** Factura *****/
	public void buscarFacturas() {
		try {
			Long idUser = su.id_usuario_log;
			facturas = ordenTrabajoI.listarFacturasPagadas(su.UNIDAD_USUARIO_LOGEADO, idUser.intValue());
			filtroFacturas = facturas;

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
		
		}
	}

	public void seleccionarFactura() {

		try {

			nuevoOrdenTrabajo.setIdFactura(selectFactura.getIdFactura());
			factura = selectFactura;
			muestras = ordenTrabajoI.listarMuestraByFactura(factura.getIdFactura());
			nuevoOrdenTrabajo.setNumeromuestraOt(muestras.size());
			muestras.clear();
			nuevoOrdenTrabajo.setCliente(ordenTrabajoI.buscarClienteById(selectFactura.getIdCliente()));

			// mensajeInfo("Se ha seleccionado al cliente: " + nuev);
			selectFactura = new Factura();

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error.");
			
		}
	}

	public void validarFactura() {

		RequestContext context = RequestContext.getCurrentInstance();
		if (nuevoOrdenTrabajo.getIdFactura() == null) {
			mensajeError("Debe buscar una factura y seleccionarla para poder continuar");
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

	public Factura obtenerFactura(String idFactura) {

		Factura f = new Factura();

		f = ordenTrabajoI.buscarFacturaById(idFactura);

		if (f == null) {
			f.setIdProforma("N/A");
		}

		return f;
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

	public String estadoF(String id) {
		String nombre = "";

		EstadoFactura e = new EstadoFactura();

		e = ordenTrabajoI.buscarEstadoFById(id);

		if (e == null) {
			nombre = "N/A";
		} else {
			nombre = e.getNombreEf();
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

	public boolean disabledButtonSeleccionM() {
		if (muestra.getIdMuestra() == null) {
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

	public boolean disabledButtonPer() {
		if (nuevoServicio.getIdServicio() == null) {
			return false;
		} else {
			return true;
		}

	}

	public boolean disabledButtonFac() {
		if (listaDetalleOrden.size() != 0) {
			return false;
		} else {
			return true;
		}

	}

	public boolean disabledButtonM() {

		if (nuevoOrdenTrabajo.getNumeromuestraOt() == 0) {
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
			// formDetalleOT
		}

	}

	public boolean disabledButton() {

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

	public List<PersonalLab> getTempPersonalLabs() {
		return tempPersonalLabs;
	}

	public void setTempPersonalLabs(List<PersonalLab> tempPersonalLabs) {
		this.tempPersonalLabs = tempPersonalLabs;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public Factura getSelectFactura() {
		return selectFactura;
	}

	public void setSelectFactura(Factura selectFactura) {
		this.selectFactura = selectFactura;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

	public List<Factura> getFiltroFacturas() {
		return filtroFacturas;
	}

	public void setFiltroFacturas(List<Factura> filtroFacturas) {
		this.filtroFacturas = filtroFacturas;
	}

	public LazyDataModel<Factura> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModel<Factura> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public List<Muestra> getMuestras() {
		return muestras;
	}

	public void setMuestras(List<Muestra> muestras) {
		this.muestras = muestras;
	}

	public Muestra getMuestraNA() {
		return muestraNA;
	}

	public void setMuestraNA(Muestra muestraNA) {
		this.muestraNA = muestraNA;
	}

	public Muestra getMuestra() {
		return muestra;
	}

	public void setMuestra(Muestra muestra) {
		this.muestra = muestra;
	}

	public List<Muestra> getFiltroMuestras() {
		return filtroMuestras;
	}

	public void setFiltroMuestras(List<Muestra> filtroMuestras) {
		this.filtroMuestras = filtroMuestras;
	}

	public int getTempIdM() {
		return tempIdM;
	}

	public void setTempIdM(int tempIdM) {
		this.tempIdM = tempIdM;
	}

	public List<Detalleorden> getListaDetalleOrdenMuestra() {
		return listaDetalleOrdenMuestra;
	}

	public void setListaDetalleOrdenMuestra(List<Detalleorden> listaDetalleOrdenMuestra) {
		this.listaDetalleOrdenMuestra = listaDetalleOrdenMuestra;
	}

	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

}
