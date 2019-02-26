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

import java.util.TimeZone;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;


public class SqlTimeConverter extends DateTimeConverter {

	 public SqlTimeConverter() {
         setTimeZone(TimeZone.getDefault());
         setType("time");
     }

     // ------------------------------------------------------- Converter Methods
	 @Override
     public Object getAsObject(FacesContext context,
             UIComponent component, String value) {

         java.util.Date converted = (java.util.Date) super .getAsObject(
                 context, component, value);
         if (converted != null) {
             return new java.sql.Time(converted.getTime());
         } else {
             return null;
         }

     }
}
