package metronome;


import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Beat{
	private AudioClip normalBeat, accentBeat;
	private MediaPlayer music;
	Beat() {
		new JFXPanel(); //이게 없으면 IllegalStateException 예외가 발생함
			
		String accentBeatFile = "/audio/Tock.wav";
        String normalBeatFile = "/audio/Tock.wav";
            
        accentBeat = new AudioClip(getClass().getResource(accentBeatFile).toString());
        normalBeat = new AudioClip(getClass().getResource(normalBeatFile).toString());
		music = new MediaPlayer(new Media(getClass().getResource("/audio/Tock.wav").toString()));
	}
	
	public void play(int idx) {
		if(idx==0) {
			accentBeat.stop();
			accentBeat.play();
		} else {
			normalBeat.stop();
			normalBeat.play();
		}
	}
	
	public void setVolume(int vol) {
		double volume = (double)vol / 100;
		normalBeat.setVolume(volume);
		accentBeat.setVolume(volume);
	}
}