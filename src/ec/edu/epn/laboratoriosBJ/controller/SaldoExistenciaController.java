package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ec.edu.epn.laboratorioBJ.beans.ExistenciaDAO;
import ec.edu.epn.laboratorioBJ.beans.SaldoExistenciaDAO;
import ec.edu.epn.laboratorioBJ.entities.Existencia;
import ec.edu.epn.laboratorioBJ.entities.SaldoExistencia;
import ec.edu.epn.laboratorioBJ.entities.SaldoExistenciaPK;
import ec.edu.epn.laboratorios.utilidades.Utilidades;
import ec.edu.epn.seguridad.VO.SesionUsuario;

@ManagedBean(name = "saldoExistenciaController")
@SessionScoped
public class SaldoExistenciaController implements Serializable {

	/** VARIABLES DE SESION ***/
	private static final long serialVersionUID = 6771930005130933302L;
	FacesContext fc = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
	HttpSession session = request.getSession();
	SesionUsuario su = (SesionUsuario) session.getAttribute("sesionUsuario");

	/****************************************************************************/

	/** SERIVICIOS **/

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/SaldoExistenciaDAOImplement!ec.edu.epn.laboratorioBJ.beans.SaldoExistenciaDAO")
	private SaldoExistenciaDAO saldoExistenciaI;

	@EJB(lookup = "java:global/ServiciosSeguridadEPN/ExistenciaDAOImplement!ec.edu.epn.laboratorioBJ.beans.ExistenciaDAO")
	private ExistenciaDAO existenciaI;

	// variables de clase
	private Integer mes1;

	private String mes;

	private SaldoExistencia saldoExistencia;
	private List<SaldoExistencia> saldosExistencia = new ArrayList<>();

	private List<Existencia> existencias = new ArrayList<>();
	private Existencia existencia;

	private List<SaldoExistenciaPK> saldosExistenciaPK = new ArrayList<>();
	private SaldoExistenciaPK saldoExistenciaPK;

	private String mesvalue;
	private Utilidades utilidades;

	// Metodo Init
	@PostConstruct
	public void init() {
		try {

			saldosExistencia = saldoExistenciaI.getAll(SaldoExistencia.class);
			existencias = existenciaI.getAll(Existencia.class);
			existencia = new Existencia();
			utilidades = new Utilidades();
		} catch (Exception e) {
		
		}

	}

	public void guardar() {

		String mensajeVal = null;
		if (mes1 != null) {
			for (Existencia existencia : existencias) {

				SaldoExistencia saldo = new SaldoExistencia();
				SaldoExistenciaPK saldopk = new SaldoExistenciaPK();
				saldopk.setAnio(getanio());
				saldopk.setIdExistencia(existencia.getIdExistencia());
				saldopk.setMes(mes1);
				saldo.setFecha(new Date());
				saldo.setId(saldopk);
				saldo.setIdUsuario(su.UNIDAD_USUARIO_LOGEADO);
				saldo.setNombreUsuario(su.nombre_usuario_logeado);
				saldo.setSaldoE(existencia.getCantidadE());

				try {
					SaldoExistencia saldoExiste = new SaldoExistencia();
					saldoExiste = (SaldoExistencia) saldoExistenciaI.getById(SaldoExistencia.class, saldopk);

					if (saldoExiste == null) {

						saldoExistenciaI.save(saldo);
						mensajeVal = "guardar";
						break;

					} else {
						mensajeVal = "saldoCerrado";
						break;

					}
				} catch (Exception e) {
					utilidades.mensajeError("Ha ocurrido un error");
				}

			}

			if (mensajeVal.contains("saldoCerrado")) {
				utilidades.mensajeError("Ya ha cerrado el saldo para el mes " + " (" + mesvalue + ") ");
			} else if (mensajeVal.contains("guardar")) {
				utilidades.mensajeInfo("El Saldo de Existencia almacenado exitosamente en el mes " + " (" + mesvalue + ") ");
			}

		} else {
			utilidades.mensajeError("Debe seleccionar un mes");

		}

	}

	public void mesString() {

		setMesvalue(String.valueOf(mes1));

		if (mes1 == 1)
			setMesvalue("Enero");
		else if (mes1 == 2)
			setMesvalue("Febrero");
		else if (mes1 == 3)
			setMesvalue("Marzo");
		else if (mes1 == 4)
			setMesvalue("Abril");
		else if (mes1 == 5)
			setMesvalue("Mayo");
		else if (mes1 == 6)
			setMesvalue("Junio");
		else if (mes1 == 7)
			setMesvalue("Julio");
		else if (mes1 == 8)
			setMesvalue("Agosto");
		else if (mes1 == 9)
			setMesvalue("Septiembre");
		else if (mes1 == 10)
			setMesvalue("Octubre");
		else if (mes1 == 11)
			setMesvalue("Noviembre");
		else if (mes1 == 12)
			setMesvalue("Diciembre");

	}

	public int getanio() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String s = "";

		String date = simpleDateFormat.format(new Date());
		s = date.substring(0, 4);

		int anioA = Integer.parseInt(s);
		return anioA;

	}

	/******* GET and SET *******/

	public List<SaldoExistencia> getSaldosExistencia() {
		return saldosExistencia;
	}

	public void setSaldosExistencia(List<SaldoExistencia> saldosExistencia) {
		this.saldosExistencia = saldosExistencia;
	}

	public List<Existencia> getExistencias() {
		return existencias;
	}

	public void setExistencias(List<Existencia> existencias) {
		this.existencias = existencias;
	}

	public Existencia getExistencia() {
		return existencia;
	}

	public void setExistencia(Existencia existencia) {
		this.existencia = existencia;
	}

	public List<SaldoExistenciaPK> getSaldosExistenciaPK() {
		return saldosExistenciaPK;
	}

	public void setSaldosExistenciaPK(List<SaldoExistenciaPK> saldosExistenciaPK) {
		this.saldosExistenciaPK = saldosExistenciaPK;
	}

	public SaldoExistenciaPK getSaldoExistenciaPK() {
		return saldoExistenciaPK;
	}

	public void setSaldoExistenciaPK(SaldoExistenciaPK saldoExistenciaPK) {
		this.saldoExistenciaPK = saldoExistenciaPK;
	}

	public SaldoExistencia getSaldoExistencia() {
		return saldoExistencia;
	}

	public void setSaldoExistencia(SaldoExistencia saldoExistencia) {
		this.saldoExistencia = saldoExistencia;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}
	
	public Integer getMes1() {
		return mes1;
	}

	public void setMes1(Integer mes1) {
		this.mes1 = mes1;
	}

	public String getMesvalue() {
		return mesvalue;
	}

	public void setMesvalue(String mesvalue) {
		this.mesvalue = mesvalue;
	}

}
