package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import metronome.Controller;
import metronome.Settings;
import metronome.sound.MusicStrategy;

public class VolumeSetting {
	private static final Controller root = Controller.getInstance();
	
	private static MusicStrategy player = root.getPlayer();
	
	public static volume getSliderEvnet() {
		return new volume();
	}

	/** 볼륨 슬라이더를 담당하는 이벤트 클래스 */
	private static class volume implements ChangeListener<Number> {
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {			
			int result = newValue.intValue();
			
			player.setVolume(result);
			
			/** 세팅클래스 동기화 */
			Settings.setVolume(result);
			//System.out.println("Volume Value Changed: " + result);
		}
	}
	
	public static void setPlayer(MusicStrategy p) {
		player = p;
	}
}