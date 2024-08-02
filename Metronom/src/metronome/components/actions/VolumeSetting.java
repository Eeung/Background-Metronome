package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class VolumeSetting {	
	private final static Controller root = Controller.getInstance();
	private static Slider volumeSlider = root.getVolumeSlider();
	
	public static volume getSliderEvnet() {
		return volume.getInstance();
	}

	private static class volume implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!volumeSlider.isValueChanging()) return;
			
			int result = newValue.intValue();
			
			SoundPlayer.setVolume(result);
			
			System.out.println("Volume Value Changed: " + result);
		}
		
		//Singleton Pattern
		static volume I = new volume();
		public static volume getInstance() {
			return I;
		}
		private volume() {
		}
	}
}
