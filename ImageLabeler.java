package lab_2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ImageLabeler {
	// variáveis que auxiliam a análise
	public int i = 1;
	public int h;
	public int w;
	public int value;
	int[][] analisada;
	Color CorConexa;

	// Declaração de mapa, lista e imagens para analises
	ArrayList<Color> ListaDeCores = new ArrayList<Color>();
	HashMap<Integer, ArrayList<Point>> mapa = new HashMap<Integer, ArrayList<Point>>();
	ImageProcessor ip;
	ImageProcessor RGB;
	ImagePlus imgRGB;

	public ImageLabeler(ImageProcessor ip, ImagePlus imgRGB) {
		// TODO Auto-generated constructor stub
		this.imgRGB = imgRGB;
		this.ip = ip;
		RGB = imgRGB.getProcessor();
		w = ip.getWidth(); // pega a largura
		h = ip.getHeight(); // pega a altura

		analisada = new int[w][h]; // matriz que analisa se pixel ja foi analisado anteriormente
									// Nao analisado: analisada[x][y] = 0
									// Analisado: analisada [x][y]= 1

	}

	// TODO Auto-generated method stub

	public void analisar() {

		ArrayList<Point> ListaParaAnaliseDeVizinhos = new ArrayList<Point>();
		// lista que guardará no mapa as informações dos conexos
		ArrayList<Point> ListaDeDados;

		// Percorrer pixel a pixel da imagem
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				value = ip.getPixel(x, y); // Pega a cor do pixel

				if (value == 0 && analisada[x][y] == 0) {

					ListaParaAnaliseDeVizinhos.add(new Point(x, y)); // adicionar ponto atual a lista/fila

					ListaDeDados = new ArrayList<Point>(); // serve para resetar a lista de dados. obs: novo conexo
					ListaDeDados.add(new Point(x, y));
					analisada[x][y] = 1;

					while (ListaParaAnaliseDeVizinhos.isEmpty() != true) {

						// Salva a posição x,y do pixel que está na posição 0 da lista de vizinhos.
						int Xconexo = ListaParaAnaliseDeVizinhos.get(0).x;
						int Yconexo = ListaParaAnaliseDeVizinhos.get(0).y;

						// analise dos vizinhos
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
										ListaParaAnaliseDeVizinhos.add(new Point(Xconexo + a, Yconexo + b));
										ListaDeDados.add(new Point(Xconexo + a, Yconexo + b));
										analisada[Xconexo + a][Yconexo + b] = 1;
									}

								}
							}
						} // fim da analise dos vizinhos
							// removemos o pixel da posição 0 da lista, pois sua vizinhança ja foi
							// analisada.
						ListaParaAnaliseDeVizinhos.remove(0);
					} // fim do laço do while
					mapa.put(i, ListaDeDados);
					i++;
				}

			}
		}

	}

	public HashMap<Integer, ArrayList<Point>> exportarMapa() {
		return mapa;
	}

	//função que pinta os componentes conexos
	public void pintar() {
		int cor;
		ArrayList<Point> listaAuxiliar = new ArrayList<Point>();
		analisada = new int[w][h];
		for (int i = 1; i <= mapa.size(); i++) {
			// gerando cor aleatória
			CorConexa = randomColor();
			cor = CorConexa.getRGB();

			listaAuxiliar = mapa.get(i);
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					if (listaAuxiliar.contains(new Point(x, y))) {
						RGB.set(x, y, cor);
					}
				}
			} // fim da varredura da imagem
		}
	}

	//função que gera a cor aleatória
	public Color randomColor() {
		int r;
		int g;
		int b;
		Random aleatorio = new Random();
		Color cor;

		do {
			r = aleatorio.nextInt(255);
			g = aleatorio.nextInt(255);
			b = aleatorio.nextInt(255);

			cor = new Color(r, g, b);
		} while (ListaDeCores.contains(cor) || (r == 255 && b == 255 && g == 255));

		ListaDeCores.add(cor);

		return cor;
	}
}
