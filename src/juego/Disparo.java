package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Disparo {

	private double x;
	private double y;
	private double diametro;
	private int velocidad;

	private boolean derecha;

	private Image imagen;

	public Disparo(double x, double y, boolean derecha) {

		this.x = x;
		this.y = y;
		this.derecha = derecha;

		this.diametro = 20;
		this.velocidad = 5;

		this.imagen = Herramientas.cargarImagen("img/disparo1.png");
	}

	public void mover() {

		if (derecha) {
			x += velocidad;
		} else {
			x -= velocidad;
		}
	}

	public void dibujar(Entorno e) {
		e.dibujarImagen(imagen, x, y, derecha ? 0 : Math.PI, 0.2);
	}
	
	public boolean estaFueraPantalla() {
		return x < -200 || x > 1200;
	}

	public double getPosicionX() {
		return x;
	}

	public double getPosicionY() {
		return y;
	}

	public double getDiametro() {
		return diametro;
	}
}