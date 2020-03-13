package ec.edu.epn.laboratoriosBJ.controller;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.RemoveException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import ec.edu.epn.laboratorioBJ.beans.ClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.DetalleProDAO;
import ec.edu.epn.laboratorioBJ.beans.ExistenciasDAO;
import ec.edu.epn.laboratorioBJ.beans.ProformaDAO;
import ec.edu.epn.laboratorioBJ.beans.ServicioDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO;
import ec.edu.epn.laboratorioBJ.entities.Cliente;
import ec.edu.epn.laboratorioBJ.entities.DetalleProforma;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.Metodo;
import ec.edu.epn.laboratorioBJ.entities.Movimientosinventario;
import ec.edu.epn.laboratorioBJ.entities.Proforma;
import ec.edu.epn.laboratorioBJ.entities.Servicio;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;
import javax.faces.application.FacesMessage;

@ManagedBean(name = "printProformaController")
@SessionScoped

public class PrintProformaController implements Serializable {

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

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadMedidaDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadMedidaDAO")
	private UnidadMedidaDAO unidadMedidaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProformaDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProformaDAO")
	private ProformaDAO proformaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/DetalleProDAOImplement!ec.edu.epn.laboratorioBJ.beans.DetalleProDAO")
	private DetalleProDAO detalleProI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.ClienteDAO")
	private ClienteDAO clienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoClienteDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoClienteDAO")
	private TipoClienteDAO tipoClienteI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ServicioDAOImplement!ec.edu.epn.laboratorioBJ.beans.ServicioDAO")
	private ServicioDAO servicioI;

	/****************************************************************************/
	/** VARIABLES **/

	/* Proforma */
	private Proforma proforma;

	/* Unidad */
	private UnidadLabo unidad = new UnidadLabo();

	/* Laboratorio */
	private LaboratorioLab laboratorio = new LaboratorioLab();
	private LaboratorioLab logoLab;

	private List<DetalleProforma> detalleProformasTemp = new ArrayList<DetalleProforma>();
	private List<DetalleProforma> detalleProformas = new ArrayList<DetalleProforma>();
	private DetalleProforma servicioDP;

	private String path = new String();

