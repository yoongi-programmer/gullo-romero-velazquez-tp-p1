package juego;

import java.awt.Image;
import java.awt.Rectangle;
import entorno.Entorno;
import entorno.Herramientas;

public class Castillo {
    private double x;
    private double y;
    private int ancho;
    private int alto;
    private Image imagen;

    public Castillo(double x, double y) {
        this.x = x;
        this.y = y;
        this.ancho = 150; // Ajustá estos valores al tamaño de tu imagen
        this.alto = 200;
        this.imagen = Herramientas.cargarImagen("img/castillo.png");
    }

    public void dibujar(Entorno e) {
        e.dibujarImagen(this.imagen, this.x, this.y, 0, 0.8);
    }

    // El castillo también debe moverse hacia atrás cuando el mapa hace scroll
    public void mover(double velocidad) {
        this.x += velocidad; // "velocidad" vendrá en negativo desde Juego
    }
    

    // Colisión idéntica a la de los enemigos
    public boolean colisionaConPrincesa(Princesa p) {
        Rectangle rectCastillo = new Rectangle((int)(x - ancho/2), (int)(y - alto/2), ancho, alto);
        Rectangle rectPrincesa = new Rectangle((int)(p.getX() - p.getAncho()/2), (int)(p.getY() - p.getAlto()/2), (int)p.getAncho(), (int)p.getAlto());
        return rectCastillo.intersects(rectPrincesa);
    }
}