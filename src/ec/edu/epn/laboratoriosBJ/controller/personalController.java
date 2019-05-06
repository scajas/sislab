package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.PersonalLab;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "personalController")
@SessionScoped
@FacesValidator("primeDateRangeValidator")

public class personalController implements Serializable, Validator {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/PersonalLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.PersonalLabDAO")
	private PersonalLabDAO personalI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;
	/****************************************************************************/

	// Variables de la clase
	private List<PersonalLab> listaPersonal = new ArrayList<>();
	private PersonalLab nuevoPersonal;
	private String nombreTP;
	private String idUnidad;
	private List<PersonalLab> filtrarPersonal;
	private PersonalLab personalSelect;
	private PersonalLab personal;

	private UnidadLabo unidadSelect;
	private List<UnidadLabo> listaUnidad = new ArrayList<UnidadLabo>();
	private UnidadLabo unidad;

	// Método init
	@PostConstruct
	public void init() {
		try {
			setListaPersonal(personalI.getAll(PersonalLab.class));
			setNuevoPersonal(new PersonalLab());
			setPersonal(new PersonalLab());

			listaUnidad = unidadI.getAll(UnidadLabo.class);
			unidad = new UnidadLabo();

		} catch (Exception e) {

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

	/****** Agregar Estado Producto ****/

	public void agregarPersonal() {

		try {
			if (buscarPersonal(nuevoPersonal.getIdPersonal()) == true) {

				mensajeError("Ha ocurrido un error, El Personal ( " + nuevoPersonal.getIdPersonal() + " ) ya existe.");

				nuevoPersonal = new PersonalLab();

			} else if (nuevoPersonal.getIdUnidad() == 25){
				mensajeError("Ha ocurrido un error, la unidad (na), no puede cumplir el regisro debido a que no existe");
			}
			else {

				/** Creacion del IdPersonal **/
				
				System.out.println("Este es el id que trae: " + nuevoPersonal.getIdUnidad());

				String codigoAux = personalI.maxIdPersonal(nuevoPersonal.getIdUnidad());
				System.out.println("Este es el id que trae: " + codigoAux);

				String codigoCortado = codigoAux.substring(5, 9);
				System.out.println("Este es el id convertido en numero: " + codigoCortado);

				Integer codigo = Integer.parseInt(codigoCortado);
				codigo = codigo + 1;
				System.out.println("Este es el id oficial: " + codigo);

				String codigoPersonal = codigo.toString();
				UnidadLabo uni = personalI.buscarUnidadById(nuevoPersonal.getIdUnidad());

				//uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);

				switch (codigoPersonal.length()) {
				case 1:
					nuevoPersonal.setIdPersonal(uni.getCodigoU() + "-PE" + "000" + codigoPersonal);
					break;
				case 2:
					nuevoPersonal.setIdPersonal(uni.getCodigoU() + "-PE" + "00" + codigoPersonal);
					break;
				case 3:
					nuevoPersonal.setIdPersonal(uni.getCodigoU() + "-PE" + "0" + codigoPersonal);
					break;
				case 4:
					nuevoPersonal.setIdPersonal(uni.getCodigoU() + "-PE" + codigoPersonal); // DC-E-2217
					break;

				default:
					break;
				}

				System.out.println("Este es el nuevo id: " + nuevoPersonal.getIdPersonal());

				/** GUARDAR **/

				personalI.save(nuevoPersonal);
				listaPersonal = personalI.listarPersonalById(su.UNIDAD_USUARIO_LOGEADO);

				mensajeInfo("El Personal (" + nuevoPersonal.getIdPersonal() + ") se ha almacenado exitosamente");

				nuevoPersonal = new PersonalLab();

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");

			e.printStackTrace();

		}
	}

	/****** Modificar Estado Producto ****/

	public void modificarPersonal() {

		try {
			if (personal.getNombresPe().equals(getNombreTP())) {
				personalI.update(personal);
				listaPersonal = personalI.getAll(PersonalLab.class);

				mensajeInfo("El Personal (" + personal.getNombresPe() + ") se ha actualizado exitosamente.");

			} else if (buscarPersonal(personal.getNombresPe()) == false) {
				personalI.update(personal);
				listaPersonal = personalI.getAll(PersonalLab.class);

				mensajeInfo("El Personal (" + personal.getNombresPe() + ") se ha actualizado exitosamente.");

			} else {
				listaPersonal = personalI.getAll(PersonalLab.class);
				mensajeError("El Personal (" + personal.getNombresPe() + ") ya existe.");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

		}
	}

	/****** Eliminar Estado Producto ****/

	public void eliminarPersonal() {

		try {

			personalI.delete(personal);
			listaPersonal = personalI.getAll(PersonalLab.class);

			mensajeInfo("El Personal (" + personal.getNombresPe() + ") se ha eliminado correctamente.");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError("La tabla Personal tiene relación con otra tabla.");

			} else {

				mensajeError("Ha ocurrido un problema.");
			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarPersonal(String valor) {

		try {
			listaPersonal = personalI.listarPersonalById(su.UNIDAD_USUARIO_LOGEADO);
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (PersonalLab personal : listaPersonal) {
			if (personal.getNombresPe().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreTP(nombre);

	}

	public void pasarId(String idUnidad) {
		setIdUnidad(idUnidad);
	}

	/****** Getter y Setter de Personal ****/

	public List<PersonalLab> getListaPersonal() {
		return listaPersonal;
	}

	public void setListaPersonal(List<PersonalLab> listaPersonal) {
		this.listaPersonal = listaPersonal;
	}

	public PersonalLab getNuevoPersonal() {
		return nuevoPersonal;
	}

	public void setNuevoPersonal(PersonalLab nuevoPersonal) {
		this.nuevoPersonal = nuevoPersonal;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public List<PersonalLab> getFiltrarPersonal() {
		return filtrarPersonal;
	}

	public void setFiltrarPersonal(List<PersonalLab> filtrarPersonal) {
		this.filtrarPersonal = filtrarPersonal;
	}

	public PersonalLab getPersonalSelect() {
		return personalSelect;
	}

	public void setPersonalSelect(PersonalLab personalSelect) {
		this.personalSelect = personalSelect;
	}

	public PersonalLab getPersonal() {
		return personal;
	}

	public void setPersonal(PersonalLab personal) {
		this.personal = personal;
	}

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
		// TODO Auto-generated method stub

	}

	public UnidadLabo getUnidadSelect() {
		return unidadSelect;
	}

	public void setUnidadSelect(UnidadLabo unidadSelect) {
		this.unidadSelect = unidadSelect;
	}

	public List<UnidadLabo> getListaUnidad() {
		return listaUnidad;
	}

	public void setListaUnidad(List<UnidadLabo> listaUnidad) {
		this.listaUnidad = listaUnidad;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

	public String getIdUnidad() {
		return idUnidad;
	}

	public void setIdUnidad(String idUnidad) {
		this.idUnidad = idUnidad;
	}

}