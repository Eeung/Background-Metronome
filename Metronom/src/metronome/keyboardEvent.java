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
	
	private Button[] bpmButtons = {root.getBpmLessDecrease(), root.getBpmDecrease(), root.getBpmLessIncrease(), root.getBpmIncrease()};
	private Button[] offsetButtons = {root.getOffsetDecrease(), root.getOffsetMoreDecrease(), root.getOffsetIncrease(), root.getOffsetMoreIncrease()};
	private Label offsetText = root.getOffsetText();
	private Button playSound = root.getPlaySound();
	private Button stopSound = root.getStopSound();
	
	/** The pressed status of each Key */
	private boolean[] keyPressed = new boolean[255];
	/** Adjusted state of the increase/decrease buttons */
	private boolean isAdjusted = false;
	
	@Override 
	public void keyPressed(GlobalKeyEvent e) {
		//System.out.println(e);
		if(keyPressed[e.getVirtualKeyCode()]) return;
		
		/** When you press Shift key, increase/decrease buttons will be adjusted */
		if(root.isFocused() && e.isShiftPressed()) {
			isAdjusted = true;
			for (int i=0;i<4;i++) {
				final int idx = i;
				Platform.runLater(() -> {
					bpmButtons[idx].setText(bpmSetting.IncreaseStepSize(bpmButtons[idx]));
					offsetButtons[idx].setText(offsetSetting.IncreaseStepSize(offsetButtons[idx]));
					// 텍스트는 바꼈는데 왜 증감치는 그대로냐 시발?
					offsetButtons[idx].setOnMouseClicked( offsetSetting.getButtonEvent( Integer.parseInt(offsetButtons[idx].getText()) ) );
				});
			}
		}
		/** When sound is playing */
		if(root.isPlayed()) {
			/** Press "Ctrl + \" to set the offset minus the time when pressed and the time stored at playback. */
			if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_OEM_5) {	
				long current = System.currentTimeMillis();
				Platform.runLater(() -> offsetText.setText(Long.toString(current-root.getStartTime())) );
				SoundPlayer.setOffset( (int)(current-root.getStartTime()) );
			}/** Press Escape key to stop sound */
			else if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
				stopSound.fire();
			} /** Press F5 key to restart sound by subtracting it from the offset for the specified time. */
			else if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_F5) {
				stopSound.fire();
				SoundPlayer.activateFastRetry();
				playSound.fire();
			}
		} else {
			/** Press "Ctrl + Enter" to start sound */
			if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_RETURN) {
				playSound.fire();
			}
		}
		
		keyPressed[e.getVirtualKeyCode()] = true;
	}
	
	@Override 
	public void keyReleased(GlobalKeyEvent e) {
		/** When you release Shift Key, the values of the increase/decrease buttons are returned */
		if(isAdjusted && e.getVirtualKeyCode() == GlobalKeyEvent.VK_LSHIFT) {
			isAdjusted = false;
			for (int i=0;i<4;i++) {
				final int idx = i;
				Platform.runLater(() -> {
					bpmButtons[idx].setText(bpmSetting.DecreaseStepSize(bpmButtons[idx]));
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