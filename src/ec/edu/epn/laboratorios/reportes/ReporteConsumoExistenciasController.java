package ec.edu.epn.laboratorios.reportes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO;
import ec.edu.epn.laboratorioBJ.beans.ConcentracionDAO;
import ec.edu.epn.laboratorioBJ.beans.EstadoProductoDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.GradoDAO;
import ec.edu.epn.laboratorioBJ.beans.HidratacionDAO;
import ec.edu.epn.laboratorioBJ.beans.LaboratoryDAO;
import ec.edu.epn.laboratorioBJ.beans.MovimientoInventarioLabDAO;
import ec.edu.epn.laboratorioBJ.beans.PosgiroDAO;
import ec.edu.epn.laboratorioBJ.beans.PresentacionDAO;
import ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO;
import ec.edu.epn.laboratorioBJ.beans.PurezaDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Hidratacion;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.ProductoLab;
import ec.edu.epn.laboratorioBJ.entities.Pureza;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.laboratorios.utilidades.conexionPostgres;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import javax.faces.application.FacesMessage;

@ManagedBean(name = "reporteConsumoExistenciasController")
@SessionScoped

public class ReporteConsumoExistenciasController implements Serializable {

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

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/MovimientoInventarioLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.MovimientoInventarioLabDAO")
	private MovimientoInventarioLabDAO movimientoInventarioI;

	/****************************************************************************/
	/** VARIABLES **/

	private String nombreEx;

	private List<Existencia> existencias = new ArrayList<>();
	private List<Existencia> filtrarExistencias = new ArrayList<>();// filtro
	private List<ProductoLab> productos = new ArrayList<>();// filtro
	private List<Movimientosinventario> movimientosinventarios = new ArrayList<>();
	private Existencia nuevoExistencia;
	private Existencia existencia;// eliminar y editar
	private Movimientosinventario movimientosInventario;
	private String nombreTP;
	private String nombrePro;

	private Date fechaInicio;
	private Date fechaFinal;

	private Utilidades utilidades;
	// reporte
	private StreamedContent streamFile = null;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {

			movimientosInventario = new Movimientosinventario();
			utilidades = new Utilidades();

		} catch (Exception e) {

		}

	}

	/** Cambiar dato **/
	public Existencia cambiarDatosExistencia(String id){
		Existencia existenciatemp = movimientoInventarioI.buscarExistenciaById(id);
		return existenciatemp;
	}
	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}
	/** Generacion de PDF **/

	public void generarPDF(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();

			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");
			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("CONTEXT", servletContext.getRealPath("/"));
			parametros.put("fechaInicio", fechaInicio);
			parametros.put("fechaFinal", fechaFinal);

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteConsumoExistencia.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteConsumoExistencia.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteConsumoExistencia.pdf");

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "ERROR"));

		}

	}

	public void cerrarArchivo() throws IOException {
		if (streamFile != null)
			streamFile.getStream().close();

		streamFile = null;
		System.gc();
	}

	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostgres conexionSQL = new conexionPostgres();
			Connection con = conexionSQL.Conexion();
			return con;
		} catch (Exception e) {
			
		}
		return null;
	}

	/****** Busqueda de Producto ****/
	public void busquedaGloblal() {

		setProductos(existenciasI.filtrarLista(getNombrePro()));
	}

	/****** Busqueda de ID Existencia ****/

	public void listaMovimientoInventario(String idExistencia) {
		try {

			setMovimientosinventarios(existenciasI.listarMovimientoById(idExistencia));
	
		} catch (Exception e) {
			
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

	public String movimientoInv(String idExistencia) {

		String nombre = "";

		Movimientosinventario movimientosinventario = new Movimientosinventario();

		movimientosinventario = existenciasI.movimientoInvenBynombred(idExistencia);

		if (movimientosinventario == null) {
			nombre = "N/A";
		} else {
			nombre = cambioFecha(movimientosinventario.getFechaMi());
		}
		return nombre;
	}

	public String tipoOrdenI(String idExistencia) {
		String nombre = "";

		Movimientosinventario movimientosinventario = new Movimientosinventario();

		movimientosinventario = existenciasI.movimientoInvenBynombred(idExistencia);

		if (movimientosinventario == null) {
			nombre = "N/A";
		} else {
			nombre = movimientosinventario.getOrdeninventario().getTipordeninv().getNombreToi();
		}
		return nombre;
	}

	public String cantidadMovI(String idExistencia) {
		String nombre = "";

		Movimientosinventario movimientosinventario = new Movimientosinventario();

		movimientosinventario = existenciasI.movimientoInvenBynombred(idExistencia);

		if (movimientosinventario == null) {
			nombre = "N/A";
		} else {
			nombre = movimientosinventario.getCantidadMov().toString();
		}
		return nombre;
	}

	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
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

	// METODO PARA BUSQUEDA
	public void consultarMovimientos() {
		try {

			movimientosinventarios = existenciasI.getParametroFecha(cambioFecha(getFechaInicio()), cambioFecha(getFechaFinal()));

			utilidades.mensajeInfo("Resultados Obtenidos:" + movimientosinventarios.size());

		} catch (Exception e) {
			
		}
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
	
	public List<Movimientosinventario> getMovimientosinventarios() {
		return movimientosinventarios;
	}
	
	public void setMovimientosinventarios(List<Movimientosinventario> movimientosinventarios) {
		this.movimientosinventarios = movimientosinventarios;
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
	
	public StreamedContent getStreamFile() {
		return streamFile;
	}
	
	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
	}
	
	public Movimientosinventario getMovimientosInventario() {
		return movimientosInventario;
	}
	
	public void setMovimientosInventario(Movimientosinventario movimientosInventario) {
		this.movimientosInventario = movimientosInventario;
	}

}
