package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Poder {
    private double x;
    private double y;
    private int tipo; // 0 = poder, 1 = vida
    private int ancho = 40;
    private int alto = 40;
    private Image[] frames;
    private int frameActual;
    private int contadorAnimacion;    // Contador para controlar la velocidad de la animación.

    public Poder(double x, double y) {
        this.x = x;
        this.y = y;     
        this.tipo = (int)(Math.random() * 2);        // 50% de probabilidad para cada uno
        frames = new Image[4];

        if (tipo == 0) {
	        frames[0] = Herramientas.cargarImagen("img/orbe1.png");
	        frames[1] = Herramientas.cargarImagen("img/orbe2.png");
	        frames[2] = Herramientas.cargarImagen("img/orbe3.png");
	        frames[3] = Herramientas.cargarImagen("img/orbe4.png");

        } else {            // Vida extra
            frames[0] = Herramientas.cargarImagen("img/cora1.png");
            frames[1] = Herramientas.cargarImagen("img/cora2.png");
            frames[2] = Herramientas.cargarImagen("img/cora3.png");
            frames[3] = Herramientas.cargarImagen("img/cora4.png");
        }
        frameActual = 0;
        contadorAnimacion = 0;
    }

    public void dibujar(Entorno entorno) {
        contadorAnimacion++;
        if (contadorAnimacion >= 10) {        // Cada 10 ciclos cambia la imagen.
            frameActual = (frameActual + 1) % 4;// Avanza al siguiente frame. El % 4 hace que vuelva a empezar al llegar al último.
            contadorAnimacion = 0;
        }
        // Dibuja la imagen actual.
        double escala;
        if (tipo == 0) {
            escala = 1;
        } else {
            escala = 2.5;
        }
        entorno.dibujarImagen(frames[frameActual], x, y, 0, escala);
    }

    // Detecta si la princesa tocó el poder.
    public boolean colisionaConPrincesa(Princesa princesa) {
        return this.x + this.ancho / 2 >= princesa.getX() - princesa.getAncho() / 2 && this.x - this.ancho / 2 <= princesa.getX() + princesa.getAncho() / 2 && this.y + this.alto / 2 >= princesa.getY() - princesa.getAlto() / 2 && this.y - this.alto / 2 <= princesa.getY() + princesa.getAlto() / 2;
    }

    public void moverConMapa(double direccion) {
        this.x += direccion;
    }
    
    //--------- Setters ----------------
    public void setX(double x) {
    	this.x = x;
    }
    public void setY(double y) {
    	this.y = y;
    }
    //--------- Getters ----------------
    public double getY() {
    	return this.y;
    }
    public double getX() {
    	return this.x;
    }
    public int getTipo() {
        return tipo;
    }
}