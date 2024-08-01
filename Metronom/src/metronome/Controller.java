package metronome;

import java.net.*;
import java.util.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import metronome.sound.SoundPlayer;
import metronome.components.actions.VolumeSetting;
import metronome.components.actions.bpmSetting;
import metronome.components.actions.indicatorSetting;
import metronome.components.actions.offsetSetting;
import metronome.components.actions.playAndStop;

public class Controller implements Initializable {
	private static Controller instance;
	
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
	
	Button[] bpmButtons;
	Button[] offsetButtons;
	
	private Stage stage;
	private boolean isFocused = false;
	
	private SoundPlayer player ;
	private boolean isPlayed = false;
	private long startTime;
	
	private GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	private int accentArray = 0b0000_0000_0000_0000_0000_0000_0000_0001;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bpmButtons = new Button[]{bpmLessDecrease, bpmDecrease, bpmLessIncrease, bpmIncrease};
		offsetButtons = new Button[]{offsetDecrease, offsetMoreDecrease, offsetIncrease, offsetMoreIncrease};
		ObservableList<Integer> beats = FXCollections.observableArrayList(new Integer[] {4,6,8,12,16,24});
		selectBeat.setItems(beats);
		selectBeat.getSelectionModel().selectFirst();
		player = new SoundPlayer();
		
		//표기 bpm 설정
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
		
		//각 컴포넌트의 이벤트 설정
		playSound.setOnAction( playAndStop.getPlay() );
		stopSound.setOnAction( playAndStop.getStop() );
		
		bpmLessDecrease.setOnMouseClicked( bpmSetting.getButtonEvent(bpmSetting.LessDecrease) );
		bpmDecrease.setOnMouseClicked( bpmSetting.getButtonEvent(bpmSetting.NormalDecrease) );
		bpmMoreDecrease.setOnMouseClicked( bpmSetting.getButtonEvent(bpmSetting.MoreDecrease) );
		bpmLessIncrease.setOnMouseClicked( bpmSetting.getButtonEvent(bpmSetting.LessIncrease) );
		bpmIncrease.setOnMouseClicked( bpmSetting.getButtonEvent(bpmSetting.NormalIncrease) );
		bpmMoreIncrease.setOnMouseClicked( bpmSetting.getButtonEvent(bpmSetting.MoreIncrease) );
		
		bpmSlider.valueProperty().addListener( bpmSetting.getSliderEvent() );
		bpmSlider.focusedProperty().addListener( indicatorSetting.getBlurEvent() );
		
		offsetDecrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.NormalDecrease) );
		offsetMoreDecrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.MoreDecrease) );
		offsetIncrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.NormalIncrease) );
		offsetMoreIncrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.MoreIncrease) );
		
		volumeSlider.valueProperty().addListener( VolumeSetting.getSliderEvnet() );
		volumeSlider.focusedProperty().addListener( indicatorSetting.getBlurEvent() );
		
		//비트 조정 추가
		
		for(int i=0; i<selectBeat.getSelectionModel().getSelectedItem(); i++) {
			Circle ball = new Circle(30);
			ball.setId( Integer.toString(i) );
			int bit = 1<<i;
			if((accentArray & bit) == bit) ball.focusedProperty().addListener( indicatorSetting.getAccentBeatEvent(ball) ); // 공의 색상 설정
			else ball.focusedProperty().addListener( indicatorSetting.getNormalBeatEvent(ball) );
			ball.setOnMouseClicked( indicatorSetting.getChangeBeatEvent(ball) );
			metronomeVisualPane.getChildren().add(ball);
		}
		
		keyboardHook.addKeyListener(keyboardEvent.getInstance());
		
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
        this.stage.focusedProperty().addListener((observable,oldValue,newValue) -> {
            if (newValue) isFocused = true;
            else isFocused = false;
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
	
	public boolean isFocused() {
		return isFocused;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long miliSec) {
		startTime = miliSec;
	}
	
	public Circle[] getBeatIndicator() {
		return metronomeVisualPane.getChildren().toArray(new Circle[0]);
	}
	
	public boolean isAccent(int idx) {
		if( ((accentArray>>>idx) & 1) == 1) return true;
		return false;
	}

	public void setIsAccent(int idx, boolean newValue) {
		if(newValue) this.accentArray |= 1 << idx;
		else this.accentArray &= ~(1 << idx) ;
	}
}