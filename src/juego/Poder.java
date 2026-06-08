package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Poder {

    // Posición del poder en el mapa.
    private double x;
    private double y;

    // Tamaño del poder.
    private int ancho = 40;
    private int alto = 40;

    // Arreglo que almacena los cuadros de animación.
    private Image[] frames;

    // Frame que se está mostrando actualmente.
    private int frameActual;

    // Contador para controlar la velocidad de la animación.
    private int contadorAnimacion;

    // Constructor.
    public Poder(double x, double y) {

        // Guarda la posición inicial.
        this.x = x;
        this.y = y;

        // Crea un arreglo para guardar 4 imágenes.
        frames = new Image[4];

        // Carga las imágenes de la animación.
        frames[0] = Herramientas.cargarImagen("img/orbe1.png");
        frames[1] = Herramientas.cargarImagen("img/orbe2.png");
        frames[2] = Herramientas.cargarImagen("img/orbe3.png");
        frames[3] = Herramientas.cargarImagen("img/orbe3.png");

        // Empieza mostrando el primer frame.
        frameActual = 0;

        // Inicializa el contador de animación.
        contadorAnimacion = 0;
    }

    // Dibuja el poder en pantalla.
    public void dibujar(Entorno entorno) {
        // Incrementa el contador en cada frame del juego.
        contadorAnimacion++;

        // Cada 10 ciclos cambia la imagen.
        if (contadorAnimacion >= 10) {
            // Avanza al siguiente frame. El % 4 hace que vuelva a empezar al llegar al último.
            frameActual = (frameActual + 1) % 4;

            // Reinicia el contador.
            contadorAnimacion = 0;
        }

        // Dibuja la imagen actual.
        entorno.dibujarImagen(frames[frameActual], x, y, 0, 1);
    }

    // Detecta si la princesa tocó el poder.
    public boolean colisionaConPrincesa(Princesa princesa) {
        return this.x + this.ancho / 2 >= princesa.getX() - princesa.getAncho() / 2 && this.x - this.ancho / 2 <= princesa.getX() + princesa.getAncho() / 2 && this.y + this.alto / 2 >= princesa.getY() - princesa.getAlto() / 2 && this.y - this.alto / 2 <= princesa.getY() + princesa.getAlto() / 2;
    }

    // Hace que el poder se mueva junto con el mapa.
    public void moverConMapa(double direccion) {
        // Desplaza el poder horizontalmente.
        this.x += direccion;
    }
}