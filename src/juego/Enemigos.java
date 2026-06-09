package juego;

import entorno.Entorno;
import entorno.Herramientas;

import java.awt.Image;
import java.util.Random;
import java.awt.Rectangle;

public class Enemigos {
	private double x;
	private double y;
	private double velocidad;
	private boolean vaDerecha;
	private int ancho = 30;
	private int alto = 30;
	private static int enemigosMuertos = 0;
	private Image[] framesIzquierda;
	private Image[] framesDerecha;
	private int frameActual;				// Control de animación
	private int contadorAnimacion;
	private boolean explotando = false;		//indica si el enemigo murio
	private Image[] explosionFrames;		// Control de animación e imgenes de explosion
	private int explosionFrame;
	private int contadorExplosion;
	
	public Enemigos(double x, double y, boolean vaDerecha) {
		this.x = x;
		this.y = y;
		this.vaDerecha = vaDerecha;
		this.velocidad = 0.8;
		framesIzquierda = new Image[3];
		framesDerecha = new Image[3];
		
		framesIzquierda[0] = Herramientas.cargarImagen("img/enemigo1_IZ.png");		// Carga de sprites mirando a la izquierda
		framesIzquierda[1] = Herramientas.cargarImagen("img/enemigo2_IZ.png");
		framesIzquierda[2] = Herramientas.cargarImagen("img/enemigo3_IZ.png");
		
		framesDerecha[0] = Herramientas.cargarImagen("img/enemigo1_DE.png");		// Carga de sprites mirando a la derecha
		framesDerecha[1] = Herramientas.cargarImagen("img/enemigo2_DE.png");
		framesDerecha[2] = Herramientas.cargarImagen("img/enemigo3_DE.png");

		frameActual = 0;
		contadorAnimacion = 0;		
		explosionFrames = new Image[4];
		
		explosionFrames[0] = Herramientas.cargarImagen("img/explosion1.png");
		explosionFrames[1] = Herramientas.cargarImagen("img/explosion2.png");
		explosionFrames[2] = Herramientas.cargarImagen("img/explosion3.png");
		explosionFrames[3] = Herramientas.cargarImagen("img/explosion4.png");
	}

	public static Enemigos generarEnemigo(Isla[] islas, Princesa princesa, Enemigos[] enemigos) {
		Random r = new Random();		// Genera un enemigo sobre una isla aleatoria
		Isla isla = null;

		while (isla == null) {
			Isla candidata = islas[r.nextInt(islas.length)];			
			if (candidata != null && !princesa.paradaSobreIsla(candidata)) {
				isla = candidata;
			}
		}

		double x = isla.getArea().x + r.nextInt(isla.getArea().width);			// Posición aleatoria sobre la isla elegida
		double y = isla.getArea().y - 30;

		for (int i = 0; i < enemigos.length; i++) {								// Evita que aparezcan enemigos muy juntos
			if (enemigos[i] != null) {
				if (Math.abs(x - enemigos[i].getX()) < 200) {
					return generarEnemigo(islas, princesa, enemigos);			// Intenta generar otro enemigo
				}
			}
		}
		return new Enemigos(x, y, r.nextBoolean());
	}

	public Poder generarPoder() {	// Genera un poder cada 3 enemigos eliminados
		enemigosMuertos++;
		if (enemigosMuertos % 3 == 0) {
			return new Poder(x, y);
		}
		return null;
	}

	public void mover(Isla[] islas) {
	    if (explotando) {
	        return;
	    }	    
		for (int i = 0; i < islas.length; i++) {			// Verifica si chocará con alguna isla
			if (islas[i] != null && vaAChocar(islas[i])) {
				vaDerecha = !vaDerecha;						// Cambia de dirección
				return;
			}
		}
		
		if (vaDerecha) {
			x += velocidad;
		}
		else {
			x -= velocidad;
		}     
        contadorAnimacion++;
        if (contadorAnimacion >= 10) {        		// Cada 10 ciclos cambia la imagen.           
            frameActual = (frameActual + 1) % 3;	// Avanza al siguiente frame. El % 3 hace que vuelva a empezar al llegar al último.
            contadorAnimacion = 0;
        } 
	}

	public void moverConMapa(double direccion) {
		this.x += direccion;
	}

	public boolean colisionDisparoEnemigo(Disparo disparo) {	// Detecta colisión entre disparo y enemigo
		double radioDisparo = disparo.getDiametro() / 2.0;
		return disparo.getPosicionX() + radioDisparo >= this.x - ancho / 2 && disparo.getPosicionX() - radioDisparo <= this.x + ancho / 2 && disparo.getPosicionY() + radioDisparo >= this.y - alto / 2 && disparo.getPosicionY() - radioDisparo <= this.y + alto / 2;
	}

	public boolean colisionaConIsla(Isla isla) {	// Detecta colisión entre enemigo e isla
		Rectangle enemigoRect = new Rectangle((int)(x - ancho/2), (int)(y - alto/2), ancho, alto);
		return enemigoRect.intersects(isla.getArea());
	}

	public boolean vaAChocar(Isla isla) {
		Rectangle futuro;

		if (vaDerecha) {
			futuro = new Rectangle((int)(x + velocidad - ancho/2), (int)(y - alto/2), ancho, alto);			
		} else {
			futuro = new Rectangle((int)(x - velocidad - ancho/2), (int)(y - alto/2), ancho, alto);
		}
		return futuro.intersects(isla.getArea());
	}

	public boolean colisionaConPrincesa(Princesa princesa) {
		return this.x + this.ancho / 2 >= princesa.getX() - princesa.getAncho() / 2 && this.x - this.ancho / 2 <= princesa.getX() + princesa.getAncho() / 2 && this.y + this.alto / 2 >= princesa.getY() - princesa.getAlto() / 2 && this.y - this.alto / 2 <= princesa.getY() + princesa.getAlto() / 2;
	}

	public void explotar() {
	    explotando = true;
	    explosionFrame = 0;
	    contadorExplosion = 0;
	}
	
	public void dibujar(Entorno entorno) {
	    if (explotando) {
	        entorno.dibujarImagen(explosionFrames[explosionFrame], x, y, 0, 1);
	        contadorExplosion++;
	        if (contadorExplosion >= 5) {
	            explosionFrame++;
	            contadorExplosion = 0;
	        }
	        return;
	    }
	    
		Image imagenActual;
		if (vaDerecha) {
			imagenActual = framesDerecha[frameActual];
		}
		else {
			imagenActual = framesIzquierda[frameActual];
		}
		entorno.dibujarImagen(imagenActual, x, y, 0, 2);
	}

	public boolean fueraDePantalla() {
		return x < -200 || x > 1200;
	}

	public boolean explosionTerminada() {
	    return explosionFrame >= explosionFrames.length;
	}
	
	public static int getEnemigosMuertos() {
	    return enemigosMuertos;
	}
	
	public static void reiniciarContador() {
	    enemigosMuertos = 0;
	}

	//--------- Getters ----------------
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getAncho() {
		return ancho;
	}
	public int getAlto() {
		return alto;
	}
	public boolean getVaDerecha() {
		return vaDerecha;
	}
	//--------- Setters ----------------
	public double setX(double x) {
		return this.x = x;
	}
}