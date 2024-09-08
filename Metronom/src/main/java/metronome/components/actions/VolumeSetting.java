package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import metronome.Controller;
import metronome.sound.MusicStrategy;

public class VolumeSetting {
	private static final Controller root = Controller.getInstance();
	
	private static MusicStrategy player = root.getPlayer();
	
	/** Get event instances of volume slider. */
	public static volume getSliderEvnet() {
		return new volume();
	}

	/** The event to control volume with slider */
	private static class volume implements ChangeListener<Number> {
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {			
			int result = newValue.intValue();
			
			player.setVolume(result);
			
			//System.out.println("Volume Value Changed: " + result);
		}
	}
	
	public static void setPlayer(MusicStrategy p) {
		player = p;
	}
}