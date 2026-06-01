package juego;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	private Entorno entorno;
	private double xMapa;
	private double yMapa;
	private Image imagen; 
    private Isla[] islas;
    private Princesa princesa;
	private Enemigos[] enemigo;
	private Disparo disparo;
	
	//CONSTRUCTOR DEL JUEGO------------------------------------------------------------------------------
	Juego(double x, double y) {
		this.entorno = new Entorno(this, "Proyecto para TP", 1000, 500);
		this.xMapa = x;
		this.yMapa = y;
		this.islas = new Isla[30];
		this.enemigo = new Enemigos[10];
		this.princesa = new Princesa (400 , 200, 25, 35,2.5); // (coord X, coord Y, ancho, alto, velocidad)
		
        this.imagen = Herramientas.cargarImagen("img/fondo3.png");
		this.entorno.iniciar(); // Inicia el juego!
		inicializarPiso();
	}
	//METODOS DE COMPORTAMIENTO---------------------------------------------------------------------------
    public void dibujar(Entorno e) {
        e.dibujarImagen(this.imagen, this.xMapa, this.yMapa, 0, 2); //(imagen, coordenada X, coordenada Y, ángulo, escala)
    }
    //--------------------------------------------------------------------- Mapa
    private void inicializarPiso() {
		int posX = 100;
		int anchoIsla = 250;
		int separacion = 100; // El hueco para que la princesa caiga

		for (int i = 0; i < 10; i++) {
			this.islas[i] = new Isla(posX, 490, anchoIsla, 50);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacion; // sumando el ancho de la isla que acabamos de crear + el hueco 
		} 		
		
	}
    private void generarIslasFlotantes() {
    	int x=0;
    }
    public void moverMapaIzquierda() {
        for (int i = 0; i < this.islas.length; i++) {
            if (this.islas[i] != null) {
                this.islas[i].moverIzquierda();
            }
        }
        this.xMapa-=3; //Muevo el fondo tambien
    }
    //--------------------------------------------------------------------- Estado interno del juego
	public void tick() {
		// Control del movimiento de la Princesa--------------------------------
		if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)) {
			// Si la princesa aún no llegó a la mitad, avanza ella
	        if (this.princesa.getX() < 500) {
	            this.princesa.moverseDerecha();
	        }else {
	        	if(this.xMapa>=0) {
		        	moverMapaIzquierda();	        		
	        	}else if (this.princesa.getX() < 1000){
	        		this.princesa.moverseDerecha();
	        	}
	        }
		}
		if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA)) {
			if (this.princesa.getX() > 0) {
	            this.princesa.moverseIzquierda();
	        }
		}
		if(entorno.estaPresionada(entorno.TECLA_ARRIBA)) {
			princesa.saltar();
		}
		if (entorno.estaPresionada(entorno.TECLA_ABAJO)) {
			princesa.moverseAbajo();
		}
		
		// ENEMIGOS Y DISPAROS---------------------------------------------------
		if (entorno.sePresiono(entorno.TECLA_ESPACIO) && disparo == null) {
			disparo = new Disparo(
					princesa.getX(),
					princesa.getY(),
					princesa.estaMirandoDerecha());
		}
		// Mover y dibujar disparo
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
		
		//DIBUJAR TODO-----------------------------------------------------------
		this.dibujar(this.entorno);		//fondo
		//----------------------------------------------- Dibujar islas
		for(int i = 0; i < this.islas.length; i++) {
			if(this.islas[i] != null) {
				this.islas[i].dibujar(this.entorno);
			}
		}
		//----------------------------------------------- Dibujar princesa
		princesa.dibujarse(this.entorno);
		//----------------------------------------------- Dibujar enemigos
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
	//-----------------------------------------------------------------------MAIN
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Juego juego = new Juego(1000,250);
	}
}