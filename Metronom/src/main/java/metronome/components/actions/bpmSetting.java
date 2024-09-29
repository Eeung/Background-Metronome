package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import metronome.Controller;
import metronome.Settings;
import metronome.sound.MetronomeStrategy;

public class bpmSetting {
	private static final Controller root = Controller.getInstance();
	
	private static Label bpmValue = root.getBpmValue();
	private static Label bpmText = root.getBpmText();
	private static Slider bpmSlider = root.getBpmSlider();
	private static MetronomeStrategy player = (MetronomeStrategy) root.getPlayer();
	
	public static bpmChange getButtonEvent(Button bpmButton) {
		return new bpmChange(bpmButton);
	}
	public static sliderEvent getSliderEvent(Slider slider) {
		return new sliderEvent(slider);
	}
	
	/** bpm 증감 버튼의 이벤트를 담당하는 클래스 */
	private static class bpmChange implements EventHandler<MouseEvent>{
		private Button bpmChange;
		
		bpmChange(Button bpm) {
			bpmChange = bpm;
		}
		
		@Override
		public void handle(MouseEvent arg0) {
			if(bpmChange == null) return;
			
			int bpm = Integer.parseInt(bpmValue.getText());
			int weight = (int) (Double.parseDouble( bpmChange.getText() )*100);
			
			bpm += weight;
			bpm = rangeCheck(bpm, 1, 100000);
			
			/** bpm값을 소수형태의 문자열로 바꾸는 작업 */
			StringBuilder sb = new StringBuilder();
			sb.append( bpm/100 );
			int remain = bpm%100;
			//소수부분
			if(remain>0) {
				double num = remain/100.0;
				String str = Double.toString(num);
				sb.append(str.substring(1));
			}
			
			bpmValue.setText( Integer.toString(bpm) );
			player.setBpm(bpm/100.0);
			bpmText.setText( sb.toString() );
			
			/** bpm슬라이더와 동기화 */
			bpmSlider.setValue(bpm/100.0);
			
			/** 세팅클래스 동기화 */
			Settings.setBpm(bpm);
			
		}
		private int rangeCheck(int target, int min, int max) {
			return Math.max(1, Math.min(target, 100000));
		}
	}

	/** bpm을 조절하는 슬라이더의 이벤트 클래스 */
	private static class sliderEvent implements ChangeListener<Number> {
		private Slider bpmSlider;
		
		sliderEvent(Slider slider) {
			bpmSlider = slider;
		}
		
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!(bpmSlider.isValueChanging() || bpmSlider.isHover()) ) return;
			
			int result = newValue.intValue();
			
			bpmValue.setText( Integer.toString(result*100) );
			player.setBpm(result);
			bpmText.setText( Integer.toString(result) );
			
			/** 세팅클래스 동기화 */
			Settings.setBpm(result*100);
			
			//System.out.println("Slider Value Changed: " + result);
		}
	}
	
	public static String IncreaseStepSize(Button b) {
		int weight = (int) (Double.parseDouble( b.getText() )*100);
		if(Math.abs(weight) == 10) weight /= 10;
		else weight *= 100;
		return removeTrailingZero(weight);
	}
	public static String DecreaseStepSize(Button b) {
		int weight = (int) (Double.parseDouble( b.getText() )*100);
		if(Math.abs(weight) == 1) weight *= 10;
		else weight /= 100;
		return removeTrailingZero(weight);
	}
	private static String removeTrailingZero(int num) {
		StringBuilder sb = new StringBuilder( Double.toString(num/100.0) );
		
		if (sb.substring(sb.length()-2).equals(".0"))
            sb.setLength(sb.length()-2);
		if (num>0) sb.insert(0, '+');
		return sb.toString();
	}
	
	public static void setPlayer(MetronomeStrategy p) {
		player = p;
	}
}

