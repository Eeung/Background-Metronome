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
	
	/** Get the event of selecting the numerator of the time signature. */
	public static signature getTimeSignatureEvent(int id, String[] integers) {
		return new signature(id, integers);
	}
	/** Get the event of selecting the denominator of the time signature. */
	public static signature getTimeSignatureEvent(int id, String[] integers, int startIdx) {
		return new signature(id, integers, startIdx);
	}
	
	/** The event of adjusting the time signature */
	private static class signature implements EventHandler<MouseEvent> {
		private CircularList<String> list;
		private Label timeSignaturePart;
		private final int id;
		
		public signature(int id, String[] arr) {
			this(id,arr,0);
		}
		public signature(int id, String[] arr, int startIdx) {
			list = new CircularList<String>(arr);
			switch(id) {
			case NUMERATOR -> timeSignaturePart = root.getTimeSignatureTime();
			case DENOMINATOR -> timeSignaturePart = root.getTimeSignatureBeat();
			}
			this.id = id;
			list.get(startIdx);
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
				if(beatCount == -1) {
					value = list.next();
					timeSignaturePart.setText( value );
					return;
				}
				break;
			case SECONDARY:
				value = list.previous();
				timeSignaturePart.setText( value );
				beatCount = root.getBeatCount();
				if(beatCount == -1) {
					value = list.previous();
					timeSignaturePart.setText( value );
					return;
				}
				break;
			default:
			}
			
			indicatorSetting.rebuildIndicator(beatCount);
			
			switch(id) {
			case 0 -> SoundPlayer.setTime( root.getBuravuraValue(value) );
			case 1 -> SoundPlayer.setBeat( root.getBuravuraValue(value) );
			}
		}
		
	}
}