package metronome.components.actions;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import metronome.Controller;
import metronome.Settings;
import metronome.sound.MusicStrategy;

public class offsetSetting {
	private static final Controller root = Controller.getInstance();
	
	private static Label offsetText = root.getOffsetText();
	private static MusicStrategy player = root.getPlayer();
	
	public static final int NormalDecrease = -1;
	public static final int MoreDecrease = -10;
	public static final int NormalIncrease = 1;
	public static final int MoreIncrease = 10;
	
	public static final int MorerDecrease = -100;
	public static final int MorestDecrease = -1000;
	public static final int MorerIncrease = 100;
	public static final int MorestIncrease = 1000;
	
	public static offsetChange getButtonEvent(int mode) {
		return new offsetChange(mode);
	}
	
	/** 오프셋 증감버튼의 이벤트를 담당하는 클래스 */
	private static class offsetChange implements EventHandler<MouseEvent>{
		private Button offsetChange;
		private int step;
		
		offsetChange(int mode) {
			step = mode;
			switch(mode) {
			case NormalDecrease -> offsetChange = root.getBpmDecrease();
			case MoreDecrease -> offsetChange = root.getBpmMoreDecrease();
			case NormalIncrease -> offsetChange = root.getBpmIncrease();
			case MoreIncrease -> offsetChange = root.getBpmMoreIncrease();
			
			case MorerDecrease -> offsetChange = root.getBpmDecrease();
			case MorestDecrease -> offsetChange = root.getBpmMoreDecrease();
			case MorerIncrease -> offsetChange = root.getBpmIncrease();
			case MorestIncrease -> offsetChange = root.getBpmMoreIncrease();
			}
		}
		
		@Override
		public void handle(MouseEvent arg0) {
			if(offsetChange == null) return;
			
			int offset = Integer.parseInt( offsetText.getText() );
			int weight = step;
			
			offset += weight;
			offset = rangeCheck(offset, 0, Integer.MAX_VALUE);
			
			final int result = offset;
			
			Platform.runLater(() -> offsetText.setText( Integer.toString(result) ) );
			player.setOffset(offset);
			
			/** 세팅 클래스와 동기화 */
			Settings.setOffset(offset);
			
		}
		
		private int rangeCheck(int target, int min, int max) {
			target = target<min ? min : target;
			target = target>max ? max : target;
			return target;
		}
		
	}
	
	public static String IncreaseStepSize(Button b) {
		int weight = Integer.parseInt(b.getText());
		weight *= 100;
		return prependPlus(weight);
	}
	public static String DecreaseStepSize(Button b) {
		int weight = Integer.parseInt(b.getText());
		weight /= 100;
		return prependPlus(weight);
	}
	private static String prependPlus(int num) {
		StringBuilder sb = new StringBuilder();
		sb.append(num);
		if (num>0) sb.insert(0, '+');
		return sb.toString();
	}
	
	public static void setPlayer(MusicStrategy p) {
		player = p;
	}
}