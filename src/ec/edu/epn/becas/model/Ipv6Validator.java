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

import sun.net.util.IPAddressUtil;

public class Ipv6Validator implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value)
			throws ValidatorException {
		
		System.out.println("Ipv6Validator");
		
		if (!IPAddressUtil.isIPv6LiteralAddress((String)value)){
			
			 ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
		       String summary = bundle.getString("ipv6Validator.IPV6_PATRON");
		       String detail = MessageFormat.format(bundle.getString("ipv6Validator.IPV6_PATRON_detail"), (String)value);
			 FacesMessage message = new FacesMessage();
		       message.setDetail(summary);
		       message.setSummary(detail);
		       message.setSeverity(FacesMessage.SEVERITY_ERROR);
		       throw new ValidatorException(message);
		}
		
	}

}
