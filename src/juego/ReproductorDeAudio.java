package juego;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class ReproductorDeAudio {
    private Clip clip;

    public void reproducirMusica(String ruta) {
		try {
			if (clip != null && clip.isRunning()) {
				clip.stop(); 
			}
			
			// Truco para buscar en raíz o dentro de src/ automáticamente
			File archivo = new File(ruta);
			if (!archivo.exists()) {
			    archivo = new File("src/" + ruta);
			}
			
			if (archivo.exists()) {
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(archivo));
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
			    System.out.println("Aviso: No se encontró la música " + ruta);
			}
		} catch (Exception e) {
			System.out.println("No se pudo reproducir: " + ruta);
		}
	}

	public void detenerMusica() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}
}