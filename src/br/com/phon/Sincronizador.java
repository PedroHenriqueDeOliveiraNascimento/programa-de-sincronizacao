package br.com.phon;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import java.sql.PreparedStatement;

public class Sincronizador {
	private final Preferences prefs = Preferences.userRoot();
	
	public Sincronizador() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		sincronizarINSERTs();
		sincronizarUPDATEs();
		sincronizarDELETEs();
	}

	private void sincronizarDELETEs() {
		// Parte da Conexão com banco de dados
		java.sql.Connection localConnection = br.com.phon.Connection.getConnection();
		java.sql.Connection remoteConnection = null;
		// Conexão Remota
		try {
			remoteConnection = DriverManager.getConnection("jdbc:mysql://"+prefs.get("host", "localhost")+"/banco?user="+prefs.get("user", "root")+"&password="+prefs.get("senha", ""));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (remoteConnection == null)
			return;

		// Musicas removidas do local que serão removidas no remoto
		String query = "SELECT * FROM removidos";
		try (PreparedStatement stmt = localConnection.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				String query2 = "DELETE FROM musicas WHERE id = " + result.getLong("id_remoto");
				PreparedStatement stmt2 = remoteConnection.prepareStatement(query2);
				stmt2.execute();
				stmt2.close();
				query2 = "DELETE FROM removidos WHERE id = " + result.getLong("id");
				stmt2 = localConnection.prepareStatement(query2);
				stmt2.execute();
				stmt2.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// Musicas removidas do local que serão removidas no remoto
		query = "SELECT * FROM removidos";
		try (PreparedStatement stmt = remoteConnection.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				String query2 = "DELETE FROM musicas WHERE id = " + result.getLong("id_local");
				PreparedStatement stmt2 = localConnection.prepareStatement(query2);
				stmt2.execute();
				stmt2.close();
				query2 = "DELETE FROM removidos WHERE id = " + result.getLong("id");
				stmt2 = localConnection.prepareStatement(query2);
				stmt2.execute();
				stmt2.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private void sincronizarUPDATEs() {
		// Parte da Conexão com banco de dados
		java.sql.Connection localConnection = br.com.phon.Connection.getConnection();
		java.sql.Connection remoteConnection = null;
		// Conexão Remota
		try {
			remoteConnection = DriverManager.getConnection("jdbc:mysql://"+prefs.get("host", "localhost")+"/banco?user="+prefs.get("user", "root")+"&password="+prefs.get("senha", ""));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (remoteConnection == null)
			return;

		// Pega as musicas locais
		String query = "SELECT * FROM musicas";
		try (PreparedStatement stmt = localConnection.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				try {
					// Pega o equivalente no db remoto
					String query2 = "SELECT * FROM musicas WHERE id_local = " + result.getLong("id");
					PreparedStatement stmt2 = remoteConnection.prepareStatement(query2);
					ResultSet result2 = stmt2.executeQuery();
					if (result2.next()) {
						String strDataLocal = result.getString("modificacao");
						strDataLocal = strDataLocal == null ? "" : strDataLocal;
						String strDataRemoto = result2.getString("modificacao");
						strDataRemoto = strDataRemoto == null ? "" : strDataRemoto;
						if ((!strDataLocal.equals(strDataRemoto))) {
							Calendar dataLocal = Ajudantes.stringParaCalendar(result.getString("modificacao"));
							Calendar dataRemote = Ajudantes.stringParaCalendar(result2.getString("modificacao"));
							if(dataLocal == null && dataRemote == null)
								continue;
							// Se dataLocal é maior que a dataRemoto
							if ((dataLocal != null && dataRemote == null)
									|| (dataLocal != null && (dataLocal.compareTo(dataRemote)) == 1)) {
								query2 = "UPDATE musicas SET modificacao = ?, titulo = ?, autor = ?, letra = ? WHERE id = ?";
								stmt2 = remoteConnection.prepareStatement(query2);
								stmt2.setString(1, Ajudantes.calendarParaString(dataLocal));
								stmt2.setString(2, result.getString("titulo"));
								stmt2.setString(3, result.getString("autor"));
								stmt2.setString(4, result.getString("letra"));
								stmt2.setString(5, result.getString("id_remoto"));
								stmt2.execute();
								stmt2.close();
							} else if ((dataRemote != null && dataLocal == null)
									|| (dataRemote != null && (dataRemote.compareTo(dataLocal) == 1))) {
								// Se dateRemote for maior que dataLocal
								query2 = "UPDATE musicas SET modificacao = ?, titulo = ?, autor = ?, letra = ? WHERE id = ?";
								stmt2 = localConnection.prepareStatement(query2);
								stmt2.setString(1, Ajudantes.calendarParaString(dataLocal));
								stmt2.setString(2, result2.getString("titulo"));
								stmt2.setString(3, result2.getString("autor"));
								stmt2.setString(4, result2.getString("letra"));
								stmt2.setString(5, result2.getString("id_local"));
								stmt2.execute();
								stmt2.close();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void sincronizarINSERTs() {
		// Parte da Conexão com banco de dados
		java.sql.Connection localConnection = br.com.phon.Connection.getConnection();
		java.sql.Connection remoteConnection = null;
		// Conexão Remota
		try {
			remoteConnection = DriverManager.getConnection("jdbc:mysql://"+prefs.get("host", "localhost")+"/banco?user="+prefs.get("user", "root")+"&password="+prefs.get("senha", ""));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (remoteConnection == null)
			return;

		// Parte do insert do local para o remoto
		String query = "SELECT * FROM musicas WHERE id_remoto = 0 OR id_remoto IS NULL";
		try (PreparedStatement stmt = localConnection.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				String query2 = "INSERT INTO musicas (id_local, titulo, autor, letra) VALUES (?, ?, ?, ?)";
				PreparedStatement stmt2 = remoteConnection.prepareStatement(query2,
						PreparedStatement.RETURN_GENERATED_KEYS);
				stmt2.setLong(1, result.getLong("id"));
				stmt2.setString(2, result.getString("titulo"));
				stmt2.setString(3, result.getString("autor"));
				stmt2.setString(4, result.getString("letra"));
				stmt2.execute();
				ResultSet keys = stmt2.getGeneratedKeys();
				keys.next();
				long id = keys.getLong(1);
				query2 = "UPDATE musicas SET id_remoto = " + id + " WHERE id = " + result.getLong("id");
				stmt2 = localConnection.prepareStatement(query2);
				stmt2.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// Parte dos INSERTs do remoto para local
		query = "SELECT * FROM musicas WHERE id_local = 0 OR id_local IS NULL";
		try (PreparedStatement stmt = remoteConnection.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				String query2 = "INSERT INTO musicas (id_remoto, titulo, autor, letra) VALUES (?, ?, ?, ?)";
				PreparedStatement stmt2 = localConnection.prepareStatement(query2,
						PreparedStatement.RETURN_GENERATED_KEYS);
				stmt2.setLong(1, result.getLong("id"));
				stmt2.setString(2, result.getString("titulo"));
				stmt2.setString(3, result.getString("autor"));
				stmt2.setString(4, result.getString("letra"));
				stmt2.execute();
				ResultSet keys = stmt2.getGeneratedKeys();
				keys.next();
				long id = keys.getLong(1);
				query2 = "UPDATE musicas SET id_local = " + id + " WHERE id = " + result.getLong("id");
				stmt2 = remoteConnection.prepareStatement(query2);
				stmt2.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
