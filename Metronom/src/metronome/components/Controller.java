package metronome.components;

import java.net.*;
import java.util.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import metronome.SoundPlayer;
import metronome.components.actions.bpmSetting;
import metronome.components.actions.offsetSetting;
import metronome.components.actions.playAndStop;

public class Controller implements Initializable {
	private static Controller instance;
	
	private Stage stage;
	boolean isFocused = false;
	
	private SoundPlayer player ;
	private boolean isPlayed = false;
	
	GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	@FXML
	private BorderPane mediaControlPane;
	@FXML
	private BorderPane metronomeControlPane;
	@FXML
	private FlowPane mediaVisualPane;
	@FXML
	private FlowPane metronomeVisualPane;
	@FXML
	private ToggleButton selectMedia;
	@FXML
	private ToggleButton selectMetronome;
	@FXML
	private Button bpmLessDecrease;
	@FXML
	private Button bpmDecrease;
	@FXML
	private Button bpmMoreDecrease;
	@FXML
	private Button bpmLessIncrease;
	@FXML
	private Button bpmIncrease;
	@FXML
	private Button bpmMoreIncrease;
	@FXML
	private Button offsetDecrease;
	@FXML
	private Button offsetMoreDecrease;
	@FXML
	private Button offsetIncrease;
	@FXML
	private Button offsetMoreIncrease;
	@FXML
	private Button playSound;
	@FXML
	private Button stopSound;
	@FXML
	private ComboBox<Integer> selectBeat;
	@FXML
	private Slider bpmSlider;
	@FXML
	private Slider volumeSlider;
	@FXML
	private Label bpmValue;
	@FXML
	private Label bpmText;
	@FXML
	private Label offsetText;
	@FXML
	private Circle beat_0;
	@FXML
	private Circle beat_1;
	@FXML
	private Circle beat_2;
	@FXML
	private Circle beat_3;
	
	Button[] bpmButtons;
	Button[] offsetButtons;
	boolean[] keyPressed = new boolean[255];
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bpmButtons = new Button[]{bpmLessDecrease, bpmDecrease, bpmLessIncrease, bpmIncrease};
		offsetButtons = new Button[]{offsetDecrease, offsetMoreDecrease, offsetIncrease, offsetMoreIncrease};
		Integer[] ArrList = {4,6,8,12,16,24};
		ObservableList<Integer> beats = FXCollections.observableArrayList(ArrList);
		selectBeat.setItems(beats);
		player = new SoundPlayer(4);
		
		int bpm = Integer.parseInt( bpmValue.getText() );
		StringBuilder sb = new StringBuilder();
		sb.append( bpm/100 );
		int remain = bpm%100;
		if(remain>0) {
			double num = remain/100.0;
			String str = Double.toString(num);
			sb.append(str.substring(1));
		}
		bpmText.setText( sb.toString() );
		
		playSound.setOnAction( playAndStop.getPlay() );
		stopSound.setOnAction( playAndStop.getStop() );
		
		bpmLessDecrease.setOnMouseClicked( bpmSetting.getBpmChange(bpmSetting.LessDecrease) );
		bpmDecrease.setOnMouseClicked( bpmSetting.getBpmChange(bpmSetting.NormalDecrease) );
		bpmMoreDecrease.setOnMouseClicked( bpmSetting.getBpmChange(bpmSetting.MoreDecrease) );
		bpmLessIncrease.setOnMouseClicked( bpmSetting.getBpmChange(bpmSetting.LessIncrease) );
		bpmIncrease.setOnMouseClicked( bpmSetting.getBpmChange(bpmSetting.NormalIncrease) );
		bpmMoreIncrease.setOnMouseClicked( bpmSetting.getBpmChange(bpmSetting.MoreIncrease) );
		
		bpmSlider.valueProperty().addListener( bpmSetting.getSliderEvent() );
		
