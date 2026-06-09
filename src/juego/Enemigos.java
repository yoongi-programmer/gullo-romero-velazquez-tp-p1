package juego;

import entorno.Entorno;
import entorno.Herramientas;

import java.awt.Image;
import java.util.Random;
import java.awt.Rectangle;

public class Enemigos {

	// Posición del enemigo
	private double x;
	private double y;

	// Velocidad de movimiento
	private double velocidad;

	// Indica si el enemigo se mueve hacia la derecha
	private boolean vaDerecha;

	// Tamaño del enemigo
	private int ancho = 30;
	private int alto = 30;

	// Contador compartido por todos los enemigos para saber cuántos fueron eliminados
	private static int enemigosMuertos = 0;

	// Imágenes para la animación
	private Image[] framesIzquierda;
	private Image[] framesDerecha;

	// Control de animación
	private int frameActual;
	private int contadorAnimacion;

	//indica si el enemigo murio
	private boolean explotando = false;

	// Control de animación e imgenes de explosion
	private Image[] explosionFrames;
	private int explosionFrame;
	private int contadorExplosion;
	
	// Constructor
	public Enemigos(double x, double y, boolean vaDerecha) {
		this.x = x;
		this.y = y;
		this.vaDerecha = vaDerecha;

		// Velocidad inicial
		this.velocidad = 0.8;

		// Arreglos de imágenes para la animación
		framesIzquierda = new Image[3];
		framesDerecha = new Image[3];

		// Carga de sprites mirando a la izquierda
		framesIzquierda[0] = Herramientas.cargarImagen("img/enemigo1_IZ.png");
		framesIzquierda[1] = Herramientas.cargarImagen("img/enemigo2_IZ.png");
		framesIzquierda[2] = Herramientas.cargarImagen("img/enemigo3_IZ.png");

		// Carga de sprites mirando a la derecha
		framesDerecha[0] = Herramientas.cargarImagen("img/enemigo1_DE.png");
		framesDerecha[1] = Herramientas.cargarImagen("img/enemigo2_DE.png");
		framesDerecha[2] = Herramientas.cargarImagen("img/enemigo3_DE.png");

		frameActual = 0;
		contadorAnimacion = 0;
		
		explosionFrames = new Image[4];

		//Carga de sprites de la explosion de los enemigos
		explosionFrames[0] = Herramientas.cargarImagen("img/explosion1.png");
		explosionFrames[1] = Herramientas.cargarImagen("img/explosion2.png");
		explosionFrames[2] = Herramientas.cargarImagen("img/explosion3.png");
		explosionFrames[3] = Herramientas.cargarImagen("img/explosion4.png");
	}

	// Genera un enemigo sobre una isla aleatoria
	public static Enemigos generarEnemigo(Isla[] islas, Princesa princesa, Enemigos[] enemigos) {
		Random r = new Random();
		Isla isla = null;

		// Busca una isla válida donde no esté la princesa
		while (isla == null) {
			Isla candidata = islas[r.nextInt(islas.length)];
			
			if (candidata != null && !princesa.paradaSobreIsla(candidata)) {
				isla = candidata;
			}
		}

		// Posición aleatoria sobre la isla elegida
		double x = isla.getArea().x + r.nextInt(isla.getArea().width);
		double y = isla.getArea().y - 30;

		// Evita que aparezcan enemigos muy juntos
		for (int i = 0; i < enemigos.length; i++) {
			if (enemigos[i] != null) {
				if (Math.abs(x - enemigos[i].getX()) < 200) {
					// Intenta generar otro enemigo
					return generarEnemigo(islas, princesa, enemigos);
				}
			}
		}
		// Crea el enemigo
		return new Enemigos(x, y, r.nextBoolean());
	}
	// Genera un poder cada 3 enemigos eliminados
	public Poder generarPoder() {
		enemigosMuertos++;
		
		if (enemigosMuertos % 3 == 0) {
			return new Poder(x, y);
		}
		return null;
	}

	// Movimiento principal del enemigo
	public void mover(Isla[] islas) {

	    if (explotando) {
	        return;
	    }
	    
		// Verifica si chocará con alguna isla
		for (int i = 0; i < islas.length; i++) {
			if (islas[i] != null && vaAChocar(islas[i])) {
				
				// Cambia de dirección
				vaDerecha = !vaDerecha;
				return;
			}
		}


		// Movimiento horizontal
		if (vaDerecha) {
			x += velocidad;
		}
		else {
			x -= velocidad;
		}

        // Incrementa el contador en cada frame del juego.
        contadorAnimacion++;

        // Cada 10 ciclos cambia la imagen.
        if (contadorAnimacion >= 10) {
            // Avanza al siguiente frame. El % 3 hace que vuelva a empezar al llegar al último.
            frameActual = (frameActual + 1) % 3;

            // Reinicia el contador.
            contadorAnimacion = 0;
        }
        
	}
		


	// Movimiento cuando el mapa se desplaza
	public void moverConMapa(double direccion) {
		this.x += direccion;
	}

	// Detecta colisión entre disparo y enemigo
	public boolean colisionDisparoEnemigo(Disparo disparo) {
		double radioDisparo = disparo.getDiametro() / 2.0;
		return disparo.getPosicionX() + radioDisparo >= this.x - ancho / 2 && disparo.getPosicionX() - radioDisparo <= this.x + ancho / 2 && disparo.getPosicionY() + radioDisparo >= this.y - alto / 2 && disparo.getPosicionY() - radioDisparo <= this.y + alto / 2;
	}

	// Detecta colisión entre enemigo e isla
	public boolean colisionaConIsla(Isla isla) {
		Rectangle enemigoRect = new Rectangle((int)(x - ancho/2), (int)(y - alto/2), ancho, alto);
		return enemigoRect.intersects(isla.getArea());
	}

	// Verifica si chocará en el próximo movimiento
	public boolean vaAChocar(Isla isla) {
		Rectangle futuro;

		if (vaDerecha) {
			futuro = new Rectangle((int)(x + velocidad - ancho/2), (int)(y - alto/2), ancho, alto);
			
		} else {

			futuro = new Rectangle((int)(x - velocidad - ancho/2), (int)(y - alto/2), ancho, alto);
		}
		return futuro.intersects(isla.getArea());
	}

	// Detecta colisión entre enemigo y princesa
	public boolean colisionaConPrincesa(Princesa princesa) {
		return this.x + this.ancho / 2 >= princesa.getX() - princesa.getAncho() / 2 && this.x - this.ancho / 2 <= princesa.getX() + princesa.getAncho() / 2 && this.y + this.alto / 2 >= princesa.getY() - princesa.getAlto() / 2 && this.y - this.alto / 2 <= princesa.getY() + princesa.getAlto() / 2;
	}

	public void explotar() {
	    explotando = true;
	    explosionFrame = 0;
	    contadorExplosion = 0;
	}
	
	// Dibuja el enemigo en pantalla y la explosion en caso de que sea el disparo especial
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

	// Verifica si salió de los límites de la pantalla
	public boolean fueraDePantalla() {
		return x < -200 || x > 1200;
	}

	//Verifica si la explosion termino
	public boolean explosionTerminada() {
	    return explosionFrame >= explosionFrames.length;
	}
	
	public static int getEnemigosMuertos() {
	    return enemigosMuertos;
	}
	
	// Getters
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
	
	public double setX(double x) {
		return this.x = x;
	}
}