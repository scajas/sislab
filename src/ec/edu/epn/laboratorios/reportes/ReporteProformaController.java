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

import ec.edu.epn.laboratorioBJ.beans.ClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.ProformaDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.entities.Cliente;
import ec.edu.epn.laboratorioBJ.entities.Proforma;
import ec.edu.epn.laboratorioBJ.entities.Tipocliente;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.laboratorios.utilidades.conexionPostgres;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "reporteProformaController")
@SessionScoped

public class ReporteProformaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProformaDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProformaDAO")
	private ProformaDAO proformaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.ClienteDAO")
	private ClienteDAO clienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO")
	private TipoClienteDAO tipoClienteI;

	private Proforma proforma;
	private List<Proforma> proformas = new ArrayList<Proforma>();
	private Proforma proformaSelect;

	private String nombreTipoCliente;
	private String estadoFactura;
	private Date fechaInicio;
	private Date fechaFinal;

	// select cliente
	private Cliente clienteSelect;
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private Cliente cliente;

	private Tipocliente tipoClienteSelect;
	private List<Tipocliente> tipoClientes = new ArrayList<Tipocliente>();
	private Tipocliente tipoCliente;

	private List<Proforma> filtroProforma = new ArrayList<>();
	private StreamedContent streamFile = null;
	private Utilidades utilidades;
	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			proforma = new Proforma();
			tipoClientes = tipoClienteI.getAll(Tipocliente.class);
			utilidades = new Utilidades();

		} catch (Exception e) {
			
		}
	}

	public void buscarProforma() {

		try {

			proformas = proformaI.getparametrosCliente(cambioFecha(getFechaInicio()), cambioFecha(getFechaFinal()),
					nombreTipoCliente, estadoFactura);

			utilidades.mensajeInfo("N�mero de coincidencias encontradas:" + proformas.size());
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

	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostgres conexionSQL = new conexionPostgres();
			Connection con = conexionSQL.Conexion();
			return con;
		} catch (Exception e) {
			
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
			parametros.put("fechaInicio", getFechaInicio());
			parametros.put("fechaFin", getFechaFinal());
			parametros.put("tipoCliente", nombreTipoCliente);
			parametros.put("estadoProforma", estadoFactura);

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteProforma.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteProforma.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "reporteProforma.pdf");

		} catch (Exception e) {
			

		}

	}

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;

	}

	public void cerrarArchivo() throws IOException {
		if (streamFile != null)
			streamFile.getStream().close();

		streamFile = null;
		System.gc();
	}

	public Proforma getProforma() {
		return proforma;
	}

	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Proforma> getFiltroProforma() {
		return filtroProforma;
	}

	public void setFiltroProforma(List<Proforma> filtroProforma) {
		this.filtroProforma = filtroProforma;
	}

	public Cliente getClienteSelect() {
		return clienteSelect;
	}

	public void setClienteSelect(Cliente clienteSelect) {
		this.clienteSelect = clienteSelect;
	}

	public Tipocliente getTipoClienteSelect() {
		return tipoClienteSelect;
	}

	public void setTipoClienteSelect(Tipocliente tipoClienteSelect) {
		this.tipoClienteSelect = tipoClienteSelect;
	}

	public Tipocliente getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(Tipocliente tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public Proforma getProformaSelect() {
		return proformaSelect;
	}

	public void setProformaSelect(Proforma proformaSelect) {
		this.proformaSelect = proformaSelect;
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

	public List<Proforma> getProformas() {
		return proformas;
	}

	public void setProformas(List<Proforma> proformas) {
		this.proformas = proformas;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Tipocliente> getTipoClientes() {
		return tipoClientes;
	}

	public void setTipoClientes(List<Tipocliente> tipoClientes) {
		this.tipoClientes = tipoClientes;
	}

	public String getNombreTipoCliente() {
		return nombreTipoCliente;
	}

	public void setNombreTipoCliente(String nombreTipoCliente) {
		this.nombreTipoCliente = nombreTipoCliente;
	}

	public String getEstadoFactura() {
		return estadoFactura;
	}

	public void setEstadoFactura(String estadoFactura) {
		this.estadoFactura = estadoFactura;
	}

}
