package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Poder {
    private double x;
    private double y;

    private int ancho = 40;
    private int alto = 40;

    private Image[] frames;

    private int frameActual;
    private int contadorAnimacion;

    public Poder(double x, double y) {
        this.x = x;
        this.y = y;

        frames = new Image[4];

        frames[0] = Herramientas.cargarImagen("img/orbe1.png");
        frames[1] = Herramientas.cargarImagen("img/orbe2.png");
        frames[2] = Herramientas.cargarImagen("img/orbe3.png");
        frames[3] = Herramientas.cargarImagen("img/orbe3.png");

        frameActual = 0;
        contadorAnimacion = 0;
    }

    public void dibujar(Entorno entorno) {
        contadorAnimacion++;

        if (contadorAnimacion >= 10) {
            frameActual = (frameActual + 1) % 4;
            contadorAnimacion = 0;
        }
        entorno.dibujarImagen(frames[frameActual], x, y, 0, 1);
    }

    public boolean colisionaConPrincesa(Princesa princesa) {
        return this.x + this.ancho / 2 >= princesa.getX() - princesa.getAncho() / 2 && this.x - this.ancho / 2 <= princesa.getX() + princesa.getAncho() / 2 && this.y + this.alto / 2 >= princesa.getY() - princesa.getAlto() / 2  && this.y - this.alto / 2 <= princesa.getY() + princesa.getAlto() / 2;
    }
    
    public void moverConMapaIzquierda() {
        x -= 3;
    }

    public void moverConMapaDerecha() {
        x += 3;
    }
    
    public double getY() {
    	return this.y;
    }
    
    public void setY(double y) {
    	this.y = y;
    }
}