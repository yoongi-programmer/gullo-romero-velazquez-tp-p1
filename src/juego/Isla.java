package juego;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;

import entorno.Entorno;
import entorno.Herramientas;

public class Isla {
	private Image imagen;
	Rectangle area;
	
	Isla(int x, int y, int ancho, int alto) {
        this.area = new Rectangle(x - ancho/2, y - alto/2, ancho, alto);
        Image imagenOriginal = Herramientas.cargarImagen("img/islas.png");
        
        // 2. Escalamos/estiramos la imagen para que mida EXACTAMENTE "ancho" x "alto"
        this.imagen = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_DEFAULT);
	}
	
	public void dibujar(Entorno e) {
        // Pide: x, y, ancho, alto, ángulo, color
		e.dibujarImagen(this.imagen, this.area.x + this.area.width/2, this.area.y + this.area.height/2, 0, 1.0);
		//e.dibujarRectangulo(this.area.x + this.area.width/2, this.area.y + this.area.height/2, this.area.width, this.area.height, 0, Color.green);
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