		offsetDecrease.setOnMouseClicked( offsetSetting.getoffsetChange( offsetSetting.NormalDecrease) );
		offsetMoreDecrease.setOnMouseClicked( offsetSetting.getoffsetChange( offsetSetting.MoreDecrease) );
		offsetIncrease.setOnMouseClicked( offsetSetting.getoffsetChange( offsetSetting.NormalIncrease) );
		offsetMoreIncrease.setOnMouseClicked( offsetSetting.getoffsetChange( offsetSetting.MoreIncrease) );
		
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			
			@Override 
			public void keyPressed(GlobalKeyEvent e) {
				if(keyPressed[e.getVirtualKeyCode()]) return;
				if(isFocused && e.isShiftPressed()) {
					keyPressed[e.getVirtualKeyCode()] = true;
					for (int i=0;i<4;i++) {
						final int idx = i;
						Platform.runLater(() -> bpmButtons[idx].setText(bpmSetting.IncreaseStepSize(bpmButtons[idx])) );
						Platform.runLater(() -> {
							offsetButtons[idx].setText(offsetSetting.IncreaseStepSize(offsetButtons[idx]));
							offsetButtons[idx].setOnMouseClicked( offsetSetting.getoffsetChange( Integer.parseInt(offsetButtons[idx].getText()) ) );
						});
					}
				}
			}
			
			@Override 
			public void keyReleased(GlobalKeyEvent e) {
				//쉬프트 땠을 때, 원상태로 복귀 코드 입력
				if(isFocused && e.getVirtualKeyCode() == GlobalKeyEvent.VK_LSHIFT) {
					keyPressed[e.getVirtualKeyCode()] = false;
					for (int i=0;i<4;i++) {
						final int idx = i;
						Platform.runLater(() -> bpmButtons[idx].setText(bpmSetting.DecreaseStepSize(bpmButtons[idx])) );
						Platform.runLater(() -> {
							offsetButtons[idx].setText(offsetSetting.DecreaseStepSize(offsetButtons[idx]));
							offsetButtons[idx].setOnMouseClicked( offsetSetting.getoffsetChange( Integer.parseInt(offsetButtons[idx].getText()) ) );
						});
					}
				}
			}
			
		});
	}
	
	public Controller() {
		instance = this;
	}
	
	public static Controller getInstance() {
	    return instance;
	}
	
	public void setStage(Stage stage) {
        this.stage = stage;

        // 창의 포커스 상태를 감지하는 리스너 추가
        this.stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    //System.out.println("Window gained focus");
                	isFocused = true;
                } else {
                    //System.out.println("Window lost focus");
                	isFocused = false;
                }
            }
        });
    }
	
	public BorderPane getMediaControlPane() {
		return mediaControlPane;
	}

	public BorderPane getMetronomeControlPane() {
		return metronomeControlPane;
	}

	public FlowPane getMediaVisualPane() {
		return mediaVisualPane;
	}

	public FlowPane getMetronomeVisualPane() {
		return metronomeVisualPane;
	}

	public ToggleButton getSelectMedia() {
		return selectMedia;
	}

	public ToggleButton getSelectMetronome() {
		return selectMetronome;
	}

	public Button getBpmLessDecrease() {
		return bpmLessDecrease;
	}

	public Button getBpmDecrease() {
		return bpmDecrease;
	}

	public Button getBpmMoreDecrease() {
		return bpmMoreDecrease;
	}

	public Button getBpmLessIncrease() {
		return bpmLessIncrease;
	}

	public Button getBpmIncrease() {
		return bpmIncrease;
	}

	public Button getBpmMoreIncrease() {
		return bpmMoreIncrease;
	}

	public Button getOffsetDecrease() {
		return offsetDecrease;
	}

	public Button getOffsetMoreDecrease() {
		return offsetMoreDecrease;
	}

	public Button getOffsetIncrease() {
		return offsetIncrease;
	}

	public Button getOffsetMoreIncrease() {
		return offsetMoreIncrease;
	}
	
	public Button getPlaySound() {
		return playSound;
	}

	public Button getStopSound() {
		return stopSound;
	}
	public ComboBox<Integer> getSelectBeat() {
		return selectBeat;
	}

	public Slider getBpmSlider() {
		return bpmSlider;
	}

	public Slider getVolumeSlider() {
		return volumeSlider;
	}
	
	public Label getBpmValue() {
		return bpmValue;
	}
	
	public Label getBpmText() {
		return bpmText;
	}

	public Label getOffsetText() {
		return offsetText;
	}
	
	public SoundPlayer getPlayer() {
		return player;
	}

	public void setPlayer(SoundPlayer player) {
		this.player = player;
	}

	public boolean isPlayed() {
		return isPlayed;
	}

	public void setPlayed(boolean isPlayed) {
		this.isPlayed = isPlayed;
	}
	public Circle[] getBeatIndicator() {
		return new Circle[] {beat_0,beat_1,beat_2,beat_3};
	}
}