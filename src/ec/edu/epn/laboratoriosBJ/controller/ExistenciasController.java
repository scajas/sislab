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

import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO;
import ec.edu.epn.laboratorioBJ.beans.ConcentracionDAO;
import ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.GradoDAO;
import ec.edu.epn.laboratorioBJ.beans.HidratacionDAO;
import ec.edu.epn.laboratorioBJ.beans.LaboratoryDAO;
import ec.edu.epn.laboratorioBJ.beans.PosgiroDAO;
import ec.edu.epn.laboratorioBJ.beans.PresentacionDAO;
import ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO;
import ec.edu.epn.laboratorioBJ.beans.PurezaDAO;

import ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Caracteristica;
import ec.edu.epn.laboratorioBJ.entities.Concentracion;
import ec.edu.epn.laboratorioBJ.entities.Estadoproducto;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Grado;
import ec.edu.epn.laboratorioBJ.entities.Hidratacion;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Posgiro;
import ec.edu.epn.laboratorioBJ.entities.Presentacion;
import ec.edu.epn.laboratorioBJ.entities.ProductoLab;
import ec.edu.epn.laboratorioBJ.entities.Pureza;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.laboratorioBJ.entities.Unidadmedida;
import ec.edu.epn.laboratorioBJ.entities.laboratory;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import javax.faces.application.FacesMessage;

@ManagedBean(name = "existenciasController")
@SessionScoped
public class ExistenciasController implements Serializable {

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

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PresentacionDAOImplement!ec.edu.epn.laboratorioBJ.beans.PresentacionDAO")
	private PresentacionDAO presentacionI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProductoLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO")
	private ProductoLabDAO productoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/EstadoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO")
	private EstadoProductoDAO estadoProductoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/GradoDAOImplement!ec.edu.epn.laboratorioBJ.beans.GradoDAO")
	private GradoDAO gradoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PosgiroDAOImplement!ec.edu.epn.laboratorioBJ.beans.PosgiroDAO")
	private PosgiroDAO posgiroI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/HidratacionDAOImplement!ec.edu.epn.laboratorioBJ.beans.HidratacionDAO")
	private HidratacionDAO hidratacionI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/CaracteristicaDAOImplement!ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO")
	private CaracteristicaDAO caracteristicaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ConcentracionDAOImplement!ec.edu.epn.laboratorioBJ.beans.ConcentracionDAO")
	private ConcentracionDAO concentracionI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO")
	private TipoProductoDAO tipoProductoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PurezaDAOImplement!ec.edu.epn.laboratorioBJ.beans.PurezaDAO")
	private PurezaDAO purezaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratoryDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratoryDAO")
	private LaboratoryDAO bodegaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadMedidaDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO")
	private UnidadMedidaDAO unidadMedidaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	/****************************************************************************/
	/** VARIABLES **/

	private String nombreEx;

	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filtrarExistencias = new ArrayList<>();// filtro
	private List<ProductoLab> productos = new ArrayList<>();// filtro
	private List<Movimientosinventario> movimientosinventarios = new ArrayList<>();
	private Existencia nuevoExistencia;
	private Existencia existencia;// eliminar y editar
	private String nombreTP;
	private String nombrePro;

	/** Variables para select **/

	// select de Presentacion
	private Presentacion presentacionSelect;
	private List<Presentacion> presentaciones = new ArrayList<Presentacion>();
	private Presentacion presentacion;

	// select de Estadoproducto
	private Estadoproducto estadoProSelect;
	private List<Estadoproducto> estadoproductos = new ArrayList<Estadoproducto>();
	private Estadoproducto estadoproducto;

	// select de Grado
	private Grado gradoSelect;
	private List<Grado> grados = new ArrayList<Grado>();
	private Grado grado;

	// select de Posgiro
	private Posgiro posGiroSelect;
	private List<Posgiro> posgiros = new ArrayList<Posgiro>();
	private Posgiro posgiro;

	// select de Hidratacion
	private Hidratacion hidratacionSelect;
	private List<Hidratacion> hidrataciones = new ArrayList<Hidratacion>();
	private Hidratacion hidratacion;

	// select de Caracteristica
	private Caracteristica caracteristicaSelect;
	private List<Caracteristica> caracteristicas = new ArrayList<Caracteristica>();
	private Caracteristica caracteristica;

	// select de Concentracion
	private Concentracion concentracionSelect;
	private List<Concentracion> concentracions = new ArrayList<Concentracion>();
	private Concentracion concentracion;

