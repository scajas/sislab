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

public class RucValidator implements Validator{

	//RUC Format
	public static final String RUC_PATTERN = "^[0-9]{13}$";
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String ruc = (String) value;
		Pattern patron = Pattern.compile(RUC_PATTERN);
        Matcher encaja = patron.matcher(ruc);
		
        //System.out.println("RUC matches: "+encaja.matches());
        if(!encaja.matches()){
        		throwErrorMessage(ruc);
        }
	}
	
	private void throwErrorMessage(String ruc){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("rucValidator.RUC_PATRON");
	       String detail = MessageFormat.format(bundle.getString("rucValidator.RUC_PATRON_detail"), ruc);
		FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}
}
