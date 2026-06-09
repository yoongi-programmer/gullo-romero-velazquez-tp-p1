package juego;
import java.awt.Image;
import java.util.concurrent.ThreadLocalRandom;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	private Entorno entorno;
    private Isla[] islas;
    private Princesa princesa;
	private Castillo castillo; 
	private Jefe jefe;
	private Enemigos[] enemigo;
	private Poder poder;	
	private boolean tienePoder = false;
	private boolean jefeInvocado = false;		// Evita que el jefe se genere más de una vez
	boolean tocandoElPiso;
	private int disparosEspeciales = 0;	
	private int ganar;
	
	private int estadoActual;
    private ReproductorDeAudio reproductor; 
	private double xMapa;
	private double yMapa;
	private Image fondo;
	private Image imgMenu;
	private Image imgGanar;
	private Image imgPerder;
	private Image imgPausa;
	private Image imgCorazon;
	
	private final int ESTADO_MENU =0;
	private final int ESTADO_JUGANDO = 1;
    private final int ESTADO_PAUSA = 2;
    private final int ESTADO_GANO = 3;
    private final int ESTADO_PERDIO = 4;

    private boolean musicaMenuSonando = false;
    private boolean musicaJuegoSonando = false;
    private boolean musicaPausaSonando = false;
    private boolean musicaGanoSonando = false;
    private boolean musicaPerdioSonando = false;
		
	//CONSTRUCTOR DEL JUEGO------------------------------------------------------------------------------
	Juego(double x, double y) {
		this.entorno = new Entorno(this, "Proyecto para TP", 1000, 500);
        this.fondo = Herramientas.cargarImagen("img/fondo2.png");  
		this.imgMenu= Herramientas.cargarImagen("img/menu.png");
		this.imgPausa= Herramientas.cargarImagen("img/pausa.png");
		this.imgGanar = Herramientas.cargarImagen("img/ganar.png");
		this.imgPerder = Herramientas.cargarImagen("img/perder.png");
		this.imgCorazon = Herramientas.cargarImagen("img/corazon.png");
		this.estadoActual = ESTADO_MENU;
		this.reproductor = new ReproductorDeAudio();
		this.entorno.iniciar(); // Inicia el juego!
	}
	
	//METODOS DE COMPORTAMIENTO---------------------------------------------------------------------------
    public void dibujar(Entorno e, Image img, double x, double y, double esc) {
        e.dibujarImagen(img, x, y, 0, esc); //(imagen, coordenada X, coordenada Y, ángulo, escala)
    }
    public void pantallaMenu(Entorno e) {
    	this.dibujar(this.entorno, this.imgMenu,500,250, 0.3);		//fondo
    	if (this.entorno.estaPresionada('S')) {
            reiniciarJuego(); 
            this.estadoActual = ESTADO_JUGANDO;
            this.musicaMenuSonando = false;
        }
    }
    public void pantallaPausa(Entorno e) {
    	this.dibujar(this.entorno, this.imgPausa,500,250, 0.3);		//fondo
    	if (this.entorno.sePresiono('R') || this.entorno.sePresiono('r')) {
            this.estadoActual = ESTADO_MENU;
            this.musicaPausaSonando = false; // reseteamos
        }
    }
    
    //--------------------------------------------------------------------- Mapa
    private void dibujarCorazones() {
    	int posX = 50;
    	int separacion = 20;
		for(int x =0; x<this.princesa.getVidas(); x++) {
			this.dibujar(this.entorno, this.imgCorazon, posX, 50, 2.5);		//fondo
			posX = posX + this.imgCorazon.getWidth(null) + separacion;
		}
    }
    private void inicializarPiso() {
		int posX = 180;
		int anchoIsla = 350;
		int separacion = 80; // El hueco para que la princesa caiga
		for (int i = 0; i < 12; i++) {
			this.islas[i] = new Isla(posX, 490, anchoIsla, 50);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacion; // sumando el ancho de la isla que acabamos de crear + el hueco 
		} 			
	}
    private void generarIslasFlotantes() {
    	int posX=500;
    	int posYAnterior = 320;
    	double anchoFondo = this.fondo.getWidth(null) * 0.7;
    	for (int i = 12; i < 30; i++) {
        	int anchoIsla = ThreadLocalRandom.current().nextInt(150, 250);
        	int variacionY = ThreadLocalRandom.current().nextInt(-100, 80);
        	int separacionX = ThreadLocalRandom.current().nextInt(80, 120);
        	int nuevoY = posYAnterior + variacionY;        	
        	if (nuevoY < 200) {		//si supera el alto maximo
                nuevoY = 200;
            }
            if (nuevoY > 390) {		//fijo el alto minimo
                nuevoY = 390;
            }          
			this.islas[i] = new Isla(posX, nuevoY, anchoIsla, 35);	//x, y, ancho, alto
			posX = posX + anchoIsla + separacionX; // sumando el ancho de la isla que acabamos de crear + el hueco
			if(posX>anchoFondo-250) {
				return;
			}
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
        //Movemos los enemigos junto con el mapa
        for (int i = 0; i < enemigo.length; i++) {
            if (enemigo[i] != null) {
                enemigo[i].setX(enemigo[i].getX() + direccion); 
            }
        }   
        //Movemos el orbe de poder con el mapa
        if (this.poder != null) {
            this.poder.setX(this.poder.getX() + direccion);
        }
        // Mueve al jefe junto con el mapa para que no quede fijo en pantalla
        if (jefe != null) {
            jefe.moverConMapa(direccion);
        }	
        // Movemos el castillo junto con el mapa
        if (this.castillo != null) {
        	this.castillo.mover(direccion);
        }        
        this.xMapa+=direccion; //Muevo el fondo tambien
    }
    
	public void reiniciarJuego() {
    	this.ganar = 0;
    	this.tienePoder = false;
    	this.disparosEspeciales = 0;
    	this.poder = null;
		this.islas = new Isla[30];
		this.enemigo = new Enemigos[10];
		this.princesa = new Princesa (500 , 400, 25, 35,2, 5); // (coord X, coord Y, ancho, alto, velocidad, vidas, altura de salto)  
        double anchoFondo = this.fondo.getWidth(null) * 0.7;
        this.castillo = new Castillo(anchoFondo - 100, 360);
		this.xMapa = anchoFondo / 2;
		this.yMapa = 250;
		this.jefeInvocado = false;
		inicializarPiso();
		generarIslasFlotantes();
    }
    //--------------------------------------------------------------------- Estado interno del juego

	public void tick() {
		if (this.entorno.sePresiono('K') || this.entorno.sePresiono('k')) {
            if (this.estadoActual == ESTADO_JUGANDO) {
                this.estadoActual = ESTADO_PAUSA;
                this.musicaJuegoSonando = false;
            } else if (this.estadoActual == ESTADO_PAUSA) {
                this.estadoActual = ESTADO_JUGANDO;
                this.musicaPausaSonando = false;
            }
        }
		
		switch (this.estadoActual) {
			case ESTADO_MENU:
				if (!musicaMenuSonando) {
			        this.reproductor.reproducirMusica("music/menu.wav");
			        musicaMenuSonando = true;
			        musicaJuegoSonando = false;
			        musicaPausaSonando = false;
			        musicaGanoSonando = false;
			        musicaPerdioSonando = false;
			    }
				this.pantallaMenu(this.entorno);
				break;
			case ESTADO_PAUSA:
				if (!musicaPausaSonando) {
					this.reproductor.reproducirMusica("music/pausa.wav");
			        musicaPausaSonando = true;
			    }
				this.pantallaPausa(this.entorno);
				break;
			case ESTADO_JUGANDO:
				if (!musicaJuegoSonando) {
					this.reproductor.reproducirMusica("music/juego.wav");
			        musicaJuegoSonando = true;
			        musicaMenuSonando = false;
			        musicaPausaSonando = false;
			        musicaGanoSonando = false;
			        musicaPerdioSonando = false;
			    }
				
				if(this.princesa.getVidas()>0 && this.ganar==0) {
					boolean tocandoElPiso = false;
					//Comprobar si está parada sobre una isla
					for(int i = 0; i < this.islas.length; i++) {
			            if(this.islas[i] != null) {
			                if (this.princesa.paradaSobreIsla(this.islas[i])) {
			                    tocandoElPiso = true;
			                    this.princesa.setY( this.islas[i].getY() - (this.islas[i].getAlto()/2) - (this.princesa.getAlto()/2) );    
			                }
			                //Comprobar si choca la cabeza con el borde inferior de una isla
				            if (this.princesa.getVelocidadY() < 0 && this.princesa.chocaCabezaConIsla(this.islas[i])) {
				                this.princesa.setY(this.islas[i].getY() + (this.islas[i].getAlto() / 2) + (this.princesa.getAlto() / 2) + 18); //seteo Y para que choque
				                this.princesa.setVelocidadY(1); 
				                Herramientas.play("music/colision.wav");
				            }
				            // Comprobar si choca con lado derecho
			                if (this.princesa.chocaLadoDerechoConIsla(this.islas[i])) {
			                	if (this.princesa.getX() < 500) { 
			                		this.princesa.setX(this.islas[i].getX() - (this.islas[i].getAncho() / 2) - (this.princesa.getAncho() / 2)); //le seteo el X para que no camine por la isla
			                	} else {
			                		// pongo lado inverso para cancelar el scroll de pantalla
			                		moverMapa(2); 
			                	}
			                }
			                //Comprobar si choca con lado izquierdo
			                if (this.princesa.chocaLadoIzquierdoConIsla(this.islas[i])) {
			                	if (this.princesa.getX() > 500) {
			                		this.princesa.setX(this.islas[i].getX() + (this.islas[i].getAncho() / 2) + (this.princesa.getAncho() / 2)); //le seteo el X para que no camine por la isla
			                	} else {
			                		// pongo el lado inverso para cancelar scroll de pantalla
			                		moverMapa(-2);
			                	}
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
					    	double anchoFondo = this.fondo.getWidth(null) * 0.7;
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
					    	double anchoFondo = this.fondo.getWidth(null) * 0.7;
					        if (this.xMapa - (anchoFondo / 2) < 0) {
					            moverMapa(2);
					            this.princesa.setMirandoDerecha(false);
					        } else if (this.princesa.getX() > 0) {
					            this.princesa.moverseIzquierda();
					        }
					    }
					}
					if (this.entorno.estaPresionada('K')){
						this.pantallaPausa(this.entorno);
					}
					//Devuelve a la princesa a la isla si cayó al vacio
					if (princesa.getY() > 500 ) {
						princesa.setVidas(princesa.getVidas()-1);
						princesa.setX(princesa.getX() - 80);
						princesa.setY(450);
					}
					
					// ENEMIGOS Y DISPAROS---------------------------------------------------			
					// Al hacer clic izquierdo se crea un disparo hacia el mouse
					if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
					    princesa.disparar(entorno.mouseX(), entorno.mouseY(), tienePoder);
					    Herramientas.play("music/disparo.wav");
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
					    if (!jefe.estaEnojado()) {		// Si todavía no está enojado, entra en modo ataque
					        jefe.enojarse(princesa);
					    }					    
					    princesa.eliminarDisparo();		// El disparo desaparece al impactar
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
					    Isla isla = islas[12];			// Se genera sobre una isla específica
					    jefe = new Jefe( isla.getX(), isla.getY() - isla.getAlto()/2 - 10);					    
					    jefeInvocado = true;			// Marca que ya fue invocado
					}

					// Verifica si existe un poder activo en el mapa
					if (poder != null) {				
					    boolean apoyado = false;		// Indica si el poder está apoyado sobre alguna isla
					    // Recorre todas las islas del mapa
					    for (int i = 0; i < islas.length; i++) {
					        if (islas[i] != null) {
					            // Comprueba si la posición horizontal del poder está dentro de los límites de la isla
					            boolean dentroDelAncho = poder.getX() >= islas[i].getX() - islas[i].getAncho()/2 && poder.getX() <= islas[i].getX() + islas[i].getAncho()/2;
					            // Calcula la posición superior de la isla donde debería quedar apoyado el poder
					            double techoIsla = islas[i].getY() - islas[i].getAlto()/2 - 20; // Mitad aproximada del tamaño del poder
					            // Si el poder está sobre la isla y llegó a su superficie
					            if (dentroDelAncho && poder.getY() >= techoIsla) {					                
					                poder.setY(techoIsla);				// Lo coloca exactamente sobre la isla					               
					                apoyado = true;						// Marca que ya encontró apoyo
					                break;
					            }
					        }
					    }

					    // Si no encontró ninguna isla debajo, continúa cayendo
					    if (!apoyado) {
					        poder.setY(poder.getY() + 1);
					    }
					    // Si cae fuera del mapa, se elimina para permitir que pueda aparecer otro poder más adelante
					    if (poder != null && poder.getY() > 550) {
					        poder = null;
					    }
					}

					// Si la princesa toca el poder
					if (poder != null && poder.colisionaConPrincesa(princesa)) {
						 if (poder.getTipo() == 0) {						    
						    tienePoder = true;			// Activa los disparos especiales						    
						    disparosEspeciales = 3;		// Otorga 3 disparos especiales						   
					     } else {						        
						        princesa.setVidas(princesa.getVidas() + 1);
						 }
					    // Elimina el poder del mapa porque ya fue recogido
					    poder = null;
					}
					
					//DIBUJAR TODO-----------------------------------------------------------
					this.dibujar(this.entorno, this.fondo,this.xMapa, this.yMapa, 0.7);		//fondo
					if (this.castillo != null) {
					    this.castillo.dibujar(this.entorno);
					    
					    if (this.castillo.colisionaConPrincesa(this.princesa)) {
					        this.ganar = 1; 
					    }
					}
					this.dibujarCorazones();
					//----------------------------------------------- Dibujar islas
					for(int i = 0; i < this.islas.length; i++) {
						if(this.islas[i] != null) {
							this.islas[i].dibujar(this.entorno);
						}
					}
					//----------------------------------------------- Dibujar princesa
					princesa.dibujarse(this.entorno);			
					// ---------------------------------------------- Dibujar disparo
					princesa.dibujarDisparo(entorno);
					//----------------------------------------------- Dibujar enemigos
					for (int i = 0; i < enemigo.length; i++) {
					    if (enemigo[i] != null) {
					        enemigo[i].mover(islas);
					        enemigo[i].dibujar(entorno);

					        // ===== COLISIÓN DISPARO - ENEMIGO =====
					        if (princesa.getDisparo() != null && enemigo[i].colisionDisparoEnemigo(princesa.getDisparo())) {
					        	Herramientas.play("music/disparo2.wav");
					            Poder nuevoPoder = enemigo[i].generarPoder();				// Puede generar un poder al morir
					            if (nuevoPoder != null && poder == null && !tienePoder) { 	// Solo aparece un poder si no existe otro activo
					                poder = nuevoPoder;
					            }

					            // Si el disparo es especial activa la explosión
					            if (princesa.getDisparo().esEspecial()) {
					                enemigo[i].explotar();
					            } else {					                
					                enemigo[i] = null;				// Disparo normal: elimina al enemigo inmediatamente
					            }
					            // El disparo desaparece tras impactar
				        	    princesa.eliminarDisparo();
				        	    continue;
					        }
					        
					        // ===== COLISIÓN ENEMIGO - PRINCESA =====					        				        
					        if (enemigo[i].colisionaConPrincesa(princesa)) {					            
					            princesa.setVidas(princesa.getVidas() - 1);		// La princesa pierde una vida					            
					            enemigo[i] = null;								// El enemigo desaparece
					            continue;
					        }

					        // Elimina enemigos que salen de la pantalla
					        if (enemigo[i].fueraDePantalla()) {
					            enemigo[i] = null;
					        }
					        // Elimina enemigos cuya animación de explosión terminó
					        if (enemigo[i] != null && enemigo[i].explosionTerminada()) {
					            enemigo[i] = null;
					        }
					    }
					}
					// ================= JEFE FINAL =================
					if (jefe != null) {
					    jefe.actualizar();					   
					    if (!jefe.estaEnojado()) {			// Mientras esté tranquilo sigue a la princesa
					        jefe.seguirPrincesa(princesa, islas);
					        jefe.verificarSalto(islas);
					        // Evita quedar fuera del mapa reapareciendo
					        jefe.reaparecer(islas, princesa);
					    }
					    // Si recibe un disparo pasa al modo enojado
					    if (!jefe.estaEnojado() && princesa.getDisparo() != null && jefe.colisiona(princesa.getDisparo())) {
					        jefe.enojarse(princesa);					        
					        princesa.eliminarDisparo();		// El disparo desaparece tras el impacto
					    }

					    // ================= DISPAROS DEL JEFE =================
					    if (jefe.estaEnojado()) {					        
					        jefe.generarDisparos();			// Genera nuevos disparos periódicamente
					        // Actualiza movimiento y colisiones de los disparos
					        jefe.actualizarDisparos(islas);
					        // Si un disparo impacta a la princesa pierde una vida
					        if (jefe.colisionDisparoPrincesa(princesa)) {
					            princesa.setVidas(princesa.getVidas() - 1);
					        }
					    }

					    jefe.dibujar(entorno);
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
				}else {
					if(this.ganar==1) {
						this.estadoActual = ESTADO_GANO;
					}else if(this.princesa.getVidas()==0) {
						this.estadoActual = ESTADO_PERDIO;
					}
				}
				break;
			case ESTADO_GANO:
				this.dibujar(this.entorno, this.imgGanar,500,250, 0.3);		//fondo
				if (!musicaGanoSonando) {
					this.reproductor.reproducirMusica("music/ganar.wav");
			        musicaGanoSonando = true;
			        musicaPerdioSonando = false;
			        musicaJuegoSonando = false;
			        musicaMenuSonando = false;
			        musicaPausaSonando = false;
				}
				if (this.entorno.estaPresionada('M')) {
		    		this.estadoActual = ESTADO_MENU;
				}
				if (this.entorno.estaPresionada('J')) {
					reiniciarJuego(); 
		    		this.estadoActual = ESTADO_JUGANDO;
				}
				break;
			case ESTADO_PERDIO:
				this.dibujar(this.entorno, this.imgPerder,500,250, 0.3);		//fondo
				if (!musicaPerdioSonando) {
					this.reproductor.reproducirMusica("music/perder.wav");
			        musicaGanoSonando = false;
			        musicaPerdioSonando = true;
			        musicaJuegoSonando = false;
			        musicaMenuSonando = false;
			        musicaPausaSonando = false;
			    }
				if (this.entorno.estaPresionada('N')) {
		    		this.estadoActual = ESTADO_MENU;
				}
				if (this.entorno.estaPresionada('Y')) {
					reiniciarJuego(); 
		    		this.estadoActual = ESTADO_JUGANDO;
		    		
				}
				break;
		}
	}

	
	//-----------------------------------------------------------------------MAIN
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Juego juego = new Juego(1000,250);
	}
}