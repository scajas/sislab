package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.sql.DriverManager;




public class conexionPostrges implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6947528897763208578L;


	private java.sql.Connection conection = null;

	public java.sql.Connection getConection() {
		return conection;
	}

	public void setConection(java.sql.Connection conection) {
		this.conection = conection;
	}
	
	private String url = "jdbc:postgresql://";
	private String serverName = "172.31.203.196";
	private String portNumber = "5432";	
	private String databaseName = "bddcorpepn";
	private String userName = "postgres";
	private String password = "seguridad";

	// Constructor
	public conexionPostrges() {
	} 

	public String getConnectionUrl() {
		return url + serverName + ":" + portNumber + "/" + databaseName;
	}
	


	public java.sql.Connection getConnection() {		
		try {
			
			//String urlDatabase =  "jdbc:postgresql://172.31.203.216:5432/bddcorpepn";
			    Class.forName("org.postgresql.Driver"); 
			     
			    conection = DriverManager.getConnection(getConnectionUrl(),  userName, password);			
			
			if (conection != null)
				System.out.println("Connection Successful!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Trace in getConnection() : "
					+ e.getMessage());
		}
		return conection;
	}

	

	public void closeConnection() {
		try {
			if (conection != null)
				conection.close();
			conection = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}