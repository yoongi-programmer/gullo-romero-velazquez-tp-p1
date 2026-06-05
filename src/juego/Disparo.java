package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Disparo {

    private double x, y;
    private double velocidadX, velocidadY;
    private double diametro = 20;

    private Image imagen;
    private Image[] frames;

    private int frameActual;
    private int contadorAnimacion;
    private boolean especial;

    public Disparo(double x, double y, double destinoX, double destinoY, boolean especial) {
        this.x = x;
        this.y = y;
        this.especial = especial;
        
        double angulo = Math.atan2(destinoY - y, destinoX - x);
        
        velocidadX = Math.cos(angulo) * 8;
        velocidadY = Math.sin(angulo) * 8;
        
        if (especial) {
            frames = new Image[] {Herramientas.cargarImagen("img/disparoEsp1.png"),Herramientas.cargarImagen("img/disparoEsp2.png")};
        } else {
            imagen = Herramientas.cargarImagen("img/disparo1.png");
        }
    }

    public void mover() {
        x += velocidadX;
        y += velocidadY;
        if (especial && ++contadorAnimacion >= 10) {
            frameActual = (frameActual + 1) % 2;
            contadorAnimacion = 0;
        }
    }

    public void dibujar(Entorno e) {
        if (frames != null) {
            e.dibujarImagen(frames[frameActual], x, y, 0, 1);
        } else {
            e.dibujarImagen(imagen, x, y, 0, 0.2);
        }
    }

    public boolean colisionaConIsla(Isla isla) {
        double radio = diametro / 2;
        return x + radio >= isla.getX() - isla.getAncho() / 2.0 && x - radio <= isla.getX() + isla.getAncho() / 2.0 && y + radio >= isla.getY() - isla.getAlto() / 2.0 && y - radio <= isla.getY() + isla.getAlto() / 2.0;
    }

    public boolean estaFueraPantalla() {
        return x < -200 || x > 1200 || y < -200 || y > 700;
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