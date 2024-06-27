package metronom;

public class SoundPlayer extends Thread {
	private double bpm=-1;
	private Beat beat;
	public SoundPlayer() {
		beat = new Beat();
	}
	@Override
	public void run() {
		if(bpm<0) return;
		
		try {
			while(true) {
				int nanosec = (int) (60_000_000/bpm);
				int milisec = nanosec/1000;
				int remain = nanosec%1000;
				beat.play();
				System.out.println("띵 ");
				Thread.sleep(milisec, remain);
			}
		} catch (InterruptedException e) {
			System.out.println("인터럽트 됨!");
		}
	}
	public void setBpm(double bpm) {
		this.bpm = bpm;
	}
}
