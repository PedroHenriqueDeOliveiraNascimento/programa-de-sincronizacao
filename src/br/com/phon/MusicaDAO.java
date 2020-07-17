package br.com.phon;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MusicaDAO {
	private java.sql.Connection dbc;

	public MusicaDAO() {
		dbc = Connection.getConnection();
	}

	public long gravar(Musica musica) {
		String query = "INSERT INTO musicas (id_remoto, modificacao, titulo, autor, letra) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = this.dbc.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, musica.getIdRemoto());
			stmt.setString(2, Ajudantes.calendarParaString(musica.getModificacao()) );
			stmt.setString(3, musica.getTitulo());
			stmt.setString(4, musica.getAutor());
			stmt.setString(5, musica.getLetra());
			stmt.execute();
			ResultSet result = stmt.getGeneratedKeys();
			long id = -1;
			if(result.next())
				id = result.getLong(1);
			return id;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Musica> buscar() {
		ArrayList<Musica> musicas = new ArrayList<Musica>();
		String query = "SELECT * FROM musicas";
		try (PreparedStatement stmt = this.dbc.prepareStatement(query)) {
			ResultSet result = stmt.executeQuery();
			while(result.next()) {
				Musica musica = new Musica();
				musica.setId(result.getLong("id"));
				musica.setIdRemoto(result.getLong("id_remoto"));
				musica.setModificacao(Ajudantes.stringParaCalendar(result.getString("modificacao")));
				musica.setTitulo(result.getString("titulo"));
				musica.setAutor(result.getString("autor"));
				musica.setLetra(result.getString("letra"));
				musicas.add(musica);
			}
			return musicas;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Musica buscar(long id) {
		String query = "SELECT * FROM musicas WHERE ID = :id";
		try (PreparedStatement stmt = this.dbc.prepareStatement(query)) {
			stmt.setLong(1, id);
			ResultSet result = stmt.executeQuery();
			boolean n = result.next();
			Musica musica = null;
			if(n) {
				musica = new Musica();
				musica.setId(result.getLong("id"));
				musica.setIdRemoto(result.getLong("id_remoto"));
				musica.setModificacao(Ajudantes.stringParaCalendar(result.getString("modificacao")));
				musica.setTitulo(result.getString("titulo"));
				musica.setAutor(result.getString("autor"));
				musica.setLetra(result.getString("letra"));
			}
			return musica;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean editar(Musica musica) {
		String query = "UPDATE musicas SET id_remoto = ?, modificacao = ?, titulo = ?, autor = ?, letra = ? WHERE id = ?";
		try (PreparedStatement stmt = this.dbc.prepareStatement(query)) {
			stmt.setLong(1, musica.getIdRemoto());
			stmt.setString(2, Ajudantes.calendarParaString(Calendar.getInstance()) );
			stmt.setString(3, musica.getTitulo());
			stmt.setString(4, musica.getAutor());
			stmt.setString(5, musica.getLetra());
			stmt.setLong(6, musica.getId());
			int result = stmt.executeUpdate();
			return result == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean remover(long id) {
		Musica musica = buscar(id);
		
		String query = "DELETE FROM musicas WHERE id = ?";
		try (PreparedStatement stmt = this.dbc.prepareStatement(query)) {
			stmt.setLong(1, id);
			int result = stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		query = "INSERT INTO removidos (id_remoto) VALUES (?)";
		try (PreparedStatement stmt = this.dbc.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, musica.getIdRemoto());
			int result = stmt.executeUpdate();
			return result == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
