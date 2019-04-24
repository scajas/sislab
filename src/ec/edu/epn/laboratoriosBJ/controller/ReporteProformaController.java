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
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
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
import ec.edu.epn.seguridad.VO.SesionUsuario;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;

@ManagedBean(name = "reporteProformaController")
@SessionScoped
@FacesValidator("primeDateRangeValidator")
public class ReporteProformaController implements Serializable, Validator {

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
	private List<Proforma> listaProforma = new ArrayList<Proforma>();
	private Proforma proformaSelect;

	private Integer nombreCli;
	private Date fechaInicio;
	private Date fechaFinal;

	// select cliente

	private Cliente clienteSelect;
	private List<Cliente> listaCliente = new ArrayList<Cliente>();
	private Cliente cliente;

	private Tipocliente tipoClienteSelect;
	private List<Tipocliente> listaTipoCliente = new ArrayList<Tipocliente>();
	private Tipocliente tipoCliente;

	private List<Proforma> filtroProforma = new ArrayList<>();
	private StreamedContent streamFile = null;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			proforma = new Proforma();
			listaProforma = proformaI.getAll(Proforma.class);

			listaCliente = clienteI.getAll(Cliente.class);
			cliente = new Cliente();

			listaTipoCliente = tipoClienteI.getAll(Tipocliente.class);
			cliente = new Cliente();

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

	public void buscarCliente() {

		try {

			listaProforma = proformaI.getparametrosCliente(cambioFecha(getFechaInicio()), cambioFecha(getFechaFinal()),
					getNombreCli(), proforma.getEstadoPo());

			System.out.print("Numero de servicios obtenidos: " + listaProforma.size());

			mensajeInfo("Número de coincidencias encontradas:" + listaProforma.size());
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

	public void generarPDF(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("fechaInicio", cambioFecha(getFechaInicio()));
			parametros.put("fechaFinal", cambioFecha(getFechaFinal()));
			parametros.put("tipoCliente", getNombreCli());
			parametros.put("estadoProforma", proforma.getEstadoPo());		
			
			
			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");
			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteProforma.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "proforma.pdf");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/pdf", "proforma.pdf");

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "ERROR"));

		}

	}

/*	public void generarEXCEL(ActionEvent event) throws Exception {
		try {

			if (streamFile != null)
				streamFile.getStream().close();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("fechaInicial", proformaSelect.getFecha());
			parametros.put("fechaFinal", proformaSelect.getFecha());
			parametros.put("tipoCliente", clienteSelect.getTipocliente());
			parametros.put("estadoProforma", proformaSelect.getEstadoPo());

			String direccion = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/");
			if (direccion.toUpperCase().contains("C:") || direccion.toUpperCase().contains("D:")
					|| direccion.toUpperCase().contains("E:") || direccion.toUpperCase().contains("F:")) {
				direccion = direccion + "\\";
			} else {
				direccion = direccion + "/";
			}

			String jrxmlFile = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/reportes/reporteProforma.jrxml");
			InputStream input = new FileInputStream(new File(jrxmlFile));
			JasperReport jasperReport = JasperCompileManager.compileReport(input);
			parametros.put(JRParameter.REPORT_CONNECTION, coneccionSQL());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros);

			File sourceFile = new File(jrxmlFile);
			File destFile = new File(sourceFile.getParent(), "reporteProforma.xlsx");

			JasperExportManager.exportReportToPdfFile(jasperPrint, destFile.toString());
			InputStream stream = new FileInputStream(destFile);

			streamFile = new DefaultStreamedContent(stream, "application/xlsx", "reporteProforma.xlsx");

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "ERROR"));

		}

	}*/

	public void cerrarArchivo() throws IOException {
		if (streamFile != null)
			streamFile.getStream().close();

		streamFile = null;
		System.gc();
	}

	private Connection coneccionSQL() throws IOException {
		try {
			conexionPostrges conexionSQL = new conexionPostrges();
			Connection con = conexionSQL.getConnection();
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setStreamFile(StreamedContent streamFile) {
		this.streamFile = streamFile;
		
		System.out.println("PASA POR AQUI "+ streamFile);
	}

	public Proforma getProforma() {
		return proforma;
	}

	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}

	public List<Proforma> getListaProforma() {
		return listaProforma;
	}

	public void setListaProforma(List<Proforma> listaProforma) {
		this.listaProforma = listaProforma;
	}

	public List<Cliente> getListaCliente() {
		return listaCliente;
	}

	public void setListaCliente(List<Cliente> listaCliente) {
		this.listaCliente = listaCliente;
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

	public List<Tipocliente> getListaTipoCliente() {
		return listaTipoCliente;
	}

	public void setListaTipoCliente(List<Tipocliente> listaTipoCliente) {
		this.listaTipoCliente = listaTipoCliente;
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

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
		// TODO Auto-generated method stub

	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public Integer getNombreCli() {
		return nombreCli;
	}

	public void setNombreCli(Integer nombreCli) {
		this.nombreCli = nombreCli;
	}
}
