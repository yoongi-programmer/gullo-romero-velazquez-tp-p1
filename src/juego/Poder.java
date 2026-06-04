package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Poder {

    private double x;
    private double y;

    private int ancho = 40;
    private int alto = 40;

    private Image imagen;

    public Poder(double x, double y) {
        this.x = x;
        this.y = y;

        imagen = Herramientas.cargarImagen("img/orbe.png");
    }

    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(imagen, x, y, 0, 0.3);
    }

    public boolean colisionaConPrincesa(Princesa princesa) {

        return this.x + this.ancho / 2 >= princesa.getX() - princesa.getAncho() / 2
                && this.x - this.ancho / 2 <= princesa.getX() + princesa.getAncho() / 2
                && this.y + this.alto / 2 >= princesa.getY() - princesa.getAlto() / 2
                && this.y - this.alto / 2 <= princesa.getY() + princesa.getAlto() / 2;
    }
    
    public void moverConMapaIzquierda() {
        x -= 3;
    }

    public void moverConMapaDerecha() {
        x += 3;
    }
}