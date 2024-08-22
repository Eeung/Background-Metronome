package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import metronome.sound.SoundPlayer;

public class VolumeSetting {
	
	/** Get event instances of volume slider. */
	public static volume getSliderEvnet(Slider s) {
		return new volume(s);
	}

	/** The event to control volume with slider */
	private static class volume implements ChangeListener<Number> {
		private Slider volumeSlider;
		
		public volume(Slider s) {
			volumeSlider = s;
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!volumeSlider.isValueChanging()) return;
			
			int result = newValue.intValue();
			
			SoundPlayer.setVolume(result);
			
			System.out.println("Volume Value Changed: " + result);
		}
	}
}