package juego;
import java.awt.Color;
import java.awt.Image;
import java.util.concurrent.ThreadLocalRandom;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	private Entorno entorno;
	Color textoColor = new Color (213, 231, 247);
	private int ganar;
	private double xMapa;
	private double yMapa;
	private Image imagen; 
    private Isla[] islas;
    private Princesa princesa;
	private Enemigos[] enemigo;
	private Disparo disparo;
	boolean tocandoElPiso;
	
	//CONSTRUCTOR DEL JUEGO------------------------------------------------------------------------------
	Juego(double x, double y) {
		this.entorno = new Entorno(this, "Proyecto para TP", 1000, 500);
		this.ganar = 0;
		this.xMapa = x;
		this.yMapa = y;
		this.islas = new Isla[30];
		this.enemigo = new Enemigos[10];
		this.princesa = new Princesa (400 , 400, 25, 35,2.5, 5,50); // (coord X, coord Y, ancho, alto, velocidad, vidas, altura de salto)
        this.imagen = Herramientas.cargarImagen("img/fondo3.png");
        
		this.entorno.iniciar(); // Inicia el juego!
		inicializarPiso();
		generarIslasFlotantes();
	}
	
	//METODOS DE COMPORTAMIENTO---------------------------------------------------------------------------
    public void dibujar(Entorno e) {
        e.dibujarImagen(this.imagen, this.xMapa, this.yMapa, 0, 2); //(imagen, coordenada X, coordenada Y, ángulo, escala)
    }
    
    //--------------------------------------------------------------------- Mapa
    private void inicializarPiso() {
		int posX = 100;
		int anchoIsla = 280;
		int separacion = 80; // El hueco para que la princesa caiga

		for (int i = 0; i < 10; i++) {
			this.islas[i] = new Isla(posX, 490, anchoIsla, 50);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacion; // sumando el ancho de la isla que acabamos de crear + el hueco 
		} 		
		
	}
    private void generarIslasFlotantes() {
    	int posX=500;
    	int posYAnterior = 320;
    	for (int i = 10; i < 30; i++) {
        	int anchoIsla = ThreadLocalRandom.current().nextInt(150, 250);
        	int variacionY = ThreadLocalRandom.current().nextInt(-120, 80);
        	int separacionX = ThreadLocalRandom.current().nextInt(80, 120);
        	int nuevoY = posYAnterior + variacionY;
        	
        	if (nuevoY < 150) {
                nuevoY = 150;
            }
            if (nuevoY > 450) {
                nuevoY = 450;
            }
            
			this.islas[i] = new Isla(posX, nuevoY, anchoIsla, 35);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacionX; // sumando el ancho de la isla que acabamos de crear + el hueco
			posYAnterior = nuevoY;
		}
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
		if(this.princesa.getVidas()>0 && this.ganar==0) {
			boolean tocandoElPiso = false;
			
			for(int i = 0; i < this.islas.length; i++) {
	            if(this.islas[i] != null) {
	                if (this.princesa.paradaSobreIsla(this.islas[i])) {
	                    tocandoElPiso = true;
	                    this.princesa.setY( this.islas[i].getY() - (this.islas[i].getAlto()/2) - (this.princesa.getAlto()/2) );
	                }
	            }
	        }
			//Gravedad
			if(!tocandoElPiso) {
				this.princesa.moverseAbajo();
			}
			
			// Control del movimiento de la Princesa----------------------------------
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
			if(entorno.estaPresionada(entorno.TECLA_ARRIBA) && tocandoElPiso) {
				princesa.saltar();
			}
			if(entorno.estaPresionada(entorno.TECLA_ARRIBA) && this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) && tocandoElPiso) {
				princesa.saltar();
				princesa.moverseDerecha();
			}
			if (entorno.estaPresionada(entorno.TECLA_ABAJO)) {
				princesa.moverseAbajo();
			}
			
			//if(princesa.getY()>500) {
			//	princesa.setVidas(this.princesa.getVidas()-1); //resto una vida cuando cae
			//}
			// ENEMIGOS Y DISPAROS---------------------------------------------------
			if (entorno.sePresiono(entorno.TECLA_ESPACIO) && disparo == null) {
				disparo = new Disparo(
						princesa.getX(),
						princesa.getY(),
						princesa.estaMirandoDerecha());
			}
			
			// Mover disparo
			if (disparo != null) {
				disparo.mover();

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
			// ---------------------------------------------- Dibujar disparo
			if (disparo != null) {
				disparo.dibujar(entorno);
			}
			//----------------------------------------------- Dibujar enemigos
			for (int i = 0; i < enemigo.length; i++) {
				if (enemigo[i] != null) {
					enemigo[i].mover();
					enemigo[i].dibujar(entorno);
					
					// Colisión disparo-enemigo
					if (disparo != null && enemigo[i].colisionDisparoEnemigo(disparo)) {
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
			entorno.cambiarFont("Tahoma", 42, textoColor, entorno.NEGRITA);
			entorno.escribirTexto("Vidas: " + princesa.getVidas(), 100, 30);
		}else {
			
			if(this.ganar==1) {
				System.out.println("Gano");		//aca va la pantalla de gano
			}else if(this.princesa.getVidas()==0) {
				System.out.println("Perdio");	//aca va la pantalla de perdio
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