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
	
	public void moverDerecha() {
        this.area.x += 3; // 3 píxeles por tick (ajustá la velocidad como quieras)
    }
	public void moverIzquierda() {
        this.area.x -= 3;
    }

    public double getX() {
        return this.area.x;
    }
    public double getY() {
        return this.area.y;
    }
    public double getAncho() {
        return this.area.width;
    }
    public double getAlto() {
        return this.area.height;
    }
}
