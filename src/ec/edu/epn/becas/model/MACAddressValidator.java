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

public class MACAddressValidator implements Validator {

	/** MACAddress Unix Format */
	private static final String UNIX_FORMAT ="^[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}$";
	
	/** MACAddress Windows Format */
	private static final String WIN_FORMAT = "^[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}$";
	
	/** MACAddress Cisco Format */
	private static final String CISCO_FORMAT = "^[0-9a-fA-F]{4}\\.[0-9a-fA-F]{4}\\.[0-9a-fA-F]{4}$";
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String mac = (String) value;
		
		Pattern unix = Pattern.compile(UNIX_FORMAT);
		Pattern windows = Pattern.compile(WIN_FORMAT);
		Pattern cisco = Pattern.compile(CISCO_FORMAT);
		
		Matcher matcher_unix = unix.matcher(mac);
		//System.out.println(matcher_unix.toString());
		Matcher matcher_windows = windows.matcher(mac);
		//System.out.println(matcher_windows.toString());
		Matcher matcher_cisco = cisco.matcher(mac);
		//System.out.println(matcher_cisco.toString());
		
		System.out.println("Unix: " + matcher_unix.matches());
		System.out.println("Windows: " + matcher_windows.matches());
		System.out.println("Cisco: " + matcher_cisco.matches());
		
		if(!matcher_unix.matches() && !matcher_windows.matches() && !matcher_cisco.matches()){
			throwErrorMessage(mac);
			return;
		}else {
			return;
		}		
	}
	
	private void throwErrorMessage(String mac){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("MACAddressValidator.MAC_PATRON");
	       String detail = MessageFormat.format(bundle.getString("MACAddressValidator.MAC_PATRON_detail"), mac);
		 FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}

}
