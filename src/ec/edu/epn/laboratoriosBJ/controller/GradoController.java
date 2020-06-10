package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import ec.edu.epn.laboratorioBJ.beans.GradoDAO;
import ec.edu.epn.laboratorioBJ.entities.Grado;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "gradoController")
@SessionScoped
public class GradoController implements Serializable {

	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/GradoDAOImplement!ec.edu.epn.laboratorioBJ.beans.GradoDAO")

	private GradoDAO gradoI;

	// variables de la clase
	private Grado grado;
	private List<Grado> Grados = new ArrayList<>();
	private List<Grado> filtroGrado = new ArrayList<>();
	private Grado nuevoGrado;
	private String nombreGrado;
	private Utilidades utilidades;
	// Mpetodo Init
	@PostConstruct
	public void init() {
		try {

			Grados = gradoI.getAll(Grado.class);
			grado = new Grado();
			nuevoGrado = new Grado();
			utilidades = new Utilidades();


		} catch (Exception e) {
		
		}
	}

	/******* Método Guardar *******/

	public void agregarGrado() {
		RequestContext context = RequestContext.getCurrentInstance();
		try {
			if (buscarGrado(nuevoGrado.getNombreGr()) == true) {
				utilidades.mensajeError("El Grado (" + nuevoGrado.getNombreGr() + ") ya existe.");

			} else {

				gradoI.save(nuevoGrado);
				Grados = gradoI.getAll(Grado.class);
				utilidades.mensajeInfo("El Grado (" + nuevoGrado.getNombreGr() + ") se ha almacenado exitosamente");
				nuevoGrado = new Grado();
				context.execute("PF('nuevoGrado').hide();");

			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");

		}

	}

	/******* Método Modificar *******/

	public void modificarGrado() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (grado.getNombreGr().equals(getNombreGrado())) {
				gradoI.update(grado);
				Grados = gradoI.getAll(Grado.class);

				utilidades.mensajeInfo("El Grado (" + grado.getNombreGr() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarGrado').hide();");

			} else if (buscarGrado(grado.getNombreGr()) == false) {
				gradoI.update(grado);
				Grados = gradoI.getAll(Grado.class);

				utilidades.mensajeInfo("El Grado (" + grado.getNombreGr() + ") se ha actualizado exitosamente.");

				context.execute("PF('modificarGrado').hide();");

			} else {
				Grados = gradoI.getAll(Grado.class);
				utilidades.mensajeError("El Grado (" + grado.getNombreGr() + ") ya existe.");

			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un error");
		}
	}

	/******* Método Eliminar *******/

	public void eliminarGrado() {
		try {

			gradoI.delete(grado);
			Grados = gradoI.getAll(Grado.class);

			utilidades.mensajeInfo("El Grado (" + grado.getNombreGr() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla Grado (" + grado.getNombreGr() + ") tiene relación con otra tabla.");

			} else {

				utilidades.mensajeError("Ha ocurrido un error");
			}

		}

	}

	/******* Validación *******/

	private boolean buscarGrado(String valor) {
		try {
			Grados = gradoI.getAll(Grado.class);
		} catch (Exception e) {
		
		}

		boolean resultado = false;
		for (Grado tipo : Grados) {
			if (tipo.getNombreGr().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setNombreGrado(nombre);
	}

	/******* GET and SET *******/

	public List<Grado> getFiltroGrado() {
		return filtroGrado;
	}

	public void setFiltroGrado(List<Grado> filtroGrado) {
		this.filtroGrado = filtroGrado;
	}

	public Grado getGrado() {
		return grado;
	}

	public void setGrado(Grado grado) {
		this.grado = grado;
	}

	public List<Grado> getGrados() {
		return Grados;
	}

	public void setGrados(List<Grado> grados) {
		Grados = grados;
	}

	public Grado getNuevoGrado() {
		return nuevoGrado;
	}

	public void setNuevoGrado(Grado nuevoGrado) {
		this.nuevoGrado = nuevoGrado;
	}

	public String getNombreGrado() {
		return nombreGrado;
	}

	public void setNombreGrado(String nombreGrado) {
		this.nombreGrado = nombreGrado;
	}

}
