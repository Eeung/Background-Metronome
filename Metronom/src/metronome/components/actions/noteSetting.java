package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
			int beatCount = root.getBeatCount();
			if(beatCount == -1) {
				root.getSelectBeat().getSelectionModel().select(oldValue);
				beatCount = root.getBeatCount();
			}
			
			indicatorSetting.rebuildIndicator(beatCount);
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
