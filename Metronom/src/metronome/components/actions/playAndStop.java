package metronome.components.actions;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class playAndStop {
	private static final Controller root = Controller.getInstance();
	private static Button playSound = root.getPlaySound();
	private static Button stopSound = root.getStopSound();
	private static Label bpmValue = root.getBpmValue();
	private static Label offsetText = root.getOffsetText();
	private static Slider volumeSlider = root.getVolumeSlider();
	private static ComboBox<Integer> selectBeat = root.getSelectBeat();
	private static VBox indicatorCol = root.getIndicatorCol();

	public static play getPlay() {
		return play.getInstance();
	}

	public static stop getStop() {
		return stop.getInstance();
	}
	
	private static class play implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {			
			playSound.setDisable(true);
			stopSound.setDisable(false);
			
			SoundPlayer.setBit( selectBeat.getSelectionModel().getSelectedItem() );
			SoundPlayer.setIndicator(root.getBeatIndicator( root.getIndicatorRows() ));
			
			SoundPlayer.setBpm( Integer.parseInt(bpmValue.getText()) / 100.0);
			SoundPlayer.setOffset( Integer.parseInt(offsetText.getText()) );
			
			SoundPlayer.setVolume( (int)volumeSlider.getValue() );
			
			SoundPlayer.start();
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
			playSound.setDisable(false);
			stopSound.setDisable(true);
			
			SoundPlayer.scheduleCancel();
			Platform.runLater(() -> indicatorCol.requestFocus() );
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

