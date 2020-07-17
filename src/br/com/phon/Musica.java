package br.com.phon;

import java.util.Calendar;

public class Musica {
	private long id;
	private long idRemoto;
	private Calendar modificacao;
	private String titulo;
	private String autor;
	private String letra;
	
	//Getters and Setters
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdRemoto() {
		return idRemoto;
	}
	public void setIdRemoto(long idRemoto) {
		this.idRemoto = idRemoto;
	}
	public Calendar getModificacao() {
		return modificacao;
	}
	public void setModificacao(Calendar modificacao) {
		this.modificacao = modificacao;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getLetra() {
		return letra;
	}
	public void setLetra(String letra) {
		this.letra = letra;
	}
}
