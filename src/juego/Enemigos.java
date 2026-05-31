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

	private Image imagen;

	public Enemigos(double x, double y, boolean vaDerecha) {

		this.x = x;
		this.y = y;

		this.vaDerecha = vaDerecha;

		this.velocidad = 0.8;

		this.imagen = Herramientas.cargarImagen("img/enemigo1_IZ.png");
	}

	public void mover() {

		if (vaDerecha) {
			x += velocidad;
		} else {
			x -= velocidad;
		}
	}

	public void dibujar(Entorno entorno) {
		entorno.dibujarImagen(imagen, x, y, 0, 2);
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

		// Aparece por la izquierda
		if (r.nextBoolean()) {
			return new Enemigos(-50, y, true);
		}

		// Aparece por la derecha
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