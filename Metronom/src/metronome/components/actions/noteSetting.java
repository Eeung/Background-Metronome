package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class noteSetting  {
	private static final Controller root = Controller.getInstance();
	
	public static select getBeatSelectEvent() {
		return select.getInstance();
	}
	
	private static class select implements ChangeListener<Integer> {
		@Override
		public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
			indicatorSetting.rebuildIndicator();
	        
	        HBox[] rows = root.getIndicatorRows();
			SoundPlayer.setIndicator( root.getBeatIndicator(rows) );
			SoundPlayer.setNote(newValue);
		}
		
		//Singleton Pattern
		private static select I = new select();
		public static select getInstance() {
			return I;
		}
		private select() {
		}
	}
}
