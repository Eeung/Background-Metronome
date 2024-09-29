package metronome.components.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import metronome.Controller;
import metronome.Settings;

public class quickTrySetting implements EventHandler<ActionEvent>{
	private static final Controller root = Controller.getInstance();
	private String name;
	
	public quickTrySetting(String name) {
		this.name = name;
	}
	
	@Override
	public void handle(ActionEvent arg0) {
		root.setGame(name);
		
		/** 세팅클래스 동기화 */
		Settings.setGame(name);
	}
}
