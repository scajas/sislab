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

public class NumericValidator implements Validator {
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String numericString = (String) value;
        
       try {  
    	  if(numericString.contains("-")){
    		  throw new Exception();
    	  }
          int temp =  Integer.parseInt( numericString );  
          if(temp<0){
        	  throw new Exception();
          }
        }catch( Exception e){  
        	throwErrorMessage(numericString);  
        }  
		
	}
	
	private void throwErrorMessage(String numericString){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.becas.model.Messages");
	       String summary = bundle.getString("telefonoValidator.NUMERIC_PATRON");
	       String detail = MessageFormat.format(bundle.getString("telefonoValidator.NUMERIC_PATRON_detail"), numericString);
		FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}

}
