package metronome.components.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import metronome.SoundPlayer;
import metronome.components.Controller;

public class playAndStop {
	private static play p = play.getInstance();
	private static stop s = stop.getInstance();

	public static play getPlay() {
		return p;
	}

	public static stop getStop() {
		return s;
	}
}
class play implements EventHandler<ActionEvent>{
	private final Controller root = Controller.getInstance();
	private Button playSound = root.getPlaySound();
	private Button stopSound = root.getStopSound();
	private Label bpmValue = root.getBpmValue();
	private Label offsetText = root.getOffsetText();
	private SoundPlayer player;
	@Override
	public void handle(ActionEvent arg0) {
		player = root.getPlayer();
		
		playSound.setDisable(true);
		stopSound.setDisable(false);
		
		player.setBit(4);
		
		player.setBpm( Integer.parseInt(bpmValue.getText()) / 100.0);
		player.setOffset( Integer.parseInt(offsetText.getText()) );
		
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

class stop implements EventHandler<ActionEvent>{
	private final Controller root = Controller.getInstance();
	private Button playSound = root.getPlaySound();
	private Button stopSound = root.getStopSound();
	private SoundPlayer player;
	
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
