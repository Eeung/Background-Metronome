package metronome.sound;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer implements MusicStrategy {
	private MediaPlayer music = new MediaPlayer(new Media(getClass().getResource("/audio/Tick.wav").toString()));
	
	private static ScheduledExecutorService musicExecutor = Executors.newSingleThreadScheduledExecutor();
	private static ScheduledFuture<?> musicScheduled;
	String filePath = "";
	
	private int offset, pre_offset = Integer.MIN_VALUE;
	private int fast_retry = 0;
	private double volume;
	
	@Override
	public void play() {
		if(filePath == null) return;
		new JFXPanel();
		
		musicScheduled = musicExecutor.schedule(() -> music.play(), fast_retry+offset, TimeUnit.MILLISECONDS);
		
		fast_retry = 0;
		pre_offset = offset;
		offset = 0;
	}

	@Override
	public void stop() {
		//musicScheduled.cancel(true);
		musicScheduled = null;
		pre_offset = Integer.MIN_VALUE;
		music.stop();
	}
	
	private void restart() {
		long realDelay = musicScheduled.getDelay(TimeUnit.MILLISECONDS);
		realDelay = realDelay<0 ? offset : realDelay+offset;
		System.out.println(realDelay);
		if(realDelay>=0) {
			music.pause();
			
			musicScheduled = musicExecutor.schedule(() -> music.play(), realDelay, TimeUnit.MILLISECONDS);
		}
		/*
		 * offset이 양수면, 음악을 멈추고 오프셋만큼 기다리기
		 * 음수면, 재생까지 남은 딜레이를 구한다음, 오프셋만큼 뺀다. 음수가 되면 0으로 고정하고 밑을 실행한다.
		 * 현재 재생 위치를 구하고 오프셋 만큼 뺀 위치에서 다시 시작한다. 만약 시작위치가 음수가 되면 0에서 시작한다.
		 */
	}
	
	@Override
	public void setOffset(int off) {
		if(pre_offset == Integer.MIN_VALUE) {
			offset = off;
			return;
		} 
		offset = off - pre_offset;
		pre_offset = off;
		
		if(musicScheduled != null) {
			restart();
		}
	}

	@Override
	public void setVolume(int vol) {
		volume = vol / 100.0;
		if(music != null) music.setVolume(volume);
	}

	@Override
	public void activateFastRetry() {
		fast_retry = 1000;
	}
	
	public void setFilePath(String path) {
		filePath = path;
		music = new MediaPlayer(new Media(new File(path).toURI().toString()));
		music.setVolume(volume);
	}

	private static MusicPlayer I = new MusicPlayer();
	private MusicPlayer() {}
	public static MusicPlayer getInstance() {
		return I;
	}
}
