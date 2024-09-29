package metronome;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;
import metronome.components.actions.bpmSetting;
import metronome.components.actions.offsetSetting;
import metronome.sound.MusicStrategy;

public class keyboardEvent implements GlobalKeyListener {
	private static final Controller root = Controller.getInstance();
	
	private Button[] bpmButtons = {root.getBpmLessDecrease(), root.getBpmDecrease(), root.getBpmLessIncrease(), root.getBpmIncrease()};
	private Button[] offsetButtons = {root.getOffsetDecrease(), root.getOffsetMoreDecrease(), root.getOffsetIncrease(), root.getOffsetMoreIncrease()};
	private Label offsetText = root.getOffsetText();
	private Button playSound = root.getPlaySound();
	private Button stopSound = root.getStopSound();
	private MusicStrategy player = root.getPlayer();
	
	/** 누른 키를 각 키코드에 해당되는 인덱스에 저장하기 위한 배열 */
	private boolean[] keyPressed = new boolean[255];
	/** 증감치 조정키를 누른 여부를 저장하는 변수 */
	private boolean isAdjusted = false;
	
	@Override 
	public void keyPressed(GlobalKeyEvent e) {
		if(keyPressed[e.getVirtualKeyCode()]) return;
		keyPressed[e.getVirtualKeyCode()] = true;
		System.out.println(e);
		
		/** 창 포커스 상태이며, 증감치 조정 버튼을 누르면 bpm과 offset버튼들의 값이 바뀜 */
		if(root.isFocused() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_LSHIFT) {
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
		/** 재생 도중 */
		if(root.isPlayed()) {
			/** 빠른 오프셋 조정 키 조합을 입력하면, 매트로놈 시작시간과 누른 시간의 차가 오프셋이 됨. */
			if(checkKeyCommand( Settings.getQuickOffsetSetting() )) {	
				long current = System.currentTimeMillis();
				Platform.runLater(() -> offsetText.setText(Long.toString(current-root.getStartTime())) );
				player.setOffset( (int)(current-root.getStartTime()) );
			}/** 종료버튼(ESC) */
			else if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
				stopSound.fire();
			} /** 빠른 리트라이 버튼(F5)을 누르면 매트로놈도 재시작함. */
			else if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_F5) {
				stopSound.fire();
				player.activateFastRetry( root.getGame() );
				playSound.fire();
			}
		} else {
			/** 매트로놈 재생 키 조합을 입력하면, 매트로놈 재생됨. */
			if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_RETURN) {
				playSound.fire();
			}
		}
	}
	
	@Override 
	public void keyReleased(GlobalKeyEvent e) {
		/** 증감치 조정키를 때면, 버튼들의 값이 되돌아옴. */
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
	
	private boolean checkKeyCommand(List<Integer> command) {
		for(int keycode : command)
			if(!keyPressed[keycode])
				return false;
		return true;
	}
	
	//Singleton Pattern
	static keyboardEvent I = new keyboardEvent();
	public static keyboardEvent getInstance() {
		return I;
	}
	private keyboardEvent() {
	}
	
}