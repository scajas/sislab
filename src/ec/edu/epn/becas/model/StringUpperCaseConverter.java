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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class StringUpperCaseConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		// TODO Auto-generated method stub
		return ((String)value).toUpperCase().trim();
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		// TODO Auto-generated method stub
		return ((String)value).toUpperCase().trim();
	}

}
