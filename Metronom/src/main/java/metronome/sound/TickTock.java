package metronome.sound;


import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import metronome.Settings;

public class TickTock{
	private AudioClip normalBeat, accentBeat;
	TickTock() {
		new JFXPanel(); //이게 없으면 IllegalStateException 예외가 발생함
			
		String accentBeatFile = "/audio/Tick.wav";
        String normalBeatFile = "/audio/Tock.wav";
            
        accentBeat = new AudioClip(getClass().getResource(accentBeatFile).toString());
        normalBeat = new AudioClip(getClass().getResource(normalBeatFile).toString());
        /** 소리 불러오기 */
        setVolume(0);
        play(true);
        play(false);
        
        setVolume( Settings.getVolume() );
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
		double volume = vol / 200.0;
		normalBeat.setVolume(volume);
		accentBeat.setVolume(volume);
	}
}