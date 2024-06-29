package metronom;

import java.net.URISyntaxException;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Beat{
	@SuppressWarnings("unused")
	private String filePath = ".\\resources\\audio\\Tock.wav";
	private MediaPlayer player;
	Beat() {
		try {
			new JFXPanel(); //이게 없으면 IllegalStateException 예외가 발생함
			
			String uriString = getClass().getResource("/audio/Tock.wav").toURI().toString();
			player = new MediaPlayer( new Media(uriString) );
		}catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		player.stop();
		player.setStartTime(Duration.ZERO);
		player.play();
	}
	
	public void setVolume(int vol) {
		double volume = (double)vol / 100;
		player.setVolume(volume);
	}
}