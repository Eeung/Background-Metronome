package metronom;

public class Run implements Runnable{
	private Beat beat;
	public Run(Beat b) {
		beat = b;
	}
	@Override
	public void run() {
		beat.play();
	}

}
