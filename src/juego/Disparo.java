package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;

public class Disparo {

    private double origenX;
    private double origenY;

    private double velocidadX;
    private double velocidadY;

    private double diametro;

    private Image imagen;
    
	private Image[] framesN;
	private int frameActual;
	private int contadorAnimacion;

    // DISPARO NORMAL
    public Disparo(double x, double y, boolean derecha) {

        this.origenX = x;
        this.origenY = y;

        this.diametro = 20;

        if (derecha) {
            velocidadX = 8;
        } else {
            velocidadX = -8;
        }

        velocidadY = 0;

        imagen = Herramientas.cargarImagen("img/disparo1.png");
    }

    // DISPARO ESPECIAL (APUNTA AL MOUSE)
    public Disparo(double x, double y,
                   double destinoX, double destinoY) {

        this.origenX = x;
        this.origenY = y;

        this.diametro = 20;

        double angulo =
                Math.atan2(destinoY - y,
                           destinoX - x);

        velocidadX = Math.cos(angulo) * 8;
        velocidadY = Math.sin(angulo) * 8;

        framesN = new Image[2];


        framesN [0] = Herramientas.cargarImagen("img/disparoEsp1.png");
        framesN [1] = Herramientas.cargarImagen("img/disparoEsp2.png");
        
		frameActual = 0;
		contadorAnimacion = 0;
    }

    public void mover() {
        origenX += velocidadX;
        origenY += velocidadY;
        
		// animación
		contadorAnimacion++;
		if (contadorAnimacion >= 10) {
			frameActual = (frameActual + 1) % 2;
			contadorAnimacion = 0;
		}
    }

    public void dibujar(Entorno e) {

        if (framesN != null) {
            e.dibujarImagen(framesN[frameActual], origenX, origenY, 0, 1);
        } else {
            e.dibujarImagen(imagen, origenX, origenY, 0, 0.2);
        }
    }

    public boolean colisionaConIsla(Isla isla) {

        double radio = diametro / 2.0;

        double miBordeIzq = origenX - radio;
        double miBordeDer = origenX + radio;
        double miBordeArriba = origenY - radio;
        double miBordeAbajo = origenY + radio;

        double islaIzq = isla.getX() - isla.getAncho() / 2.0;
        double islaDer = isla.getX() + isla.getAncho() / 2.0;
        double islaArriba = isla.getY() - isla.getAlto() / 2.0;
        double islaAbajo = isla.getY() + isla.getAlto() / 2.0;
		
        return miBordeIzq <= islaDer
                && miBordeDer >= islaIzq
                && miBordeArriba <= islaAbajo
                && miBordeAbajo >= islaArriba;
                        
    }

    public boolean estaFueraPantalla() {
        return origenX < -200
                || origenX > 1200
                || origenY < -200
                || origenY > 700;
    }

    public double getPosicionX() {
        return origenX;
    }

    public double getPosicionY() {
        return origenY;
    }

    public double getDiametro() {
        return diametro;
    }
}