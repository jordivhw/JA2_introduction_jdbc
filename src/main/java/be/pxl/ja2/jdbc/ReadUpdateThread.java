package be.pxl.ja2.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReadUpdateThread extends Thread {

	private static final Logger LOGGER = LogManager.getLogger(ReadUpdateThread.class);
	private Connection connection;
	private String newPhone;
	private boolean commit;

	ReadUpdateThread(int transactionLevel, String newPhone, boolean commit) throws SQLException {
		this.connection = createConnection(transactionLevel);
		this.newPhone = newPhone;
		this.commit = commit;
	}

	@Override
	public void run() {
		try {
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			readPhoneNumber(statement);
			Thread.sleep(5000);
			updatePhoneNumber(statement);
			if (commit) {
				connection.commit();
			} else {
				connection.rollback();
			}
		} catch (SQLException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void updatePhoneNumber(Statement statement) throws SQLException {
		System.out.println("Update phone to " + newPhone);
		statement.executeUpdate("UPDATE contacts set phone='"+ newPhone + "' WHERE name = 'Jane'");
	}

	private void readPhoneNumber(Statement statement) throws SQLException {
		ResultSet resultSet = statement.executeQuery("SELECT phone FROM contacts where name = 'Jane'");
		if (resultSet.next()) {
			System.out.println(this.getName() + " read " + resultSet.getString("phone"));
		}
	}

	public static Connection createConnection(int transactionLevel) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicdb", "user", "password");
		conn.setTransactionIsolation(transactionLevel);
		return conn;
	}

	public static void main(String[] args) {
		try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicdb", "user", "password")) {
			LOGGER.info(conn.getTransactionIsolation());
			conn.createStatement().executeUpdate("UPDATE contacts set phone='11111' WHERE name = 'Jane'");
			//int transactionLevel = Connection.TRANSACTION_READ_UNCOMMITTED;
			int transactionLevel = Connection.TRANSACTION_READ_COMMITTED;
			Thread thread1 = new ReadUpdateThread(transactionLevel, "22222", false);
			Thread thread2 = new ReadUpdateThread(transactionLevel, "44444", true);
			thread1.start();
			Thread.sleep(6000);
			thread2.start();
			thread1.join();
			thread2.join();
		} catch (Exception e) {
			LOGGER.fatal("Something went wrong.", e);
		}
	}
}
