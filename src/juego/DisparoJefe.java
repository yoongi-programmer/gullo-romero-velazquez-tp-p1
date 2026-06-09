package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class DisparoJefe {

	private double x;
    private double y;
    // Velocidad de caída del disparo
    private double velocidad = 4;
    // Frames de la animación del fuego
    private Image[] frames;
    // Control de la animación
    private int frameActual;
    private int contadorAnimacion;

    public DisparoJefe(double x, double y) {
        this.x = x;
        this.y = y;
        // Crea el arreglo para almacenar los frames
        frames = new Image[3];
        // Carga las imágenes de la animación
        frames[0] = Herramientas.cargarImagen("img/fuego1.png");
        frames[1] = Herramientas.cargarImagen("img/fuego2.png");
        frames[2] = Herramientas.cargarImagen("img/fuego3.png");

        // Inicializa la animación
        frameActual = 0;
        contadorAnimacion = 0;
    }

    // Actualiza la posición y la animación del disparo
    public void mover() {
        y += velocidad;

        // Incrementa el contador de animación
        contadorAnimacion++;

        // Cambia de frame cada 10 ciclos
        if (contadorAnimacion >= 10) {
            frameActual = (frameActual + 1) % 3;
            contadorAnimacion = 0;
        }
    }

    // Dibuja el disparo en pantalla
    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(frames[frameActual], x, y, 0, 0.3);
    }

    // Verifica si el disparo salió por la parte inferior de la pantalla
    public boolean fueraDePantalla() {
        return y > 600;
    }

    // Verifica si el disparo impactó a la princesa
    public boolean colisionaConPrincesa(Princesa princesa) {
        return x >= princesa.getX() - princesa.getAncho() / 2 && x <= princesa.getX() + princesa.getAncho() / 2 && y >= princesa.getY() - princesa.getAlto() / 2 && y <= princesa.getY() + princesa.getAlto() / 2;
    }

    // Verifica si el disparo chocó contra una isla
    public boolean colisionaConIsla(Isla isla) {
        return isla.getArea().contains(x, y);
    }

    // Desplaza el disparo horizontalmente cuando se mueve el mapa
    public void moverConMapa(double direccion) {
        x += direccion;
    }
}