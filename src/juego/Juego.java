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
	private Image imgAtaque;
	
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
		this.imgAtaque = Herramientas.cargarImagen("img/ataque.png");
		this.estadoActual = ESTADO_MENU;
		this.reproductor = new ReproductorDeAudio();
		this.entorno.iniciar(); // Inicia el juego!
	}
	
	//METODOS DE COMPORTAMIENTO---------------------------------------------------------------------------
    public void dibujar(Entorno e, Image img, double x, double y, double esc) {
        e.dibujarImagen(img, x, y, 0, esc); //(imagen, coordenada X, coordenada Y, ángulo, escala)
    }
    public void pantallaMenu(Entorno e) {
    	this.dibujar(this.entorno, this.imgMenu,500,250, 0.3);		
    	if (this.entorno.estaPresionada('S')) {
            reiniciarJuego(); 
            this.estadoActual = ESTADO_JUGANDO;
            this.musicaMenuSonando = false;
        }
    }
    public void pantallaPausa(Entorno e) {
    	this.dibujar(this.entorno, this.imgPausa,500,250, 0.3);		
    	if (this.entorno.sePresiono('R') || this.entorno.sePresiono('r')) {
            this.estadoActual = ESTADO_MENU;
            this.musicaPausaSonando = false; 
        }
    }
    private void dibujarIndicadores(int cantidad, int posX, Image img, double escala) {
    	int separacion = 20;
		for(int x =0; x<cantidad; x++) {
			this.dibujar(this.entorno, img, posX, 50, escala);		//entorno, imagen, x, y, escala
			posX = posX + img.getWidth(null) + separacion;
		}
    }
    //--------------------------------------------------------------------- Mapa
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
        int posX = 500;
        int posYAnterior = 320;
        double anchoFondo = this.fondo.getWidth(null) * 0.7;        
        int direccionY = -1; // -1 significa que la próxima isla irá hacia arriba, 1 hacia abajo
        int islasEnMismaDireccion = 0; // Para contar cuántos escalones llevamos

        for (int i = 12; i < 30; i++) {
            int anchoIsla = ThreadLocalRandom.current().nextInt(150, 220);            
            int separacionX = ThreadLocalRandom.current().nextInt(40, 80);             
            int variacionY = ThreadLocalRandom.current().nextInt(25, 60);            //diferencia de altura
            int nuevoY = posYAnterior + (variacionY * direccionY);                   // Calculamos el nuevo Y aplicando la dirección

            if (nuevoY < 180) {                 // Si toca el techo virtual, la forzamos a bajar en la siguiente
                nuevoY = 180;
                direccionY = 1; 
                islasEnMismaDireccion = 0;
            } else if (nuevoY > 360) {                 // Si baja mucho, la forzamos a subir para que no estorbe los huecos del piso
                nuevoY = 360;
                direccionY = -1; 
                islasEnMismaDireccion = 0;
            } else {
                islasEnMismaDireccion++;                // Para hacer el zigzag: si ya hicimos 2 escalones, o si toca al azar, cambiamos de dirección
                if (islasEnMismaDireccion >= 2 || ThreadLocalRandom.current().nextBoolean()) {
                    direccionY *= -1; // Invertimos la dirección (de 1 a -1, o de -1 a 1)
                    islasEnMismaDireccion = 0;
                }
            }                    
            this.islas[i] = new Isla(posX, nuevoY, anchoIsla, 35);
            posX = posX + anchoIsla + separacionX;
            
            if(posX > anchoFondo - 280) {            // Cortar si llegamos al final del mapa (Castillo)
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
        if (jefe != null) {									// Mueve al jefe junto con el mapa 
            jefe.moverConMapa(direccion);
        }	
        for (int i = 0; i < enemigo.length; i++) {          //Movemos los enemigos junto con el mapa
            if (enemigo[i] != null) {
                enemigo[i].setX(enemigo[i].getX() + direccion); 
            }
        }   
        if (this.poder != null) {							//Movemos el orbe de poder con el mapa
            this.poder.setX(this.poder.getX() + direccion);
        }
        if (jefe != null) {				        			// Mueve al jefe junto con el mapa 
            jefe.moverConMapa(direccion);
        }	
        if (this.castillo != null) {				        // Movemos el castillo junto con el mapa
        	this.castillo.mover(direccion);
        }        
        this.xMapa+=direccion; 								//Muevo el fondo tambien
    }
    //--------------------------------------------------------------------- Estado interno del juego
	public void tick() {
		if (this.entorno.sePresiono('K') || this.entorno.sePresiono('k')) {		//Control de la funcion pausa
            if (this.estadoActual == ESTADO_JUGANDO) {
                this.estadoActual = ESTADO_PAUSA;
                this.musicaJuegoSonando = false;
            } else if (this.estadoActual == ESTADO_PAUSA) {
                this.estadoActual = ESTADO_JUGANDO;
                this.musicaPausaSonando = false;
            }
        }
		//CONTROLO LOS ESTADOS DEL JUEGO CON UN SWITCH-------------------------------------------------------
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
					
					// DISPAROS---------------------------------------------------------------------------			
					if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
					    princesa.disparar(entorno.mouseX(), entorno.mouseY(), tienePoder);
					    Herramientas.play("music/disparo.wav");
					    if (tienePoder) {
					        disparosEspeciales--;
					        // Cuando se terminan los disparos especiales se pierde el poder
					        if (disparosEspeciales <= 0) {
					            tienePoder = false;
					        }
					    }
					}
					princesa.actualizarDisparo(islas);					// Actualiza el movimiento y colisiones del disparo
					
					// Si un disparo de la princesa golpea al jefe
					if (jefe != null && princesa.getDisparo() != null && jefe.colisiona(princesa.getDisparo())) {
					    if (!jefe.estaEnojado()) {		// Si todavía no está enojado, entra en modo ataque
					        jefe.enojarse(princesa);
					    }					    
					    princesa.eliminarDisparo();		// El disparo desaparece al impactar
					}

					// ENEMIGOS Y PODER---------------------------------------------------------------------------
					int vivos = 0;									// Cuenta cuántos enemigos siguen vivos
					for (int i = 0; i < enemigo.length; i++) {
					    if (enemigo[i] != null) {
					        vivos++;
					    }
					}
					while (vivos < 3) {								// Genera enemigos hasta mantener un mínimo de 3 en pantalla
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
					    Isla isla = islas[12];			
					    jefe = new Jefe( isla.getX(), isla.getY() - isla.getAlto()/2 - 10);					    
					    jefeInvocado = true;			
					}

					if (poder != null) {					// Verifica si existe un poder activo en el mapa				
					    boolean apoyado = false;		
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

					    if (!apoyado) {					    // Si no encontró ninguna isla debajo, continúa cayendo
					        poder.setY(poder.getY() + 1);
					    }
					    if (poder != null && poder.getY() > 550) {
					        poder = null;					    // Si cae fuera del mapa, se elimina para permitir que pueda aparecer otro poder más adelante
					    }
					}

					if (poder != null && poder.colisionaConPrincesa(princesa)) {					// Si la princesa toca el poder
						 if (poder.getTipo() == 0) {						    
						    tienePoder = true;									    
						    disparosEspeciales = 5;		// Otorga 3 disparos especiales						   
					     } else {						        
						        princesa.setVidas(princesa.getVidas() + 1);
						 }
					    poder = null;					    // Elimina el poder del mapa porque ya fue recogido
					}
			
					//DIBUJAR TODO-----------------------------------------------------------
					this.dibujar(this.entorno, this.fondo,this.xMapa, this.yMapa, 0.7);		//fondo
					if (this.castillo != null) {
					    this.castillo.dibujar(this.entorno);
					    
					    if (this.castillo.colisionaConPrincesa(this.princesa)) {
					        this.ganar = 1; 
					    }
					}
					this.dibujarIndicadores(this.princesa.getVidas(),50, this.imgCorazon, 2.5); 	//cantidad, x, img, escala
					
					this.dibujarIndicadores(this.disparosEspeciales,700, this.imgAtaque, 0.1); 	//cantidad, x, img, escala	
									
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
					            if (princesa.getDisparo().esEspecial()) {
					                enemigo[i].explotar();
					            } else {					                
					                enemigo[i] = null;				// Disparo normal: elimina al enemigo inmediatamente
					            }					          
				        	    princesa.eliminarDisparo();			// El disparo desaparece tras impactar
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
					        jefe.reaparecer(islas, princesa);			// Evita quedar fuera del mapa reapareciendo
					    }
					    // Si recibe un disparo pasa al modo enojado
					    if (!jefe.estaEnojado() && princesa.getDisparo() != null && jefe.colisiona(princesa.getDisparo())) {
					        jefe.enojarse(princesa);					        
					        princesa.eliminarDisparo();		// El disparo desaparece tras el impacto
					    }

					    // ================= DISPAROS DEL JEFE =================
					    if (jefe.estaEnojado()) {					        
					        jefe.generarDisparos();			// Genera nuevos disparos periódicamente
					        jefe.actualizarDisparos(islas);
					        if (jefe.colisionDisparoPrincesa(princesa)) {
					            princesa.setVidas(princesa.getVidas() - 1);
					        }
					    }

					    jefe.dibujar(entorno);
					    jefe.dibujarDisparos(entorno);
					  
					    if (jefe.desaparecio()) {		// Elimina la referencia cuando termina su ciclo de vida
					        jefe = null;
					    }
					}					
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