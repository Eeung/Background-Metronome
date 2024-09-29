package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import metronome.Controller;
import metronome.Settings;
import metronome.sound.MetronomeStrategy;

public class noteSetting  {
	private static final Controller root = Controller.getInstance();
	
	private static MetronomeStrategy player = (MetronomeStrategy) root.getPlayer();
	
	public static select getBeatSelectEvent() {
		return select.getInstance();
	}
	
	/** 비트(note)를 선택하는 이벤트 클래스 */
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
			
			/** 세팅 클래스와 동기화 */
			Settings.setNote(newValue);
			
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
