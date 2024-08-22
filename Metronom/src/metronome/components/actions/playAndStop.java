package metronome.components.actions;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class playAndStop {
	private static final Controller root = Controller.getInstance();
	
	private static Button playSound = root.getPlaySound();
	private static Button stopSound = root.getStopSound();
	private static VBox indicatorCol = root.getIndicatorCol();

	/** Get the event to play sound. */
	public static play getPlay() {
		return play.getInstance();
	}

	/** Get the event to stop sound. */
	public static stop getStop() {
		return stop.getInstance();
	}
	
	/** The event to play sound */
	private static class play implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {			
			playSound.setDisable(true);
			stopSound.setDisable(false);
			
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

	/** The event to stop sound */
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

