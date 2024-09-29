package metronome.shortcut;

import java.net.URL;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;
import metronome.Settings;

public class keySettingController implements Initializable {
	private Stage stage;
	
	@FXML
	Button fastOffsetSetting;
	
	private GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		fastOffsetSetting.setText( Settings.getQuickOffsetSettingString() );
		fastOffsetSetting.setOnAction(e -> 
			keyboardHook.addKeyListener(new setShortcut(fastOffsetSetting))
		);
		
	}
	
	public void setStage(Stage s) {
		stage = s;
	}
	
	//--------------------------------------------------------------------------------
	private class setShortcut implements GlobalKeyListener{
		Button button;
		String before;
		
		private boolean[] keyPressed = new boolean[255];
		byte functionKey = 0b00000000;
		PriorityQueue<Integer> que = new PriorityQueue<>();
		
		public setShortcut(Button b) {
			button = b;
			before = b.getText();
			
			setDiableAllButtons(true);
			b.setText("취소하려면 ESC를 누르세요.");
		}
		
		@Override
		public void keyPressed(GlobalKeyEvent e) {
			if(keyPressed[e.getVirtualKeyCode()]) return;
			if(!stage.isFocused()) return;
			
			keyPressed[e.getVirtualKeyCode()] = true;
			System.out.println(e);
			
			if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
				Platform.runLater(() -> button.setText(before));
				cancelShortcutSetting();
				return;
			}
			
			switch(e.getVirtualKeyCode()) {
			case GlobalKeyEvent.VK_LCONTROL -> functionKey |= 1;
			case GlobalKeyEvent.VK_LMENU -> functionKey |= 2;
			case GlobalKeyEvent.VK_LSHIFT -> functionKey |= 4;
			case GlobalKeyEvent.VK_RCONTROL -> functionKey |= 8;
			case GlobalKeyEvent.VK_RMENU -> functionKey |= 16;
			case GlobalKeyEvent.VK_RSHIFT ->functionKey |= 32;
			default ->
				que.add(e.getVirtualKeyCode());
			}
		}
		@Override
		public void keyReleased(GlobalKeyEvent e) {
			keyPressed[e.getVirtualKeyCode()] = false;
			
			LinkedList<Integer> list = new LinkedList<>();
			StringBuilder sb = new StringBuilder();
			if((functionKey&1) == 1) sb.append("LCtrl + ");
			if((functionKey&8) == 8) sb.append("RCtrl + ");
			if((functionKey&2) == 2) sb.append("LAlt + ");
			if((functionKey&16) == 16) sb.append("RAlt + ");
			if((functionKey&4) == 4) sb.append("LShift + ");
			if((functionKey&32) == 32) sb.append("RShift + ");
			for(int val : que) {
				list.add(val);
				sb.append( KeyValue.getKeyValuetoString().get(val) ).append(" + ");
			}
			
			sb.setLength(sb.length()-3);
			Platform.runLater(() -> button.setText(sb.toString()));
			Settings.setQuickOffsetSetting(list);
			Settings.setQuickOffsetSettingString(sb);
			cancelShortcutSetting();
		}
		
		private void cancelShortcutSetting() {
			setDiableAllButtons(false);
			keyboardHook.shutdownHook();
			keyboardHook = new GlobalKeyboardHook(false); 
		}
		
		private void setDiableAllButtons(boolean bol) {
			fastOffsetSetting.setDisable(bol);
		}
	}
}