	// select de Tipoproducto
	private Tipoproducto tipoproductoSelect;
	private List<Tipoproducto> tipoproductos = new ArrayList<Tipoproducto>();
	private Tipoproducto tipoproducto;
	private Tipoproducto tempTipoproducto;

	// select de Pureza
	private Pureza purezaSelect;
	private List<Pureza> purezas = new ArrayList<Pureza>();
	private Pureza pureza;

	// select de Bodega
	private laboratory bodegaSelect;
	private List<laboratory> bodegas = new ArrayList<laboratory>();
	private laboratory bodega;

	// select de Unidadmedida
	private Unidadmedida unidadmedida2Select;
	private List<Unidadmedida> unidadmedidas = new ArrayList<Unidadmedida>();
	private Unidadmedida unidadmedida;

	private Unidadmedida tempUnidadMedida;

	/** METODO Init **/
	@PostConstruct
	public void init() {

		try {

			/** Existencia **/
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			existencias = existenciasI.getListExByTP(uni.getCodigoU());

			filtrarExistencias = existencias;

			existencia = new Existencia();
			nuevoExistencia = new Existencia();

			// init de Presentacion
			presentaciones = presentacionI.getAll(Presentacion.class);
			presentacion = existenciasI.reemplazarNullPresentacion();

			// init de Estadoproducto
			estadoproductos = estadoProductoI.getAll(Estadoproducto.class);
			estadoproducto = existenciasI.reemplazarNullEstadoPro();

			// init de Grado
			grados = gradoI.getAll(Grado.class);
			grado = existenciasI.reemplazarNullGrado();

			// init de Posgiro
			posgiros = posgiroI.getAll(Posgiro.class);
			posgiro = existenciasI.reemplazarNullPosgiro();

			// init de Hidratacion
			hidrataciones = hidratacionI.getAll(Hidratacion.class);
			hidratacion = existenciasI.reemplazarNullHidratacion();

			// init de Caracteristica
			caracteristicas = caracteristicaI.getAll(Caracteristica.class);
			caracteristica = existenciasI.reemplazarNullCaracteristica();

			// init de Concentracion
			concentracions = concentracionI.getAll(Concentracion.class);
			concentracion = existenciasI.reemplazarNullConcentracion();

			// init de Tipoproducto
			tipoproductos = tipoProductoI.getAll(Tipoproducto.class);
			tipoproducto = new Tipoproducto();

			tempTipoproducto = existenciasI.tempTipoPro();
			tempTipoproducto.setIdTipoprod(99999);

			// init de Pureza
			purezas = purezaI.getAll(Pureza.class);
			pureza = existenciasI.reemplazarNullPureza();

			// init de Bodega
			bodegas = bodegaI.getAll(laboratory.class);
			bodega = existenciasI.reemplazarNullBodega();

			// init de Unidadmedida
			unidadmedidas = unidadMedidaI.getAll(Unidadmedida.class);
			unidadmedida = new Unidadmedida();

			tempUnidadMedida = existenciasI.tempUnidadMedida();
			tempUnidadMedida.setIdUmedida(99999);

		} catch (Exception e) {

		}

	}

	public void updateTable() {
		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			existencias = existenciasI.getListExByTP(uni.getCodigoU());
		} catch (Exception e) {

		}
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

	/****** Agregar Estado Producto ****/
	public void limpiarCampos() {

		nuevoExistencia = new Existencia();
		presentacionSelect = new Presentacion();
		estadoProSelect = new Estadoproducto();
		gradoSelect = new Grado();
		posGiroSelect = new Posgiro();
		
		hidratacionSelect = new Hidratacion(); //err
		
		caracteristicaSelect = new Caracteristica();
		concentracionSelect = new Concentracion();
		tipoproductoSelect = new Tipoproducto();
		purezaSelect = new Pureza();
		bodegaSelect = new laboratory();
		unidadmedida2Select = new Unidadmedida();

		setNombrePro(null);

	}

