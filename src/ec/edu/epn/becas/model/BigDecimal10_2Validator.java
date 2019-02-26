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

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class BigDecimal10_2Validator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent conponent, Object value)
			throws ValidatorException {
		BigDecimal number = (BigDecimal) value;
		int scale = number.scale();
		int precision = number.precision();
		if(scale>2 || precision>10 || number.doubleValue() >= 100000000){
			throwErrorMessage(number);
		}
	}
	
	private void throwErrorMessage(BigDecimal value){
		ResourceBundle bundle = ResourceBundle.getBundle("ec.edu.epn.cmdb.model.Messages");
	       String summary = bundle.getString("bigDecimal10_2Validator.NUMERIC10_2_PATRON");
	       String detail = MessageFormat.format(bundle.getString("bigDecimal10_2Validator.NUMERIC10_2_PATRON_detail"), value);
		 FacesMessage message = new FacesMessage();
	       message.setDetail(summary);
	       message.setSummary(detail);
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	}

}
