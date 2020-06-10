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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.laboratorioBJ.beans.CompraDAO;
import ec.edu.epn.laboratorioBJ.entities.Compra;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.laboratorios.utilidades.conexionPostgres;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "reporteComprasController")
@SessionScoped

public class ReporteComprasController implements Serializable {

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

	private Compra compra;
	private List<Compra> compras = new ArrayList<Compra>();
	private Compra filtroCompra;

	private StreamedContent streamFile = null;

	private Date fechaInicio;
	private Date fechaFin;
	private Utilidades utilidades;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			setCompra(new Compra());
			utilidades = new Utilidades();
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

	/****** Generacion de PDF ****/
	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostgres conexionSQL = new conexionPostgres();
			Connection con = conexionSQL.Conexion();
			return con;
		} catch (Exception e) {
		
		}
		return null;
	}

	/****** Reporte Compra ****/
	public void consultarCompra() {
		try {

			compras = compraI.getParametrosCompra(cambioFecha(getFechaInicio()), cambioFecha(getFechaFin()), su.UNIDAD_USUARIO_LOGEADO);

			utilidades.mensajeInfo("Numero de coincidencias encontradas:" + compras.size());

		} catch (Exception e) {
	
		}
	}

	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}
	/****** Generacion de PDF ****/

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
			parametros.put("fechaInicio", getFechaInicio());
			parametros.put("fechaFin", getFechaFin());
			parametros.put("idUnidad", su.UNIDAD_USUARIO_LOGEADO);

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteCompras.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteCompras.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteCompras.pdf");

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

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
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

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
	}

	public Compra getFiltroCompra() {
		return filtroCompra;
	}

	public void setFiltroCompra(Compra filtroCompra) {
		this.filtroCompra = filtroCompra;
	}

	public List<Compra> getCompras() {
		return compras;
	}

	public void setCompras(List<Compra> compras) {
		this.compras = compras;
	}
	
}
