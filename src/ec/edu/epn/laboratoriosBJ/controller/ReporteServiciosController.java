package ec.edu.epn.laboratoriosBJ.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.Tiposervicio;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "reporteServiciosController")
@SessionScoped

public class ReporteServiciosController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoServicioDAO")
	private TipoServicioDAO tipoServicioI;

	private StreamedContent streamFile = null;

	private Servicio servicio;
	private List<Servicio> servicios = new ArrayList<Servicio>();
	private List<Servicio> filtroServicios;

	private String tipoServicio;
	private List<Tiposervicio> tipoServicios = new ArrayList<Tiposervicio>();

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			setTipoServicios(tipoServicioI.getAll(Tiposervicio.class));

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

	public void buscarServicio() {

		System.out.println("Unidad Usuario Log: " + su.UNIDAD_USUARIO_LOGEADO);
		servicios = servicioI.getparametrosTipoServicio(tipoServicio, su.UNIDAD_USUARIO_LOGEADO);

		mensajeInfo("Resultados Obtenidos" + servicios.size());

	}

	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}

	public void generarPDF(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			System.out.println("Tipo de Servicio : " + tipoServicio);

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
			parametros.put("tipoServicio", tipoServicio);
			parametros.put("idUnidad", su.UNIDAD_USUARIO_LOGEADO);

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteServicios.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteServicios.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteServicios.pdf");

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
			conexionPostrges conexionSQL = new conexionPostrges();
			Connection con = conexionSQL.Conexion();
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
	}

	public StreamedContent getStreamFile() {
		return streamFile;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio> servicios) {
		this.servicios = servicios;
	}

	public List<Servicio> getFiltroServicios() {
		return filtroServicios;
	}

	public void setFiltroServicios(List<Servicio> filtroServicios) {
		this.filtroServicios = filtroServicios;
	}

	public String getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public List<Tiposervicio> getTipoServicios() {
		return tipoServicios;
	}

	public void setTipoServicios(List<Tiposervicio> tipoServicios) {
		this.tipoServicios = tipoServicios;
	}

}
