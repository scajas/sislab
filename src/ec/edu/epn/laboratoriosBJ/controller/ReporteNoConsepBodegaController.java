package ec.edu.epn.laboratoriosBJ.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "noConsepBodega")
@SessionScoped

public class ReporteNoConsepBodegaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	private StreamedContent streamFile = null;
	private List<String> anios = new ArrayList<String>();

	private String mes;
	private String anio;
	private String formato;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			setMes(new String());
			setAnio(new String());
			setFormato(new String());
			llenarListaAño();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void llenarListaAño() {
		int a1 = 2009; // fecha de inicio por defecto

		String fecha = cambioFecha(new Date());
		String[] partsFecha = fecha.split("-");
		int anio = Integer.valueOf(partsFecha[0]);

		for (int i = a1; i < anio; i++) {
			anios.add(String.valueOf(i));
		}

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

		System.out.println("Este es el mes: " + mes);

		return mes;
	}

	/** Metodo para setear la fecha **/
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
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
			parametros.put("imagen", servletContext.getRealPath("/"));
			parametros.put("subReporte", servletContext.getRealPath("/"));
			parametros.put("nombreUsuario", su.nombre_usuario_logeado);
			parametros.put("mes", Integer.parseInt(mes));
			parametros.put("anio", Integer.parseInt(anio));
			parametros.put("nombreMes", obtenerMes(Integer.parseInt(mes)));

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/noConsepBodega.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "noConsepBodega.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "noConsepBodega.pdf");

		} catch (Exception e) {

			e.printStackTrace();

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

	public List<String> getAnios() {
		return anios;
	}

	public void setAnios(List<String> anios) {
		this.anios = anios;
	}

}
