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

import ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO;
import ec.edu.epn.laboratorioBJ.beans.RiesgoEspecificoDAO;
import ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO;
import ec.edu.epn.laboratorioBJ.entities.ProductoLab;
import ec.edu.epn.laboratorioBJ.entities.Riesgoespecifico;
import ec.edu.epn.laboratorioBJ.entities.Tipoproducto;
import ec.edu.epn.seguridad.VO.SesionUsuario;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "productoController")
@SessionScoped
public class ProductoLabController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 1L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/
	/** SERVICIOS **/
	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ProductoLabDAOImplement!ec.edu.epn.laboratorioBJ.beans.ProductoLabDAO")
	private ProductoLabDAO productoI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/RiesgoEspecificoDAOImplement!ec.edu.epn.laboratorioBJ.beans.RiesgoEspecificoDAO")
	private RiesgoEspecificoDAO riesgoEspI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/TipoProductoDAOImplement!ec.edu.epn.laboratorioBJ.beans.TipoProductoDAO")
	private TipoProductoDAO tipoProI;

	/****************************************************************************/
	/** VARIABLES **/

	private List<ProductoLab> listarProductos = new ArrayList<>();
	private ProductoLab nuevoProducto;
	private ProductoLab productoLab;// eliminar y editar
	private String nombreTP;
	private String auxDes;
	private Integer riesgoS;
	private Integer riesgoI;
	private Integer riesgoR;
	private List<ProductoLab> productosfiltrados = new ArrayList<>();//filtro global

	// select de riesgo
	private Riesgoespecifico riesgoEspecificoSelect;
	private List<Riesgoespecifico> listarRiesgosEsp = new ArrayList<Riesgoespecifico>();
	private Riesgoespecifico riesgoEsp;

	// select de tipo producto
	private Tipoproducto tipoProductoSelect;
	private List<Tipoproducto> listarTipoProducto = new ArrayList<Tipoproducto>();
	private Tipoproducto tipoProducto;

	// Método init
	@PostConstruct
	public void init() {
		try {

			//setListarProductos(productoI.getAll(ProductoLab.class));
			setListarProductos(productoI.filtrarLista("l"));
			setListarRiesgosEsp(riesgoEspI.getAll(Riesgoespecifico.class));
			setListarTipoProducto(tipoProI.getAll(Tipoproducto.class));
			setNuevoProducto(new ProductoLab());
			productoLab = new ProductoLab();

			// riesgo

			listarRiesgosEsp = riesgoEspI.getAll(Riesgoespecifico.class);
			riesgoEsp = new Riesgoespecifico();

			// tipo producto
			listarTipoProducto = tipoProI.getAll(Tipoproducto.class);
			tipoProducto = new Tipoproducto();

		} catch (Exception e) {

		}

	}

	/****** Agregar Estado Producto ****/

	public void agregarProducto(ActionEvent event) {

		try {
			if (buscarProducto(nuevoProducto.getNombrePr()) == true) {
				FacesContext.getCurrentInstance()
						.addMessage(event.getComponent().getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"ERROR!", "Ha ocurrido un error, El Producto (" + nuevoProducto.getNombrePr()
										+ ")ya existe."));
				nuevoProducto = new ProductoLab();

			} else {

				Integer idAuxliar = productoI.generarId("ProductoLab", "auxIdprod");
				String codigoAux = String.valueOf(idAuxliar);
				nuevoProducto.setAuxIdprod(idAuxliar);

				switch (codigoAux.length()) {
				case 1:
					nuevoProducto.setIdProducto("PR" + "-" + "0000" + codigoAux);
					break;
				case 2:
					nuevoProducto.setIdProducto("PR" + "-" + "000" + codigoAux);
					break;
				case 3:
					nuevoProducto.setIdProducto("PR" + "-" + "00" + codigoAux);
					break;
				case 4:
					nuevoProducto.setIdProducto("PR" + "-" + "0" + codigoAux);
					break;
				case 5:
					nuevoProducto.setIdProducto("PR" + "-" + codigoAux);
					break;

				default:
					break;
				}

				nuevoProducto.setRiesgoespecifico(riesgoEspecificoSelect);
				nuevoProducto.setTipoproducto(tipoProductoSelect);
				

				nuevoProducto.setRiesgoPr("S" + nuevoProducto.getRiesgosaludPr() + "I"
						+ nuevoProducto.getRiesgoinflamabilidadPr() + "R" + nuevoProducto.getRiesgoreactividadPr());

				nuevoProducto.setRiesgosaludPr("S" + nuevoProducto.getRiesgosaludPr());
				nuevoProducto.setRiesgoinflamabilidadPr("I" + nuevoProducto.getRiesgoinflamabilidadPr());
				nuevoProducto.setRiesgoreactividadPr("R" + nuevoProducto.getRiesgoreactividadPr());

				productoI.save(nuevoProducto);
				listarProductos = productoI.getAll(ProductoLab.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El Producto se ha almacenado exitosamente"));

				nuevoProducto = new ProductoLab();
				riesgoEspecificoSelect = new Riesgoespecifico();
				tipoProductoSelect = new Tipoproducto();

			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR!", ""));
		}

	}

	/****** Modificar Estado Producto ****/

	public void modificarProducto(ActionEvent event) {
		try {
			if (productoLab.getNombrePr().equals(getNombreTP())) {

				productoLab.setRiesgoPr("S" + riesgoS.toString() + "I" + riesgoI.toString() + "R" + riesgoR.toString());

				productoLab.setRiesgosaludPr("S" + riesgoS.toString());
				productoLab.setRiesgoinflamabilidadPr("I" + riesgoI.toString());
				productoLab.setRiesgoreactividadPr("R" + riesgoR.toString());
				productoI.update(productoLab);
				listarProductos = productoI.getAll(ProductoLab.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"Producto (" + productoLab.getNombrePr() + ") se actualizado exitosamente"));

			} else if (buscarProducto(productoLab.getNombrePr()) == false) {

				productoLab.setRiesgoPr("S" + riesgoS.toString() + "I" + riesgoI.toString() + "R" + riesgoR.toString());

				productoLab.setRiesgosaludPr("S" + riesgoS.toString());
				productoLab.setRiesgoinflamabilidadPr("I" + riesgoI.toString());
				productoLab.setRiesgoreactividadPr("R" + riesgoR.toString());
				listarProductos = productoI.getAll(ProductoLab.class);

				productoI.update(productoLab);
				listarProductos = productoI.getAll(ProductoLab.class);

				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_INFO, "",
								"Producto (" + productoLab.getNombrePr() + ") se actualizado exitosamente"));

			} else {
				listarProductos = productoI.getAll(ProductoLab.class);
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(), new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Ha ocurrido un error", "El Producto ya existe."));
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Ha ocurrido un error"));
		}
	}

	/****** Eliminar Estado Producto ****/

	public void eliminarProducto(ActionEvent event) {

		try {

			productoI.delete(productoLab);
			listarProductos = productoI.getAll(ProductoLab.class);

			FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
					new FacesMessage(FacesMessage.SEVERITY_INFO, "",
							"El Producto (" + productoLab.getNombrePr() + ") se ha eliminado correctamente"));

		} catch (Exception e) {

			if (e.getMessage() == "Transaction rolled back") {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "NO SE PUEDE ELIMINAR EL REGISTRO!",
								"La tabla Producto tiene relación con otra tabla"));
			} else {
				FacesContext.getCurrentInstance().addMessage(event.getComponent().getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR!", ""));
			}

		}

	}

	/****** Busqueda de Estado Producto ****/

	private boolean buscarProducto(String valor) {

		try {
			listarProductos = productoI.getAll(ProductoLab.class);
		} catch (Exception e) {

			e.printStackTrace();
		}

		boolean resultado = false;
		for (ProductoLab tipo : listarProductos) {
			if (tipo.getNombrePr().equals(valor)) {
				resultado = true;
				break;
			} else {
				resultado = false;
			}
		}

		return resultado;
	}

	public void pasarNombre(String nombre, Riesgoespecifico riesgoE, Tipoproducto tipoPro) {
		setNombreTP(nombre);
		riesgoEsp = riesgoE;
		tipoProducto = tipoPro;
		String riesgo1 = productoLab.getRiesgosaludPr();
		String riesgo2 = productoLab.getRiesgoinflamabilidadPr();
		String riesgo3 = productoLab.getRiesgoreactividadPr();
		riesgoS = Integer.parseInt(riesgo1.substring(1, 2));
		riesgoI = Integer.parseInt(riesgo2.substring(1, 2));
		riesgoR = Integer.parseInt(riesgo3.substring(1, 2));

		// System.out.println("Unidad"+unidadSelect.getNombreU());
	}

	/****** Getter y Setter de Estado Producto ****/

	public List<ProductoLab> getListarProductos() {
		return listarProductos;
	}

	public void setListarProductos(List<ProductoLab> listarProductos) {
		this.listarProductos = listarProductos;
	}

	public ProductoLab getNuevoProducto() {
		return nuevoProducto;
	}

	public void setNuevoProducto(ProductoLab nuevoProducto) {
		this.nuevoProducto = nuevoProducto;
	}

	public ProductoLab getProductoLab() {
		return productoLab;
	}

	public void setProductoLab(ProductoLab productoLab) {
		this.productoLab = productoLab;
	}

	public String getNombreTP() {
		return nombreTP;
	}

	public void setNombreTP(String nombreTP) {
		this.nombreTP = nombreTP;
	}

	public Riesgoespecifico getRiesgoEspecificoSelect() {
		return riesgoEspecificoSelect;
	}

	public void setRiesgoEspecificoSelect(Riesgoespecifico riesgoEspecificoSelect) {
		this.riesgoEspecificoSelect = riesgoEspecificoSelect;
	}

	public List<Riesgoespecifico> getListarRiesgosEsp() {
		return listarRiesgosEsp;
	}

	public void setListarRiesgosEsp(List<Riesgoespecifico> listarRiesgosEsp) {
		this.listarRiesgosEsp = listarRiesgosEsp;
	}

	public Riesgoespecifico getRiesgoEsp() {
		return riesgoEsp;
	}

	public void setRiesgoEsp(Riesgoespecifico riesgoEsp) {
		this.riesgoEsp = riesgoEsp;
	}

	public List<Tipoproducto> getListarTipoProducto() {
		return listarTipoProducto;
	}

	public void setListarTipoProducto(List<Tipoproducto> listarTipoProducto) {
		this.listarTipoProducto = listarTipoProducto;
	}

	public Tipoproducto getTipoProductoSelect() {
		return tipoProductoSelect;
	}

	public void setTipoProductoSelect(Tipoproducto tipoProductoSelect) {
		this.tipoProductoSelect = tipoProductoSelect;
	}

	public Tipoproducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(Tipoproducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public String getAuxDes() {
		return auxDes;
	}

	public void setAuxDes(String auxDes) {
		this.auxDes = auxDes;
	}

	public Integer getRiesgoS() {
		return riesgoS;
	}

	public void setRiesgoS(Integer riesgoS) {
		this.riesgoS = riesgoS;
	}

	public Integer getRiesgoI() {
		return riesgoI;
	}

	public void setRiesgoI(Integer riesgoI) {
		this.riesgoI = riesgoI;
	}

	public Integer getRiesgoR() {
		return riesgoR;
	}

	public void setRiesgoR(Integer riesgoR) {
		this.riesgoR = riesgoR;
	}

	public List<ProductoLab> getProductosfiltrados() {
		return productosfiltrados;
	}

	public void setProductosfiltrados(List<ProductoLab> productosfiltrados) {
		this.productosfiltrados = productosfiltrados;
	}

}
