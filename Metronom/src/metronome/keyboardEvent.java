package metronome;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;
import metronome.components.actions.bpmSetting;
import metronome.components.actions.offsetSetting;
import metronome.sound.SoundPlayer;

public class keyboardEvent implements GlobalKeyListener {
	private static final Controller root = Controller.getInstance();
	private boolean[] keyPressed = new boolean[255];
	private Button[] bpmButtons = {root.getBpmLessDecrease(), root.getBpmDecrease(), root.getBpmLessIncrease(), root.getBpmIncrease()};
	private Button[] offsetButtons = {root.getOffsetDecrease(), root.getOffsetMoreDecrease(), root.getOffsetIncrease(), root.getOffsetMoreIncrease()};
	private Label offsetText = root.getOffsetText();
	private Button playSound = root.getPlaySound();
	private Button stopSound = root.getStopSound();
	
	private boolean isAdjusted = false;
	
	@Override 
	public void keyPressed(GlobalKeyEvent e) {
		//System.out.println(e);
		if(keyPressed[e.getVirtualKeyCode()]) return;
		
		if(root.isFocused() && e.isShiftPressed()) {	//shift
			isAdjusted = true;
			for (int i=0;i<4;i++) {
				final int idx = i;
				Platform.runLater(() -> bpmButtons[idx].setText(bpmSetting.IncreaseStepSize(bpmButtons[idx])) );
				Platform.runLater(() -> {
					offsetButtons[idx].setText(offsetSetting.IncreaseStepSize(offsetButtons[idx]));
					offsetButtons[idx].setOnMouseClicked( offsetSetting.getButtonEvent( Integer.parseInt(offsetButtons[idx].getText()) ) );
				});
			}
		}
		
		if(root.isPlayed()) {
			if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_OEM_5) {	// ctrl + \
				long current = System.currentTimeMillis();
				Platform.runLater(() -> offsetText.setText(Long.toString(current-root.getStartTime())) );
				SoundPlayer.setOffset( (int)(current-root.getStartTime()) );
			}
			if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {	// esc
				stopSound.fire();
			}
		} else {
			if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_RETURN) {	// ctrl + enter
				playSound.fire();
			}
		}
		
		keyPressed[e.getVirtualKeyCode()] = true;
	}
	
	@Override 
	public void keyReleased(GlobalKeyEvent e) {
		//쉬프트 땠을 때, 원상태로 복귀 코드 입력
		if(isAdjusted && e.getVirtualKeyCode() == GlobalKeyEvent.VK_LSHIFT) {
			isAdjusted = false;
			for (int i=0;i<4;i++) {
				final int idx = i;
				Platform.runLater(() -> bpmButtons[idx].setText(bpmSetting.DecreaseStepSize(bpmButtons[idx])) );
				Platform.runLater(() -> {
					offsetButtons[idx].setText(offsetSetting.DecreaseStepSize(offsetButtons[idx]));
					offsetButtons[idx].setOnMouseClicked( offsetSetting.getButtonEvent( Integer.parseInt(offsetButtons[idx].getText()) ) );
				});
			}
		}
		
		keyPressed[e.getVirtualKeyCode()] = false;
	}
	
	//Singleton Pattern
	static keyboardEvent I = new keyboardEvent();
	public static keyboardEvent getInstance() {
		return I;
	}
	private keyboardEvent() {
	}
	
}