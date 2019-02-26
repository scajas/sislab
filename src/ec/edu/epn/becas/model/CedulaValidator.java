/*******************************************************************************
 * Derechos de autor registrados en el IEPI
 * 
 * Titular de la obra: Escuela Politécnica Nacional
 * Año:  2012
 * 
 * Desarrollador:
 * 	
 * 	Saulo Velasco
 ******************************************************************************/
package ec.edu.epn.becas.model;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class CedulaValidator implements Validator{

	public static final int NUMERO_DE_PROVINCIAS = 24; 
	public static final String CEDULA_PATTERN = "^[0-9]{10}$";
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String cedula = (String) value;
		Pattern patron = Pattern.compile(CEDULA_PATTERN);
        Matcher encaja = patron.matcher(cedula);
		
        if(encaja.matches()){
        	int prov = Integer.parseInt(cedula.substring(0,2));
        	if(!((prov > 0) && (prov <= NUMERO_DE_PROVINCIAS))){
        		throwErrorMessage(cedula);
        	}
        }else{
        	throwErrorMessage(cedula);
        }
        
	}
	
	private void throwErrorMessage(String cedula){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("cedulaValidator.CEDULA_PATRON");
	       String detail = MessageFormat.format(bundle.getString("cedulaValidator.CEDULA_PATRON_detail"), cedula);
		FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}

}
