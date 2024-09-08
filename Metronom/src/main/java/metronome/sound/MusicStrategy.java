package metronome.sound;

public interface MusicStrategy {
	void play();
	void stop();
	void setOffset(int offset);
	void setVolume(int vol);
	void activateFastRetry();
}
