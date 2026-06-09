package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Disparo {
    private double x, y;
    private double velocidadX, velocidadY;
    private double diametro = 20;    
    private double angulo;
    private Image imagen;    // Imagen del disparo normal
    private Image[] frames;
    private int frameActual;
    private int contadorAnimacion;
    private boolean especial;

    public Disparo(double x, double y, double destinoX, double destinoY, boolean especial) {
        this.x = x;
        this.y = y;
        this.especial = especial;
        this.angulo = Math.atan2(destinoY - y, destinoX - x);        // Calcula el ángulo entre el origen y el destino
        velocidadX = Math.cos(this.angulo) * 8;
        velocidadY = Math.sin(this.angulo) * 8;

        if (especial) {        // Carga las imágenes correspondientes
            frames = new Image[] { Herramientas.cargarImagen("img/disparoEsp1.png"), Herramientas.cargarImagen("img/disparoEsp2.png")};   // Disparo especial animado
        } else {
            imagen = Herramientas.cargarImagen("img/disparo1.png");            // Disparo normal
        }
    }

    public void mover() {
        x += velocidadX;
        y += velocidadY;

        if (especial && ++contadorAnimacion >= 10) {        	
            frameActual = (frameActual + 1) % 2;            // Cambia entre frame 0 y 1
            contadorAnimacion = 0;
        }
    }

    public void dibujar(Entorno e) {
        if (frames != null) {        // Si tiene frames, es un disparo especial
        	e.dibujarImagen(frames[frameActual], x, y, angulo, 1);
        } else {
        	e.dibujarImagen(imagen, x, y, angulo, 0.2);            // Dibuja el disparo normal
        }
    }

    public boolean colisionaConIsla(Isla isla) {        // Comprueba superposición entre el disparo y la isla
        double radio = diametro / 2;
        return x + radio >= isla.getX() - isla.getAncho() / 2.0 && x - radio <= isla.getX() + isla.getAncho() / 2.0 && y + radio >= isla.getY() - isla.getAlto() / 2.0 && y - radio <= isla.getY() + isla.getAlto() / 2.0;
    }

    public boolean estaFueraPantalla() {
        return x < -200 || x > 1200 || y < -200 || y > 700;
    }
    
    public boolean esEspecial() {
        return especial;
    }
    
	//--------- Getters ----------------
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