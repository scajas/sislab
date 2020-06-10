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

import ec.edu.epn.laboratorioBJ.beans.NormaDAO;
import ec.edu.epn.laboratorioBJ.entities.Norma;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "normaController")
@SessionScoped
public class NormaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/NormaDAOImplement!ec.edu.epn.laboratorioBJ.beans.NormaDAO")

	private NormaDAO normaI;

	// variables de la clase
	private Norma norma;
	private List<Norma> normas = new ArrayList<>();
	private List<Norma> filtrarNorma;
	private Norma nuevoNorma;
	private String nombreN;
	private Utilidades utilidades;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			normas = normaI.getAll(Norma.class);
			norma = new Norma();
			nuevoNorma = new Norma();
			utilidades = new Utilidades();
		} catch (Exception e) {
			
		}
	}

	public void agregarNorma() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (buscarNorma(nuevoNorma.getNombreN()) == true) {
				utilidades.mensajeError("La Norma (" + nuevoNorma.getNombreN() + ") ya existe.");

			} else {

				normaI.save(nuevoNorma);
				normas = normaI.getAll(Norma.class);

				utilidades.mensajeInfo("La Norma (" + nuevoNorma.getNombreN() + ") se ha almacenado exitosamente");

				nuevoNorma = new Norma();

				context.execute("PF('nuevoN').hide();");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		}

	}

	public void modificarNorma() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (norma.getNombreN().equals(getnombreN())) {
				normaI.update(norma);
				normas = normaI.getAll(Norma.class);

				context.execute("PF('modificarN').hide();");

				utilidades.mensajeInfo("La Norma (" + norma.getNombreN() + ") se ha actualizado exitosamente");

			} else if (buscarNorma(norma.getNombreN()) == false) {

				normaI.update(norma);
				normas = normaI.getAll(Norma.class);
				context.execute("PF('modificarN').hide();");

				utilidades.mensajeInfo("La Norma (" + norma.getNombreN() + ") se ha actualizado exitosamente");

			} else {
				normas = normaI.getAll(Norma.class);
				utilidades.mensajeError("La norma (" + norma.getNombreN() + ") ya existe.");
			}

		} catch (Exception e) {
			utilidades.mensajeError("Ha ocurrido un problema");
		}
	}

	public void eliminarNorma() {
		try {

			normaI.delete(norma);
			normas = normaI.getAll(Norma.class);
			utilidades.mensajeInfo("La Norma (" + norma.getNombreN() + ") se ha eliminado exitosamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				utilidades.mensajeError("La tabla Norma (" + norma.getNombreN() + ") tiene relación con otra tabla.");
			} else {
				utilidades.mensajeError("Ha ocurrido un problema");
			}

		}
	}

	private boolean buscarNorma(String valor) {

		try {
			normas = normaI.getAll(Norma.class);
		} catch (Exception e) {
	
			e.printStackTrace();
		}

		boolean resultado = false;
		for (Norma norma : normas) {
			if (norma.getNombreN().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre) {
		setnombreN(nombre);
	}
	
	/****** Getter y Setter ****/

	public Norma getNorma() {
		return norma;
	}

	public void setNorma(Norma norma) {
		this.norma = norma;
	}

	public List<Norma> getNormas() {
		return normas;
	}

	public void setNormas(List<Norma> normas) {
		this.normas = normas;
	}

	public Norma getNuevoNorma() {
		return nuevoNorma;
	}

	public void setNuevoNorma(Norma nuevoNorma) {
		this.nuevoNorma = nuevoNorma;
	}

	public String getnombreN() {
		return nombreN;
	}

	public void setnombreN(String nombreN) {
		this.nombreN = nombreN;
	}

	public List<Norma> getFiltrarNorma() {
		return filtrarNorma;
	}

	public void setFiltrarNorma(List<Norma> filtrarNorma) {
		this.filtrarNorma = filtrarNorma;
	}

}
