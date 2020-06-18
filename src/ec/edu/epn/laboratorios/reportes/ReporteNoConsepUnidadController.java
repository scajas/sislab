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
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.laboratorios.utilidades.conexionPostgres;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "reporteNoConsepUnidad")
@SessionScoped

public class ReporteNoConsepUnidadController implements Serializable {

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

	private List<String> anios = new ArrayList<String>();
	private StreamedContent streamFile = null;

	private String mes;
	private String anio;
	private String formato;
	private Utilidades utilidades;
	// Metodo Init
	@PostConstruct
	public void init() {
		try {
			mes = new String();
			anio = new String();
			formato = new String();
			llenarListaAño();
			utilidades = new Utilidades();

		} catch (Exception e) {
			
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
			parametros.put("imagen", servletContext.getRealPath("/"));
			parametros.put("SubReporte", servletContext.getRealPath("/"));
			parametros.put("nombreUsuario", su.nombre_usuario_logeado);
			parametros.put("mes", Integer.parseInt(mes));
			parametros.put("anio", Integer.parseInt(anio));
			parametros.put("nombreMes", obtenerMes(Integer.parseInt(mes)));

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reportNoConcepUnidad.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteNoConcepUnidad.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteNoConcepUnidad.pdf");

		} catch (Exception e) {

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

	public void llenarListaAño() {
		int a1 = 2009; // fecha de inicio por defecto

		String fecha = cambioFecha(new Date());
		String[] partsFecha = fecha.split("-");
		int anio = Integer.valueOf(partsFecha[0]);

		for (int i = a1; i <= anio; i++) {
			anios.add(String.valueOf(i));
		}

		obtenerMes(12);

	}

	public String obtenerMes(int m) {
		String meses[] = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		String mes = "";
		for (int i = 0; i < meses.length; i++) {
			if (i == (m - 1)) {
				mes = meses[i];
				break;
			}
		}
		

		return mes;
	}


	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;

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

	public List<String> getAnios() {
		return anios;
	}

	public void setAnios(List<String> anios) {
		this.anios = anios;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

}
