package juego;

import entorno.Entorno;
import entorno.Herramientas;
import java.awt.Image;
public class Disparo {

	private double origenX;
	private double origenY;
	private double destinoX;
	private double destinoY;
	private double diametro;
	private int velocidad;
	private double angulo;


	private Image imagen;

	public Disparo(double x, double y, double destinoX, double destinoY) {
		this.origenX = x;
        this.origenY = y;
        this.diametro = 20;
        this.velocidad = 5; 

        // 1. Angulo del mouse (saber a donde disparar)
        this.angulo = Math.atan2(destinoY - y, destinoX - x); //calculo la distancia (catetos de un triangulo como vimos en clase)

        // 2. Multiplico cos * angulo y sin * angulo para obtener y lo multiplico por velocidad para obtener ubicaciob exacta de hacia donde disparar
        this.destinoX = Math.cos(this.angulo) * this.velocidad; 
        this.destinoY = Math.sin(this.angulo) * this.velocidad;

		this.imagen = Herramientas.cargarImagen("img/disparo1.png");
	}
	
	

	public void mover() {
		this.origenX += this.destinoX;
		this.origenY += this.destinoY;
	}
	
	public boolean colisionaConIsla(Isla isla) {
	    double radio = this.diametro / 2.0; //el radio mide la distancia desde el centro hasta un borde determinado
	    
	    // 1. Todos los bordes del disparo
	    double miBordeIzq = this.origenX - radio;
	    double miBordeDer = this.origenX + radio;
	    double miBordeArriba = this.origenY - radio;
	    double miBordeAbajo = this.origenY + radio;

	    // 2. calculo los bordes de las islas usando los getters
	    double islaIzq = isla.getX() - (isla.getAncho() / 2.0);
	    double islaDer = isla.getX() + (isla.getAncho() / 2.0);
	    double islaArriba = isla.getY() - (isla.getAlto() / 2.0);
	    double islaAbajo = isla.getY() + (isla.getAlto() / 2.0);

	    // 3. Verificar si las cajas se superponen
	    boolean superposicionX = (miBordeIzq <= islaDer) && (miBordeDer >= islaIzq);
	    boolean superposicionY = (miBordeArriba <= islaAbajo) && (miBordeAbajo >= islaArriba);

	    return superposicionX && superposicionY;
	}
	
	public void dibujar(Entorno e) {
		e.dibujarImagen(imagen, origenX, origenY, Math.PI, 0.2);
	}
	
	public boolean estaFueraPantalla() {
		return origenX < -200 || origenX > 1200 || origenY > 500 ||origenY < 0;
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
	public double getAngulo() {
		return angulo;
	}
	
	public void setAngulo(double angulo){
		this.angulo = angulo;
	}
}