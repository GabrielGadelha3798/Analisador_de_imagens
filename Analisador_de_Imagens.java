//Gabriel Galdino Gadelha
//Regina Maria Silva Gomes

package lab_2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Lab_02_Regina_Gabriel implements PlugInFilter {
	public int i = 1;
	public int h;
	public int w;
	public int value;
	public ImagePlus RGBcomb;	// declaração das imagens criadas
	public ImagePlus PBcomb;	//
	public ImagePlus RGBsemb;	//
	public ImagePlus PBsemb;	//
	public ImagePlus imp1;		//imagem que será aberta "original"
	
	ImageProcessor RGB;			
	
	// cria uma lista de listas para salvar informações futuras sobre os conexos
	HashMap<Integer, ArrayList<Point>> mapaDeListas = new HashMap<Integer, ArrayList<Point>>();

	public Lab_02_Regina_Gabriel() {

		// abre a janela para a seleção da imagem
		OpenDialog op = new OpenDialog("selecione a imagem:"); 
		String path = op.getPath(); // pega o caminho da imagem
		System.out.println(path); // imprime o caminho da imagem
		// abre a imagem com o caminho da variavel path
		imp1 = new Opener().openImage(path);
		w = imp1.getWidth(); // varre a largura
		h = imp1.getHeight(); // varre a altura
		imp1.show();
		
		//criação de todas as 4 imagens em branco
		RGBcomb = NewImage.createRGBImage("RGB com borda", w, h, 1, NewImage.FILL_WHITE);
		RGBsemb = NewImage.createRGBImage("RGB sem borda", w, h, 1, NewImage.FILL_WHITE);
		PBcomb = NewImage.createByteImage("Binária com borda", w, h, 1, NewImage.FILL_WHITE);
		PBsemb = NewImage.createByteImage("Binária sem borda", w, h, 1, NewImage.FILL_WHITE);
		PBcomb = imp1.duplicate();
		PBcomb.show();
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		// TODO Auto-generated method stub
		this.imp1 = imp;	//determina que a imagem original aberta será usada no processo ip
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) { // função que analiza e modifica a imagem 
		// TODO Auto-generated method stub
		
		ImageLabeler Labelercomb = new ImageLabeler(ip,RGBcomb);
		ImageLabeler Labelersemb = new ImageLabeler(ip,RGBsemb);
		
		//CHAMANDO funções que analisam PB COM BORDA e pintam RGB COM BORDA
		Labelercomb.analisar();
		Labelercomb.pintar();
		
		//gerando imagem RGB sem borda.
		RGBsemb = RGBcomb.duplicate();
		RGB = RGBsemb.getProcessor();
		
		//Importando mapa da classe ImageLabeler
		mapaDeListas = Labelercomb.exportarMapa();
		
		LabelProcessor Processorcomb = new LabelProcessor(ip,mapaDeListas);
		
		//pintar fundo para analise de perimetro
		Processorcomb.pintarFundo(255,100);
		
		//funções de extração de informação e geração de tabela
		Processorcomb.perimetroeBorda();
		Processorcomb.centroDeMassa();
		Processorcomb.area();
		Processorcomb.circularidade();
		Processorcomb.tabelas();
		
		//função para apagar bordas das imagens RGB e PB
		Processorcomb.apagarBordas(RGB);
		
		//funções para reefetuar analises e gerar tabela resumo
		Labelersemb.analisar();
		mapaDeListas = Labelersemb.exportarMapa();
		LabelProcessor Processorsemb = new LabelProcessor(ip,mapaDeListas);
		
		Processorsemb.perimetroeBorda();
		Processorsemb.centroDeMassa();
		Processorsemb.area();
		Processorsemb.circularidade();
		Processorsemb.pintarFundo(100,255);
		Processorsemb.tabelaresumo();
		
		//atualizar e abrir imagens RGB
		RGBsemb.updateAndDraw();
		RGBsemb.show();
		RGBcomb.updateAndDraw();
		RGBcomb.show();
		}

}