package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import metronome.SoundPlayer;
import metronome.components.Controller;

public class VolumeSetting {	
	private static Controller root = Controller.getInstance();
	private static Slider volumeSlider = root.getVolumeSlider();
	private static SoundPlayer player;
	
	public static volume getSliderEvnet() {
		return volume.getInstance();
	}

	private static class volume implements ChangeListener<Number> {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!volumeSlider.isValueChanging()) return;
			player = root.getPlayer();
			
			int result = newValue.intValue();
			
			player.setVolume(result);
			
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
