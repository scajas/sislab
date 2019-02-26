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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailValidator implements Validator{

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		String email = (String) value;
		
		if(email.equals("")){
			return;
		}
		
		 boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   
		   if(result==false){
			   throwErrorMessage(email);
		   }
		
	}

	private void throwErrorMessage(String email){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("emailValidator.EMAIL_PATRON");
	       String detail = MessageFormat.format(bundle.getString("emailValidator.EMAIL_PATRON_detail"), email);
		FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}
}
