package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Disparo {
    private double x, y;
    // Velocidad horizontal y vertical
    private double velocidadX, velocidadY;
    private double diametro = 20;
    private double angulo;

    // Imagen del disparo normal
    private Image imagen;
    private Image[] frames;

    // Variables para controlar la animación
    private int frameActual;
    private int contadorAnimacion;
    private boolean especial;
    
    public Disparo(double x, double y, double destinoX, double destinoY, boolean especial) {

        // Guarda la posición inicial
        this.x = x;
        this.y = y;

        // Guarda si el disparo es especial
        this.especial = especial;

        // Calcula el ángulo entre el origen y el destino
        this.angulo = Math.atan2(destinoY - y, destinoX - x);

        // Calcula la velocidad en X e Y según el ángulo
        velocidadX = Math.cos(this.angulo) * 8;
        velocidadY = Math.sin(this.angulo) * 8;

        // Carga las imágenes correspondientes
        if (especial) {
            // Disparo especial animado
            frames = new Image[] { Herramientas.cargarImagen("img/disparoEsp1.png"), Herramientas.cargarImagen("img/disparoEsp2.png")};

        } else {
            // Disparo normal
            imagen = Herramientas.cargarImagen("img/disparo1.png");
        }
    }

    // Actualiza la posición del disparo
    public void mover() {
        // Mueve el disparo según su velocidad
        x += velocidadX;
        y += velocidadY;

        // Si es especial actualiza la animación
        if (especial && ++contadorAnimacion >= 10) {
        	
            // Cambia entre frame 0 y 1
            frameActual = (frameActual + 1) % 2;

            // Reinicia el contador
            contadorAnimacion = 0;
        }
    }

    // Dibuja el disparo en pantalla
    public void dibujar(Entorno e) {

        // Si tiene frames, es un disparo especial
        if (frames != null) {
        	e.dibujarImagen(frames[frameActual], x, y, angulo, 1);

        } else {
            // Dibuja el disparo normal
        	e.dibujarImagen(imagen, x, y, angulo, 0.2);
        }
    }

    // Verifica si el disparo colisiona con la isla
    public boolean colisionaConIsla(Isla isla) {

        // Calcula el radio del disparo
        double radio = diametro / 2;

        // Comprueba superposición entre el disparo y la isla
        return x + radio >= isla.getX() - isla.getAncho() / 2.0 && x - radio <= isla.getX() + isla.getAncho() / 2.0 && y + radio >= isla.getY() - isla.getAlto() / 2.0 && y - radio <= isla.getY() + isla.getAlto() / 2.0;
    }

    // Verifica si el disparo salió de la pantalla
    public boolean estaFueraPantalla() {
        return x < -200 || x > 1200 || y < -200 || y > 700;
    }
    
    //verifica si el disparo es con poder o no
    public boolean esEspecial() {
        return especial;
    }
    
    // Devuelve la posición X del disparo
    public double getPosicionX() {
        return x;
    }

    // Devuelve la posición Y del disparo
    public double getPosicionY() {
        return y;
    }

    // Devuelve el diámetro del disparo
    public double getDiametro() {
        return diametro;
    }
}