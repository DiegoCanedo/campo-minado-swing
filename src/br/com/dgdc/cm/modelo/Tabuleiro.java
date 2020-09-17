package br.com.dgdc.cm.modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObserver {
	private final int linhas;
	private final int colunas;
	private final int minas;

	
	private final List<Campo> campos = new ArrayList<>();
	private final List<Consumer<ResultadoEvento>> observers = new ArrayList<>();

	public Tabuleiro(int linha, int coluna, int minas) {
		this.linhas = linha;
		this.colunas = coluna;
		this.minas = minas;

		gerarCampos();
		associarVizinhos();
		sortearMinas();
	}
	
	public void paraCadaCampo(Consumer<Campo> funcao) {
		campos.forEach(funcao);
	}

	public void registrarObservers(Consumer<ResultadoEvento> observer) {
		observers.add(observer);
	}

	private void notificarObservers(boolean resultado) {
		observers.stream().forEach(o -> o.accept(new ResultadoEvento(resultado)));
	}

	public void abrir(int linha, int coluna) {
		campos.parallelStream()
		.filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
		.findFirst()
		.ifPresent(c -> c.abrir());
	}

	public void alternarMarcacao(int linha, int coluna) {
		campos.parallelStream().filter(c -> c.getLinha() == linha && c.getColuna() == coluna).findFirst()
				.ifPresent(c -> c.alternarMarcacao());
	}

	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(c -> c.objetivoAlcancado());
	}

	private void sortearMinas() {
		long minasArmadas = 0;
		Predicate<Campo> minado = c -> c.isMinado();

		do {

			minasArmadas = campos.stream().filter(minado).count();

			int aleatorio = (int) (Math.random() * campos.size());

			campos.get(aleatorio).minarCampo();

		} while (minasArmadas < minas);

	}

	private void associarVizinhos() {
		for (Campo c1 : campos) {
			for (Campo c2 : campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}

	private void gerarCampos() {
		for (int linha = 0; linha < this.linhas; linha++) {
			for (int coluna = 0; coluna < this.colunas; coluna++) {
				Campo campo = new Campo(linha, coluna);
				campo.registrarObservers(this);
				this.campos.add(campo);
			}
		}
	}

	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}
	
	

	@Override
	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		if (evento == CampoEvento.EXPLODIR) {
			mostrarMinas();
			notificarObservers(false);
		} else if (objetivoAlcancado()) {
			notificarObservers(true);
		}

	}

	private void mostrarMinas() {
		campos.stream()
		.filter(c -> c.isMinado())
		.filter(c -> !c.isMarcado())
		.forEach(c -> c.setAberto(true));
	}
	
	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}


}