	public void crearIdExistencia() {

		try {

			String codigoAux = existenciasI.maxIdServ(su.UNIDAD_USUARIO_LOGEADO);
			String codigoCortado = codigoAux.substring(5, 10);

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			String codigoExistencia = codigo.toString();
			nuevoExistencia.setIdUnidad(su.UNIDAD_USUARIO_LOGEADO);
			UnidadLabo uni = new UnidadLabo();

			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			switch (codigoExistencia.length()) {
			case 1:
				nuevoExistencia.setIdExistencia(uni.getCodigoU() + "-E-" + "0000" + codigoExistencia);
				break;
			case 2:
				nuevoExistencia.setIdExistencia(uni.getCodigoU() + "-E-" + "000" + codigoExistencia);
				break;
			case 3:
				nuevoExistencia.setIdExistencia(uni.getCodigoU() + "-E-" + "00" + codigoExistencia);
				break;
			case 4:
				nuevoExistencia.setIdExistencia(uni.getCodigoU() + "-E-" + "0" + codigoExistencia); // DC-E-2217
				break;
			case 5:
				nuevoExistencia.setIdExistencia(uni.getCodigoU() + "-E-" + codigoExistencia); // DC-E-2217
				break;

			default:
				break;

			}

		} catch (Exception e) {

		}

	}


