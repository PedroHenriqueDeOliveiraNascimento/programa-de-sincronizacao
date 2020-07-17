package br.com.phon;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DialogSetterDB extends JDialog{
	private final GridBagLayout layout;
	private final GridBagConstraints constraints;
	
	public DialogSetterDB(JFrame frame, String title) {
		super(frame, title, true);
		
		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		setLayout(layout);
		
		final Preferences prefs = Preferences.userRoot();
		
		JLabel lbUsuario = new JLabel("Usuário");
		JTextField tfUsuario = new JTextField(prefs.get("user", "root"), 30);
		JLabel lbSenha = new JLabel("Senha");
		JPasswordField tfSenha = new JPasswordField(prefs.get("senha", ""), 30);
		JLabel lbHost = new JLabel("Endereço");
		JTextField tfHost = new JTextField(prefs.get("host", "localhost"), 30);
		JButton btnSalvar = new JButton("Salvar");
		
		addComponent(lbUsuario, 0, 0, 2, 1);
		addComponent(tfUsuario, 1, 0, 2, 1);
		addComponent(lbSenha, 2, 0, 2, 1);
		addComponent(tfSenha, 3, 0, 2, 1);
		addComponent(lbHost, 4, 0, 2, 1);
		addComponent(tfHost, 5, 0, 2, 1);
		addComponent(btnSalvar, 6, 0, 1, 1);
		
		btnSalvar.addActionListener((e) -> {
			prefs.put("user", tfUsuario.getText());
			prefs.put("senha", new String(tfSenha.getPassword()));
			prefs.put("host", tfHost.getText());
			dispose();
			JOptionPane.showMessageDialog(frame, "Salvo com sucesso.");
		});
		
		pack();
		setLocationRelativeTo(frame);
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
