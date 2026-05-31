package juego;

import java.awt.Color;
import java.awt.Rectangle;

import entorno.Entorno;

public class Isla {
	private Rectangle area;
	
	Isla(int x, int y, int ancho, int alto) {
		this.area = new Rectangle(x,y,ancho,alto);
	}
	
	public void dibujar(Entorno e) {
        // Pide: x, y, ancho, alto, ángulo, color
        e.dibujarRectangulo(this.area.x, this.area.y, this.area.width, this.area.height, 0, Color.green);
    }
	public void dibujarP(Entorno e) {
        // Pide: x, y, ancho, alto, ángulo, color
        e.dibujarRectangulo(this.area.x, this.area.y, this.area.width, this.area.height, 0, Color.PINK);
    }
	
	public void moverDerecha() {
        this.area.x += 3; // 3 píxeles por tick (ajustá la velocidad como quieras)
    }
	public void moverIzquierda() {
        this.area.x -= 3;
    }

    public double getX() {
        return this.area.x;
    }
}
