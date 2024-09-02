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
	
	private static double bpm=120;
	private static int offset = 0;
	/** Store previous offset value. */
	private static int pre_offset = Integer.MIN_VALUE;

	/** If you use the Quick Restart feature, it reduces the offset by a predetermined amount. */
	private static int fast_retry = 0;
	private static int beat_sequence = 0, beat_count = -1;
	private static int note, beat=4, time=4;
	
	private static ScheduledExecutorService beatExecutor = Executors.newSingleThreadScheduledExecutor();
	private static ScheduledFuture<?> beatScheduled;
	/** The function of playing beat sound. */
	private static Runnable play;
	private static Sound sound = new Sound();
	
	private static Circle[] indicator;
	
	public static void start() {
		if(bpm<0) return;
		/** The Delay of between each beat sound. */
		long nanoPeriod = (long) (60_000_000_000L/(bpm* note/beat ));
		
		play = () -> {
			beat_sequence %= beat_count;
			sound.play( root.isAccent(beat_sequence) );
			Platform.runLater( () -> indicator[beat_sequence++].requestFocus() );
		};
		
		/** (runnable function, delay before starting, interval, time unit) */
		beatScheduled = beatExecutor.scheduleAtFixedRate(
				play, (offset-fast_retry)*1_000_000L, nanoPeriod, TimeUnit.NANOSECONDS);
		
		fast_retry = 0;
		pre_offset = offset;
		offset = 0;
	}
	
	/** Cancel the schedule that play sound at regular intervals. */
	public static void scheduleCancel() {
		beatScheduled.cancel(true);
		beatScheduled = null;
		beat_sequence = 0;
		pre_offset = Integer.MIN_VALUE;
		System.out.println("종료 됨!");
	}
	
	/** Reset the interval when you change value(bpm, offset, note or beat) */
	private static void scheduleRestart() {
		long nanoPeriod = (long) (60_000_000_000L/(bpm* note/beat ));	//바뀐 bpm에 따라 간격 구하기
		
		Long remainDelay = beatScheduled.getDelay(TimeUnit.NANOSECONDS);	//다음 비트가 재생될 때까지 남은 딜레이 가져오기
		Long nanoOff = offset*1_000_000L;
		remainDelay = remainDelay + nanoOff < 0 ? 0 : remainDelay + nanoOff; //오프셋만큼 밀기 (만약, 오프셋이 음수일 때, 남은 딜레이보다 절댓값이 크면 0으로 고정)
		
		beatScheduled.cancel(true);	//스케쥴 중지
		//바뀐 정보대로 스케쥴 재시작
		beatScheduled = beatExecutor.scheduleAtFixedRate(play, remainDelay, nanoPeriod, TimeUnit.NANOSECONDS);
	}
	
	private static void setBeatCount() {
		beat_count = note *time/beat;
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
		sound.setVolume(vol);
	}
	
	public static void setNote(int b) {
		note = b;
		setBeatCount();
		//매트로놈 중간에 비트를 바꿀 때,
		if(beatScheduled != null) {
			/** if beat_sequence is bigger than beat_count, it becomes 0. Or 1 */
			int zeroOrOne = ((beat_sequence - beat_count) >> 31) & 1;
			beat_sequence *= zeroOrOne;
			indicator[beat_sequence-zeroOrOne].requestFocus();
			scheduleRestart();
		}
	}
	
	public static void setBeat(int b) {
		beat = b;
		setBeatCount();
		//매트로놈 중간에 비트를 바꿀 때,
		if(beatScheduled != null) {
			/** if beat_sequence is bigger than beat_count, it becomes 0. Or 1 */
			int zeroOrOne = ((beat_sequence - beat_count) >> 31) & 1;
			beat_sequence *= zeroOrOne;
			indicator[beat_sequence-zeroOrOne].requestFocus();
		}
	}
	
	public static void setTime(int b) {
		time = b;
		setBeatCount();
		//매트로놈 중간에 비트를 바꿀 때,
		if(beatScheduled != null) {
			/** if beat_sequence is bigger than beat_count, it becomes 0. Or 1 */
			int zeroOrOne = ((beat_sequence - beat_count) >> 31) & 1;
			beat_sequence *= zeroOrOne;
			indicator[beat_sequence-zeroOrOne].requestFocus();
		}
	}
	
	public static void setIndicator(Circle[] indi) {
		indicator = indi;
	}
	
	public static int getBeat_sequence() {
		int result = (beat_sequence-1)%beat_count;
		return result;
	}
	
	public static void activateFastRetry() {
		fast_retry = 1000;
	}
}