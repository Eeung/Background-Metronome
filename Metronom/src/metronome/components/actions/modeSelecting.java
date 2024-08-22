package metronome.components.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public class modeSelecting {
	
	public static setMode getModeSelectEvent(Node visualPane, Node controlPane) {
		return new setMode(visualPane,controlPane);
	}
	
	private static class setMode implements EventHandler<ActionEvent> {
		Node visualNode;
		Node controlNode;
		
		public setMode(Node v, Node c) {
			visualNode = v;
			controlNode = c;
		}
		
		@Override
		public void handle(ActionEvent arg0) {
			System.out.println("changed");
			visualNode.toFront();
			controlNode.toFront();
		}
		
	}
}
