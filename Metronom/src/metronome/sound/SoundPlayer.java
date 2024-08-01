package metronome.sound;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.shape.Circle;
import metronome.Controller;

public class SoundPlayer{
	private final static Controller root = Controller.getInstance();
	
	private double bpm=-1;
	private int pre_offset = Integer.MIN_VALUE;

	private int offset = 0;
	private Beat beat = new Beat();
	private static int beat_sequence, bit;
	
	private ScheduledExecutorService beatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> beatScheduled;
	private Runnable play;
	private Circle[] indicator;
		
	public SoundPlayer() {
		beat_sequence = 0;
	}
	
	public void start() {
		if(bpm<0) return;
		
		long nanoPeriod = (long) (60_000_000_000L/(bpm*bit/4));
		play = () -> {
			beat_sequence %= bit;
			beat.play( root.isAccent(beat_sequence) );
			Platform.runLater( () -> indicator[beat_sequence++].requestFocus() );
		};
		beatScheduled = beatExecutor.scheduleAtFixedRate(play, offset*1_000_000L, nanoPeriod, TimeUnit.NANOSECONDS);
		
		pre_offset = offset;
		offset = 0;
	}
	
	public void scheduleCancel() {
		beatScheduled.cancel(true);
		beatScheduled = null;
		pre_offset = Integer.MIN_VALUE;
		System.out.println("종료 됨!");
	}
	
	private void scheduleRestart() {
		long nanoPeriod = (long) (60_000_000_000L/(bpm*bit/4));	//바뀐 bpm에 따라 간격 구하기
		
		Long remainDelay = beatScheduled.getDelay(TimeUnit.NANOSECONDS);	//다음 비트가 재생될 때까지 남은 딜레이 가져오기
		Long nanoOff = offset*1_000_000L;
		remainDelay = remainDelay + nanoOff < 0 ? 0 : remainDelay + nanoOff; //오프셋만큼 밀기 (만약, 오프셋이 음수일 때, 남은 딜레이보다 절댓값이 크면 0으로 고정)
		
		beatScheduled.cancel(true);	//스케쥴 중지
		//바뀐 정보대로 스케쥴 재시작
		beatScheduled = beatExecutor.scheduleAtFixedRate(play, remainDelay, nanoPeriod, TimeUnit.NANOSECONDS);
	}
	
	public void setBpm(double bpm) {
		this.bpm = bpm;
		//매트로놈 중간에 bpm 바꿀 때,
		if(beatScheduled != null)
			scheduleRestart();
	}
	
	public void setOffset(int off) {
		if(pre_offset == Integer.MIN_VALUE) {
			offset = off;
			return;
		}
		offset = off - pre_offset;
		pre_offset = off;
		//매트로놈 중간에 오프셋 바꿀 때,
		if(beatScheduled != null)
			scheduleRestart();
	}
	
	public void setVolume(int vol) {
		beat.setVolume(vol);
	}
	
	public void setBit(int b) {
		bit = b;
	}
	
	public void setIndicator(Circle[] indicator) {
		this.indicator = indicator;
	}
	
	public static int getBeat_sequence() {
		return (beat_sequence-1)%bit;
	}
}