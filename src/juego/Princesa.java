package juego;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import entorno.Entorno;
import entorno.Herramientas;

public class Princesa {
    private double x, y, ancho, alto, velocidad, altoMaximo;
    private int vidas;
    private boolean mirandoDerecha = true;
    // Animación
 	private Image[] framesIzquierda;
 	private Image[] framesDerecha;

 	private int frameActual;
 	private int contadorAnimacion;

    //  --- Constructor ---
    public Princesa (double x, double y, double ancho, double alto, double velocidad, int vidas, double altoM) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
        this.vidas = vidas;
        this.altoMaximo = altoM;
        
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
    	return this.ancho;
    }
    
    public double getVelocidad() {
    	return this.velocidad;
    }
	public int getVidas() {
		return vidas;
	}

    public boolean estaMirandoDerecha() {
    	return mirandoDerecha;
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
    //El codigo siguiente crea un rectangulo que cubre el cuerpo de la princesa y distintos sensores en cada  lado como pequeños rectangulos
    
    //  --- Cuerpo de la princesa ---
    public Rectangle2D.Double getCuerpo() {
        return new Rectangle2D.Double(x, y, ancho, alto);
    }

    // --- SENSORES DE LOS LADOS ---

    public Rectangle2D.Double getSensorAbajo() {
        // Posicionado justo en la base, con 2 píxeles de alto
        return new Rectangle2D.Double(x + 5, y + alto - 2, ancho - 10, 2);
    }

    public Rectangle2D.Double getSensorArriba() {
        // Justo en el techo
        return new Rectangle2D.Double(x + 5, y, ancho - 10, 2);
    }

    public Rectangle2D.Double getSensorIzquierda() {
        // En el borde izquierdo, con 2 píxeles de ancho
        return new Rectangle2D.Double(x, y + 5, 2, alto - 10);
    }

    public Rectangle2D.Double getSensorDerecha() {
        // En el borde derecho
        return new Rectangle2D.Double(x + ancho - 2, y + 5, 2, alto - 10);
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
			frameActual = (frameActual + 1) % 3;
			contadorAnimacion = 0;
		}
    }
    
    public void saltar() {
    	this.y-= this.altoMaximo;
    }
    
    public void moverseAbajo() {
    	this.y+=this.velocidad;
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
