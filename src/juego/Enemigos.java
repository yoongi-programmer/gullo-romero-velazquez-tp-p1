package juego;

import entorno.Entorno;
import entorno.Herramientas;

import java.awt.Image;
import java.util.Random;
import java.awt.Rectangle;

public class Enemigos {

	private double x;
	private double y;
	private double velocidad;
	
	private boolean vaDerecha;

	private int ancho = 30;
	private int alto = 30;

	// Animación
	private Image[] framesIzquierda;
	private Image[] framesDerecha;

	private int frameActual;
	private int contadorAnimacion;

	public Enemigos(double x, double y, boolean vaDerecha) {

		this.x = x;
		this.y = y;
		this.vaDerecha = vaDerecha;

		this.velocidad = 0.8;

		framesIzquierda = new Image[3];
		framesDerecha = new Image[3];

		framesIzquierda[0] = Herramientas.cargarImagen("img/enemigo1_IZ.png");
		framesIzquierda[1] = Herramientas.cargarImagen("img/enemigo2_IZ.png");
		framesIzquierda[2] = Herramientas.cargarImagen("img/enemigo3_IZ.png");

		framesDerecha[0] = Herramientas.cargarImagen("img/enemigo1_DE.png");
		framesDerecha[1] = Herramientas.cargarImagen("img/enemigo2_DE.png");
		framesDerecha[2] = Herramientas.cargarImagen("img/enemigo3_DE.png");

		frameActual = 0;
		contadorAnimacion = 0;
	}
	
	public static Enemigos generarEnemigo(Isla[] islas) {

		Random r = new Random();
		// Elegir una isla al azar
		Isla isla = null;
		while (isla == null) {
			isla = islas[r.nextInt(islas.length)];
		}
		// Aparece sobre la isla
		double x = isla.getArea().x + r.nextInt(isla.getArea().width);
		// Un poco arriba de la isla
		double y = isla.getArea().y - 40;
		boolean vaDerecha = r.nextBoolean();
		return new Enemigos(x, y, vaDerecha);
	}

	
	public void mover(Isla[] islas) {

		for (int i = 0; i < islas.length; i++) {
			if (islas[i] != null && vaAChocar(islas[i])) {
				// cambia de dirección
				vaDerecha = !vaDerecha;
				return;
			}
		}
		if (vaDerecha) {
			x += velocidad;
		} else {
			x -= velocidad;
		}
		// animación
		contadorAnimacion++;
		if (contadorAnimacion >= 10) {
			frameActual = (frameActual + 1) % 3;
			contadorAnimacion = 0;
		}
	}
	
	
	public void moverConMapaIzquierda() {
	    this.x -= 3;
	}
	
	public void moverConMapaDerecha() {
	    this.x += 3;
	}


	public boolean colisionDisparoEnemigo(Disparo disparo) {

		double radioDisparo = disparo.getDiametro() / 2.0;
		return disparo.getPosicionX() + radioDisparo >= this.x - ancho / 2
				&& disparo.getPosicionX() - radioDisparo <= this.x + ancho / 2
				&& disparo.getPosicionY() + radioDisparo >= this.y - alto / 2
				&& disparo.getPosicionY() - radioDisparo <= this.y + alto / 2;
	}
	
	
	public boolean colisionaConIsla(Isla isla) {

		Rectangle enemigoRect = new Rectangle((int)(x - ancho/2), (int)(y - alto/2), ancho, alto);
		return enemigoRect.intersects(isla.getArea());
	}

	
	public boolean vaAChocar(Isla isla) {

		Rectangle futuro;
		if (vaDerecha) {
			futuro = new Rectangle((int)(x + velocidad - ancho/2), (int)(y - alto/2), ancho, alto);
		} else {
			futuro = new Rectangle((int)(x - velocidad - ancho/2), (int)(y - alto/2), ancho, alto);
		}
		return futuro.intersects(isla.getArea());
	}
	

	public void dibujar(Entorno entorno) {

		Image imagenActual;
		if (vaDerecha) {
			imagenActual = framesDerecha[frameActual];
		} else {
			imagenActual = framesIzquierda[frameActual];
		}
		entorno.dibujarImagen(imagenActual, x, y, 0, 2);
	}

	
	public boolean fueraDePantalla() {
		return x < -100 || x > 900;
	}

	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getAncho() {
		return ancho;
	}

	public int getAlto() {
		return alto;
	}
}