package ec.edu.epn.laboratoriosBJ.controller;

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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import ec.edu.epn.laboratorioBJ.beans.ClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.DetalleProDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.ProformaDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Cliente;
import ec.edu.epn.laboratorioBJ.entities.DetalleProforma;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Proforma;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import javax.faces.application.FacesMessage;

@ManagedBean(name = "proformaController")
@SessionScoped

public class ProformaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/
	/** SERVICIOS **/
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

	/* Proforma */
	private Proforma proforma;
	private Proforma nuevoProforma;
	private Proforma proformaBuscar;
	private List<Proforma> proformas = new ArrayList<Proforma>();
	private List<Proforma> filtroProformas = new ArrayList<Proforma>();

	/* Detalle Proforma */
	private DetalleProforma detalleProforma;
	private DetalleProforma detalleProformaTemp;
	private DetalleProforma selectDetalleProforma;
	private DetalleProforma nuevodetalleProforma;
	private List<DetalleProforma> detalleProformas = new ArrayList<DetalleProforma>();
	private List<DetalleProforma> detalleProformasTemp = new ArrayList<DetalleProforma>();
	private List<DetalleProforma> filtroDetalleProformas = new ArrayList<DetalleProforma>();
	private List<DetalleProforma> detalleProformasAdd = new ArrayList<DetalleProforma>();
	private List<DetalleProforma> detalleProformasDelete = new ArrayList<DetalleProforma>();

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
	private Utilidades utilidades;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {

			/** Proforma **/
			UnidadLabo uni = new UnidadLabo();
			Long iduser = su.id_usuario_log;
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			// proformas = proformaI.listaProformaByUnidadLab(uni.getCodigoU(),
			// iduser.intValue());
			proforma = new Proforma();
			filtroProformas.clear();
			nuevoProforma = new Proforma();
			proformaBuscar = new Proforma();
			// Seteo de valores a 0 para la vista
			nuevoProforma.setSubtotalPo(0);
			nuevoProforma.setIvaPo(0);
			nuevoProforma.setTotalPo(0);
			nuevoProforma.setEstadoPo("Pendiente");
			nuevoProforma.setFecha(new Date());
			proforma.setFecha(new Date());

			/** Detalle Proforma **/
			nuevodetalleProforma = new DetalleProforma();
			detalleProforma = new DetalleProforma();
			selectDetalleProforma = new DetalleProforma();
			detalleProformasAdd.clear();
			detalleProformasDelete.clear();
			// Seteo de valores a 0 para la vista
			nuevodetalleProforma.setCantidadPo(0);

			/** Cliente **/
			cliente = new Cliente();
			selectServicio = new Servicio();
			setTempIdServ(0);
			metodoNA = proformaI.findMetodoById("120");
	

			/** Valores Adicionales **/
			fechaInicio = null;
			fechaFinal = null;

			disabledSelectItem();
			
			utilidades = new Utilidades();

		} catch (Exception e) {

		}

	}

	/* Metodos de limpieza de formularios */

	public void limpiarProforma() {

		proforma = new Proforma();
		nuevoProforma = new Proforma();
		proformaBuscar = new Proforma();
		// Seteo de valores a 0 para la vista
		nuevoProforma.setSubtotalPo(0);
		nuevoProforma.setIvaPo(0);
		nuevoProforma.setTotalPo(0);
		nuevoProforma.setEstadoPo("Pendiente");
		nuevoProforma.setFecha(new Date());

		proformas = new ArrayList<Proforma>();
	}

	public void limpiarDetalleProforma() {

		nuevodetalleProforma = new DetalleProforma();
		detalleProforma = new DetalleProforma();
		// Seteo de valores a 0 para la vista
		nuevodetalleProforma.setCantidadPo(0);

	}

	public void limpiarTodosCampos() {
		try {

			/** Proforma **/
			UnidadLabo uni = new UnidadLabo();
			Long iduser = su.id_usuario_log;
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			// proformas = proformaI.listaProformaByUnidadLab(uni.getCodigoU(),
			// iduser.intValue());
			limpiarProforma();

			/** Detalle Proforma **/
			limpiarDetalleProforma();
			detalleProformas.clear();
			detalleProformasAdd.clear();
			detalleProformasDelete.clear();

			setTempIdServ(0);

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
	
		}

	}

	/****** Metodos de Proformas ****/

	public void guardarProforma() {
		try {
			/** Creacion de ID Proforma **/
			obtenerIDPro();

			/** Seteo de Ids en Detalle Proforma **/
			cambiarIDDetallePro(nuevoProforma);

			/** GUARDAR EN DB **/
			proformaI.save(nuevoProforma);
			guardarDetallePro(detalleProformas);

			utilidades.mensajeInfo("Se ha guardado la nueva proforma con el id (" + nuevoProforma.getIdProforma() + ")");

			/** Limpiar todos los campos **/
			limpiarTodosCampos();

		} catch (Exception e) {
	
			utilidades.mensajeError("Ha ocurrido un error interno.");
		}
	}

	/****** Metodo Editar Proforma ******/

	public void editarPro() {
		// Settear Id a las lista
		cambiarIDDetallePro(proforma);
		cambiarIDDetalleProAdd(proforma);

		// Eliminar A la base de datos
		eliminarDetallesPro(detalleProformasDelete);

		// Agregar A la base de datos
		guardarDetallePro(detalleProformasAdd);

		// Modificar A la base de datos
		actualizarDetallePro(detalleProformas);
		actualizarProforma(proforma);

		utilidades.mensajeInfo("Se ha modificado la proforma (" + proforma.getIdProforma() + ")");

		limpiarTodosCampos();

	}

	public void abrirProforma() {

		try {

			/*
			 * HttpSession sesion=request.getSession();
			 * session.setAttribute("Hola", "Hola");
			 */

			FacesContext contex = FacesContext.getCurrentInstance();
			contex.getExternalContext().getSessionMap().put("id", proforma.getIdProforma());

			contex.getExternalContext().redirect("/SisLab/pages/printProforma.jsf");
		} catch (Exception e) {
				
		}
	}

	public void eliminarProforma() {
		try {
			cargarDetalleProforma(proforma.getIdProforma());

			eliminarDetallesPro(detalleProformas);

			proformaI.delete(getProforma());

			proformas.remove(proforma);

			utilidades.mensajeInfo("Se ha eliminado la proforma (" + proforma.getIdProforma() + ")");
			limpiarProforma();

		} catch (Exception e) {
			// TODO Auto-generated catch block
	
			utilidades.mensajeError("Ha ocurrido un error en la eliminacion");
		}
	}

	public void eliminarDetallesPro(List<DetalleProforma> dps) {
		
		if (dps.size() == 0) {
			
		} else {
			for (DetalleProforma dp : dps) {
				try {
					detalleProI.delete(dp);
				} catch (Exception e) {
			
				}
			}
		}

	}

	public void buscarProforma() {

		try {

			UnidadLabo uni = new UnidadLabo();
			proformaBuscar.setCliente(getCliente());
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			proformas = proformaI.listaProformaByUnidadLab(uni.getCodigoU(), 1, proformaBuscar, fechaInicio,
					fechaFinal);
			filtroProformas = proformas;

			utilidades.mensajeInfo("Resultados Obtenidos :" + proformas.size());
		} catch (Exception e) {
			
		}

	}

	public void buscarAllProformas() {

		try {
			UnidadLabo uni = new UnidadLabo();
			proformaBuscar.setCliente(getCliente());
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			proformas = proformaI.listaAllProformas(uni.getCodigoU(), 1, proformaBuscar, fechaInicio, fechaFinal);

		} catch (Exception e) {
	
		}

	}

	public void calcularTotalPrecios() {
		double resultado = 0;
		for (DetalleProforma dp : detalleProformas) {

			nuevoProforma.setSubtotalPo(dp.getTotalservicioPo() + resultado);
			resultado = nuevoProforma.getSubtotalPo();

		}

		/** Calcular IVA/Total **/
		nuevoProforma.setIvaPo(obtenerIva(nuevoProforma.getSubtotalPo(),
				nuevoProforma.getCliente().getTipocliente().getIvaTcl().doubleValue()));

		nuevoProforma.setTotalPo(nuevoProforma.getIvaPo() + nuevoProforma.getSubtotalPo());

	}

	public void calcularTotalPreciosEdit() {
		double resultado = 0;
		for (DetalleProforma dp : detalleProformas) {

			proforma.setSubtotalPo(dp.getTotalservicioPo() + resultado);
			resultado = proforma.getSubtotalPo();

		}

		/** Calcular IVA/Total **/
		proforma.setIvaPo(
				obtenerIva(proforma.getSubtotalPo(), proforma.getCliente().getTipocliente().getIvaTcl().doubleValue()));

		proforma.setTotalPo(proforma.getIvaPo() + proforma.getSubtotalPo());

	}

	public void obtenerIDPro() {
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

			String codigoAux = proformaI.maxIdProforma(uni.getCodigoU(), fecha);

			/* Validacion en caso cambie de año */
			if (codigoAux == null) {
				codigoAux = (uni.getCodigoU() + "-P0000-" + anio);
			}

			String[] partsId = codigoAux.split("-P", 2);

			partsId = partsId[1].split("-");

			String codigoCortado = partsId[0];

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			Long id = su.id_usuario_log;

			String codigoPro = codigo.toString();

			nuevoProforma.setIdUsuario(id.intValue());
			// nuevoExistencia.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.getUnidad().setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);

			//
			// unidadLabo =
			// ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			// nuevoOrdeninventario.setUnidad(unidadLabo);

			switch (codigoPro.length()) {
			case 1:
				nuevoProforma.setIdProforma(uni.getCodigoU() + "-P" + "000" + codigoPro + "-" + anio);
				break;
			case 2:
				nuevoProforma.setIdProforma(uni.getCodigoU() + "-P" + "00" + codigoPro + "-" + anio);
				break;
			case 3:
				nuevoProforma.setIdProforma(uni.getCodigoU() + "-P" + "0" + codigoPro + "-" + anio);
				break;
			case 4:
				nuevoProforma.setIdProforma(uni.getCodigoU() + "-P" + codigoPro + "-" + anio);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");
		
		}
	}

	public void cargarDetalleProforma(String id) {
		detalleProformasTemp.clear();

		detalleProformasTemp = proformaI.listarDetalleProByIdPro(id);

	}

	public void validarEstadoPro(Proforma p, String panel) {
		RequestContext context = RequestContext.getCurrentInstance();
		if (p.getEstadoPo().equals("Facturada")) {
			context.execute("PF('cdPanelError').show();");
		} else {
			context.execute("PF('" + panel + "').show();");
		}
	}

	public void validarEstadoProEdit(Proforma p, String panel) {
		RequestContext context = RequestContext.getCurrentInstance();
		if (p.getEstadoPo().equals("Facturada")) {
			context.execute("PF('cdPanelError').show();");
		} else {
			cargarDetalleProformaEdit(p.getIdProforma());
			context.execute("PF('" + panel + "').show();");
		}
	}

	/****** Metodos de Detalle Proforma ****/

	public void agregarDetallePro() {
		RequestContext context = RequestContext.getCurrentInstance();

		if (getTempIdServ() == 1) {

			nuevodetalleProforma.setServicio(servicio);
		}

		if (validarCantidadPro()) {
			/** Seteo de valores **/
			nuevodetalleProforma.setValorunitarioPo(nuevodetalleProforma.getServicio().getPrecioS());
			nuevodetalleProforma.setIdLaboratorio(
					String.valueOf(nuevodetalleProforma.getServicio().getLaboratorio().getIdLaboratorio()));

			detalleProformas.add(nuevodetalleProforma);

			if (getTempId() == 1) {
				
				detalleProformasAdd.add(nuevodetalleProforma);
			}

			utilidades.mensajeInfo("Se ha agregado exitosamente.");
			limpiarDetalleProforma();

			if (getTempId() == 0) {
				calcularTotalPrecios();
				context.update("formAgregarPro");

			} else {
				calcularTotalPreciosEdit();
				context.update("formEditarPro");
			}

		} else {
	
		}
	}

	public void actualizarProforma(Proforma p) {
		try {
			proformaI.update(p);
		} catch (Exception e) {
		
		}
	}

	public void editarEstado() {
		try {
			proformaI.update(getProforma());
			utilidades.mensajeInfo("Se ha editado correctamente el estado de (" + proforma.getIdProforma() + ")");
			limpiarProforma();
		} catch (Exception e) {
		
			utilidades.mensajeError("Ha ocurrido un error!");
		}
	}

	public void guardarDetallePro(List<DetalleProforma> dps) {

		if (dps.size() == 0) {
		
		} else {
			for (DetalleProforma detallePo : dps) {
				try {
					detalleProI.save(detallePo);
				} catch (Exception e) {
				
				}

			}
		}

	}

	public void actualizarDetallePro(List<DetalleProforma> dps) {
		
		for (DetalleProforma detallePo : dps) {
			try {
				detalleProI.update(detallePo);
			} catch (Exception e) {
				
			}

		}
	}

	public void guardarTempDetallePro() {
	
		setDetalleProformaTemp(getDetalleProforma());
		
	}

	public void editarDetallePro() {
		RequestContext context = RequestContext.getCurrentInstance();

		if (getTempId() == 1) {

			editarDetalleTemp(detalleProforma);

			calcularTotalPreciosEdit();

			context.update("formEditarPro");

			context.execute("PF('editarDetallePro').hide();");

		} else {
			calcularTotalPrecios();

			context.update("formAgregarPro");

			context.execute("PF('editarDetallePro').hide();");

		}

		utilidades.mensajeInfo("Se ha editado Correctamente");

	}

	public void editarDetalleTemp(DetalleProforma detalleProforma) {
		int i = 0;
		for (DetalleProforma dp : detalleProformasAdd) {
			if (dp.getServicio().getIdServicio().equals(detalleProforma.getServicio().getIdServicio())) {
				detalleProformasAdd.set(i, detalleProforma);
				break;
			} else {
				i++;
			}
		}
	}

	public void validarCantidad(FacesContext ctx, UIComponent component, Integer value) throws ValidatorException {
		// String prueba = value.toString();

		if (detalleProforma.getServicio().getPrecioS() == 0) {

			return;
		} else {
			if (value.intValue() == 0) {

				// mensajeError("La cantidad es mayor que el saldo existente");

				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error",
						"La cantidad debe ser mayor a 0"));
			} else {

				return;

			}
		}

	}

	public void eliminarDetallePro() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();

			// Seteo de listas temporales
			setListasTempDelete();

			detalleProformas.remove(detalleProforma);

			if (getTempId() == 0) {
				calcularTotalPrecios();
				context.update("formAgregarPro");
			} else {
				calcularTotalPreciosEdit();
				context.update("formEditarPro");
			}

			utilidades.mensajeInfo(
					"Se ha eliminado el datelle de la proforma (" + detalleProforma.getServicio().getNombreS() + ")");

		} catch (Exception e) {
		
			utilidades.mensajeError("Ha ocurrido un error interno.");
		}
	}

	public void setListasTempDelete() {
		if (getTempId() == 1) {
			if (buscarDetalleProAdd(detalleProforma)) {
				detalleProformasAdd.remove(detalleProforma);
			
			} else {
				detalleProformasDelete.add(detalleProforma);
			
			}
		}
	}

	public void cambiarIDDetallePro(Proforma p) {
	
		int i = 0;
		for (DetalleProforma detallePo : detalleProformas) {

			detallePo.setProforma(p);
			detalleProformas.set(i, detallePo);

			i++;
		}
	}

	public void cambiarIDDetalleProAdd(Proforma p) {
	
		int i = 0;
		if (detalleProformasAdd.size() == 0) {
			
		} else {
			for (DetalleProforma detallePo : detalleProformasAdd) {

				detallePo.setProforma(p);
				detalleProformasAdd.set(i, detallePo);

				i++;
			}
		}

	}

	public boolean buscarDetallePro(DetalleProforma detallePro) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;


		for (DetalleProforma dp : detalleProformas) {

			if (dp.getServicio().getIdServicio().equals(detallePro.getServicio().getIdServicio())) {

				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public boolean buscarDetalleProAdd(DetalleProforma detalleProforma) {
		// Realiza una busqueda en la lista temporal de Detalle Proforma
		boolean resultado = false;

		for (DetalleProforma dp : detalleProformasAdd) {
			
			if (dp.getServicio().getNombreS().equals(detalleProforma.getServicio().getNombreS())) {
			
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	/* Metodos de servicios */

	public void cargarServicios() {
		try {

			UnidadLabo uni = new UnidadLabo();
			Long iduser = su.id_usuario_log;
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			servicios = proformaI.listarServiciosByLab(su.UNIDAD_USUARIO_LOGEADO + "");
			filtroServicios = servicios;

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error.");
	
		}

	}

	public void cargarAllServicios() {
		try {

			allServicios = servicioI.getAll(Servicio.class);
			filtroAllServicios = allServicios;
		
		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error.");
		
		}

	}

	public void seleccionarServicio() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {

			if (buscarServicio(selectServicio) == false) {

				setServicio(selectServicio);
				nuevodetalleProforma.setServicio(getSelectServicio());

				utilidades.mensajeInfo("El servicio " + selectServicio.getIdServicio() + " se ha seleccionado.");
				cargarMetodo(nuevodetalleProforma);
				context.update("formDetallePro");
				context.execute("PF('listadoServ').hide()");
				setTempIdServ(1);

			} else {
				utilidades.mensajeError("El servicio " + selectServicio.getNombreS() + " ya esta agregado.");
				throw new Exception("Error");
			}

		} catch (Exception e) {
			
		}

	}

	public boolean buscarServicio(Servicio servicio) {
		boolean resultado = false;

		for (DetalleProforma dp : detalleProformas) {
		
			if (dp.getServicio().getIdServicio().equals(servicio.getIdServicio())) {
			
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void validarAllDetallePro() {

		if (buscarDetallePro(selectDetalleProforma) == true) {
			utilidades.mensajeError("El servicio " + selectDetalleProforma.getServicio().getNombreS() + " ya esta agregado.");
		}

	}

	public void validarDetallePro() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (buscarDetallePro(nuevodetalleProforma) == true) {
			utilidades.mensajeError("El servicio " + nuevodetalleProforma.getServicio().getNombreS() + " ya esta agregado.");
		} else {
			cargarMetodo(nuevodetalleProforma);
					setTempIdServ(0);
			context.update("formDetallePro");
		}

	}

	public boolean validarCantidadPro() {

		if (nuevodetalleProforma.getServicio().getPrecioS() == 0) {
			return true;
		} else {
			if (nuevodetalleProforma.getCantidadPo() == 0) {

				utilidades.mensajeError("Debe ingresar una cantidad que no sea 0");
				return false;
			} else {

				return true;

			}
		}

	}

	public void cargarMetodo(DetalleProforma detalleProforma) {
		try {

			metodos = proformaI.listarMetodosByIdServicio(detalleProforma.getServicio().getIdServicio());

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error.");
		
		}
	}

	public void calcularTotal() {
		nuevodetalleProforma.setTotalservicioPo(
				nuevodetalleProforma.getServicio().getPrecioS() * nuevodetalleProforma.getCantidadPo());
	}

	public void calcularTotalEdit() {

		detalleProforma
				.setTotalservicioPo(detalleProforma.getServicio().getPrecioS() * detalleProforma.getCantidadPo());
	}

	public void cargarDetalleProformaEdit(String id) {
		detalleProformas.clear();
	
		detalleProformas = proformaI.listarDetalleProByIdPro(id);

	}

	/* Metodos Detalle Pro */

	public void buscarClientes() {
		try {

			clientes = proformaI.listarClienteBY(getCliente());
			filtroClientes = clientes;

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error.");

		}
	}

	public void seleccionarCliente() {

		try {

			nuevoProforma.setCliente(selectCliente);
			nuevoProforma.setRepresentantePo(selectCliente.getContactoCl());
			utilidades.mensajeInfo("Se ha seleccionado al cliente: " + selectCliente.getNombreCl());
			selectCliente = new Cliente();

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error.");
	
		}

	}

	public void validarCliente() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (nuevoProforma.getCliente() == null) {
			utilidades.mensajeError("Debe buscar un cliente y seleccionarlo");
		} else {
			cargarServicios();
			cambiarIdTempPanel(0, 0);
			context.execute("PF('detallePro').show();");
		}

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
	
	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}

	/** Calcular IVA **/
	public double obtenerIva(double base, double porcentaje) {
		return base * porcentaje / 100;
	}

	/****** Manejo de fechas ****/
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

	public boolean disabledButton() {

		if (detalleProformas.size() == 0) {
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

	public Proforma getProforma() {
		return proforma;
	}

	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}

	public Proforma getNuevoProforma() {
		return nuevoProforma;
	}

	public void setNuevoProforma(Proforma nuevoProforma) {
		this.nuevoProforma = nuevoProforma;
	}

	public List<Proforma> getProformas() {
		return proformas;
	}

	public void setProformas(List<Proforma> proformas) {
		this.proformas = proformas;
	}

	public List<Proforma> getFiltroProformas() {
		return filtroProformas;
	}

	public void setFiltroProformas(List<Proforma> filtroProformas) {
		this.filtroProformas = filtroProformas;
	}

	public DetalleProforma getNuevodetalleProforma() {
		return nuevodetalleProforma;
	}

	public void setNuevodetalleProforma(DetalleProforma nuevodetalleProforma) {
		this.nuevodetalleProforma = nuevodetalleProforma;
	}

	public DetalleProforma getDetalleProforma() {
		return detalleProforma;
	}

	public void setDetalleProforma(DetalleProforma detalleProforma) {
		this.detalleProforma = detalleProforma;
	}

	public List<DetalleProforma> getDetalleProformas() {
		return detalleProformas;
	}

	public void setDetalleProformas(List<DetalleProforma> detalleProformas) {
		this.detalleProformas = detalleProformas;
	}

	public List<DetalleProforma> getFiltroDetalleProformas() {
		return filtroDetalleProformas;
	}

	public void setFiltroDetalleProformas(List<DetalleProforma> filtroDetalleProformas) {
		this.filtroDetalleProformas = filtroDetalleProformas;
	}

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

	public List<DetalleProforma> getDetalleProformasTemp() {
		return detalleProformasTemp;
	}

	public void setDetalleProformasTemp(List<DetalleProforma> detalleProformasTemp) {
		this.detalleProformasTemp = detalleProformasTemp;
	}

	public DetalleProforma getDetalleProformaTemp() {
		return detalleProformaTemp;
	}

	public void setDetalleProformaTemp(DetalleProforma detalleProformaTemp) {
		this.detalleProformaTemp = detalleProformaTemp;
	}

	public int getTempId() {
		return tempId;
	}

	public void setTempId(int tempId) {
		this.tempId = tempId;
	}

	public List<DetalleProforma> getDetalleProformasAdd() {
		return detalleProformasAdd;
	}

	public void setDetalleProformasAdd(List<DetalleProforma> detalleProformasAdd) {
		this.detalleProformasAdd = detalleProformasAdd;
	}

	public List<DetalleProforma> getDetalleProformasDelete() {
		return detalleProformasDelete;
	}

	public void setDetalleProformasDelete(List<DetalleProforma> detalleProformasDelete) {
		this.detalleProformasDelete = detalleProformasDelete;
	}

	public Proforma getProformaBuscar() {
		return proformaBuscar;
	}

	public void setProformaBuscar(Proforma proformaBuscar) {
		this.proformaBuscar = proformaBuscar;
	}

	public int getTempId2() {
		return tempId2;
	}

	public void setTempId2(int tempId2) {
		this.tempId2 = tempId2;
	}

	public DetalleProforma getSelectDetalleProforma() {
		return selectDetalleProforma;
	}

	public void setSelectDetalleProforma(DetalleProforma selectDetalleProforma) {
		this.selectDetalleProforma = selectDetalleProforma;
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

}
