package metronom;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SoundPlayer extends Thread {
	private double bpm=-1;
	private int pre_offset = Integer.MIN_VALUE, offset = 0;
	private Beat beat;
	private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> scheduledFuture = null;
	public SoundPlayer() {
		beat = new Beat();
	}
	@Override
	public void run() {
		if(bpm<0) return;
		
		long nanoPeriod = (long) (60_000_000_000L/bpm);
		
		scheduledFuture = executorService.scheduleAtFixedRate(()->{
			beat.play();
		}, offset*1_000_000L, nanoPeriod, TimeUnit.NANOSECONDS);
		
		pre_offset = offset;
		offset = 0;
	}
	
	public void scheduleCancel() {
		scheduledFuture.cancel(true);
		scheduledFuture = null;
		pre_offset = Integer.MIN_VALUE;
		System.out.println("종료 됨!");
	}
	
	public void setBpm(double bpm) {
		this.bpm = bpm;
		//매트로놈 중간에 bpm 바꿀 때,
		if(scheduledFuture != null)
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
		if(scheduledFuture != null)
			scheduleRestart();
	}
	
	public void setVolume(int vol) {
		beat.setVolume(vol);
	}
	
	private void scheduleRestart() {
		long nanoPeriod = (long) (60_000_000_000L/bpm);	//바뀐 bpm에 따라 간격 구하기
		Long remainDelay = scheduledFuture.getDelay(TimeUnit.NANOSECONDS);	//다음 비트가 재생될 때까지 남은 딜레이 가져오기
		Long nanoOff = offset*1_000_000L;
		remainDelay = remainDelay < -1*nanoOff ? 0 : remainDelay + nanoOff; //오프셋만큼 밀기 (만약, 오프셋이 음수일 때, 남은 딜레이보다 절댓값이 크면 0으로 고정)
		scheduledFuture.cancel(true);	//스케쥴 중지
		scheduledFuture = executorService.scheduleAtFixedRate(()->{	//바뀐 정보대로 스케쥴 재시작
			beat.play();
		}, remainDelay, nanoPeriod, TimeUnit.NANOSECONDS);
	}
}
