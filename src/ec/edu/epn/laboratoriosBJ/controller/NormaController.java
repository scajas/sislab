package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import ec.edu.epn.laboratorioBJ.beans.NormaDAO;

import ec.edu.epn.laboratorioBJ.entities.Norma;

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
	private List<Norma> filtroNorma = new ArrayList<>();
	private Norma nuevoNorma;
	private String nombreN;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			normas = normaI.getAll(Norma.class);
			norma = new Norma();
			nuevoNorma = new Norma();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void agregarNorma(ActionEvent event) {
		try {
			if (buscarNorma(nuevoNorma.getNombreN()) == true) {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
								"Ha ocurrido un error, la norma (" + nuevoNorma.getNombreN() + ") ya existe."));
				nuevoNorma = new Norma();
			} else {
				normaI.save(nuevoNorma);

				normas = normaI.getAll(Norma.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Norma almacenada exitosamente"));

				nuevoNorma = new Norma();
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}

	}

	public void modificarNorma(ActionEvent event) {
		try {
			if (norma.getNombreN().equals(getnombreN())) {
				normaI.update(norma);
				normas = normaI.getAll(Norma.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarN').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Norma actualizada exitosamente"));

			} else if (buscarNorma(norma.getNombreN()) == false) {
				normaI.update(norma);
				normas = normaI.getAll(Norma.class);

				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('modificarN').hide();");

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Norma actualizada exitosamente"));
			} else {
				normas = normaI.getAll(Norma.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error",
								"La norma (" + norma.getNombreN() + ") ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	public void eliminarNorma(ActionEvent event) {
		try {

			normaI.delete(norma);
			normas = normaI.getAll(Norma.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Norma eliminada exitosamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "",
								"Ha ocurrido un error interno, comuniquese con el personal DGIP"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Ha ocurrido un error"));
			}

		}
	}

	private boolean buscarNorma(String valor) {

		try {
			normas = normaI.getAll(Norma.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	/*
	 * get and set
	 */

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

	public List<Norma> getFiltroNorma() {
		return filtroNorma;
	}

	public void setFiltroNorma(List<Norma> filtroNorma) {
		this.filtroNorma = filtroNorma;
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

}
