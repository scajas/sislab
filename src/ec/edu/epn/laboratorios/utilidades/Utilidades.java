package ec.edu.epn.laboratorios.utilidades;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class Utilidades {

	public String mensajeError(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "¡ERROR!", mensaje));
		return mensaje;
	}

	public String mensajeInfo(String mensaje) {

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INFORMACIÓN", mensaje));
		return mensaje;
	}

}

