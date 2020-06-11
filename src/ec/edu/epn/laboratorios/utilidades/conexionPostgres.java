package ec.edu.epn.laboratorios.utilidades;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;

public class conexionPostgres implements Serializable {

	private static final long serialVersionUID = -6947528897763208578L;

	private Connection conexion = null;

	private String userName = "postgres";
	private String password = "1234";

	public java.sql.Connection Conexion() {

		try {

			if (conexion != null)

			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://192.168.1.117:5432/bddcorpepn", userName,
			password);

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