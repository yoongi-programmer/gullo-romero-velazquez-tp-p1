package juego;
import java.awt.Image;
import java.awt.Rectangle;

import entorno.Entorno;
import entorno.Herramientas;

public class Princesa {
    private double x, y, ancho, alto, velocidad;
    private int vidas;
    private boolean mirandoDerecha = true;
    
    // Animación
 	private Image[] framesIzquierda;
 	private Image[] framesDerecha;
 	
 	private Disparo disparo;

 	private int frameActual;
 	private int contadorAnimacion;
 	private boolean tocandoElSuelo;
 	private double gravedad;
 	private double velocidadY;
 	private double potenciaSalto;

    //  --- Constructor ---
    public Princesa (double x, double y, double ancho, double alto, double velocidad, int vidas) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
        this.vidas = vidas;
        
        framesIzquierda = new Image[4];
		framesDerecha = new Image[4];

		framesIzquierda[0] = Herramientas.cargarImagen("img/princess_izq1.png");
		framesIzquierda[1] = Herramientas.cargarImagen("img/princess_izq2.png");
		framesIzquierda[2] = Herramientas.cargarImagen("img/princess_izq3.png");
		framesIzquierda[3] = Herramientas.cargarImagen("img/princess_izq4.png");

		framesDerecha[0] = Herramientas.cargarImagen("img/princess_der1.png");
		framesDerecha[1] = Herramientas.cargarImagen("img/princess_der2.png");
		framesDerecha[2] = Herramientas.cargarImagen("img/princess_der3.png");
		framesDerecha[3] = Herramientas.cargarImagen("img/princess_der4.png");

		frameActual = 0;
		contadorAnimacion = 0;
		gravedad = 0.4;
		velocidadY = 0;
		potenciaSalto = -9.5;
    }

    //  --- getters --- 
    public double getX() {
    	return this.x;
    }
    
    public double getY() {
    	return this.y;
    }
    
    public double getAncho() {
    	return this.ancho;
    }
    
    public double getAlto() {
    	return this.alto;
    }
    
    public double getVelocidad() {
    	return this.velocidad;
    }
	public int getVidas() {
		return vidas;
	}

    public boolean getEstaMirandoDerecha() {
    	return mirandoDerecha;
    }
    
    public boolean getTcandoElSUelo() {
    	return tocandoElSuelo;
    }
    
    public double getVelocidadY() {
    	return velocidadY;
    }
    
    public double getPotenciaSalto() {
    	return potenciaSalto;
    }

	public Disparo getDisparo() {
	    return disparo;
	}
    //  --- Setters --- 
    
    public void setX(double x) {
    	this.x = x;
    }
    
    public void setY(double y) {
    	this.y = y;
    }
    
    public void setAncho(double ancho) {
    	this.ancho = ancho;
    }
    
    public void setAlto(double alto) {
    	this.alto = alto;
    }
    
    public void setVelocidad(double velocidad) {
    	this.velocidad = velocidad;
    }
    
	public void setVidas(int vidas) {
		this.vidas = vidas;
	} 
	
	public void setTocandoElSuelo(boolean tocandoSuelo) {
		this.tocandoElSuelo = tocandoSuelo; 
	}
	
	public void setVelocidadY(double velocidadY) {
		this.velocidadY = velocidadY;
	}
	public void setMirandoDerecha(boolean mirandoD) {
		this.mirandoDerecha = mirandoD;
	}
	public void setGravedad (double gravedad) {
		this.gravedad = gravedad;
	}
	public void desacelerarY (double cantidad) {
		this.y += cantidad;
	}

    //metodos
	public boolean paradaSobreIsla(Isla isla) {
        double misPies = this.y + (this.alto / 2);
        double techoIsla = isla.getY() - (isla.getAlto() / 2);

        double miBordeIzq = this.x - (this.ancho / 2);
        double miBordeDer = this.x + (this.ancho / 2);
        double islaIzq = isla.getX() - (isla.getAncho() / 2);
        double islaDer = isla.getX() + (isla.getAncho() / 2);

        boolean alineadaEnX = (miBordeDer >= islaIzq) && (miBordeIzq <= islaDer);
        boolean tocandoTecho = (misPies >= techoIsla - 5) && (misPies <= techoIsla + 5); // margen de 5 píxeles de error por la velocidad a la que cae.

        return alineadaEnX && tocandoTecho;
    }
    
    public boolean estaMirandoDerecha() {
    	return mirandoDerecha;
    }
    
    public void moverseDerecha() {
    	this.x += this.velocidad;
    	this.mirandoDerecha = true;
    	// animación
		contadorAnimacion++;
		if (contadorAnimacion >= 10) {
			frameActual = (frameActual + 1) % 4;
			contadorAnimacion = 0;
		}
    }
    
    public void moverseIzquierda() {
    	this.x -=this.velocidad ;
    	this.mirandoDerecha = false;
    	// animación
		contadorAnimacion++;
		if (contadorAnimacion >= 10) {
			frameActual = (frameActual + 1) % 4;
			contadorAnimacion = 0;
		}
    }
    
    public void saltar() {
    	if(tocandoElSuelo) {
    		this.velocidadY = potenciaSalto;
    		this.tocandoElSuelo = false;
    		}
    }
    
    public void modificarFisica() {
    	if(!tocandoElSuelo) {
    		this.y += this.velocidadY;
        	this.velocidadY += this.gravedad;       	
    	} 	
    	else {
    		this.velocidadY = 0;
    	}
    	
    }
    
    public void moverseAbajo() {
    	this.y+=this.gravedad;
    }
    
    public void disparar(double mouseX, double mouseY, boolean especial) {
        if (disparo == null) {
            disparo = new Disparo(this.x, this.y - 10, mouseX, mouseY, especial);
        }
    }
    
    public void actualizarDisparo(Isla[] islas) {
        if (disparo != null) {
            disparo.mover();
            if (disparo.estaFueraPantalla()) {
                disparo = null;
                return;
            }

            for (int i = 0; i < islas.length; i++) {
                if (islas[i] != null &&
                    disparo.colisionaConIsla(islas[i])) {
                    disparo = null;
                    return;
                }
            }
        }
    }
    
    public void dibujarDisparo(Entorno entorno) {
        if (disparo != null) {
            disparo.dibujar(entorno);
        }
    }
    
    public void eliminarDisparo() {
        disparo = null;
    }
    
    public void dibujarse (Entorno e) {
    	Image imagenActual;
		if (this.mirandoDerecha) {
			imagenActual = framesDerecha[frameActual];
		} else {
			imagenActual = framesIzquierda[frameActual];
		}
		
		double ajusteY = -22;
		e.dibujarImagen(imagenActual, x, y + ajusteY, 0, 2);
    }
}
