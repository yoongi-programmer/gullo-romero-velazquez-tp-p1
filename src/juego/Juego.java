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
		this.princesa = new Princesa (500 , 400, 25, 35,2, 5,50); // (coord X, coord Y, ancho, alto, velocidad, vidas, altura de salto)
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
            if (nuevoY > 380) {
                nuevoY = 380;
            }
            
			this.islas[i] = new Isla(posX, nuevoY, anchoIsla, 35);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacionX; // sumando el ancho de la isla que acabamos de crear + el hueco
			posYAnterior = nuevoY;
		}
    }
    public void moverMapa(double direccion) {
        for (int i = 0; i < this.islas.length; i++) {
            if (this.islas[i] != null) {
                this.islas[i].moverIslas(direccion);
            }
        }
        
        for (int i = 0; i < enemigo.length; i++) {
            if (enemigo[i] != null) {

                if (direccion < 0) {
                    enemigo[i].moverConMapaIzquierda();
                }
                if (direccion > 0) {
                    enemigo[i].moverConMapaDerecha();
                }
            }
        }
        
        this.xMapa+=direccion; //Muevo el fondo tambien
    }
    
    //--------------------------------------------------------------------- Estado interno del juego
	public void tick() {	
		if(this.princesa.getVidas()>0 && this.ganar==0) {
			boolean tocandoElPiso = false;
			//Comprobar si está parada sobre una isla
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
			if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) || entorno.estaPresionada('D')) {
			    if (this.princesa.getX() < 500) {
			        this.princesa.moverseDerecha();
			    } else {
			        if (this.xMapa >= 0) {
			            moverMapa(-2);
			        } else if (this.princesa.getX() < 1000) {
			            this.princesa.moverseDerecha();
			        }
			    }
			}

			if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || entorno.estaPresionada('A')) {
			    if (this.princesa.getX() > 500) {
			        this.princesa.moverseIzquierda();
			    } else {
			        if (this.xMapa <= 1000) {
			            moverMapa(2);
			        } else if (this.princesa.getX() > 0) {
			            this.princesa.moverseIzquierda();
			        }
			    }
			}

			if ((entorno.estaPresionada(entorno.TECLA_ARRIBA) || entorno.estaPresionada('W')) && tocandoElPiso) {
			    princesa.saltar();
			}
			//correr a la derecha y saltar
			if(entorno.estaPresionada(entorno.TECLA_ARRIBA) && (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) || this.entorno.estaPresionada('A')) && tocandoElPiso) {
				princesa.saltar();
				princesa.moverseDerecha();
			}
			//correr a la izquierda y saltar
			if(entorno.estaPresionada(entorno.TECLA_ARRIBA) && (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('A')) && tocandoElPiso) {
				princesa.saltar();
				princesa.moverseIzquierda();
			}
			if (entorno.estaPresionada(entorno.TECLA_ABAJO) || entorno.estaPresionada('S'))  {
				princesa.moverseAbajo();
			}
			
			
			//Devuelve a la princesa a la isla si cayó al vacio
			if (princesa.getY() > 500 ) {
				princesa.setVidas(princesa.getVidas()-1);
				princesa.setX(princesa.getX() - 80);
				princesa.setY(450);
			}
			
			// ENEMIGOS Y DISPAROS---------------------------------------------------
			if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO) && disparo == null) {
				disparo = new Disparo(
						princesa.getX(),
						princesa.getY(),
						entorno.mouseX(),
						entorno.mouseY());
			}
			
			// Mover disparo
			if (disparo != null) {
				disparo.mover();
				
				if (disparo.estaFueraPantalla()) {
					disparo = null;
				}
				for (int i = 0; i < islas.length; i++) {
			        if (islas[i] != null) { //condicion para asegurarme de que la isla exista antes de seguir
			            if (disparo.colisionaConIsla(islas[i])) {
			                disparo = null; // El disparo impactó, desaparece
			                break;          // si el disparo desaparece corto el for para que no siga revisando otras islas
			            }
			        }
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
						enemigo[i] = Enemigos.generarEnemigo(islas, princesa, enemigo);
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

			        enemigo[i].mover(islas);
			        enemigo[i].dibujar(entorno);

			        // COLISION DISPARO
			        if (disparo != null &&
			            enemigo[i].colisionDisparoEnemigo(disparo)) {

			            enemigo[i] = null;
			            disparo = null;
			            continue;
			        }
			        
			        //COLISION PRINCSA
			        if (enemigo[i].colisionaConPrincesa(princesa)) {
			            princesa.setVidas(princesa.getVidas() - 1);
			            enemigo[i] = null;
			            continue;
			        }

			        // FUERA DE PANTALLA
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