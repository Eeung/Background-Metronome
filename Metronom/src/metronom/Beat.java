package metronom;

import java.io.*;

import javax.sound.sampled.*;

public class Beat /*implements LineListener*/{
	private String filePath = ".//audio//Tock.wav";
	private FloatControl control;
	private Clip clip;
	Beat() {
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            clip = AudioSystem.getClip();
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);
			//clip.addLineListener(this);
			clip.open(audioStream);
			control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			setVolume(100);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void stop() {
		clip.stop();
	}
	
	public void setVolume(int vol) {
		Double cur = 20*Math.log(vol / 100.0 + 0.01);
		float db = cur<-80 ? -80 : cur.floatValue();
		control.setValue(db);
	}
	/*@Override
	public void update(LineEvent event) {
		if (LineEvent.Type.START == event.getType()) {
            System.out.println("Playback started.");
        } else if (LineEvent.Type.STOP == event.getType()) {
            System.out.println("Playback completed.");
        }
	}*/
}