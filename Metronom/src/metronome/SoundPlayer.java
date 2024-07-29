package metronome;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SoundPlayer extends Thread {
	//private final Controller cont = Controller.getInstance();
	
	private double bpm=-1;
	private int bit, pre_offset = Integer.MIN_VALUE, offset = 0;
	private Beat beat = new Beat();
	private static int idx = 0;
	
	private ScheduledExecutorService beatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> beatScheduled;
	private Runnable play;
	
	public SoundPlayer(int b) {
		idx = 0;
		bit = b;
	}
	
	@Override
	public void run() {
		if(bpm<0) return;
		
		long nanoPeriod = (long) (60_000_000_000L/(bpm*bit/4));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		play = () -> {
			beat.play(idx++);
			//Platform.runLater(() -> cont.getBpmText().setText("aaa"));
			try {
				bw.write(48+idx);
				bw.write(13);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			idx %= bit;
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
}