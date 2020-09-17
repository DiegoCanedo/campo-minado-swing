package br.com.dgdc.cm.visao;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.com.dgdc.cm.modelo.Tabuleiro;

@SuppressWarnings("serial")
public class PainelTabuleiro extends JPanel {

	public PainelTabuleiro(Tabuleiro tabuleiro) {

		setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));

		tabuleiro.paraCadaCampo(c -> add(new BotaoCampo(c)));
		tabuleiro.registrarObservers(e -> {

			SwingUtilities.invokeLater(() -> {
				if (e.isGanhou()) {
					JOptionPane.showMessageDialog(this, "Ganhou :)");
				} else {
					JOptionPane.showMessageDialog(this, "Perdeu :(");
				}
				
				tabuleiro.reiniciar();
			});
		});
	}
}
