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

public class TelefonoValidator implements Validator{

	public static final String TELEFONO_PATTERN = "^[0-9]{7,15}$"; 
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String telefono = (String) value;
		
		if(telefono.equals("")){
			return;
		}
		
		Pattern patron = Pattern.compile(TELEFONO_PATTERN);
        Matcher encaja = patron.matcher(telefono);
        
        if(!encaja.matches()){
        	throwErrorMessage(telefono);
        }
		
	}

	private void throwErrorMessage(String telefono){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("telefonoValidator.TELEFONO_PATRON");
	       String detail = MessageFormat.format(bundle.getString("telefonoValidator.TELEFONO_PATRON_detail"), telefono);
		FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}
}
