package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Jefe {

    // Posición del jefe en pantalla
    private double x;
    private double y;

    // Tamaño del jefe
    private int ancho = 20;
    private int alto = 20;

    // Variables para la animación
    private Image[] frames;
    private int frameActual;
    private int contadorAnimacion;

    // Tiempo que permanece vivo (1800 frames ≈ 30 segundos)
    private int tiempoVida;

    // Variables de movimiento y gravedad
    private double velocidadY = 0;
    private double gravedad = 0.2;
    private boolean tocandoSuelo = false;

    // Indica si el jefe entró en modo enojado
    private boolean enojado = false;

    // Dirección en la que se mueve
    private boolean vaDerecha = true;

    // Arreglo que almacena los disparos del jefe
    private DisparoJefe[] disparos;

    // Controla cada cuánto dispara
    private int contadorDisparos;

    public Jefe(double x, double y) {

        // Posición inicial
        this.x = x;
        this.y = y;

        // Se reserva espacio para hasta 50 disparos
        disparos = new DisparoJefe[50];

        // Duración total del jefe
        this.tiempoVida = 1800;

        // Inicializa contador de disparos
        this.contadorDisparos = 0;

        // Carga los frames de animación
        frames = new Image[2];

        frames[0] = Herramientas.cargarImagen("img/pato1.png");
        frames[1] = Herramientas.cargarImagen("img/pato2.png");

        frameActual = 0;
        contadorAnimacion = 0;
    }

    public void actualizar() {

        // Reduce el tiempo de vida en cada frame
        tiempoVida--;

        // Actualiza animación
        contadorAnimacion++;

        if (contadorAnimacion >= 10) {
            frameActual = (frameActual + 1) % 2;
            contadorAnimacion = 0;
        }

        // Cuenta los frames para disparar
        contadorDisparos++;
    }

    public void dibujar(Entorno entorno) {

        // Si está enojado se dibuja más grande
        double escala;

        if (enojado) {
            escala = 2;
        } else {
            escala = 1;
        }
        entorno.dibujarImagen(frames[frameActual], x, y, 0, escala);
    }

    // Determina cuándo debe generar un nuevo disparo
    public boolean debeDisparar() {

        if (contadorDisparos >= 20) {
            contadorDisparos = 0;
            return true;
        }
        return false;
    }

    // Crea nuevos disparos
    public void generarDisparos() {

        if (debeDisparar()) {

            for (int i = 0; i < disparos.length; i++) {

                // Busca una posición libre en el arreglo
                if (disparos[i] == null) {

                    // Aparece desde una posición aleatoria arriba de la pantalla
                    double posX = Math.random() * 1000;
                    disparos[i] = new DisparoJefe(posX, -20);
                    break;
                }
            }
        }
    }

    // Actualiza todos los disparos del jefe
    public void actualizarDisparos(Isla[] islas) {

        for (int i = 0; i < disparos.length; i++) {

            if (disparos[i] != null) {

                // Mueve el disparo
                disparos[i].mover();

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

    // Dibuja todos los disparos activos
    public void dibujarDisparos(Entorno entorno) {

        for (int i = 0; i < disparos.length; i++) {

            if (disparos[i] != null) {
                disparos[i].dibujar(entorno);
            }
        }
    }

    // Detecta si un disparo de la princesa golpea al jefe
    public boolean colisiona(Disparo disparo) {

        double radio = disparo.getDiametro() / 2.0;
        return disparo.getPosicionX() + radio >= x - ancho / 2 && disparo.getPosicionX() - radio <= x + ancho / 2 && disparo.getPosicionY() + radio >= y - alto / 2 && disparo.getPosicionY() - radio <= y + alto / 2;
    }

    // Detecta si algún disparo del jefe golpea a la princesa
    public boolean colisionDisparoPrincesa(Princesa princesa) {

        for (int i = 0; i < disparos.length; i++) {

            if (disparos[i] != null && disparos[i].colisionaConPrincesa(princesa)) {

                // El disparo desaparece al impactar
                disparos[i] = null;
                return true;
            }
        }
        return false;
    }

    // Hace que el jefe persiga a la princesa
    public void seguirPrincesa(Princesa princesa, Isla[] islas) {

        double velocidad = 1;

        // Movimiento horizontal hacia la princesa
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

                double techoIsla =
                    islas[i].getY()
                    - islas[i].getAlto()/2
                    - alto/2;

                boolean encimaDeIsla =
                    x >= islas[i].getX() - islas[i].getAncho()/2 &&
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

        // Si no tiene piso debajo, cae
        if (!tocandoSuelo) {
            velocidadY += gravedad;
            y += velocidadY;
        }

        // Si cae al vacío reaparece
        if (y > 500) {
            reaparecer(islas, princesa);
        }
    }

    // Hace saltar al jefe cuando se termina una isla
    public void verificarSalto(Isla[] islas) {

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

        // Busca si existe una isla adelante
        for (int i = 0; i < islas.length; i++) {

            if (islas[i] != null) {

                if (adelante >= islas[i].getX() - islas[i].getAncho()/2 &&
                    adelante <= islas[i].getX() + islas[i].getAncho()/2) {

                    hayPisoAdelante = true;
                    break;
                }
            }
        }

        // Si no hay piso, salta
        if (!hayPisoAdelante) {
            velocidadY = -9;
        }
    }

    // Reaparece cerca de la princesa cuando cae al vacío
    public void reaparecer(Isla[] islas, Princesa princesa) {

        if (y > 500) {

            Isla islaMasCercana = null;
            double distanciaMinima = Double.MAX_VALUE;

            // Busca la isla más cercana a la princesa
            for (int i = 0; i < islas.length; i++) {

                if (islas[i] != null) {

                    double distancia =
                        Math.abs(islas[i].getX() - princesa.getX());

                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        islaMasCercana = islas[i];
                    }
                }
            }

            if (islaMasCercana != null) {

                // Aparece cerca de la princesa
                x = princesa.getX() + 60;

                y = islaMasCercana.getY()
                    - islaMasCercana.getAlto()/2
                    - alto/2
                    - 5;

                velocidadY = 0;
                tocandoSuelo = true;
            }
        }
    }

    // Indica si el jefe ya agotó su tiempo de vida
    public boolean desaparecio() {
        return tiempoVida <= 0;
    }

    // Mueve al jefe junto con el desplazamiento del mapa
    public void moverConMapa(double direccion) {

        x += direccion;

        // También mueve todos sus disparos
        for (int i = 0; i < disparos.length; i++) {

            if (disparos[i] != null) {
                disparos[i].moverConMapa(direccion);
            }
        }
    }

    // Devuelve si está en modo enojado
    public boolean estaEnojado() {
        return enojado;
    }

    // Activa el modo enojado
    public void enojarse(Princesa princesa) {

        enojado = true;

        // Aumenta de tamaño
        ancho = 120;
        alto = 120;

        // Se posiciona arriba de la pantalla
        y = 80;
        x = princesa.getX();

        // Deja de verse afectado por la gravedad
        velocidadY = 0;
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