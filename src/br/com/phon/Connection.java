package br.com.phon;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public final class Connection {
	private static java.sql.Connection connection;

	public static java.sql.Connection getConnection() {
		if (connection == null) {
			try {
				File root = new File(System.getProperty("user.dir"));
				File path = new File(root, "database.db");
				System.out.println(path.getCanonicalPath());
				Connection.connection = DriverManager.getConnection("jdbc:sqlite:" + path.getCanonicalPath());
				String query = "CREATE TABLE IF NOT EXISTS musicas (id INTEGER PRIMARY KEY AUTOINCREMENT, id_remoto INTEGER, modificacao DATETIME, titulo VARCHAR (60), autor VARCHAR (60), letra TEXT)";
				PreparedStatement stmt = Connection.connection.prepareStatement(query);
				stmt.execute();
				query = "CREATE TABLE IF NOT EXISTS removidos (id INTEGER PRIMARY KEY AUTOINCREMENT, id_remoto INTEGER)";
				stmt = Connection.connection.prepareStatement(query);
				stmt.execute();
			} catch (SQLException | IOException e) {
				throw new RuntimeException(e);
			}
		}
		return Connection.connection;
	}
}
