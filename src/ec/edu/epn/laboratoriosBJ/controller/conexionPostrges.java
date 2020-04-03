package ec.edu.epn.laboratoriosBJ.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;

public class conexionPostrges implements Serializable {

	private static final long serialVersionUID = -6947528897763208578L;

	private Connection conexion = null;

	private String url = "jdbc:postgresql://";
	private String serverName = "196.168.1.114";
	private String portNumber = "5432";
	private String databaseName = "bddcorpepn";
	private String userName = "postgres";
	private String password = "1234";

	// Constructor
	public conexionPostrges() {
	}

	public String conexionUrl() {
		return url + serverName + ":" + portNumber + "/" + databaseName;
	}

	public java.sql.Connection Conexion() {

		try {

			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://192.168.1.114:5432/bddcorpepn", userName,
					password);

			if (conexion != null)
				System.out.println("Conexión exitosa!");
		} catch (Exception e) {

			e.printStackTrace();

		}
		return conexion;
	}

	public void closeConnection() {
		try {
			if (conexion != null)
				conexion.close();
			conexion = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public java.sql.Connection getConexion() {
		return conexion;
	}

	public void setConexion(java.sql.Connection conexion) {
		this.conexion = conexion;
	}

}