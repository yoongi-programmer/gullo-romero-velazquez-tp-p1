package juego;

import java.awt.Color;
import java.awt.Rectangle;

import entorno.Entorno;

public class Isla {
	private Rectangle area;
	
	Isla(int x, int y, int ancho, int alto) {
		this.area = new Rectangle(x - ancho/2, y - alto/2, ancho, alto);
	}
	
	public void dibujar(Entorno e) {
        // Pide: x, y, ancho, alto, ángulo, color
        e.dibujarRectangulo(this.area.x + this.area.width/2, this.area.y + this.area.height/2, this.area.width, this.area.height, 0, Color.green);
    }
	
	public void moverIslas(double dir) {
        this.area.x += dir;
    }

    public double getX() {
        return this.area.x + this.area.width/2.0;
    }

    public double getY() {
        return this.area.y + this.area.height/2.0;
    }
    public double getAncho() {
        return this.area.width;
    }
    
    public double getAlto() {
        return this.area.height;
    }
    
    public Rectangle getArea() {
    	return this.area;
    }
}
