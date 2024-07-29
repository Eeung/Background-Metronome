package metronome.components.actions;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import metronome.SoundPlayer;
import metronome.components.Controller;

public class offsetSetting {	
	public static final int NormalDecrease = -1;
	public static final int MoreDecrease = -10;
	public static final int NormalIncrease = 1;
	public static final int MoreIncrease = 10;
	public static final int MorerDecrease = -100;
	public static final int MorestDecrease = -1000;
	public static final int MorerIncrease = 100;
	public static final int MorestIncrease = 1000;
	
	public static offsetChange getoffsetChange(int mode) {
		offsetChange a = new offsetChange(mode);
		return a;
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
}

class offsetChange implements EventHandler<MouseEvent>{
	private final Controller root = Controller.getInstance();
	
	private Label offsetText = root.getOffsetText();
	private Button offsetChange;
	private SoundPlayer player = root.getPlayer();
	private int step;
	
	public offsetChange(int mode) {
		step = mode;
		switch(mode) {
		case offsetSetting.NormalDecrease -> offsetChange = root.getBpmDecrease();
		case offsetSetting.MoreDecrease -> offsetChange = root.getBpmMoreDecrease();
		case offsetSetting.NormalIncrease -> offsetChange = root.getBpmIncrease();
		case offsetSetting.MoreIncrease -> offsetChange = root.getBpmMoreIncrease();
		case offsetSetting.MorerDecrease -> offsetChange = root.getBpmDecrease();
		case offsetSetting.MorestDecrease -> offsetChange = root.getBpmMoreDecrease();
		case offsetSetting.MorerIncrease -> offsetChange = root.getBpmIncrease();
		case offsetSetting.MorestIncrease -> offsetChange = root.getBpmMoreIncrease();
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
	}
	
	private int rangeCheck(int target, int min, int max) {
		target = target<min ? min : target;
		target = target>max ? max : target;
		return target;
	}
	
	public void setStep(String s) {
		step = Integer.parseInt(s);
	}
}