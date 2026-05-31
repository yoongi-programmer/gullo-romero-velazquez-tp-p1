package juego;

import entorno.Entorno;
import entorno.Herramientas;

import java.awt.Image;
import java.util.Random;

public class Enemigos {

	private double x;
	private double y;
	private double velocidad;

	private boolean vaDerecha;

	private int ancho = 45;
	private int alto = 45;

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

	public void mover() {

		if (vaDerecha) {
			x += velocidad;
		} else {
			x -= velocidad;
		}

		// Cambiar frame cada 10 ticks
		contadorAnimacion++;

		if (contadorAnimacion >= 10) {
			frameActual++;
			contadorAnimacion = 0;

			if (frameActual >= 3) {
				frameActual = 0;
			}
		}
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

	public static Enemigos generarEnemigo() {

		Random r = new Random();

		int y = 100 + r.nextInt(300);

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