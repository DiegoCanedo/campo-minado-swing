package br.com.dgdc.cm.modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;


public class Campo {

	private final int linha;
	private final int coluna;

	private boolean aberto;
	private boolean minado;
	private boolean marcado;

	private List<Campo> vizinhos = new ArrayList<Campo>();
	private List<CampoObserver> observers = new ArrayList<CampoObserver>();

	Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public void registrarObservers(CampoObserver observer) {
		observers.add(observer);
	}
	
	private void notificarObservers(CampoEvento evento) {
		observers.stream()
		.forEach(o -> o.eventoOcorreu(this, evento));
	}

	boolean adicionarVizinho(Campo vizinho) {

		boolean linhaDiferente = linha != vizinho.linha;
		boolean colunaDiferente = coluna != vizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;

		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna = Math.abs(coluna - vizinho.coluna);
		int deltaGeral = deltaLinha + deltaColuna;

		if (deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else if (deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else
			return false;
	}

	public void alternarMarcacao() {
		if (!aberto) {
			marcado = !marcado;
			
			if(marcado)
				notificarObservers(CampoEvento.MARCAR);
			else
				notificarObservers(CampoEvento.DESMARCAR);
		}
	}

	public boolean abrir() {

		if (!aberto && !marcado) {
			
			if (minado) {
				notificarObservers(CampoEvento.EXPLODIR);
				return true;
			}
			
			setAberto(true);

			if (vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}

	void minarCampo() {
		minado = true;
	}

	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;

		return desvendado || protegido;
	}

	public int minasNaVizinhanca() {
		return (int)vizinhos.stream().filter(v -> v.minado).count();
	}

	void reiniciar() {
		marcado = false;
		aberto = false;
		minado = false;
		notificarObservers(CampoEvento.REINICIAR);
	}


	public boolean isMarcado() {
		return marcado;
	}

	public boolean isAberto() {
		return aberto;
	}

	public boolean isMinado() {
		return minado;
	}

	public boolean isFechado() {
		return !isAberto();
	}
	
	public void setAberto(boolean aberto)
	{
		this.aberto = aberto;
		
		if(this.aberto)
			notificarObservers(CampoEvento.ABRIR);
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

}
