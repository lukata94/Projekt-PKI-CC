package zts;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

/**
 * Klasa okienka generatora CC.
 * @author £ukasz DŸwigulski Rafa³ Sosnowski
 *
 */
public class OknoGlowne extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	
	/**
	 * Funkcja main.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					OknoGlowne frame = new OknoGlowne();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Tworzy nowy JFrame generatora.
	 */
	public OknoGlowne() {
		setTitle("Zaufana Trzecia Strona");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 179);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JLabel lblNazwaUzytkownika = new JLabel("Nazwa u\u017Cytkownika");
		textField = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField, 7, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField, 157, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField, -10, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNazwaUzytkownika, -6, SpringLayout.WEST, textField);
		JButton btnGenerujKlucze = new JButton("Generuj klucze");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnGenerujKlucze, 58, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnGenerujKlucze, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnGenerujKlucze, -63, SpringLayout.EAST, contentPane);
		btnGenerujKlucze.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Generator gen;
				try {
					gen = new Generator(textField.getText());
					gen.GeneracjaCertyfikatu(textField_1.getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.toString(), "Error",
                            JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		contentPane.add(btnGenerujKlucze);
		
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNazwaUzytkownika, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNazwaUzytkownika, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblNazwaUzytkownika);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblHaso = new JLabel("Has\u0142o u\u017Cytkownika");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblHaso, 25, SpringLayout.SOUTH, lblNazwaUzytkownika);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblHaso, 0, SpringLayout.WEST, lblNazwaUzytkownika);
		contentPane.add(lblHaso);
		
		textField_1 = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnGenerujKlucze, 19, SpringLayout.SOUTH, textField_1);
		sl_contentPane.putConstraint(SpringLayout.NORTH, textField_1, -3, SpringLayout.NORTH, lblHaso);
		sl_contentPane.putConstraint(SpringLayout.WEST, textField_1, 0, SpringLayout.WEST, textField);
		sl_contentPane.putConstraint(SpringLayout.EAST, textField_1, 0, SpringLayout.EAST, textField);
		textField_1.setColumns(10);
		contentPane.add(textField_1);

	}
}
