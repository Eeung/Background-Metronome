package metronom;

public class SoundPlayer extends Thread {
	private double bpm=-1;
	private int pre_offset = Integer.MIN_VALUE, offset = 0;
	private Beat beat;
	public SoundPlayer() {
		beat = new Beat();
	}
	@Override
	public void run() {
		if(bpm<0) return;
		
		try {
			int nanosec = (int) (60_000_000/bpm);
			int milisec = nanosec/1000 + offset;
			pre_offset = offset;
			offset = 0;
			int remain = nanosec%1000;
			Thread.sleep(milisec<0 ? 0 : milisec, remain);
			while(true) {
				beat.play();
				nanosec = (int) (60_000_000/bpm);
				milisec = nanosec/1000 + offset;
				pre_offset = offset;
				offset = 0;
				remain = nanosec%1000;
				Thread.sleep(milisec<0 ? 0 : milisec, remain);
			}
		} catch (InterruptedException e) {
			beat.stop();
			System.out.println("인터럽트 됨!");
		}
	}
	
	public void setBpm(double bpm) {
		this.bpm = bpm;
	}
	
	public void setOffset(int off) {
		if(pre_offset == Integer.MIN_VALUE) {
			offset = off;
			return;
		}
		offset = off - pre_offset;
	}
	
	public void setVolume(int vol) {
		beat.setVolume(vol);
	}
}
