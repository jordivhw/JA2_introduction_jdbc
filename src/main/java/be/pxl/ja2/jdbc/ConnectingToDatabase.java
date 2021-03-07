package be.pxl.ja2.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectingToDatabase {
	private static final Logger LOGGER = LogManager.getLogger(ConnectingToDatabase.class);

	public static void main(String[] args) {
		try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicdb", "user", "password")) {
			LOGGER.info("Connnection established: " + conn.getCatalog());
			LOGGER.info("Connnection established: " + conn.getMetaData().getDriverName());
			LOGGER.info(conn.getTransactionIsolation());
			LOGGER.info(conn.getMetaData().supportsGetGeneratedKeys());
		} catch (SQLException e) {
			LOGGER.fatal("Something went wrong.", e);
		}
	}
}
