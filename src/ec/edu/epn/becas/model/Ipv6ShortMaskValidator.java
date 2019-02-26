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

public class Ipv6ShortMaskValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String mask = (String) value;
		
		try{
			Integer numMask = Integer.parseInt(mask);
			if(!(numMask>0 && numMask<129)){
				throwErrorMessage(mask);
				return;
			}
		}catch(Exception e){
			throwErrorMessage(mask);
		}

	}
	
	private void throwErrorMessage(String mask){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("ipv6ShortMaskValidator.IPV6MASK_PATRON");
	       String detail = MessageFormat.format(bundle.getString("ipv6ShortMaskValidator.IPV6MASK_PATRON_detail"), mask);
		 FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}

}
