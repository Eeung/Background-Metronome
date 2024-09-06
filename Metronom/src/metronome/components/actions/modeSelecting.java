package metronome.components.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import metronome.Controller;
import metronome.sound.MusicStrategy;

public class modeSelecting {
	private static final Controller root = Controller.getInstance();
	
	
	public static setMode getModeSelectEvent(Node visualPane, Node controlPane, MusicStrategy player) {
		return new setMode(visualPane,controlPane,player);
	}
	
	private static class setMode implements EventHandler<ActionEvent> {
		Node visualNode;
		Node controlNode;
		MusicStrategy player;
		
		public setMode(Node v, Node c, MusicStrategy p) {
			visualNode = v;
			controlNode = c;
			player = p;
		}
		
		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("changed");
			visualNode.toFront();
			controlNode.toFront();
			
			root.setPlayer(player);
		}
		
	}
}
