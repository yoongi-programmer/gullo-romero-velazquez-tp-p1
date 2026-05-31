package juego;
//import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	// El objeto Entorno que controla el tiempo y otros
	private Entorno entorno;
	private double x;
	private double y;
	private Image imagen; 
    private Isla[] islas;
    private Princesa princesa;
	private Enemigos[] enemigo;
	private Disparo disparo;
    
    
	Juego(double x, double y) {
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Proyecto para TP", 800, 500);
		this.x = x;
		this.y = y;
		this.islas = new Isla[30];
		inicializarPiso();
		
        // Cargamos la imagen UNA SOLA VEZ al nacer el objeto.
        this.imagen = Herramientas.cargarImagen("img/fondo3.png");
		// Inicia el juego!
		this.entorno.iniciar();
		
		//iNSTANCIA DE PRINCESA
		this.princesa = new Princesa (400 , 200, 25, 35,2.5); // (coordenada X, coordenada Y, ancho, alto, velocidad)
		this.enemigo = new Enemigos[10];
	}
	// Comportamiento
    public void dibujar(Entorno e) {
        // Ángulo 0 significa sin rotación. Escala 1.0 es el tamaño original.
        e.dibujarImagen(this.imagen, this.x, this.y, 0, 2); //(imagen, coordenada X, coordenada Y, ángulo, escala)
    }
    
    private void inicializarPiso() {
		// La primera isla arranca en X=100, Y=550 (bien abajo)
		int posX = 50;
		int anchoIsla = 100;
		int separacion = 50; // El hueco para que la princesa caiga

		for (int i = 0; i < 10; i++) {
			this.islas[i] = new Isla(posX, 490, anchoIsla, 50);
			posX = posX + anchoIsla + separacion; // sumando el ancho de la isla que acabamos de crear + el hueco 
		} 		
		
	}
    public void tick() {

    	// Fondo
    	this.dibujar(this.entorno);

    	// Islas
    	for (int i = 0; i < this.islas.length; i++) {
    		if (this.islas[i] != null) {
    			this.islas[i].dibujar(this.entorno);
    		}
    	}

    	// Princesa
    	princesa.dibujarse(this.entorno);

    	// Movimiento princesa
    	if (entorno.estaPresionada(entorno.TECLA_DERECHA)) {
    		princesa.moverseDerecha();
    	}

    	if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) {
    		princesa.moverseIzquierda();
    	}

    	if (entorno.estaPresionada(entorno.TECLA_ARRIBA)) {
    		princesa.saltar();
    	}

    	if (entorno.estaPresionada(entorno.TECLA_ABAJO)) {
    		princesa.moverseAbajo();
    	}

    	// Crear disparo
    	if (entorno.sePresiono(entorno.TECLA_ESPACIO) && disparo == null) {

    		disparo = new Disparo(
    				princesa.getX(),
    				princesa.getY(),
    				princesa.estaMirandoDerecha());
    	}

    	// Mover disparo
    	if (disparo != null) {

    		disparo.mover();
    		disparo.dibujar(entorno);

    		if (disparo.estaFueraPantalla()) {
    			disparo = null;
    		}
    	}

    	// Mantener mínimo 3 enemigos
    	int vivos = 0;

    	for (int i = 0; i < enemigo.length; i++) {
    		if (enemigo[i] != null) {
    			vivos++;
    		}
    	}

    	while (vivos < 3) {

    		for (int i = 0; i < enemigo.length; i++) {

    			if (enemigo[i] == null) {

    				enemigo[i] = Enemigos.generarEnemigo();
    				vivos++;
    				break;
    			}
    		}
    	}

    	// Dibujar enemigos
    	for (int i = 0; i < enemigo.length; i++) {

    		if (enemigo[i] != null) {

    			enemigo[i].mover();
    			enemigo[i].dibujar(entorno);

    			// Colisión disparo-enemigo
    			if (disparo != null &&
    				enemigo[i].colisionDisparoEnemigo(disparo)) {

    				enemigo[i] = null;
    				disparo = null;
    				continue;
    			}

    			// Sale de pantalla
    			if (enemigo[i].fueraDePantalla()) {
    				enemigo[i] = null;
    			}
    		}
    	}
    }
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Juego juego = new Juego(800,250);
	}
}