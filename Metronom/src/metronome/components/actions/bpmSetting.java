package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import metronome.SoundPlayer;
import metronome.components.Controller;

public class bpmSetting {	
	public static final int LessDecrease = -1;
	public static final int NormalDecrease = -2;
	public static final int MoreDecrease = -3;
	public static final int LessIncrease = 1;
	public static final int NormalIncrease = 2;
	public static final int MoreIncrease = 3;
	
	public static bpmChange getBpmChange(int mode) {
		bpmChange a = new bpmChange(mode);
		return a;
	}
	public static sliderEvent getSliderEvent() {
		return sliderEvent.getInstance();
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
}

class bpmChange implements EventHandler<MouseEvent>{
	private final Controller root = Controller.getInstance();
	
	private Label bpmValue = root.getBpmValue();
	private Label bpmText = root.getBpmText();
	private Slider bpmSlider = root.getBpmSlider();
	private Button bpmChange;
	private SoundPlayer player = root.getPlayer();

	public bpmChange(int mode) {
		switch(mode) {
		case bpmSetting.LessDecrease -> bpmChange = root.getBpmLessDecrease();
		case bpmSetting.NormalDecrease -> bpmChange = root.getBpmDecrease();
		case bpmSetting.MoreDecrease -> bpmChange = root.getBpmMoreDecrease();
		case bpmSetting.LessIncrease -> bpmChange = root.getBpmLessIncrease();
		case bpmSetting.NormalIncrease -> bpmChange = root.getBpmIncrease();
		case bpmSetting.MoreIncrease -> bpmChange = root.getBpmMoreIncrease();
		}
	}
	
	@Override
	public void handle(MouseEvent arg0) {
		if(bpmChange == null) return;
		int bpm = Integer.parseInt(bpmValue.getText());
		int weight = (int) (Double.parseDouble( bpmChange.getText() )*100);
		
		bpm += weight;
		bpm = rangeCheck(bpm, 1, 100000);
		
		StringBuilder sb = new StringBuilder();
		sb.append( bpm/100 );
		int remain = bpm%100;
		if(remain>0) {
			double num = remain/100.0;
			String str = Double.toString(num);
			sb.append(str.substring(1));
		}
		
		bpmValue.setText( Integer.toString(bpm) );
		player.setBpm(bpm/100.0);
		bpmText.setText( sb.toString() );
		
		bpmSlider.setValue(bpm/100.0);
	}
	
	private int rangeCheck(int target, int min, int max) {
		target = target<min ? min : target;
		target = target>max ? max : target;
		return target;
	}
}

class sliderEvent implements ChangeListener<Number> {
	private final Controller root = Controller.getInstance();
	private Label bpmValue = root.getBpmValue();
	private Label bpmText = root.getBpmText();
	private Slider bpmSlider = root.getBpmSlider();
	private SoundPlayer player = root.getPlayer();
	private boolean isShift = false;
	
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		if(!bpmSlider.isValueChanging()) return;
		int result = (int) (newValue.doubleValue()*100);
		
		StringBuilder sb = new StringBuilder();
		sb.append( result/100 );
		int remain = result%100;
		if(isShift && remain>0) {
			double num = remain/100.0;
			String str = Double.toString(num);
			sb.append(str.substring(1));
		}
		
		bpmValue.setText( Integer.toString(result/100*100) );
		player.setBpm((result/100*100)/100.0);
		bpmText.setText( sb.toString() );
		System.out.println("Slider Value Changed: " + sb.toString());
	}
	
	//Singleton Pattern
	static sliderEvent I = new sliderEvent();
	public static sliderEvent getInstance() {
		return I;
	}
	private sliderEvent() {
	}
}