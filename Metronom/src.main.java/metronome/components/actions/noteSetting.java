package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import metronome.Controller;
import metronome.sound.MetronomeStrategy;

public class noteSetting  {
	private static final Controller root = Controller.getInstance();
	
	private static MetronomeStrategy player = (MetronomeStrategy) root.getPlayer();
	
	/** Get the event to select musical note */
	public static select getBeatSelectEvent() {
		return select.getInstance();
	}
	
	/** The event to select musical note */
	private static class select implements ChangeListener<Integer> {
		
		@Override
		public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
			int beatCount = root.getBeatCount();
			if(beatCount == -1) {
				root.getSelectBeat().setValue(oldValue);
				return;
			}
			
			indicatorSetting.rebuildIndicator(beatCount);
			player.setNote(newValue);
		}
		
		//Singleton Pattern
		static select I = new select();
		static select getInstance() {
			return I;
		}
	}
	
	public static void setPlayer(MetronomeStrategy p) {
		player = p;
	}
}
