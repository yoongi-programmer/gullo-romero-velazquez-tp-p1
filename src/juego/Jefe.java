package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Jefe {
    private double x;
    private double y;
    private int ancho = 20;
    private int alto = 20;
    private Image[] frames;    		// Variables para la animación
    private int frameActual;
    private int contadorAnimacion;
    private int tiempoVida;			// Tiempo que permanece vivo (1800 frames = 30 segundos)
    private double velocidadY = 0;
    private double gravedad = 0.2;
    private boolean tocandoSuelo = false;
    private boolean enojado = false;
    private boolean vaDerecha = true;
    private DisparoJefe[] disparos;
    private int contadorDisparos;	// Controla cada cuánto dispara

    public Jefe(double x, double y) {
        this.x = x;
        this.y = y;
        disparos = new DisparoJefe[50];
        this.tiempoVida = 1800;
        this.contadorDisparos = 0;
        frames = new Image[2];        // Carga los frames de animación
        frames[0] = Herramientas.cargarImagen("img/pato1.png");
        frames[1] = Herramientas.cargarImagen("img/pato2.png");
        frameActual = 0;
        contadorAnimacion = 0;
    }

    public void actualizar() {        
        tiempoVida--;		// Reduce el tiempo de vida en cada frame
        contadorAnimacion++;        // Actualiza animación

        if (contadorAnimacion >= 10) {
            frameActual = (frameActual + 1) % 2;
            contadorAnimacion = 0;
        }        
        contadorDisparos++;			// Cuenta los frames para disparar
    }

    public void dibujar(Entorno entorno) {        // Si está enojado se dibuja más grande
        double escala;
        if (enojado) {
            escala = 2;
        } else {
            escala = 0.8;
        }
        entorno.dibujarImagen(frames[frameActual], x, y - 25, 0, escala);
    }

    public boolean debeDisparar() {
        if (contadorDisparos >= 20) {
            contadorDisparos = 0;
            return true;
        }
        return false;
    }

    public void generarDisparos() {
        if (debeDisparar()) {
            for (int i = 0; i < disparos.length; i++) {                
                if (disparos[i] == null) {					// Busca una posición libre en el arreglo
                    // Aparece desde una posición aleatoria arriba de la pantalla
                    double posX = Math.random() * 1000;
                    disparos[i] = new DisparoJefe(posX, -20);
                    break;
                }
            }
        }
    }

    public void actualizarDisparos(Isla[] islas) {
        for (int i = 0; i < disparos.length; i++) {
            if (disparos[i] != null) {
                disparos[i].mover();                // Mueve el disparo
                // Verifica colisiones con las islas
                for (int j = 0; j < islas.length; j++) {
                    if (islas[j] != null && disparos[i].colisionaConIsla(islas[j])) {
                        disparos[i] = null;
                        break;
                    }
                }
                // Elimina disparos fuera de pantalla
                if (disparos[i] != null && disparos[i].fueraDePantalla()) {
                    disparos[i] = null;
                }
            }
        }
    }

    public void dibujarDisparos(Entorno entorno) {
        for (int i = 0; i < disparos.length; i++) {
            if (disparos[i] != null) {
                disparos[i].dibujar(entorno);
            }
        }
    }

    public boolean colisiona(Disparo disparo) {    // Detecta si un disparo de la princesa golpea al jefe
        double radio = disparo.getDiametro() / 2.0;
        return disparo.getPosicionX() + radio >= x - ancho / 2 && disparo.getPosicionX() - radio <= x + ancho / 2 && disparo.getPosicionY() + radio >= y - alto / 2 && disparo.getPosicionY() - radio <= y + alto / 2;
    }

    public boolean colisionDisparoPrincesa(Princesa princesa) {
        for (int i = 0; i < disparos.length; i++) {
            if (disparos[i] != null && disparos[i].colisionaConPrincesa(princesa)) {                
                disparos[i] = null;		// El disparo desaparece al impactar
                return true;
            }
        }
        return false;
    }

    // Hace que el jefe persiga a la princesa
    public void seguirPrincesa(Princesa princesa, Isla[] islas) {
        double velocidad = 1;
        if (princesa.getX() > x) {
            x += velocidad;
            vaDerecha = true;
        }
        else if (princesa.getX() < x) {
            x -= velocidad;
            vaDerecha = false;
        }
        tocandoSuelo = false;

        // Verifica si está parado sobre una isla
        for (int i = 0; i < islas.length; i++) {
            if (islas[i] != null) {
                double techoIsla = islas[i].getY() - islas[i].getAlto()/2 - alto/2;
                boolean encimaDeIsla = x >= islas[i].getX() - islas[i].getAncho()/2 &&
                x <= islas[i].getX() + islas[i].getAncho()/2;

                if (encimaDeIsla &&
                    y + alto/2 >= islas[i].getY() - islas[i].getAlto()/2 &&
                    y < islas[i].getY()) {
                    // Se apoya sobre la isla
                    y = techoIsla;
                    velocidadY = 0;
                    tocandoSuelo = true;
                    break;
                }
            }
        }
        if (!tocandoSuelo) {        // Si no tiene piso debajo, cae
            velocidadY += gravedad;
            y += velocidadY;
        }
        if (y > 500) {        		// Si cae al vacío reaparece
            reaparecer(islas, princesa);
        }
    }

    public void verificarSalto(Isla[] islas) {    // Hace saltar al jefe cuando se termina una isla
        if (!tocandoSuelo) {
            return;
        }
        boolean hayPisoAdelante = false;
        double adelante;

        if (vaDerecha) {
            adelante = x + 40;
        } else {
            adelante = x - 40;
        }

        for (int i = 0; i < islas.length; i++) {        // Busca si existe una isla adelante
            if (islas[i] != null) {
                if (adelante >= islas[i].getX() - islas[i].getAncho()/2 &&
                    adelante <= islas[i].getX() + islas[i].getAncho()/2) {
                    hayPisoAdelante = true;
                    break;
                }
            }
        }
        if (!hayPisoAdelante) {        // Si no hay piso, salta
            velocidadY = -9;
        }
    }

    // Reaparece cerca de la princesa cuando cae al vacío
    public void reaparecer(Isla[] islas, Princesa princesa) {
        if (y > 500) {
            Isla islaMasCercana = null;
            double distanciaMinima = Double.MAX_VALUE;

            for (int i = 0; i < islas.length; i++) {            // Busca la isla más cercana a la princesa
                if (islas[i] != null) {
                    double distancia =
                    Math.abs(islas[i].getX() - princesa.getX());
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        islaMasCercana = islas[i];
                    }
                }
            }
            if (islaMasCercana != null) {                // Aparece cerca de la princesa
                x = princesa.getX() + 60;
                y = islaMasCercana.getY() - islaMasCercana.getAlto()/2 - alto/2 - 5;
                velocidadY = 0;
                tocandoSuelo = true;
            }
        }
    }

    public boolean desaparecio() {
        return tiempoVida <= 0;
    }

    public void moverConMapa(double direccion) {
        x += direccion;
        // También mueve todos sus disparos
        for (int i = 0; i < disparos.length; i++) {
            if (disparos[i] != null) {
                disparos[i].moverConMapa(direccion);
            }
        }
    }

    public boolean estaEnojado() {
        return enojado;
    }

    public void enojarse(Princesa princesa) {
        enojado = true;
        ancho = 120;        // Aumenta de tamaño
        alto = 120;
        y = 80;        		// Se posiciona arriba de la pantalla
        x = princesa.getX();
        velocidadY = 0;		       // Deja de verse afectado por la gravedad
        tocandoSuelo = false;
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
}