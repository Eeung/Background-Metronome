package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import metronome.Controller;
import metronome.sound.MetronomeStrategy;

public class bpmSetting {
	private static final Controller root = Controller.getInstance();
	
	private static Label bpmValue = root.getBpmValue();
	private static Label bpmText = root.getBpmText();
	private static Slider bpmSlider = root.getBpmSlider();
	private static MetronomeStrategy player = (MetronomeStrategy) root.getPlayer();
	
	/** Get event instances of bpm increase/decrease buttons. */
	public static bpmChange getButtonEvent(Button bpmButton) {
		return new bpmChange(bpmButton);
	}
	/** Get event instances of bpm slider. */
	public static sliderEvent getSliderEvent(Slider slider) {
		return new sliderEvent(slider);
	}
	
	/** The event that Bpm is increased or decreased */
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
			
			/** Convert bpm from int to float text */
			StringBuilder sb = new StringBuilder();
			sb.append( bpm/100 );
			int remain = bpm%100;
			/** decimal places */
			if(remain>0) {
				double num = remain/100.0;
				String str = Double.toString(num);
				sb.append(str.substring(1));
			}
			
			bpmValue.setText( Integer.toString(bpm) );
			player.setBpm(bpm/100.0);
			bpmText.setText( sb.toString() );
			
			/** Synchronizing Bpm and bpmSlider*/
			bpmSlider.setValue(bpm/100.0);
		}
		
		private int rangeCheck(int target, int min, int max) {
			target = target<min ? min : target;
			target = target>max ? max : target;
			return target;
		}
		
	}

	/** The event to control Bpm with slider */
	private static class sliderEvent implements ChangeListener<Number> {
		private Slider bpmSlider;
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(!bpmSlider.isValueChanging()) return;
			
			int result = newValue.intValue();
			
			bpmValue.setText( Integer.toString(result*100) );
			player.setBpm(result);
			bpmText.setText( Integer.toString(result) );
			
			//System.out.println("Slider Value Changed: " + result);
		}

		sliderEvent(Slider slider) {
			bpmSlider = slider;
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

