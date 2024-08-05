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
	
	private static double bpm=-1;
	private static int pre_offset = Integer.MIN_VALUE;

	private static int offset = 0;
	private static Beat beat = new Beat();
	private static int bit_sequence = -1, bit;
	
	private static ScheduledExecutorService beatExecutor = Executors.newSingleThreadScheduledExecutor();
	private static ScheduledFuture<?> beatScheduled;
	private static Runnable play;
	private static Circle[] indicator;
	
	public static void start() {
		if(bpm<0) return;
		
		long nanoPeriod = (long) (60_000_000_000L/(bpm*bit/4));
		play = () -> {
			bit_sequence++;
			bit_sequence %= bit;
			beat.play( root.isAccent(bit_sequence) );
			Platform.runLater( () -> indicator[bit_sequence].requestFocus() );
		};
		beatScheduled = beatExecutor.scheduleAtFixedRate(play, offset*1_000_000L, nanoPeriod, TimeUnit.NANOSECONDS);
		
		pre_offset = offset;
		offset = 0;
	}
	
	public static void scheduleCancel() {
		beatScheduled.cancel(true);
		beatScheduled = null;
		bit_sequence = -1;
		pre_offset = Integer.MIN_VALUE;
		System.out.println("종료 됨!");
	}
	
	private static void scheduleRestart() {
		long nanoPeriod = (long) (60_000_000_000L/(bpm*bit/4));	//바뀐 bpm에 따라 간격 구하기
		
		Long remainDelay = beatScheduled.getDelay(TimeUnit.NANOSECONDS);	//다음 비트가 재생될 때까지 남은 딜레이 가져오기
		Long nanoOff = offset*1_000_000L;
		remainDelay = remainDelay + nanoOff < 0 ? 0 : remainDelay + nanoOff; //오프셋만큼 밀기 (만약, 오프셋이 음수일 때, 남은 딜레이보다 절댓값이 크면 0으로 고정)
		
		beatScheduled.cancel(true);	//스케쥴 중지
		//바뀐 정보대로 스케쥴 재시작
		beatScheduled = beatExecutor.scheduleAtFixedRate(play, remainDelay, nanoPeriod, TimeUnit.NANOSECONDS);
	}
	
	public static void setBpm(double b) {
		bpm = b;
		//매트로놈 중간에 bpm을 바꿀 때,
		if(beatScheduled != null) scheduleRestart();
	}
	
	public static void setOffset(int off) {
		if(pre_offset == Integer.MIN_VALUE) {
			offset = off;
			return;
		}
		offset = off - pre_offset;
		pre_offset = off;
		//매트로놈 중간에 오프셋을 바꿀 때,
		if(beatScheduled != null) scheduleRestart();
	}
	
	public static void setVolume(int vol) {
		beat.setVolume(vol);
	}
	
	public static void setBit(int b) {
		bit = b;
		//매트로놈 중간에 비트를 바꿀 때,
		if(beatScheduled != null) {
			bit_sequence *= ((bit_sequence - bit) >> 31) & 1;
			indicator[bit_sequence].requestFocus();
			scheduleRestart();
		}
	}
	
	public static void setIndicator(Circle[] indi) {
		indicator = indi;
	}
	
	public static int getBeat_sequence() {
		int result = (bit_sequence)%bit;
		return result;
	}
}