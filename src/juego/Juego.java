package juego;
import java.awt.Color;
import java.awt.Image;
import java.util.concurrent.ThreadLocalRandom;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	
	private Entorno entorno;
    private Isla[] islas;
    private Princesa princesa;

	private int ganar;
	
	private double xMapa;
	private double yMapa;
	
	private Image imagen; 
	
	boolean tocandoElPiso;
	
	// Referencia al arreglo de enemigos del juego
	private Enemigos[] enemigo;

	// Referencia al poder que puede aparecer en el mapa
	private Poder poder;	

	// Indica si la princesa tiene activo el poder especial
	private boolean tienePoder = false;

	// Cantidad de disparos especiales restantes
	private int disparosEspeciales = 0;
	
	// Referencia al jefe final del juego
	private Jefe jefe;

	// Evita que el jefe se genere más de una vez
	private boolean jefeInvocado = false;
	
	Color textoColor = new Color (213, 231, 247);	
	
	//CONSTRUCTOR DEL JUEGO------------------------------------------------------------------------------
	Juego(double x, double y) {
		this.entorno = new Entorno(this, "Proyecto para TP", 1000, 500);
		this.ganar = 0;
		this.xMapa = x;
		this.yMapa = y;
		this.islas = new Isla[30];
		this.enemigo = new Enemigos[10];
		this.princesa = new Princesa (500 , 400, 25, 35,2, 5); // (coord X, coord Y, ancho, alto, velocidad, vidas, altura de salto)
        this.imagen = Herramientas.cargarImagen("img/fondo.png");  
        double anchoFondo = this.imagen.getWidth(null) * 0.8;
		this.xMapa = anchoFondo / 2;
		this.yMapa = 250;
		this.entorno.iniciar(); // Inicia el juego!
		inicializarPiso();
		generarIslasFlotantes();
	}
	
	//METODOS DE COMPORTAMIENTO---------------------------------------------------------------------------
    public void dibujar(Entorno e) {
        e.dibujarImagen(this.imagen, this.xMapa, this.yMapa, 0, 0.8); //(imagen, coordenada X, coordenada Y, ángulo, escala)
    }
    
    //--------------------------------------------------------------------- Mapa
    private void inicializarPiso() {
		int posX = 100;
		int anchoIsla = 300;
		int separacion = 80; // El hueco para que la princesa caiga
		for (int i = 0; i < 12; i++) {
			this.islas[i] = new Isla(posX, 490, anchoIsla, 50);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacion; // sumando el ancho de la isla que acabamos de crear + el hueco 
		} 			
	}
    
    private void generarIslasFlotantes() {
    	int posX=500;
    	int posYAnterior = 320;
    	for (int i = 12; i < 30; i++) {
        	int anchoIsla = ThreadLocalRandom.current().nextInt(150, 250);
        	int variacionY = ThreadLocalRandom.current().nextInt(-110, 80);
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
     // Mueve al jefe junto con el mapa para que no quede fijo en pantalla
        if (jefe != null) {
            jefe.moverConMapa(direccion);
        }	
        
     // Mueve los enemigos junto con el mapa cuando se hace scroll
        for (int i = 0; i < enemigo.length; i++) {
            if (enemigo[i] != null) {
                enemigo[i].moverConMapa(direccion);
            }
        }
        // Mueve el poder junto con el mapa cuando se hace scroll
        if (poder != null) {
            poder.moverConMapa(direccion);
        }
        
        this.xMapa+=direccion; //Muevo el fondo tambien
    }
    
    //--------------------	------------------------------------------------- Estado interno del juego
	public void tick() {	
		if(this.princesa.getVidas()>0 && this.ganar==0) {
			boolean tocandoElPiso = false;
			
			//Comprobar si está parada sobre una isla
			for(int i = 0; i < this.islas.length; i++) {
	            if(this.islas[i] != null) {
	                if (this.princesa.paradaSobreIsla(this.islas[i])) {
	                    tocandoElPiso = true;
	                    this.princesa.setY( this.islas[i].getY() - (this.islas[i].getAlto()/2) - (this.princesa.getAlto()/2) );
	                    break;
	                }
	            }
	        }
			
			this.princesa.setTocandoElSuelo(tocandoElPiso);
			
			if (this.entorno.estaPresionada(this.entorno.TECLA_ARRIBA) || this.entorno.estaPresionada('w')) {
				this.princesa.saltar();
			}
			this.princesa.modificarFisica();

			// Control del movimiento de la Princesa----------------------------------
			if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) || entorno.estaPresionada('D')) {
			    if (this.princesa.getX() < 500) {
			        this.princesa.moverseDerecha();
			    } else {
			    	double anchoFondo = this.imagen.getWidth(null) * 0.8;
			    	
			        // Permite scrollear hasta que el borde derecho de la imagen llegue al borde derecho de la pantalla (1000)
			        if (this.xMapa + (anchoFondo / 2) > 1000) {
			            moverMapa(-2);
			            this.princesa.setMirandoDerecha(true);
			        } else if (this.princesa.getX() < 1000) {
			            this.princesa.moverseDerecha();
			        }
			    }
			}

			if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || entorno.estaPresionada('A')) {
			    if (this.princesa.getX() > 500) {
			        this.princesa.moverseIzquierda();
			    } else {
			    	double anchoFondo = this.imagen.getWidth(null) * 0.8;
			        if (this.xMapa - (anchoFondo / 2) < 0) {
			            moverMapa(2);
			            this.princesa.setMirandoDerecha(false);
			        } else if (this.princesa.getX() > 0) {
			            this.princesa.moverseIzquierda();
			        }
			    }
			}

			//Devuelve a la princesa a la isla si cayó al vacio
			if (princesa.getY() > 500 ) {
				princesa.setVidas(princesa.getVidas()-1);
				princesa.setX(princesa.getX() - 80);
				princesa.setY(450);
			}
			
			// ENEMIGOS Y DISPAROS---------------------------------------------------
			
			
			// ================= DISPAROS =================

			// Al hacer clic izquierdo se crea un disparo hacia el mouse
			if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
			    princesa.disparar(entorno.mouseX(), entorno.mouseY(), tienePoder);

			    // Si el poder está activo, consume un disparo especial
			    if (tienePoder) {
			        disparosEspeciales--;

			        // Cuando se terminan los disparos especiales se pierde el poder
			        if (disparosEspeciales <= 0) {
			            tienePoder = false;
			        }
			    }
			}

			// Actualiza el movimiento y colisiones del disparo
			princesa.actualizarDisparo(islas);
			
			// Si un disparo de la princesa golpea al jefe
			if (jefe != null && princesa.getDisparo() != null && jefe.colisiona(princesa.getDisparo())) {

			    // Si todavía no está enojado, entra en modo ataque
			    if (!jefe.estaEnojado()) {
			        jefe.enojarse(princesa);
			    }

			    // El disparo desaparece al impactar
			    princesa.eliminarDisparo();
			}
			
			// ================= ENEMIGOS =================

			// Cuenta cuántos enemigos siguen vivos
			int vivos = 0;
			for (int i = 0; i < enemigo.length; i++) {
			    if (enemigo[i] != null) {
			        vivos++;
			    }
			}

			// Genera enemigos hasta mantener un mínimo de 3 en pantalla
			while (vivos < 3) {
			    for (int i = 0; i < enemigo.length; i++) {
			        if (enemigo[i] == null) {
			            enemigo[i] = Enemigos.generarEnemigo(islas, princesa, enemigo);
			            vivos++;
			            break;
			        }
			    }
			}
			
			// Cuando se eliminan 10 enemigos aparece el jefe
			if (!jefeInvocado && jefe == null && Enemigos.getEnemigosMuertos() >= 10) {

			    // Se genera sobre una isla específica
			    Isla isla = islas[12];

			    jefe = new Jefe( isla.getX(), isla.getY() - isla.getAlto()/2 - 10);

			    // Marca que ya fue invocado
			    jefeInvocado = true;
			}
			
			// ================= PODER =================

			// Si la princesa toca el poder, lo recoge
			if (poder != null && poder.colisionaConPrincesa(princesa)) {

			    // Activa el modo de disparos especiales
			    tienePoder = true;

			    // Otorga 3 disparos especiales
			    disparosEspeciales = 3;

			    // El poder desaparece del mapa
			    poder = null;
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
			// Dibuja el disparo actual de la princesa
			princesa.dibujarDisparo(entorno);
								
			//----------------------------------------------- Dibujar enemigos
			// ================= ENEMIGOS EN PANTALLA =================

			for (int i = 0; i < enemigo.length; i++) {
			    if (enemigo[i] != null) {

			        // Actualiza movimiento del enemigo
			        enemigo[i].mover(islas);

			        // Dibuja el enemigo
			        enemigo[i].dibujar(entorno);

			        // ===== COLISIÓN DISPARO - ENEMIGO =====
			        if (princesa.getDisparo() != null &&
			            enemigo[i].colisionDisparoEnemigo(princesa.getDisparo())) {

			            // Puede generar un poder al morir
			            Poder nuevoPoder = enemigo[i].generarPoder();

			            // Solo aparece un poder si no existe otro activo
			            if (nuevoPoder != null && poder == null && !tienePoder) {
			                poder = nuevoPoder;
			            }

			            // Si el disparo es especial activa la explosión
			            if (princesa.getDisparo().esEspecial()) {
			                enemigo[i].explotar();
			            } else {
			                // Disparo normal: elimina al enemigo inmediatamente
			                enemigo[i] = null;
			            }

			            // El disparo desaparece tras impactar
			        	    princesa.eliminarDisparo();
			        	    continue;
			        	}
			        
			        // ===== COLISIÓN ENEMIGO - PRINCESA =====
			        if (enemigo[i].colisionaConPrincesa(princesa)) {

			            // La princesa pierde una vida
			            princesa.setVidas(princesa.getVidas() - 1);

			            // El enemigo desaparece
			            enemigo[i] = null;
			            continue;
			        }

			        // Elimina enemigos que salen de la pantalla
			        if (enemigo[i].fueraDePantalla()) {
			            enemigo[i] = null;
			        }

			        // Elimina enemigos cuya animación de explosión terminó
			        if (enemigo[i] != null &&
			            enemigo[i].explosionTerminada()) {
			            enemigo[i] = null;
			        }
			    }
			}
			
			// ================= JEFE FINAL =================
			if (jefe != null) {

			    // Actualiza temporizadores y animaciones internas
			    jefe.actualizar();

			    // Mientras esté tranquilo sigue a la princesa
			    if (!jefe.estaEnojado()) {

			        // Persigue a la princesa
			        jefe.seguirPrincesa(princesa, islas);

			        // Decide cuándo saltar entre plataformas
			        jefe.verificarSalto(islas);

			        // Evita quedar fuera del mapa reapareciendo
			        jefe.reaparecer(islas, princesa);
			    }

			    // Si recibe un disparo pasa al modo enojado
			    if (!jefe.estaEnojado() &&
			        princesa.getDisparo() != null &&
			        jefe.colisiona(princesa.getDisparo())) {

			        jefe.enojarse(princesa);

			        // El disparo desaparece tras el impacto
			        princesa.eliminarDisparo();
			    }

			    // ================= DISPAROS DEL JEFE =================

			    // Solo ataca cuando está enojado
			    if (jefe.estaEnojado()) {

			        // Genera nuevos disparos periódicamente
			        jefe.generarDisparos();

			        // Actualiza movimiento y colisiones de los disparos
			        jefe.actualizarDisparos(islas);

			        // Si un disparo impacta a la princesa pierde una vida
			        if (jefe.colisionDisparoPrincesa(princesa)) {
			            princesa.setVidas(princesa.getVidas() - 1);
			        }
			    }

			    // Dibuja al jefe en pantalla
			    jefe.dibujar(entorno);

			    // Dibuja todos los disparos activos del jefe
			    jefe.dibujarDisparos(entorno);

			    // Elimina la referencia cuando termina su ciclo de vida
			    if (jefe.desaparecio()) {
			        jefe = null;
			    }
			}
			// Dibuja el poder si existe uno activo en el mapa
			if (poder != null) {
			    poder.dibujar(entorno);
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