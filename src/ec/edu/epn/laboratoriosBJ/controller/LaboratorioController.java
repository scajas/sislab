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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.primefaces.model.UploadedFile;

import ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO;
import ec.edu.epn.laboratorioBJ.beans.UnidadDAO;
import ec.edu.epn.laboratorioBJ.entities.LaboratorioLab;
import ec.edu.epn.laboratorioBJ.entities.UnidadLabo;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "laboratorioController")
@SessionScoped

public class LaboratorioController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;

	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/LaboratorioDAOImplement!ec.edu.epn.laboratorioBJ.beans.LaboratorioDAO")
	private LaboratorioDAO laboratorioI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/UnidadDAOImplement!ec.edu.epn.laboratorioBJ.beans.UnidadDAO")
	private UnidadDAO unidadI;

	/****************************************************************************/
	/** VARIABLES **/
	private List<LaboratorioLab> listaLaboratorioLab = new ArrayList<>();
	private LaboratorioLab nuevoLaboratorioLab;
	private LaboratorioLab LaboratorioLab;
	private String nombreL;
	private UnidadLabo unidadSelect;
	private UnidadLabo unidadSelectEmpty;
	private List<UnidadLabo> unidades = new ArrayList<UnidadLabo>();
	private UnidadLabo unidad;

	private List<LaboratorioLab> filtrarLaboratorios;
	private List<UnidadLabo> filtrarUnidades;

	private UploadedFile file;
	private FileUploadEvent fileUploadEvent;
	private UploadedFile uploadedFile;

	/** M…TODOS **/
	@PostConstruct
	public void init() {
		try {

			updateTable();
			nuevoLaboratorioLab = new LaboratorioLab();

			unidades = unidadI.getAll(UnidadLabo.class);
			unidad = new UnidadLabo();

			unidadSelect = new UnidadLabo();
			unidadSelectEmpty = new UnidadLabo();

		} catch (Exception e) {

		}

	}

	public void updateTable() {

		try {
			UnidadLabo uni = new UnidadLabo();
			uni = (UnidadLabo) unidadI.getById(UnidadLabo.class, su.UNIDAD_USUARIO_LOGEADO);
			listaLaboratorioLab = laboratorioI.getListLabById(uni.getCodigoU());
		} catch (Exception e) {

		}

	}

	/****** Mensajes Personalizados ****/
	public void mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "°ERROR!", mensaje));
	}

	public void mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACI”N", mensaje));

	}

	/****** Nuevo - Nuevo Lab ****/

	public void guardarLab() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (disabledButton()) {

				if (buscarLaboratorioLab(nuevoLaboratorioLab.getNombreL()) == true) {

					mensajeError("El Laboratorio (" + nuevoLaboratorioLab.getNombreL() + ") ya existe.");

				} else if (nuevoLaboratorioLab.getPath() == null) {

					mensajeError("Debe seleccionar una Imagen.");

				} else {
					cargarImg(getFileUploadEvent());
					nuevoLaboratorioLab.setUnidad(unidadSelect);

					laboratorioI.save(nuevoLaboratorioLab);
					mensajeInfo(
							"El Laboratorio (" + nuevoLaboratorioLab.getNombreL() + ") se ha almacenado exitosamente");

					updateTable();
					nuevoLaboratorioLab = new LaboratorioLab();
					unidadSelect = new UnidadLabo();
					context.execute("PF('nuevoLaboratorio').hide();");
				}

			} else {

				mensajeError("Debe llenar los campos obligatorios");
			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un error");
			e.printStackTrace();

		}

	}

	/****** Nuevo - Disable Button ****/

	public boolean disabledButton() {

		boolean resultado = false;

		if (unidadSelect.getNombreU() == null) {

			mensajeError("Debe Seleccionar la Unidad.");
			resultado = false;
		}

		else {
			resultado = true;
		}

		if (nuevoLaboratorioLab.getNombreL() == null || nuevoLaboratorioLab.getNombreL().equals("")) {

			mensajeError("Debe Ingresar el Nombre del Laboratorio.");
			resultado = false;

		} else {
			resultado = true;
		}
		if (buscarLaboratorioLab(nuevoLaboratorioLab.getNombreL()) == true) {
			mensajeError("El laboratorio (" + nuevoLaboratorioLab.getNombreL() + ") ya existe.");
			resultado = false;

		} else {
			resultado = true;
		}
		if (nuevoLaboratorioLab.getCentrocostoL() == null || nuevoLaboratorioLab.getCentrocostoL().equals("")) {

			mensajeError("Debe Ingresar el Centro de Costo.");
			resultado = false;

		} else {
			resultado = true;
		}

		return resultado;

	}

	/****** Nuevo - Cargar Logo Lab ****/

	public void cargarImg(FileUploadEvent event) throws IOException {

		try {

			String original = "¿¡¬√ƒ≈∆«»… ÀÃÕŒœ–—“”‘’÷ÿŸ⁄€‹›ﬂ‡·‚„‰ÂÊÁËÈÍÎÏÌÓÔÒÚÛÙıˆ¯˘˙˚¸˝ˇ";

			String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";
			String nuevoNombre = nuevoLaboratorioLab.getNombreL();
			for (int i = 0; i < original.length(); i++) {
				nuevoNombre = nuevoNombre.replace(original.charAt(i), ascii.charAt(i));

			}

			String url = "images/laboratorio/";
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			String name = nuevoNombre
					+ event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.'));

			File file = new File(path + url + name);

			InputStream is = event.getFile().getInputstream();
			OutputStream out = new FileOutputStream(file);
			byte buf[] = new byte[2048];
			int len;
			while ((len = is.read(buf)) > 0)
				out.write(buf, 0, len);

			is.close();
			out.close();

			getNuevoLaboratorioLab().setPath(name);
			mensajeInfo("La imagen (" + name + ") fue subida con Èxito.");

			updateTable();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/****** Nuevo - Logo Temp ****/

	public void guardarImgTmp(FileUploadEvent event) throws IOException {

		try {
			setFileUploadEvent(event);
			String name = nuevoLaboratorioLab.getNombreL()
					+ event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.'));
			getNuevoLaboratorioLab().setPath(name);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", "La imagen fue cargada correctamente."));
			// updateTable();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/****** Modificar - Modificar Lab ****/

	public void modificarLaboratorioLab() {

		RequestContext context = RequestContext.getCurrentInstance();

		try {
			if (LaboratorioLab.getNombreL().equals(getNombreL())) {

				modificarImg(getFileUploadEvent());
				laboratorioI.update(LaboratorioLab);
				updateTable();
				mensajeInfo("El Laboratorio (" + LaboratorioLab.getNombreL() + ") se actualizado exitosamente.");

				context.execute("PF('modificarLaboratorio').hide();");

			} else if (buscarLaboratorioLab(LaboratorioLab.getNombreL()) == false) {

				modificarImg(getFileUploadEvent());
				laboratorioI.update(LaboratorioLab);
				updateTable();
				mensajeInfo("El Laboratorio (" + LaboratorioLab.getNombreL() + ") se actualizado exitosamente.");

				context.execute("PF('modificarLaboratorio').hide();");

			} else {
				updateTable();
				mensajeError("El Laboratorio (" + LaboratorioLab.getNombreL() + ") ya existe");

			}

		} catch (Exception e) {

			mensajeError("Ha ocurrido un problema");

		}
	}

	/****** Nuevo - Logo Temp ****/

	public void modificarImgTmp(FileUploadEvent event) throws IOException {

		try {

			setFileUploadEvent(event);
			String name = LaboratorioLab.getNombreL()
					+ event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.'));
			getNuevoLaboratorioLab().setPath(name);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", "La imagen fue actualizada correctamente."));

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/****** Modificar - Modificar Logo Lab ****/

	public void modificarImg(FileUploadEvent event) throws IOException {

		try {

			eliminarImg();

			String original = "¿¡¬√ƒ≈∆«»… ÀÃÕŒœ–—“”‘’÷ÿŸ⁄€‹›ﬂ‡·‚„‰ÂÊÁËÈÍÎÏÌÓÔÒÚÛÙıˆ¯˘˙˚¸˝ˇ";

			String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";
			String nuevoNombre = LaboratorioLab.getNombreL();
			for (int i = 0; i < original.length(); i++) {
				nuevoNombre = nuevoNombre.replace(original.charAt(i), ascii.charAt(i));

			}

			String url = "images/laboratorio/";
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			String name = nuevoNombre
					+ event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.'));

			File file = new File(path + url + name);

			String direccion = name;

			InputStream is = event.getFile().getInputstream();
			OutputStream out = new FileOutputStream(file);
			byte buf[] = new byte[2048]; // 2mb
			int len;
			while ((len = is.read(buf)) > 0)
				out.write(buf, 0, len);

			is.close();
			out.close();

			LaboratorioLab.setPath(direccion);

			mensajeInfo("La imagen (" + name + ") fue actualizada con Èxito");

			updateTable();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/****** Modificar - Eliminar Logo Lab Anterior ****/

	public void eliminarImg() {

		String urlA = "images/laboratorio/";
		String pathA = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
		String nameA = LaboratorioLab.getPath();

		File fichero = new File(pathA + urlA + nameA);
		eliminarFichero(fichero);

	}

	/****** Eliminar - Eliminar Laboratorio ****/

	public void eliminarLaboratorioLab() {

		try {
			String url = "images/laboratorio/";
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
			String name = LaboratorioLab.getPath();

			File fichero = new File(path + url + name);
			eliminarFichero(fichero);

			laboratorioI.delete(LaboratorioLab);
			updateTable();
			mensajeInfo("El Laboratorio (" + LaboratorioLab.getNombreL() + ") se ha eliminado correctamente");

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {

				mensajeError(
						"La tabla Laboratorio (" + LaboratorioLab.getNombreL() + ") tiene relaciÛn con otra tabla");

			} else {

				mensajeError("Ha ocurrido un problema");

			}

		}

	}

	/****** Busqueda de Laboratorios ****/

	private boolean buscarLaboratorioLab(String valor) {

		try {
			updateTable();
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (LaboratorioLab lab : listaLaboratorioLab) {
			if (lab.getNombreL().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	/****** Pasar Nombre ****/

	public void pasarNombre(String nombre, UnidadLabo uni) {
		setNombreL(nombre);
		unidad = uni;
	}

	public void limpiarCampos() {

		try {

			LaboratorioLab = new LaboratorioLab();
			nuevoLaboratorioLab = new LaboratorioLab();
			unidadSelect = new UnidadLabo();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void eliminarFichero(File fichero) {

		if (!fichero.exists()) {

		} else {
			fichero.delete();

		}

	}

	/****** Getter y Setter de Estado Producto ****/

	public LaboratorioLab getNuevoLaboratorioLab() {
		return nuevoLaboratorioLab;
	}

	public void setNuevoLaboratorioLab(LaboratorioLab nuevoLaboratorioLab) {
		this.nuevoLaboratorioLab = nuevoLaboratorioLab;
	}

	public List<LaboratorioLab> getListaLaboratorioLab() {
		return listaLaboratorioLab;
	}

	public void setListaLaboratorioLab(List<LaboratorioLab> listaLaboratorioLab) {
		this.listaLaboratorioLab = listaLaboratorioLab;
	}

	public LaboratorioLab getLaboratorioLab() {
		return LaboratorioLab;
	}

	public void setLaboratorioLab(LaboratorioLab LaboratorioLab) {
		this.LaboratorioLab = LaboratorioLab;
	}

	public String getNombreL() {
		return nombreL;
	}

	public void setNombreL(String nombreL) {
		this.nombreL = nombreL;
	}

	public UnidadLabo getUnidadSelect() {
		return unidadSelect;
	}

	public void setUnidadSelect(UnidadLabo unidadSelect) {
		this.unidadSelect = unidadSelect;
	}

	public List<UnidadLabo> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<UnidadLabo> unidades) {
		this.unidades = unidades;
	}

	public UnidadLabo getUnidad() {
		return unidad;
	}

	public void setUnidad(UnidadLabo unidad) {
		this.unidad = unidad;
	}

	public List<LaboratorioLab> getFiltrarLaboratorios() {
		return filtrarLaboratorios;
	}

	public void setFiltrarLaboratorios(List<LaboratorioLab> filtrarLaboratorios) {
		this.filtrarLaboratorios = filtrarLaboratorios;
	}

	public List<UnidadLabo> getFiltrarUnidades() {
		return filtrarUnidades;
	}

	public void setFiltrarUnidades(List<UnidadLabo> filtrarUnidades) {
		this.filtrarUnidades = filtrarUnidades;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public FileUploadEvent getFileUploadEvent() {
		return fileUploadEvent;
	}

	public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
		this.fileUploadEvent = fileUploadEvent;
	}

	public UnidadLabo getUnidadSelectEmpty() {
		return unidadSelectEmpty;
	}

	public void setUnidadSelectEmpty(UnidadLabo unidadSelectEmpty) {
		this.unidadSelectEmpty = unidadSelectEmpty;
	}

}