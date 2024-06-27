package metronom;

import java.io.*;

import javax.sound.sampled.*;

public class Beat /*implements LineListener*/{//
	String filePath = ".//audio//Tock.wav";
	private Clip clip;
	Beat() {
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            clip = AudioSystem.getClip();
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);
			//clip.addLineListener(this);
			clip.open(audioStream);
			FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			
			control.setValue(-10.0f);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void play() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
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