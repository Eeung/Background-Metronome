package metronome.components.actions;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import metronome.CircularList;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class timeSignatureSetting {
	private static final Controller root = Controller.getInstance();
	public static final int NUMERATOR = 0;
	public static final int DENOMINATOR = 1;
	
	public static signature getTimeSignatureEvent(int id, String[] integers) {
		return new signature(id, integers);
	}
	public static signature getTimeSignatureEvent(int id, String[] integers, int startIdx) {
		return new signature(id, integers, startIdx);
	}
	
	private static class signature implements EventHandler<MouseEvent> {
		private CircularList<String> list;
		private Label timeSignature;
		private int id;
		public signature(int id, String[] arr) {
			this(id,arr,0);
		}
		public signature(int id, String[] arr, int startIdx) {
			list = new CircularList<String>(arr);
			switch(id) {
			case NUMERATOR -> timeSignature = root.getTimeSignatureTime();
			case DENOMINATOR -> timeSignature = root.getTimeSignatureBeat();
			}
			this.id = id;
			list.setIndex(startIdx);
		}
		
		@Override
		public void handle(MouseEvent arg0) {
			if(timeSignature == null) return;
			
			String value = null;
			switch(arg0.getButton()) {
			case PRIMARY:
				value = list.next();
				timeSignature.setText( value );
				break;
			case SECONDARY:
				value = list.previous();
				timeSignature.setText( value );
				break;
			default:
				break;
			}
			
			switch(id) {
			case 0 -> SoundPlayer.setTime( root.getBuravuraValue(value) );
			//case 1 -> SoundPlayer.setBeat( root.getBuravuraValue(value) );
			}
			indicatorSetting.rebuildIndicator();
			// 사운드플레이어에서 beat, time 설정 및 인디케이터 업데이트
		}
		
	}
}