package examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.RunScript;

import utils.Config;

public class SmartHome {
	public static void prepareH2Database() throws ClassNotFoundException, SQLException, FileNotFoundException {
		String driver = "org.h2.Driver";
		String dbConnection = "jdbc:h2:mem:smart_home;DB_CLOSE_DELAY=-1;";
		String user = "iot";
		String password = "iot";
		
		Class.forName(driver);
		
		try (Connection connection = DriverManager.getConnection(dbConnection, user, password);) {
			RunScript.execute(connection, new FileReader(Config.getInstance().getProperty("smartHomeDB")));
		}
	}
	
}
