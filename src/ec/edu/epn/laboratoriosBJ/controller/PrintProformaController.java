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

	private List<DetalleProforma> detalleProformasTemp = new ArrayList<DetalleProforma>();

	/** METODO Init **/
	@PostConstruct
	public void init() {
		try {
			String id = obtenerVariable();
			proforma = proformaI.buscarProformaById(id);
			detalleProformasTemp = proformaI.listarDetalleProByIdPro(id);
			System.out.println("Lista de detallePro: " + detalleProformasTemp.size());

			// /** Proforma **/

			Long iduser = su.id_usuario_log;
			// iduser.intValue());
			setUnidad((UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO));

			laboratorio = proformaI.obtenerLaboratorioByUsr(iduser.intValue(), su.UNIDAD_USUARIO_LOGEADO);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* Metodos de limpieza de formularios */

	public void limpiarProforma() {
		try {
			String id = obtenerVariable();
			proforma = proformaI.buscarProformaById(id);
			detalleProformasTemp = proformaI.listarDetalleProByIdPro(id);
			System.out.println("Lista de detallePro: " + detalleProformasTemp.size());

			// /** Proforma **/

			Long iduser = su.id_usuario_log;
			// iduser.intValue());
			setUnidad((UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO));

			laboratorio = proformaI.obtenerLaboratorioByUsr(iduser.intValue(), su.UNIDAD_USUARIO_LOGEADO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			FacesContext contex = FacesContext.getCurrentInstance();
			// contex.getExternalContext().getSessionMap().put("id",
			// proforma.getIdProforma());
			contex.getExternalContext().redirect("/SisLab/pages/proforma.jsf");

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
			System.out.println("Me voy al carajo, no funciona esta redireccion");
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

}
