package metronome.sound;

import javafx.scene.shape.Circle;

public interface MetronomeStrategy extends MusicStrategy {
	void setBpm(double b);
	void setNote(int n);
	void setBeat(int b);
	void setTime(int t);
	void setIndicator(Circle[] indi);
	int getBeat_sequence();
	
}