	public void agregarExistencia() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarExistencia(nuevoExistencia.getIdExistencia()) == true) {

				mensajeError("La Muestra (" + nuevoExistencia.getIdExistencia() + ") ya existe.");

			} else {

				crearIdExistencia();

				nuevoExistencia.setBodega(bodegaSelect);
				nuevoExistencia.setPresentacion(presentacionSelect);
				nuevoExistencia.setUnidadmedida(unidadmedida2Select);
				nuevoExistencia.setEstadoproducto(estadoProSelect);
				nuevoExistencia.setGrado(gradoSelect);
				nuevoExistencia.setPosgiro(posGiroSelect);
				nuevoExistencia.setPurezaE(purezaSelect.getIdPureza());
				nuevoExistencia.setConcentracion(concentracionSelect);
				nuevoExistencia.setCaracteristica(caracteristicaSelect);
				nuevoExistencia.setTipoproducto(tipoproductoSelect);

				nuevoExistencia.setIdHidratacion(String.valueOf(hidratacionSelect.getIdHidratacion()));

				existenciasI.save(nuevoExistencia);

				mensajeInfo("La existencia (" + nuevoExistencia.getIdExistencia() + ") se ha almacenado exitosamente");

				updateTable();

				limpiarCampos();

				productos.clear();

				context.execute("PF('nuevoEx').hide();");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

			e.printStackTrace();

		}

	}

	/****** Modificar Existencia ****/

	public void modificarExistencia() {
		try {

			existenciasI.update(existencia);
			updateTable();

			mensajeInfo("La Existencia (" + existencia.getIdExistencia() + ") se ha actualizado exitosamente.");

		} catch (Exception e) {
			mensajeError("Ha ocurrido un problema");
		}
	}

	/****** Eliminar Existencia ****/

	public void eliminarExistencia() {

		try {

			existenciasI.delete(existencia);
			updateTable();
			mensajeInfo("La Existencia (" + existencia.getIdExistencia() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				mensajeError("La Existencia (" + existencia.getIdExistencia() + ") tiene relación con otra tabla.");
			} else {
				mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	/****** Busqueda de Producto ****/
	public void busquedaGlobal() {

		setProductos(existenciasI.filtrarLista(getNombrePro()));
	}

	/****** Wizard Validation ****/

	public String onFlowProcess(FlowEvent event) {

		String resultado = null;

		switch (event.getNewStep()) {

		case "infoGeneral":

			if (tipoproductoSelect.getIdTipoprod() == 99999) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese el Tipo de Producto.", null);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				resultado = event.getOldStep();
			} else {
				resultado = event.getNewStep();
			}

			break;

		case "confirm":

			System.out.println("Entra al Case 2");
			if (Integer.valueOf(bodegaSelect.getIdBodega()) == 1) {

				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese la Bodega.", null);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				resultado = event.getOldStep();

			} else if (nuevoExistencia.getCantidadE().intValue() == 0) {

				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese el Saldo.", null);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				resultado = event.getOldStep();

			} else if (unidadmedida2Select.getIdUmedida() == 99999) {

				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese la Unidad de Medida.", null);
				FacesContext.getCurrentInstance().addMessage(null, msg);
				resultado = event.getOldStep();

			}

			else {

				resultado = event.getNewStep();
			}

			break;

		default:

			System.out.println("Entra al default");
			resultado = event.getNewStep();
			break;

		}

		System.out.println("Case: " + resultado);
		return resultado;

	}

	/****** Busqueda de ID Existencia ****/

	private boolean buscarExistencia(String valor) {

		try {
			existencias = existenciasI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (Existencia existencia : existencias) {
			if (existencia.getIdExistencia().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void listaMovimientoInventario(String idExistencia) {
		try {

			setMovimientosinventarios(existenciasI.listarMovimientoById(idExistencia));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****** Reemplazar valores de la tabla o formulario ****/

	public String pureza(String idPureza) {
		String nombre = "";

		Pureza purezaN = new Pureza();

		purezaN = existenciasI.buscarPurezaById(idPureza);

		if (purezaN == null) {
			nombre = "N/A";
		} else {
			nombre = purezaN.getNombrePureza();
		}

		return nombre;
	}

	public String hidratacion(String idHidratacion) {
		String nombre = "";

		Hidratacion hidratacionN = new Hidratacion();

		hidratacionN = existenciasI.buscarHidratacionById(idHidratacion);

		if (hidratacionN == null) {
			nombre = "N/A";
		} else {
			nombre = hidratacionN.getNombreHi();
		}

		return nombre;
	}

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

	/****** Getter y Setter de Estado Producto ****/

	public String getNombreEx() {
		return nombreEx;
	}

	public void setNombreEx(String nombreEx) {
		this.nombreEx = nombreEx;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public Existencia getNuevoExistencia() {
		return nuevoExistencia;
	}

	public void setNuevoExistencia(Existencia nuevoExistencia) {
		this.nuevoExistencia = nuevoExistencia;
	}

	public Existencia getExistencia() {
		return existencia;
	}

	public void setExistencia(Existencia existencia) {
		this.existencia = existencia;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public List<Existencia> getFiltrarExistencias() {
		return filtrarExistencias;
	}

	public void setFiltrarExistencias(List<Existencia> filtrarExistencias) {
		this.filtrarExistencias = filtrarExistencias;
	}

	public List<ProductoLab> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoLab> productos) {
		this.productos = productos;
	}

	public String getNombrePro() {
		return nombrePro;
	}

	public void setNombrePro(String nombrePro) {
		this.nombrePro = nombrePro;
	}

	public Presentacion getPresentacionSelect() {
		return presentacionSelect;
	}

	public void setPresentacionSelect(Presentacion presentacionSelect) {
		this.presentacionSelect = presentacionSelect;
	}

	public List<Presentacion> getPresentaciones() {
		return presentaciones;
	}

	public void setPresentaciones(List<Presentacion> presentaciones) {
		this.presentaciones = presentaciones;
	}

	public Presentacion getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}

	public Estadoproducto getEstadoProSelect() {
		return estadoProSelect;
	}

	public void setEstadoProSelect(Estadoproducto estadoProSelect) {
		this.estadoProSelect = estadoProSelect;
	}

	public List<Estadoproducto> getEstadoproductos() {
		return estadoproductos;
	}

	public void setEstadoproductos(List<Estadoproducto> estadoproductos) {
		this.estadoproductos = estadoproductos;
	}

	public Estadoproducto getEstadoproducto() {
		return estadoproducto;
	}

	public void setEstadoproducto(Estadoproducto estadoproducto) {
		this.estadoproducto = estadoproducto;
	}

	public Grado getGradoSelect() {
		return gradoSelect;
	}

	public void setGradoSelect(Grado gradoSelect) {
		this.gradoSelect = gradoSelect;
	}

	public List<Grado> getGrados() {
		return grados;
	}

	public void setGrados(List<Grado> grados) {
		this.grados = grados;
	}

	public Grado getGrado() {
		return grado;
	}

	public void setGrado(Grado grado) {
		this.grado = grado;
	}

	public Posgiro getPosGiroSelect() {
		return posGiroSelect;
	}

	public void setPosGiroSelect(Posgiro posGiroSelect) {
		this.posGiroSelect = posGiroSelect;
	}

	public List<Posgiro> getPosgiros() {
		return posgiros;
	}

	public void setPosgiros(List<Posgiro> posgiros) {
		this.posgiros = posgiros;
	}

	public Posgiro getPosgiro() {
		return posgiro;
	}

	public void setPosgiro(Posgiro posgiro) {
		this.posgiro = posgiro;
	}

	public Hidratacion getHidratacionSelect() {
		return hidratacionSelect;
	}

	public void setHidratacionSelect(Hidratacion hidratacionSelect) {
		this.hidratacionSelect = hidratacionSelect;
	}

	public List<Hidratacion> getHidrataciones() {
		return hidrataciones;
	}

	public void setHidrataciones(List<Hidratacion> hidrataciones) {
		this.hidrataciones = hidrataciones;
	}

	public Hidratacion getHidratacion() {
		return hidratacion;
	}

	public void setHidratacion(Hidratacion hidratacion) {
		this.hidratacion = hidratacion;
	}

	public Caracteristica getCaracteristicaSelect() {
		return caracteristicaSelect;
	}

	public void setCaracteristicaSelect(Caracteristica caracteristicaSelect) {
		this.caracteristicaSelect = caracteristicaSelect;
	}

	public List<Caracteristica> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<Caracteristica> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public Caracteristica getCaracteristica() {
		return caracteristica;
	}

	public void setCaracteristica(Caracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}

	public Concentracion getConcentracionSelect() {
		return concentracionSelect;
	}

	public void setConcentracionSelect(Concentracion concentracionSelect) {
		this.concentracionSelect = concentracionSelect;
	}

	public List<Concentracion> getConcentracions() {
		return concentracions;
	}

	public void setConcentracions(List<Concentracion> concentracions) {
		this.concentracions = concentracions;
	}

	public Concentracion getConcentracion() {
		return concentracion;
	}

	public void setConcentracion(Concentracion concentracion) {
		this.concentracion = concentracion;
	}

	public Tipoproducto getTipoproductoSelect() {
		return tipoproductoSelect;
	}

	public void setTipoproductoSelect(Tipoproducto tipoproductoSelect) {
		this.tipoproductoSelect = tipoproductoSelect;
	}

	public List<Tipoproducto> getTipoproductos() {
		return tipoproductos;
	}

	public void setTipoproductos(List<Tipoproducto> tipoproductos) {
		this.tipoproductos = tipoproductos;
	}

	public Tipoproducto getTipoproducto() {
		return tipoproducto;
	}

	public void setTipoproducto(Tipoproducto tipoproducto) {
		this.tipoproducto = tipoproducto;
	}

	public Pureza getPurezaSelect() {
		return purezaSelect;
	}

	public void setPurezaSelect(Pureza purezaSelect) {
		this.purezaSelect = purezaSelect;
	}

	public List<Pureza> getPurezas() {
		return purezas;
	}

	public void setPurezas(List<Pureza> purezas) {
		this.purezas = purezas;
	}

	public Pureza getPureza() {
		return pureza;
	}

	public void setPureza(Pureza pureza) {
		this.pureza = pureza;
	}

	public laboratory getBodegaSelect() {
		return bodegaSelect;
	}

	public void setBodegaSelect(laboratory bodegaSelect) {
		this.bodegaSelect = bodegaSelect;
	}

	public List<laboratory> getBodegas() {
		return bodegas;
	}

	public void setBodegas(List<laboratory> bodegas) {
		this.bodegas = bodegas;
	}

	public laboratory getBodega() {
		return bodega;
	}

	public void setBodega(laboratory bodega) {
		this.bodega = bodega;
	}

	public Unidadmedida getUnidadmedida2Select() {
		return unidadmedida2Select;
	}

	public void setUnidadmedida2Select(Unidadmedida unidadmedida2Select) {
		this.unidadmedida2Select = unidadmedida2Select;
	}

	public List<Unidadmedida> getUnidadmedidas() {
		return unidadmedidas;
	}

	public void setUnidadmedidas(List<Unidadmedida> unidadmedidas) {
		this.unidadmedidas = unidadmedidas;
	}

	public Unidadmedida getUnidadmedida() {
		return unidadmedida;
	}

	public void setUnidadmedida(Unidadmedida unidadmedida) {
		this.unidadmedida = unidadmedida;
	}

	public List<Movimientosinventario> getMovimientosinventarios() {
		return movimientosinventarios;
	}

	public void setMovimientosinventarios(List<Movimientosinventario> movimientosinventarios) {
		this.movimientosinventarios = movimientosinventarios;
	}

	public Tipoproducto getTempTipoproducto() {
		return tempTipoproducto;
	}

	public void setTempTipoproducto(Tipoproducto tempTipoproducto) {
		this.tempTipoproducto = tempTipoproducto;
	}

	public Unidadmedida getTempUnidadMedida() {
		return tempUnidadMedida;
	}

	public void setTempUnidadMedida(Unidadmedida tempUnidadMedida) {
		this.tempUnidadMedida = tempUnidadMedida;
	}

}
