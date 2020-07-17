package br.com.phon;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class JDialogCadastro extends JDialog{
	private final GridBagLayout layout;
	private final GridBagConstraints constraints;
	
	public JDialogCadastro(Frame frame, String title) {
		super(frame, title, true);
		
		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		
		setLayout(layout);
		
		JLabel labelTitulo = new JLabel("Título");
		JTextField textFieldTitulo = new JTextField(30);
		JLabel labelAutor = new JLabel("Autor");
		JTextField textFieldAutor = new JTextField(30);
		JLabel labelLetra = new JLabel("Letra");
		JTextArea textAreaLetra = new JTextArea(10, 20);
		JButton btnSalvar = new JButton("Salvar");
		
		Insets insetField = new Insets(0, 2, 10, 2);
		Insets insetLabel = new Insets(0, 3, 0, 3);
		
		constraints.insets = insetLabel;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.SOUTHWEST;
		addComponent(labelTitulo, 0, 0, 1, 1);
		
		constraints.insets = insetField;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(textFieldTitulo, 1, 0, 2, 1);
		
		constraints.insets = insetLabel;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		addComponent(labelAutor, 2, 0, 1, 1);
		
		constraints.insets = insetField;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(textFieldAutor, 3, 0, 2, 1);
		
		constraints.insets = insetLabel;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		addComponent(labelLetra, 4, 0, 1, 1);
		
		constraints.insets = insetField;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		addComponent(new JScrollPane(textAreaLetra), 5, 0, 2, 10);
		
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(btnSalvar, 15, 0, 2, 1);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(frame);
		btnSalvar.addActionListener((event) -> {
			btnSalvar.setEnabled(false);
			final Musica musica = new Musica();
			musica.setTitulo(textFieldTitulo.getText());
			musica.setAutor(textFieldAutor.getText());
			musica.setLetra(textAreaLetra.getText());
			new SwingWorker<Long, Long>() {
				@Override
				protected Long doInBackground() throws Exception {
					MusicaDAO dao = new MusicaDAO();
					long id = dao.gravar(musica);
					return id;
				}
				
				@Override
				protected void done() {
					try {
						long id = get();
						if(id >= 1)
							JOptionPane.showMessageDialog(frame, "Música salva com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE);
						else
							JOptionPane.showMessageDialog(frame, "Erro ao escrever dados", "Erro", JOptionPane.ERROR_MESSAGE);
						btnSalvar.setEnabled(true);
						dispose();
						
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Erro ao escrever dados", "Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}.execute();
		});
	}
	
	public JDialogCadastro(Frame frame, String title, long id) {
		super(frame, title, true);
		
		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		
		MusicaDAO dao = new MusicaDAO();
		final Musica musica = dao.buscar(id);
		if(musica == null) {
			dispose();
			JOptionPane.showMessageDialog(frame, "Música não encontrada");
			return;
		}
		
		setLayout(layout);
		
		JLabel labelTitulo = new JLabel("Título");
		JTextField textFieldTitulo = new JTextField(30);
		JLabel labelAutor = new JLabel("Autor");
		JTextField textFieldAutor = new JTextField(30);
		JLabel labelLetra = new JLabel("Letra");
		JTextArea textAreaLetra = new JTextArea(10, 20);
		JButton btnSalvar = new JButton("Salvar");
		
		
		Insets insetField = new Insets(0, 2, 10, 2);
		Insets insetLabel = new Insets(0, 3, 0, 3);
		
		constraints.insets = insetLabel;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.SOUTHWEST;
		addComponent(labelTitulo, 0, 0, 1, 1);
		
		constraints.insets = insetField;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(textFieldTitulo, 1, 0, 2, 1);
		
		constraints.insets = insetLabel;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		addComponent(labelAutor, 2, 0, 1, 1);
		
		constraints.insets = insetField;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		addComponent(textFieldAutor, 3, 0, 2, 1);
		
		constraints.insets = insetLabel;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		addComponent(labelLetra, 4, 0, 1, 1);
		
		constraints.insets = insetField;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		addComponent(new JScrollPane(textAreaLetra), 5, 0, 2, 10);
		
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		addComponent(btnSalvar, 15, 0, 2, 1);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(frame);
		
		textFieldTitulo.setText(musica.getTitulo());
		textFieldAutor.setText(musica.getAutor());
		textAreaLetra.setText(musica.getLetra());
		
		btnSalvar.addActionListener((event) -> {
			btnSalvar.setEnabled(false);
			musica.setTitulo(textFieldTitulo.getText());
			musica.setAutor(textFieldAutor.getText());
			musica.setLetra(textAreaLetra.getText());
			new SwingWorker<Boolean, Boolean>() {
				@Override
				protected Boolean doInBackground() throws Exception {
					MusicaDAO dao = new MusicaDAO();
					boolean success = dao.editar(musica);
					return success;
				}
				
				@Override
				protected void done() {
					try {
						boolean success = get();
						if(success)
							JOptionPane.showMessageDialog(frame, "Música salva com sucesso!", "Sucesso", JOptionPane.PLAIN_MESSAGE);
						else
							JOptionPane.showMessageDialog(frame, "Erro ao escrever dados", "Erro", JOptionPane.ERROR_MESSAGE);
						btnSalvar.setEnabled(true);
						dispose();
						
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Erro ao escrever dados", "Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			}.execute();
		});
	}
	
	private void addComponent(Component component, int row, int column, int width, int height) {
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		layout.setConstraints(component, constraints);
		add(component);
	}
}