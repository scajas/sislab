package ec.edu.epn.laboratoriosBJ.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.CompraDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO;
import ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.Compra;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Ordeninventario;
import ec.edu.epn.laboratorioBJ.entities.ProveedorLab;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean(name = "compraController")
@SessionScoped

public class CompraController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/CompraDAOImplement!ec.edu.epn.laboratorioBJ.beans.CompraDAO")
	private CompraDAO compraI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProveedorLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProveedorLabDAO")
	private ProveedorLabDAO proveedorI;// I (interface)

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/OrdenInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.OrdenInventarioDAO") /* system */
	private OrdenInventarioDAO ordenInventarioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MovimientosInventarioDAOImplement!ec.edu.epn.laboratorioBJ.beans.MovimientosInventarioDAO")
	private MovimientosInventarioDAO movimientoInventarioI;

	// Variables

	private Compra compra;
	private List<Compra> compras = new ArrayList<Compra>();
	private Compra nuevaCompra;
	private List<Compra> filterCompras;
	private List<Compra> nuevaCompras = new ArrayList<>();

	// proveedorLab
	private ProveedorLab proveedor;
	private List<ProveedorLab> proveedores = new ArrayList<ProveedorLab>();
	private ProveedorLab proveedorSelect;

	private Date fechaInicio;
	private Date fechaFin;

	private Ordeninventario ordeninventario;
	private Ordeninventario nuevoOrdeninventario;
	private List<Ordeninventario> ordenInventarios = new ArrayList<>();
	private List<Ordeninventario> tempOrdenInventarios = new ArrayList<>();
	private List<Ordeninventario> nuevoOrdenInventarios = new ArrayList<>();
	private Ordeninventario tempOrdenInventario;

	private Existencia existencia;
	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filterExistencia;
	private Existencia selectExistencia;
	private List<Existencia> tempExistencias = new ArrayList<>();

	private List<Movimientosinventario> movimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> tempMovimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> filtroMovimientoInventarios = new ArrayList<>();
	private List<Movimientosinventario> nuevoMovimientoInventarios = new ArrayList<>();
	private Movimientosinventario nuevoMovimientoInventario;
	private Movimientosinventario nuevoMovimientoInventarioAux;
	private Movimientosinventario movimientoInventario;

	UnidadLabo unidadLabo;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			tblCompras();
			compra = new Compra();
			nuevaCompra = new Compra();

			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			ordenInventarios = ordenInventarioI.getListOIById(uni.getCodigoU());

			ordeninventario = new Ordeninventario();
			nuevoOrdeninventario = new Ordeninventario();

			proveedores = proveedorI.getListProveedor();
			proveedor = new ProveedorLab();
			proveedorSelect = new ProveedorLab();

			nuevoMovimientoInventarios = new ArrayList<>();
			tempMovimientoInventarios = new ArrayList<>();
			nuevoMovimientoInventario = new Movimientosinventario();
			nuevoMovimientoInventarioAux = new Movimientosinventario();
			movimientoInventario = new Movimientosinventario();
			nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));
			nuevoMovimientoInventarioAux.setCantidadMov(new BigDecimal(0));

			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
			tempExistencias = new ArrayList<Existencia>();
			selectExistencia = new Existencia();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void tblCompras() throws Exception {

		UnidadLabo uni = new UnidadLabo();
		uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
		compras = compraI.getListCompras(uni.getCodigoU());
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

	/****** Busqueda de Existencias ****/
	public Existencia cambiarDatosExistencia(String id) {
		Existencia existenciatemp = movimientoInventarioI.buscarExistenciaById(id);
		return existenciatemp;
	}

	/****** Ordenes de Inventario por ID Compra ****/
	public Ordeninventario obtenerOrden(String id) {
		Ordeninventario existenciatemp = compraI.ordenInvByCompra(id);
		return existenciatemp;
	}

	public String cambiarResponsable(String session) {
		String nombre = "";

		nuevoOrdeninventario.setResponsableOi(session);

		if (nuevaCompra == null) {
			nombre = "N/A";
		} else {
			nombre = nuevoOrdeninventario.getResponsableOi();
		}

		return nombre;
	}

	/****** Metodo de Seleccion de Existencia ****/
	public void seleccionarExistencia() {

		try {

			if (buscarExistencia(selectExistencia.getIdExistencia()) == false) {

				RequestContext context = RequestContext.getCurrentInstance();
				getNuevoMovimientoInventario().setIdExistencia(getSelectExistencia().getIdExistencia());
				mensajeInfo("Se seleccionó la Existencia (" + selectExistencia.getIdExistencia());

				setExistencia(selectExistencia);
				selectExistencia = new Existencia();
				filterExistencia = new ArrayList<Existencia>();
				context.execute("PF('listadoEx').hide();");

			} else {
				mensajeError("La existencia (" + selectExistencia.getIdExistencia() + ") ya ha sido seleccionada");
				selectExistencia = new Existencia();
			}

		} catch (Exception e) {
			mensajeError("No se ha seleccionado ninguna Existencia");
			e.printStackTrace();
		}

	}

	/****** Metodo de Agregar Existencia Temporal ****/

	public void agregarMovimientoTemporal() {

		try {

			RequestContext context = RequestContext.getCurrentInstance();

			double resultado;
			double cantidadMo = nuevoMovimientoInventario.getCantidadMov().doubleValue();
			double cantidadE = existencia.getCantidadE().doubleValue();

			resultado = cantidadE + cantidadMo;

			existencia.setCantidadE(BigDecimal.valueOf(resultado));
			nuevoMovimientoInventario.setSaldoE(getExistencia().getCantidadE());
			nuevoMovimientoInventario.setIncrem(BigDecimal.valueOf(cantidadMo));

			tempExistencias.add(getExistencia());
			nuevoMovimientoInventarios.add(nuevoMovimientoInventario);
			setMovimientoInventarios(nuevoMovimientoInventarios);

			mensajeInfo("Se ha almacenado (" + nuevoMovimientoInventario.getIdExistencia() + ") correctamente.");

			nuevoMovimientoInventario = new Movimientosinventario();
			existencia = new Existencia();
			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
			tempExistencias = new ArrayList<Existencia>();
			selectExistencia = new Existencia();

			context.execute("PF('ingresarMI').hide();");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void limpiarCampos() {

		try {

			// compras
			compra = new Compra();

			nuevaCompra = new Compra();
			proveedorSelect = new ProveedorLab();

			// Movimientos de Inventario
			nuevoMovimientoInventarios.clear();
			tempMovimientoInventarios.clear();
			nuevoMovimientoInventario = new Movimientosinventario();
			movimientoInventario = new Movimientosinventario();
			nuevoMovimientoInventarioAux = new Movimientosinventario();
			nuevoMovimientoInventario.setCantidadMov(new BigDecimal(0));
			nuevoMovimientoInventarioAux.setCantidadMov(new BigDecimal(0));

			// Orden inventario
			ordeninventario = new Ordeninventario();
			nuevoOrdeninventario = new Ordeninventario();

			// Existencias
			existencia = new Existencia();
			existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);

			mensajeInfo("Se ha limpiado todo el formulario");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void actualizarTabla() {

		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			compras = compraI.getListCompras(uni.getCodigoU());
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/****** Reporte Compra ****/

	public void guardarCompra() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {

			System.out.println("tabla Ajuste Inventario" + nuevoMovimientoInventarios.size());
			if (nuevaCompra.getDescrCompra() == ("")) {

				mensajeError("Ingrese la Descripción");

			} else if (nuevaCompra.getFechaCo() == null) {

				mensajeError("Ingrese la Fecha de Compra");

			} else if (nuevaCompra.getDocumentoCo() == "") {

				mensajeError("Ingrese el Documento");

			} else if (nuevaCompra.getMontoCo() == 0) {

				mensajeError("Ingrese el Monto");

			} else if (nuevoOrdeninventario.getFechaingresoOi() == null) {

				mensajeError("Ingrese la feha de Ingreso");
			} else if (nuevoMovimientoInventarios.size() == 0) {

				mensajeError("Debe ingresar al menos 1 Movimiento de Inventario");
			}

			else {

				/** ID COMPRA **/
				obtenerIdCompra();
				nuevaCompra.setProveedor(proveedorSelect);
				compraI.save(nuevaCompra);

				/** ID ORD INV **/
				obtenerIdOrdenInv();
				nuevoOrdeninventario.setCompra(nuevaCompra);
				ordenInventarioI.save(nuevoOrdeninventario);

				/** ID ORD INV - MOV INV **/
				obtenerIdOrd(nuevoOrdeninventario);
				guardarMovimientosInv();

				mensajeInfo("La Compra (" + nuevaCompra.getIdCompra() + ") se ha almacenado exitosamente");
				context.execute("PF('nuevaCompra').hide();");

				// Limpiar Campos y actualizar
				actualizarExistencias();
				proveedorSelect = new ProveedorLab();
				actualizarTabla();
				nuevaCompra = new Compra();
				ordeninventario = new Ordeninventario();
				nuevoOrdeninventario = new Ordeninventario();
				movimientoInventario = new Movimientosinventario();
				movimientoInventarios.clear();

				existencias = movimientoInventarioI.listarExistenciaById(su.UNIDAD_USUARIO_LOGEADO);
				tempExistencias = new ArrayList<Existencia>();
				selectExistencia = new Existencia();

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/****** Construccion de Id Compra ****/

	public void obtenerIdCompra() {
		try {
			/** Obtener fecha **/
			String fecha = obtenerFecha();
			String[] partsFecha = fecha.split("-");
			String anio = partsFecha[0];

			/** Creacion del IdOrdenInv **/
			String codigoAux = compraI.maxIdOrdenI(su.UNIDAD_USUARIO_LOGEADO, fecha);

			String[] partsId = codigoAux.split("-");

			String codigoCortado = partsId[2];

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			String codigoOrden = codigo.toString();
			UnidadLabo uni = new UnidadLabo();

			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

			unidadLabo = ordenInventarioI.obtenerUnidad(su.UNIDAD_USUARIO_LOGEADO);
			nuevaCompra.setUnidad(unidadLabo);

			switch (codigoOrden.length()) {
			case 1:
				nuevaCompra.setIdCompra(uni.getCodigoU() + "-C-" + "0000" + codigoOrden + "-" + anio);
				break;
			case 2:
				nuevaCompra.setIdCompra(uni.getCodigoU() + "-C-" + "000" + codigoOrden + "-" + anio);
				break;
			case 3:
				nuevaCompra.setIdCompra(uni.getCodigoU() + "-C-" + "00" + codigoOrden + "-" + anio);
				break;
			case 4:
				nuevaCompra.setIdCompra(uni.getCodigoU() + "-C-" + "0" + codigoOrden + "-" + anio);
				break;
			case 5:
				nuevaCompra.setIdCompra(uni.getCodigoU() + "-C-" + codigoOrden + "-" + anio);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			mensajeError("Ha ocurrido un error");
			e.printStackTrace();
		}
	}

	/****** Construccion de Id Orden Inv ****/

	public void obtenerIdOrdenInv() {
		try {
			/** Obtener fecha **/
			String fecha = obtenerFecha();
			String[] partsFecha = fecha.split("-");
			String anio = partsFecha[0];

			String codigoAux = ordenInventarioI.maxIdOrdenI(su.UNIDAD_USUARIO_LOGEADO, fecha);

			String[] partsId = codigoAux.split("-");
			String codigoCortado = partsId[2];

			Integer codigo = Integer.parseInt(codigoCortado);
			codigo = codigo + 1;

			String codigoOrden = codigo.toString();

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
				nuevoOrdeninventario.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + "0" + codigoOrden + "-" + anio);
				break;
			case 5:
				nuevoOrdeninventario.setIdOrdeninventario(uni.getCodigoU() + "-OI-" + codigoOrden + "-" + anio);
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

	public void obtenerIdOrd(Ordeninventario ordeninventario) {

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

	/** Guarda MI en la base de datos **/
	public void guardarMovimientosInv() {

		for (Movimientosinventario m : nuevoMovimientoInventarios) {
			try {
				movimientoInventarioI.save(m);
			} catch (Exception e) {

			}

		}
	}

	public String obtenerFecha() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String fecha = sdf.format(calendar.getTime());

		return fecha;

	}

	/****** Lista de Existencias ****/

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

	public void actualizarExistencias() {

		for (Existencia existencia : tempExistencias) {
			try {
				existenciasI.update(existencia);
			} catch (Exception e) {

			}

		}
	}

	public void eliminarTemp() {

		try {
			nuevoMovimientoInventarios.remove(movimientoInventario);
			setMovimientoInventarios(nuevoMovimientoInventarios);
			mensajeInfo("Se ha eliminado el Mov. de Inventario (" + movimientoInventario.getIdExistencia() + ")");
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError("Ha ocurrido un error interno.");
		}

	}

	public void editarTemp() {

		RequestContext context = RequestContext.getCurrentInstance();
		for (Movimientosinventario m : nuevoMovimientoInventarios) {
			if (m.getIdExistencia().equals(movimientoInventario.getIdExistencia())) {

				mensajeInfo("Se ha editado el Mov. de Inventario (" + movimientoInventario.getIdExistencia() + ")");
				context.execute("PF('editarMI').hide();");

				break;
			} else {

			}

		}

	}

	public void cargarMovInv(String id) {

		tempMovimientoInventarios = compraI.listaMovInv(id);

	}

	// seteo de fecha
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public List<Compra> getCompras() {
		return compras;
	}

	public void setCompras(List<Compra> compras) {
		this.compras = compras;
	}

	public ProveedorLab getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorLab proveedor) {
		this.proveedor = proveedor;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public List<Compra> getFilterCompras() {
		return filterCompras;
	}

	public void setFilterCompras(List<Compra> filterCompras) {
		this.filterCompras = filterCompras;
	}

	public Compra getNuevaCompra() {
		return nuevaCompra;
	}

	public void setNuevaCompra(Compra nuevaCompra) {
		this.nuevaCompra = nuevaCompra;
	}

	public Ordeninventario getNuevoOrdeninventario() {
		return nuevoOrdeninventario;
	}

	public void setNuevoOrdeninventario(Ordeninventario nuevoOrdeninventario) {
		this.nuevoOrdeninventario = nuevoOrdeninventario;
	}

	public List<Ordeninventario> getOrdenInventarios() {
		return ordenInventarios;
	}

	public void setOrdenInventarios(List<Ordeninventario> ordenInventarios) {
		this.ordenInventarios = ordenInventarios;
	}

	public Ordeninventario getOrdeninventario() {
		return ordeninventario;
	}

	public void setOrdeninventario(Ordeninventario ordeninventario) {
		this.ordeninventario = ordeninventario;
	}

	public List<ProveedorLab> getProveedores() {
		return proveedores;
	}

	public void setProveedores(List<ProveedorLab> proveedores) {
		this.proveedores = proveedores;
	}

	public ProveedorLab getProveedorSelect() {
		return proveedorSelect;
	}

	public void setProveedorSelect(ProveedorLab proveedorSelect) {
		this.proveedorSelect = proveedorSelect;
	}

	public Existencia getExistencia() {
		return existencia;
	}

	public void setExistencia(Existencia existencia) {
		this.existencia = existencia;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public List<Movimientosinventario> getMovimientoInventarios() {
		return movimientoInventarios;
	}

	public void setMovimientoInventarios(List<Movimientosinventario> movimientoInventarios) {
		this.movimientoInventarios = movimientoInventarios;
	}

	public List<Movimientosinventario> getTempMovimientoInventarios() {
		return tempMovimientoInventarios;
	}

	public void setTempMovimientoInventarios(List<Movimientosinventario> tempMovimientoInventarios) {
		this.tempMovimientoInventarios = tempMovimientoInventarios;
	}

	public List<Movimientosinventario> getFiltroMovimientoInventarios() {
		return filtroMovimientoInventarios;
	}

	public void setFiltroMovimientoInventarios(List<Movimientosinventario> filtroMovimientoInventarios) {
		this.filtroMovimientoInventarios = filtroMovimientoInventarios;
	}

	public List<Movimientosinventario> getNuevoMovimientoInventarios() {
		return nuevoMovimientoInventarios;
	}

	public void setNuevoMovimientoInventarios(List<Movimientosinventario> nuevoMovimientoInventarios) {
		this.nuevoMovimientoInventarios = nuevoMovimientoInventarios;
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

	public Movimientosinventario getNuevoMovimientoInventarioAux() {
		return nuevoMovimientoInventarioAux;
	}

	public void setNuevoMovimientoInventarioAux(Movimientosinventario nuevoMovimientoInventarioAux) {
		this.nuevoMovimientoInventarioAux = nuevoMovimientoInventarioAux;
	}

	public List<Existencia> getFilterExistencia() {
		return filterExistencia;
	}

	public void setFilterExistencia(List<Existencia> filterExistencia) {
		this.filterExistencia = filterExistencia;
	}

	public Existencia getSelectExistencia() {
		return selectExistencia;
	}

	public void setSelectExistencia(Existencia selectExistencia) {
		this.selectExistencia = selectExistencia;
	}

	public List<Existencia> getTempExistencias() {
		return tempExistencias;
	}

	public void setTempExistencias(List<Existencia> tempExistencias) {
		this.tempExistencias = tempExistencias;
	}

	public List<Compra> getNuevaCompras() {
		return nuevaCompras;
	}

	public void setNuevaCompras(List<Compra> nuevaCompras) {
		this.nuevaCompras = nuevaCompras;
	}

	public List<Ordeninventario> getTempOrdenInventarios() {
		return tempOrdenInventarios;
	}

	public void setTempOrdenInventarios(List<Ordeninventario> tempOrdenInventarios) {
		this.tempOrdenInventarios = tempOrdenInventarios;
	}

	public Ordeninventario getTempOrdenInventario() {
		return tempOrdenInventario;
	}

	public void setTempOrdenInventario(Ordeninventario tempOrdenInventario) {
		this.tempOrdenInventario = tempOrdenInventario;
	}

	public List<Ordeninventario> getNuevoOrdenInventarios() {
		return nuevoOrdenInventarios;
	}

	public void setNuevoOrdenInventarios(List<Ordeninventario> nuevoOrdenInventarios) {
		this.nuevoOrdenInventarios = nuevoOrdenInventarios;
	}

}
