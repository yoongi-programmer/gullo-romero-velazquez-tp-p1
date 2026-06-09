package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class DisparoJefe {
    private double x;
    private double y;
    private double velocidad = 4;
    private Image[] frames;
    private int frameActual;
    private int contadorAnimacion;

    public DisparoJefe(double x, double y) {
        this.x = x;
        this.y = y;
        frames = new Image[3];        // Crea el arreglo para almacenar los frames
        frames[0] = Herramientas.cargarImagen("img/fuego1.png");
        frames[1] = Herramientas.cargarImagen("img/fuego2.png");
        frames[2] = Herramientas.cargarImagen("img/fuego3.png");
        frameActual = 0;
        contadorAnimacion = 0;
    }

    public void mover() {
        y += velocidad;        // Hace descender el disparo
        contadorAnimacion++;
        if (contadorAnimacion >= 10) {        // Cambia de frame cada 10 ciclos
            frameActual = (frameActual + 1) % 3;
            contadorAnimacion = 0;
        }
    }

    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(frames[frameActual], x, y, 0, 0.3);
    }

    public boolean fueraDePantalla() {
        return y > 600;
    }

    public boolean colisionaConPrincesa(Princesa princesa) {
        return x >= princesa.getX() - princesa.getAncho() / 2 && x <= princesa.getX() + princesa.getAncho() / 2 && y >= princesa.getY() - princesa.getAlto() / 2 && y <= princesa.getY() + princesa.getAlto() / 2;
    }

    public boolean colisionaConIsla(Isla isla) {
        return isla.getArea().contains(x, y);
    }

    public void moverConMapa(double direccion) {
        x += direccion;
    }
}