package metronome.components.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import metronome.SoundPlayer;
import metronome.components.Controller;

public class playAndStop {
	private static final Controller root = Controller.getInstance();
	private static Button playSound = root.getPlaySound();
	private static Button stopSound = root.getStopSound();
	private static Label bpmValue = root.getBpmValue();
	private static Label offsetText = root.getOffsetText();
	private static Slider volumeSlider = root.getVolumeSlider();
	private static SoundPlayer player;

	public static play getPlay() {
		return play.getInstance();
	}

	public static stop getStop() {
		return stop.getInstance();
	}
	
	private static class play implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {
			player = root.getPlayer();
			
			playSound.setDisable(true);
			stopSound.setDisable(false);
			
			player.setBit(4);
			
			player.setBpm( Integer.parseInt(bpmValue.getText()) / 100.0);
			player.setOffset( Integer.parseInt(offsetText.getText()) );
			
			player.setVolume( (int)volumeSlider.getValue() );
			
			player.start();
			root.setPlayed(true);
			root.setStartTime( System.currentTimeMillis() );
		}
		
		//Singleton Pattern
		static play I = new play();
		public static play getInstance() {
			return I;
		}
		private play() {
		}
	}

	private static class stop implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {
			player = root.getPlayer();
			
			playSound.setDisable(false);
			stopSound.setDisable(true);
			
			player.scheduleCancel();
			root.setPlayer(new SoundPlayer(4));
			root.setPlayed(false);
		}
		
		//Singleton Pattern
		static stop I = new stop();
		public static stop getInstance() {
			return I;
		}
		private stop() {
		}
	}
}

