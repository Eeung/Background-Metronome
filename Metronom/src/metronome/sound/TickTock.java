package metronome.sound;


import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Sound{
	private AudioClip normalBeat, accentBeat;
	private MediaPlayer music;
	Sound() {
		new JFXPanel(); //이게 없으면 IllegalStateException 예외가 발생함
			
		String accentBeatFile = "/audio/Tick.wav";
        String normalBeatFile = "/audio/Tock.wav";
            
        accentBeat = new AudioClip(getClass().getResource(accentBeatFile).toString());
        normalBeat = new AudioClip(getClass().getResource(normalBeatFile).toString());
		music = new MediaPlayer(new Media(getClass().getResource("/audio/Tock.wav").toString()));
	}
	
	public void play(boolean isAccent) {
		if(isAccent) {
			accentBeat.stop();
			normalBeat.stop();
			accentBeat.play();
		} else {
			accentBeat.stop();
			normalBeat.stop();
			normalBeat.play();
		}
	}
	
	public void setVolume(int vol) {
		double volume = vol / 100.0;
		normalBeat.setVolume(volume);
		accentBeat.setVolume(volume);
	}
}