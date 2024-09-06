package metronome.sound;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.shape.Circle;
import metronome.Controller;

public class MetronomePlayer implements MetronomeStrategy {
	private static Controller root;
	
	private double bpm=120;
	private int offset = 0;
	/** Store previous offset value. */
	private int pre_offset = Integer.MIN_VALUE;

	/** If you use the Quick Restart feature, it reduces the offset by a predetermined amount. */
	private int fast_retry = 0;
	private int beat_sequence = 0, beat_count = -1;
	private int note, beat=4, time=4;
	
	private ScheduledExecutorService beatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> beatScheduled;
	/** The function of playing beat sound. */
	private Runnable runnable;
	private static TickTock TickTock = new TickTock();
	
	private Circle[] indicator;
	
	@Override
	public void play() {
		if(bpm<0) return;
		root = Controller.getInstance();
		/** The Delay of between each beat sound. */
		long nanoPeriod = (long) (60_000_000_000L/(bpm* note/beat ));
		
		runnable = () -> {
			beat_sequence %= beat_count;
			TickTock.play( root.isAccent(beat_sequence) );
			Platform.runLater( () -> indicator[beat_sequence++].requestFocus() );
		};
		
		/** (runnable function, delay before starting, interval, time unit) */
		beatScheduled = beatExecutor.scheduleAtFixedRate(
				runnable, (offset-fast_retry)*1_000_000L, nanoPeriod, TimeUnit.NANOSECONDS);
		
		fast_retry = 0;
		pre_offset = offset;
		offset = 0;
	}
	
	/** Cancel the schedule that play sound at regular intervals. */
	@Override
	public void stop() {
		beatScheduled.cancel(true);
		beatScheduled = null;
		beat_sequence = 0;
		pre_offset = Integer.MIN_VALUE;
		System.out.println("종료 됨!");
	}
	
	/** Reset the interval when you change value(bpm, offset, note or beat) */
	private void restart() {
		long nanoPeriod = (long) (60_000_000_000L/(bpm* note/beat ));	//바뀐 bpm에 따라 간격 구하기
		
		Long remainDelay = beatScheduled.getDelay(TimeUnit.NANOSECONDS);	//다음 비트가 재생될 때까지 남은 딜레이 가져오기
		Long nanoOff = offset*1_000_000L;
		remainDelay = remainDelay + nanoOff < 0 ? 0 : remainDelay + nanoOff; //오프셋만큼 밀기 (만약, 오프셋이 음수일 때, 남은 딜레이보다 절댓값이 크면 0으로 고정)
		
		beatScheduled.cancel(true);	//스케쥴 중지
		//바뀐 정보대로 스케쥴 재시작
		beatScheduled = beatExecutor.scheduleAtFixedRate(runnable, remainDelay, nanoPeriod, TimeUnit.NANOSECONDS);
	}
	
	private void setBeatCount() {
		beat_count = note *time/beat;
	}
	
	@Override
	public void setBpm(double b) {
		bpm = b;
		//매트로놈 중간에 bpm을 바꿀 때,
		if(beatScheduled != null) restart();
	}
	
	@Override
	public void setOffset(int off) {
		if(pre_offset == Integer.MIN_VALUE) {
			offset = off;
			return;
		}
		offset = off - pre_offset;
		pre_offset = off;
		//매트로놈 중간에 오프셋을 바꿀 때,
		if(beatScheduled != null) restart();
	}
	
	@Override
	public void setVolume(int vol) {
		TickTock.setVolume(vol);
	}
	
	@Override
	public void setNote(int b) {
		note = b;
		setBeatCount();
		//매트로놈 중간에 비트를 바꿀 때,
		if(beatScheduled != null) {
			/** if beat_sequence is bigger than beat_count, it becomes 0. Or 1 */
			int zeroOrOne = ((beat_sequence - beat_count) >> 31) & 1;
			beat_sequence *= zeroOrOne;
			indicator[beat_sequence-zeroOrOne].requestFocus();
			restart();
		}
	}
	
	@Override
	public void setBeat(int b) {
		beat = b;
		setBeatCount();
		//매트로놈 중간에 비트를 바꿀 때,
		if(beatScheduled != null) {
			/** if beat_sequence is bigger than beat_count, it becomes 0. Or 1 */
			int zeroOrOne = ((beat_sequence - beat_count) >> 31) & 1;
			beat_sequence *= zeroOrOne;
			indicator[beat_sequence-zeroOrOne].requestFocus();
			restart();
		}
	}
	
	@Override
	public void setTime(int b) {
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
	
	@Override
	public void setIndicator(Circle[] indi) {
		indicator = indi;
	}
	
	@Override
	public int getBeat_sequence() {
		int result = (beat_sequence-1)%beat_count;
		return result;
	}
	
	@Override
	public void activateFastRetry() {
		fast_retry = 1000;
	}
	
	//Singleton Pattern
	private static MetronomePlayer I = new MetronomePlayer();
	private MetronomePlayer() {}
	public static MetronomePlayer getInstance() {
		return I;
	}
}