	private LaboratorioLab titulo;
	private String nota1;
	private String nota2;
	private String nota3;
	private String nota4;
	private String notaTitulo;

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {
			String id = obtenerVariable();
			proforma = proformaI.buscarProformaById(id);
			detalleProformasTemp = proformaI.listarDetalleProByIdPro(id);
			detalleProformas = detalleProformasTemp;

			servicioDP = proformaI.getServicio(id);

			notaTitulo = new String();
			// /** Proforma **/

			Long iduser = su.id_usuario_log;
			// iduser.intValue());
			setUnidad((UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO));

			laboratorio = proformaI.obtenerLaboratorioByUsr(iduser.intValue(), su.UNIDAD_USUARIO_LOGEADO);

			logoLab = new LaboratorioLab();

			titulo = new LaboratorioLab();

			limpiarProforma();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* Metodos de Comparar Unidades x Lab - Logo */

	public boolean compararListas() {
		boolean resultado = false;
		for (DetalleProforma dp : detalleProformasTemp) {
			for (DetalleProforma dpt : detalleProformas) {

				if (dpt.getServicio().getLaboratorio().getIdLaboratorio() == dp.getServicio().getLaboratorio()
						.getIdLaboratorio()) {
					setLogoLab(dpt.getServicio().getLaboratorio());
					resultado = true;
				} else {
					// resultado = false;
					setLogoLab(dpt.getServicio().getLaboratorio());

					// break;
					return false;
				}
			}
		}
		return resultado;
	}

	public void limpiarProforma() {
		try {
			String id = obtenerVariable();
			proforma = proformaI.buscarProformaById(id);
			detalleProformasTemp = proformaI.listarDetalleProByIdPro(id);
			detalleProformas = detalleProformasTemp;
			
			servicioDP = proformaI.getServicio(id);

			System.out.println("Servicio de la lista:" + servicioDP.getServicio().getIdServicio());
			System.out.println("Lab obtenido:" + servicioDP.getServicio().getLaboratorio().getNombreL());

			if (unidad.getIdUnidad() == 1) {

				if (compararListas()) {

					setPath(logoLab.getPath());

					setNotaTitulo("");
					setNota1(getServicioDP().getServicio().getLaboratorio().getNota1());
					setNota2(getServicioDP().getServicio().getLaboratorio().getNota2());
					setNota3(getServicioDP().getServicio().getLaboratorio().getNota3());
					setNota4("");

				} else {
					setPath("DECAB.png");

					setNotaTitulo("F-PG-7.1-01-01");

					setNota1(laboratorio.getUnidad().getNota1());
					setNota2(laboratorio.getUnidad().getNota2());
					setNota3(laboratorio.getUnidad().getNota3());
					setNota4("");

				}
			} else {

				setPath("buho.jpg");
			}

			// /** Proforma **/

			Long iduser = su.id_usuario_log;
			// iduser.intValue());
			setUnidad((UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO));

			laboratorio = proformaI.obtenerLaboratorioByUsr(iduser.intValue(), su.UNIDAD_USUARIO_LOGEADO);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String cambiarFormatoDouble(double numero) {
		DecimalFormat formato = new DecimalFormat("#.00");
		return formato.format(numero);
	}

	public void limpiarDetalleProforma() {

	}

	public void holaMundo() {
		System.err.println("esta funcando");
	}

	public void regresarPanelPro() {

		try {

			System.out.println("ingresa al metodo regresar");
			FacesContext contex = FacesContext.getCurrentInstance();
			// contex.getExternalContext().getSessionMap().put("id",
			// proforma.getIdProforma());

			contex.getExternalContext().redirect("/SisLab/pages/proforma.jsf");

			limpiarProforma();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String obtenerVariable() {
		try {

			FacesContext contex = FacesContext.getCurrentInstance();
			String id = contex.getExternalContext().getSessionMap().get("id").toString();

			System.out.println("Esto es el id trae: " + id);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Me voy al carajo, no funciona esta
			// redireccion");
			return null;
		}
	}

	/****** Manejo de fechas ****/
	public String cambioFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String fechaFinal = format.format(fecha);

		return fechaFinal;
	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext contextM = FacesContext.getCurrentInstance();
		contextM.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {
		FacesContext contextM = FacesContext.getCurrentInstance();

		contextM.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));

	}

	/****** Getter y Setter de Estado Producto ****/

	public Proforma getProforma() {
		return proforma;
	}

	public void setProforma(Proforma proforma) {
		this.proforma = proforma;
	}

	public List<DetalleProforma> getDetalleProformasTemp() {
		return detalleProformasTemp;
	}

	public void setDetalleProformasTemp(List<DetalleProforma> detalleProformasTemp) {
		this.detalleProformasTemp = detalleProformasTemp;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

	public LaboratorioLab getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(LaboratorioLab laboratorio) {
		this.laboratorio = laboratorio;
	}

	public LaboratorioLab getLogoLab() {
		return logoLab;
	}

	public void setLogoLab(LaboratorioLab logoLab) {
		this.logoLab = logoLab;
	}

	public List<DetalleProforma> getDetalleProformas() {
		return detalleProformas;
	}

	public void setDetalleProformas(List<DetalleProforma> detalleProformas) {
		this.detalleProformas = detalleProformas;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public LaboratorioLab getTitulo() {
		return titulo;
	}

	public void setTitulo(LaboratorioLab titulo) {
		this.titulo = titulo;
	}

	public String getNota1() {
		return nota1;
	}

	public void setNota1(String nota1) {
		this.nota1 = nota1;
	}

	public String getNota2() {
		return nota2;
	}

	public void setNota2(String nota2) {
		this.nota2 = nota2;
	}

	public String getNota3() {
		return nota3;
	}

	public void setNota3(String nota3) {
		this.nota3 = nota3;
	}

	public String getNota4() {
		return nota4;
	}

	public void setNota4(String nota4) {
		this.nota4 = nota4;
	}

	public String getNotaTitulo() {
		return notaTitulo;
	}

	public void setNotaTitulo(String notaTitulo) {
		this.notaTitulo = notaTitulo;
	}

	public DetalleProforma getServicioDP() {
		return servicioDP;
	}

	public void setServicioDP(DetalleProforma servicioDP) {
		this.servicioDP = servicioDP;
	}

}
