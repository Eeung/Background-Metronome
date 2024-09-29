package metronome.components.actions;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import metronome.CircularList;
import metronome.Controller;
import metronome.Settings;
import metronome.sound.MetronomeStrategy;

public class timeSignatureSetting {
	private static final Controller root = Controller.getInstance();
	public static final int NUMERATOR = 0;
	public static final int DENOMINATOR = 1;
	
	private static MetronomeStrategy player = (MetronomeStrategy) root.getPlayer();
	
	public static signature getTimeSignatureEvent(int id, String[] integers, String start) {
		return new signature(id, integers, start);
	}
	
	/** 박자표 조정을 위한 이벤트 클래스 */
	private static class signature implements EventHandler<MouseEvent> {
		private CircularList<String> list;
		private Label timeSignaturePart;
		private final int id;
		
		public signature(int id, String[] arr, String start) {
			list = new CircularList<String>(arr);
			switch(id) {
			case NUMERATOR -> timeSignaturePart = root.getTimeSignatureTime();//분자
			case DENOMINATOR -> timeSignaturePart = root.getTimeSignatureBeat();//분모
			}
			this.id = id;
			list.get(list.indexOf(start));
		}
		
		@Override
		public void handle(MouseEvent arg0) {
			if(timeSignaturePart == null) return;
			int beatCount = 0;
				
			String value = null;
			switch(arg0.getButton()) {
			case PRIMARY:
				value = list.next();
				timeSignaturePart.setText( value );
				beatCount = root.getBeatCount();
				while(beatCount == -1) {
					value = list.next();
					timeSignaturePart.setText( value );
					beatCount = root.getBeatCount();
				}
				break;
			case SECONDARY:
				value = list.previous();
				timeSignaturePart.setText( value );
				beatCount = root.getBeatCount();
				while(beatCount == -1) {
					value = list.previous();
					timeSignaturePart.setText( value );
					beatCount = root.getBeatCount();
				}
				break;
			default:
			}
			
			indicatorSetting.rebuildIndicator(beatCount);
			
			switch(id) {
			case 0 -> {
				int time = root.getBuravuraValue(value);
				player.setTime(time);
				/** 세팅클래스 동기화 */
				Settings.setTime(time);
			}
			case 1 -> {
				int beat = root.getBuravuraValue(value);
				player.setBeat(beat);
				/** 세팅클래스 동기화 */
				Settings.setBeat(beat);
			}
			}
		}
	}
	
	public static void setPlayer(MetronomeStrategy p) {
		player = p;
	}
}