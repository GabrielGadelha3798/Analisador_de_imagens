package lab_2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

public class LabelProcessor {
	ImageProcessor ip;
	public int i = 1;
	public int h;
	public int w;
	public int value;
	HashMap<Integer, ArrayList<Point>> mapaDeLista = new HashMap<Integer, ArrayList<Point>>();
	ArrayList<Double> listaCentroDeMassaX = new ArrayList<Double>();
	ArrayList<Double> listaCentroDeMassaY = new ArrayList<Double>();
	ArrayList<Double> listaArea = new ArrayList<Double>();
	ArrayList<Double> listaCircularidade = new ArrayList<Double>();
	ArrayList<Point> listaCopia = new ArrayList<Point>();
	ArrayList<Double> listaPerimetro = new ArrayList<Double>();
	ArrayList<Boolean> listaBorda = new ArrayList<Boolean>();
	ImageProcessor RGB;

	public LabelProcessor(ImageProcessor ip, HashMap<Integer, ArrayList<Point>> mapaDeLista) {
		// TODO Auto-generated constructor stub
		this.mapaDeLista = mapaDeLista;
		this.ip = ip;
		w = ip.getWidth(); // pega a largura
		h = ip.getHeight(); // pega a altura
	}

	public void perimetroeBorda() {

		double perimetro = 0;
		boolean borda;
		
		// iteração que seleciona chave do HashMap
		for (int i = 1; i <= mapaDeLista.size(); i++) {

			// iteração que varre imagem
			listaCopia = mapaDeLista.get(i);
			
			//caso não possua nenhum pixel na borda a variável
			//permanecerá false até o fim da iteração da chave
			borda = false;
			
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {

					// apenas analisa pixels do conexo i
					if (listaCopia.contains(new Point(x, y))) {

						// verifica pixels na borda para calculo do perimetro
						if (x == 0 || x == w - 1 || y == 0 || y == h - 1) {
							perimetro = perimetro + 1;
							
							//borda só será true se o conexo possuir pixel na borda
							borda = true;
							
							if ((x == 0 && y == 0) || (x == 0 && y == h - 1) || (x == w - 1 && y == 0)
									|| (x == w - 1 && y == h - 1)) {
								perimetro = perimetro + 1;
							}
						}

						// Analise de vizinhanca conec4 para calculo do perímetro
						for (int a = -1; a < 2; a++) {

							// Verifica se possui vizinhos cinzas
							if ((a != 0) && x + a >= 0 && x + a < w) {

								value = ip.getPixel(x + a, y);
								if (value == 100) {
									perimetro = perimetro + 1;
								}
							}

							if ((a != 0) && y + a >= 0 && y + a < h) {

								value = ip.getPixel(x, y + a);
								if (value == 100) {
									perimetro = perimetro + 1;
								}
							}
						} // fim da analise dos vizinhos
					}

				}
			} // fim da varredura da imagem
			listaBorda.add(borda);
			listaPerimetro.add(perimetro);
			perimetro = 0;
		} // fim da analise do Mapa completo
	}

	public void area() {
		double area;

		for (int i = 1; i <= mapaDeLista.size(); i++) {
			listaCopia = mapaDeLista.get(i);
			area = listaCopia.size();
			listaArea.add(area);

		}
		// system.out.println("Areas:" + listaArea);
	}

	public void centroDeMassa() {
		double centroXsoma;
		double centroYsoma;
		double value;
		double centroX;
		double centroY;

		for (int i = 1; i <= mapaDeLista.size(); i++) {
			listaCopia = mapaDeLista.get(i);
			centroXsoma = 0;
			centroYsoma = 0;

			for (int a = 0; a < listaCopia.size(); a++) {
				centroXsoma = centroXsoma + listaCopia.get(a).x;
				centroYsoma = centroYsoma + listaCopia.get(a).y;
			}
			value = (double) listaCopia.size();
			centroX = (centroXsoma / value);
			centroY = (centroYsoma / value);
			listaCentroDeMassaX.add(centroX);
			listaCentroDeMassaY.add(centroY);

		}

	}

	public void circularidade() {
		double circularidade;
		double pi = 3.14159265359;
		double auxiliar1;
		double auxiliar2;
		for (int i = 0; i < mapaDeLista.size(); i++) {

			auxiliar1 = listaArea.get(i);
			auxiliar2 = listaPerimetro.get(i);
			circularidade = 4 * pi * (auxiliar1 / (auxiliar2 * auxiliar2));
			listaCircularidade.add(circularidade);
		}
		// system.out.println("Circularidades: " + listaCircularidade);
	}

	// pintar fundo de cinza para analisar o perimetro
	public void pintarFundo(int corOriginal, int corNova) {
		ArrayList<Point> ListaParaAnaliseDeVizinhos = new ArrayList<Point>();
		int x = 0;
		int y = 0;
		int[][] Analisada;
		Analisada = new int[w][h];
		ListaParaAnaliseDeVizinhos.add(new Point(x, y)); // adicionar ponto atual a lista/fila
		Analisada[x][y] = 1;

		while (ListaParaAnaliseDeVizinhos.isEmpty() != true) {

			// Salva a posição x,y do pixel que está na posição 0 da lista de vizinhos.
			x = ListaParaAnaliseDeVizinhos.get(0).x;
			y = ListaParaAnaliseDeVizinhos.get(0).y;

			// Pintar o Conexo
			ip.set(x, y, corNova);

			for (int a = -1; a < 2; a++) {
				for (int b = -1; b < 2; b++) {

					// verifica condições de borda e verifica se pixel já foi analisado
					// anteriormente
					if ((a == 0 && b == 0) || x + a < 0 || x + a >= w || y + b < 0 || y + b >= h
							|| Analisada[x + a][y + b] == 1) {
						continue;
					} else {

						value = ip.getPixel(x + a, y + b);
						if (value == corOriginal) {
							// Caso o vizinho seja preto, adicionamos ele as listas e contamos como
							// analisada
							ListaParaAnaliseDeVizinhos.add(new Point(x + a, y + b));
							Analisada[x + a][y + b] = 1;
						}

					}
				}
			} // fim da analise dos vizinhos

			ListaParaAnaliseDeVizinhos.remove(0);
		} // fim do laço do while

	}

	public void apagarBordas(ImageProcessor RGB) {
		this.RGB = RGB;
		int value;
		ArrayList<Point> listaParaAnaliseDeVizinhos = new ArrayList<Point>();
		int[][] analisada;
		analisada = new int[w][h];

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (x == 0 || x == w - 1 || y == 0 || y == h - 1) {

					value = ip.getPixel(x, y);

					if (value == 0 && analisada[x][y] == 0) {
						listaParaAnaliseDeVizinhos.add(new Point(x, y)); // adicionar ponto atual a lista/fila
						analisada[x][y] = 1;

						while (listaParaAnaliseDeVizinhos.isEmpty() != true) {

							// Salva a posição x,y do pixel que está na posição 0 da lista de vizinhos.
							int Xconexo = listaParaAnaliseDeVizinhos.get(0).x;
							int Yconexo = listaParaAnaliseDeVizinhos.get(0).y;

							// Pintar o Conexo
							ip.set(Xconexo, Yconexo, 255);
							RGB.set(Xconexo, Yconexo, 16777215);
							for (int a = -1; a < 2; a++) {
								for (int b = -1; b < 2; b++) {

									// verifica condições de borda e verifica se pixel já foi analisado
									// anteriormente
									if ((a == 0 && b == 0) || Xconexo + a < 0 || Xconexo + a >= w || Yconexo + b < 0
											|| Yconexo + b >= h || analisada[Xconexo + a][Yconexo + b] == 1) {
										continue;
									} else {

										value = ip.getPixel(Xconexo + a, Yconexo + b);
										if (value == 0) {
											// Caso o vizinho seja preto, adicionamos ele as listas e contamos como
											// analisada
											listaParaAnaliseDeVizinhos.add(new Point(Xconexo + a, Yconexo + b));
											analisada[Xconexo + a][Yconexo + b] = 1;
										}

									}
								}
							} // fim da analise dos vizinhos

							listaParaAnaliseDeVizinhos.remove(0);
						} // fim do laço do while

					}

				}
			} // fim da varredura da imagem
		}
	}

	public void tabelas() {

		ResultsTable tabela = new ResultsTable();
		for (i = 0; i < mapaDeLista.size(); i++) {
			tabela.incrementCounter();
			
			tabela.addValue("Indice", i + 1);
			
			
			tabela.addValue("Está na Borda", listaBorda.get(i).toString());
			
			tabela.addValue("centro massa X", listaCentroDeMassaX.get(i));

			tabela.addValue("centro de massa Y", listaCentroDeMassaY.get(i));

			tabela.addValue("Area", listaArea.get(i));
 
			tabela.addValue("Perimetro", listaPerimetro.get(i));

			tabela.addValue("Circularidade", listaCircularidade.get(i));
		}
		tabela.show("Tabela de resultados RGB com Borda");
	}

	public void tabelaresumo() {
		ResultsTable tabelaresumo = new ResultsTable();

		tabelaresumo.incrementCounter();

		tabelaresumo.addValue("Número de conexos", mapaDeLista.size());
 
		tabelaresumo.addValue("Média centro massa X", funcaoMedia(listaCentroDeMassaX));
		
		tabelaresumo.addValue("Média centro de massa Y", funcaoMedia(listaCentroDeMassaY));
		
		tabelaresumo.addValue("Desvio Padrao CMX", desvioPadrao(listaCentroDeMassaX));
		
		tabelaresumo.addValue("Desvio Padrao CMY", desvioPadrao(listaCentroDeMassaY));
		
		tabelaresumo.addValue("Média Area", funcaoMedia(listaArea));
		
		tabelaresumo.addValue("Desvio Padrao Area", desvioPadrao(listaArea));

		tabelaresumo.addValue("Média Perimetro", funcaoMedia(listaPerimetro));
		
		tabelaresumo.addValue("Desvio Padrao Perimetro", desvioPadrao(listaPerimetro));

		tabelaresumo.addValue("Média Circularidade", funcaoMedia(listaCircularidade));
		
		tabelaresumo.addValue("Desvio Padrao Circularidade", desvioPadrao(listaCircularidade));
		
		tabelaresumo.show("Tabela resumo RGB sem Borda");
	}

	public double funcaoMedia(ArrayList<Double> valores) {

		double soma = 0;
		for (int i = 0; i < valores.size(); i++) {
			soma = soma + valores.get(i);
		}
		soma = soma/valores.size();
		return soma;
	}

	public double desvioPadrao(ArrayList<Double> valores) {
		double desvioPadrao = 0;
		double valorMedio = funcaoMedia(valores);
		for (int i = 0; i < valores.size(); i++) {
			desvioPadrao = desvioPadrao + (valores.get(i) - valorMedio)*(valores.get(i) - valorMedio);
		}
		desvioPadrao = Math.sqrt(desvioPadrao/valores.size());
		return desvioPadrao;
	}
}
