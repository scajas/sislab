package ec.edu.epn.laboratoriosBJ.controller;

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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO;
import ec.edu.epn.laboratorioBJ.entities.Caracteristica;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "reporteExistenciasController")
@SessionScoped

public class ReporteExistenciasController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciasDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO")
	private ExistenciasDAO existenciasI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO")
	private TipoProductoDAO tipoProductoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/CaracteristicaDAOImplement!ec.edu.epn.laboratorioBJ.beans.CaracteristicaDAO")
	private CaracteristicaDAO caracteristicaI;

	private Existencia existencia;
	private List<Existencia> existencias = new ArrayList<Existencia>();
	private Existencia filtroExistencia;

	private Tipoproducto tipoProducto;
	private List<Tipoproducto> tipoProductos = new ArrayList<Tipoproducto>();

	private Caracteristica caracteristica;
	private List<Caracteristica> caracteristicas = new ArrayList<Caracteristica>();

	private StreamedContent streamFile = null;

	private String nombreTP;
	private String nombreC;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			existencia = new Existencia();

			tipoProducto = new Tipoproducto();
			tipoProductos = tipoProductoI.getAll(Tipoproducto.class);

			caracteristica = new Caracteristica();
			caracteristicas = caracteristicaI.getAll(Caracteristica.class);

		} catch (Exception e) {
			e.printStackTrace();
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

	public void buscarExistencias() {

		try {

			existencias = existenciasI.existenciasByParametros(nombreTP, nombreC);
			mensajeInfo("Resultados Obtenidos :" + existencias.size());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/****** Metodo para setear la fecha ****/
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
	}

	public StreamedContent getStreamFile() {
		return streamFile;
	}

	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}

	/****** Generacion de PDF ****/
	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostrges conexionSQL = new conexionPostrges();
			Connection con = conexionSQL.Conexion();
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void generarPDF() throws Exception {
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
			parametros.put("tipoproducto", nombreTP);
			parametros.put("caracteristica", nombreC);

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteExistencias.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteExistencias.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteExistencias.pdf");

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/*
	 * public void generarEXCEL(ActionEvent event) throws Exception { try {
	 * 
	 * if (streamFile != null) streamFile.getStream().close();
	 * 
	 * Map<String, Object> parametros = new HashMap<String, Object>();
	 * parametros.put("fechaInicial", proformaSelect.getFecha());
	 * parametros.put("fechaFinal", proformaSelect.getFecha());
	 * parametros.put("tipoCliente", clienteSelect.getTipocliente());
	 * parametros.put("estadoProforma", proformaSelect.getEstadoPo());
	 * 
	 * String direccion =
	 * FacesContext.getCurrentInstance().getExternalContext().getRealPath(
	 * "/reportes/"); if (direccion.toUpperCase().contains("C:") ||
	 * direccion.toUpperCase().contains("D:") ||
	 * direccion.toUpperCase().contains("E:") ||
	 * direccion.toUpperCase().contains("F:")) { direccion = direccion + "\\"; }
	 * else { direccion = direccion + "/"; }
	 * 
	 * String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
	 * .getRealPath("/reportes/reporteProforma.jrxml"); InputStream input = new
	 * FileInputStream(new File(jrxmlFile)); JasperReport jasperReport =
	 * JasperCompileManager.compileReport(input);
	 * parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());
	 * 
	 * JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
	 * parametros);
	 * 
	 * File sourceFile = new File(jrxmlFile); File destFile = new
	 * File(sourceFile.getParent(), "reporteProforma.xlsx");
	 * 
	 * JasperExportManager.exportReportToPdfFile(jasperPrint,
	 * destFile.toString()); InputStream stream = new FileInputStream(destFile);
	 * 
	 * streamFile = new DefaultStreamedContent(stream, "application/xlsx",
	 * "reporteProforma.xlsx");
	 * 
	 * } catch (Exception e) {
	 * FacesContext.getCurrentInstance().addMessage(event.getComponent().
	 * getClientId(), new FacesMessage(FacesMessage.SEVERITY_FATAL, "",
	 * "ERROR"));
	 * 
	 * }
	 * 
	 * }
	 */

	public void cerrarArchivo() throws IOException {
		if (streamFile != null)
			streamFile.getStream().close();

		streamFile = null;
		System.gc();
	}

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;

		System.out.println("PASA POR AQUI " + streamFile);
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

	public Existencia getFiltroExistencia() {
		return filtroExistencia;
	}

	public void setFiltroExistencia(Existencia filtroExistencia) {
		this.filtroExistencia = filtroExistencia;
	}

	public Tipoproducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(Tipoproducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public List<Tipoproducto> getTipoProductos() {
		return tipoProductos;
	}

	public void setTipoProductos(List<Tipoproducto> tipoProductos) {
		this.tipoProductos = tipoProductos;
	}

	public Caracteristica getCaracteristica() {
		return caracteristica;
	}

	public void setCaracteristica(Caracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}

	public List<Caracteristica> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<Caracteristica> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public String getNombreC() {
		return nombreC;
	}

	public void setNombreC(String nombreC) {
		this.nombreC = nombreC;
	}

}
