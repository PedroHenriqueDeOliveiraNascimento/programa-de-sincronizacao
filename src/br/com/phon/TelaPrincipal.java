package br.com.phon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.JProgressBar;

public class TelaPrincipal extends JPanel {
	public TelaPrincipal(JFrame frame) {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		InputStream url = this.getClass().getResourceAsStream("/imagens/quelea-logo-short-full.png");
		try {
			frame.setIconImage(ImageIO.read(url));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JTable table = new JTable();
		
		JPopupMenu popupmenu = new JPopupMenu();
		JMenuItem itemEditar = new JMenuItem("Editar");
		itemEditar.addActionListener((e) -> {
			new JDialogCadastro(frame, "Adicionar Música", (long) table.getValueAt(table.getSelectedRow(), 0)).setVisible(true);
			listarMusicas(table);
		});
		JMenuItem itemExcluir = new JMenuItem("Remover");
		itemExcluir.addActionListener((e) -> {
			long id = (long) table.getValueAt(table.getSelectedRow(), 0);
			String musica = (String) table.getValueAt(table.getSelectedRow(), 1);
			int response = JOptionPane.showConfirmDialog(frame, "Você tem certeza que deseja excluir " + musica + "?", "Excluir Música", JOptionPane.YES_NO_OPTION);
			if(response == JOptionPane.OK_OPTION) {
				new SwingWorker<Boolean, Boolean>(){
					@Override
					protected Boolean doInBackground() throws Exception {
						MusicaDAO dao = new MusicaDAO();
						return dao.remover(id);
					}
					
					@Override
					protected void done() {
						try {
							if(get())
								JOptionPane.showMessageDialog(frame, "Registro excluido com sucesso");
							listarMusicas(table);
						} catch (HeadlessException | InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}
				}.execute();
			}
		});
		popupmenu.add(itemEditar);
		popupmenu.add(itemExcluir);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(table), BorderLayout.CENTER);
		table.setComponentPopupMenu(popupmenu);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu menuArquivo = new JMenu("Arquivo");
		menuBar.add(menuArquivo);
		JMenuItem itemAdicionar = new JMenuItem("Adicionar");
		itemAdicionar.addActionListener((event) -> {
			new JDialogCadastro(frame, "Adicionar Música").setVisible(true);
			listarMusicas(table);
		});
		JMenuItem itemBancoRemoto = new JMenuItem("Configurar Banco Remoto");
		itemBancoRemoto.addActionListener((e) -> {
			new DialogSetterDB(frame, "Configurar banco remoto").setVisible(true);
		});
		menuArquivo.add(itemAdicionar);
		menuArquivo.add(itemBancoRemoto);
		JMenu menuSincronizacao = new JMenu("Sincronização");
		JMenuItem itSincrozar = new JMenuItem("Sincronizar Agora");
		itSincrozar.addActionListener((event) -> {
			final JDialog dlg = new JDialog(frame, "Carregando...", true);
			JProgressBar dpb = new JProgressBar(0, 500);
			dpb.setIndeterminate(true);
			dlg.add(BorderLayout.CENTER, dpb);
			dlg.add(BorderLayout.NORTH, new JLabel("Carregando..."));
			dlg.setSize(300, 75);
			dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dlg.setLocationRelativeTo(frame);
			new Thread() {
				public void run() {
					dlg.setVisible(true);
				};
			}.start();
			SwingWorker worker = new SwingWorker<Boolean, Boolean>(){
				@Override
				protected Boolean doInBackground() throws Exception {
					dlg.setVisible(true);
					new Sincronizador();
					return true;
				}
				
				protected void done() {
					try {
						dlg.dispose();
						listarMusicas(table);
						if(get())
							JOptionPane.showMessageDialog(frame, "Sincronizado com sucesso!");
						else
							JOptionPane.showMessageDialog(frame, "Erro ao sincronizar", "ERRO", JOptionPane.ERROR_MESSAGE);
					} catch (HeadlessException | InterruptedException | ExecutionException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Código do erro:\n" + e.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			worker.execute();
		});
		menuSincronizacao.add(itSincrozar);
		menuBar.add(menuSincronizacao);
		
		listarMusicas(table);
	}

	private void listarMusicas(JTable table) {
		new SwingWorker<List<Musica>, List<Musica>>(){
			@Override
			protected List<Musica> doInBackground() throws Exception {
				return new MusicaDAO().buscar();
			}
			
			protected void done() {
				try {
					table.setModel(new MusicasModel(get()));
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			};
			
		}.execute();
	}
}

class MusicasModel extends AbstractTableModel {
	
	private List<Musica> musicas;

	public MusicasModel(List<Musica> musicas) {
		this.musicas = musicas;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return musicas.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Musica musica = musicas.get(rowIndex);
		return columnIndex == 1 ? musica.getTitulo() + " - " + musica.getAutor() : musica.getId() ;
	}
	
	@Override
	public String getColumnName(int column) {
		return column == 1 ? "Música" : "Id";
	}
	
}