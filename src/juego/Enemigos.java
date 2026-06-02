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

	public void mover(Isla[] islas) {

		double viejoX = x;

		if (vaDerecha) {
			x += velocidad;
		} else {
			x -= velocidad;
		}

		for (int i = 0; i < islas.length; i++) {

			if (islas[i] != null &&
				colisionaConIsla(islas[i])) {

				x = viejoX; // vuelve atrás
				break;
			}
		}

		// animación
		contadorAnimacion++;

		if (contadorAnimacion >= 10) {
			frameActual = (frameActual + 1) % 3;
			contadorAnimacion = 0;
		}
	}
	
	public void moverConMapaIzquierda() {
	    this.x -= 1;
	}
	
	public void moverConMapaDerecha() {
	    this.x += 1;
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

	public static Enemigos generarEnemigo() {

		Random r = new Random();

		int y = 150 + r.nextInt(300);

		if (r.nextBoolean()) {
			return new Enemigos(-50, y, true);
		}

		return new Enemigos(850, y, false);
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