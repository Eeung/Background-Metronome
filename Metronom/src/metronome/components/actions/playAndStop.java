package metronome.components.actions;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import metronome.Controller;
import metronome.sound.MusicStrategy;

public class playAndStop {
	private static final Controller root = Controller.getInstance();
	
	private static Button playSound = root.getPlaySound();
	private static Button stopSound = root.getStopSound();
	private static VBox indicatorCol = root.getIndicatorCol();
	private static MusicStrategy player = root.getPlayer();

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
			
			player.play();
			root.setPlayed(true);
			root.setStartTime( System.currentTimeMillis() );
		}
		
		//Singleton Pattern
		static play I = new play();
		static play getInstance() {
			return I;
		}
	}

	/** The event to stop sound */
	private static class stop implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent arg0) {
			playSound.setDisable(false);
			stopSound.setDisable(true);
			
			player.stop();
			Platform.runLater(() -> indicatorCol.requestFocus() );
			root.setPlayed(false);
		}
		
		//Singleton Pattern
		static stop I = new stop();
		static stop getInstance() {
			return I;
		}
	}
	
	public static void setPlayer(MusicStrategy p) {
		player = p;
	}
}

