package metronome;

import java.net.*;
import java.util.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import metronome.components.actions.VolumeSetting;
import metronome.components.actions.noteSetting;
import metronome.components.actions.bpmSetting;
import metronome.components.actions.indicatorSetting;
import metronome.components.actions.offsetSetting;
import metronome.components.actions.playAndStop;
import metronome.components.actions.timeSignatureSetting;

public class Controller implements Initializable {
	private static Controller instance;
	
	@FXML
	private FlowPane metronomeVisualPane;
	@FXML
	private BorderPane mediaControlPane;
	@FXML
	private BorderPane metronomeControlPane;
	@FXML
	private FlowPane mediaVisualPane;
	@FXML
	private VBox indicatorCol;
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
	private ComboBox<Integer> selectNote;
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
	private Label timeSignatureTime;
	@FXML
	private Label timeSignatureBeat;
	@FXML
	private Rectangle timeClickJone;
	@FXML
	private Rectangle beatClickJone;
	
	private Stage stage;
	private boolean isFocused = false;
	
	private boolean isPlayed = false;
	private long startTime;
	
	private GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	private long accentArray = 0x00_00_00_00_00_00_00_01L;
	private static HashMap<Character,Integer> buravura = new HashMap<>(10);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//ObservableList<Integer> notes = FXCollections.observableArrayList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32});
		ObservableList<Integer> notes = FXCollections.observableArrayList(new Integer[] {4,6,8,12,16,24,32});
		selectNote.setItems(notes);
		
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
		
		selectNote.valueProperty().addListener( noteSetting.getBeatSelectEvent() );
		selectNote.getSelectionModel().select(0);
		
		timeClickJone.setOnMouseClicked(timeSignatureSetting.getTimeSignatureEvent(timeSignatureSetting.NUMERATOR, new String[]{"","","",""} , 1 ));
		beatClickJone.setOnMouseClicked(timeSignatureSetting.getTimeSignatureEvent(timeSignatureSetting.DENOMINATOR, new String[]{"",""} ));
		
		keyboardHook.addKeyListener(keyboardEvent.getInstance());
		
		metronomeVisualPane.widthProperty().addListener((observable, oldValue, newValue) -> indicatorSetting.adjustBallSizes( getIndicatorRows() ));
		metronomeVisualPane.heightProperty().addListener((observable, oldValue, newValue) -> indicatorSetting.adjustBallSizes( getIndicatorRows() ));
	}
	
	public Controller() {
		instance = this;
		char[] TimeSignatureCharacter = {'','','','','','','','','',''};
		for(int i=0;i<10;i++)
			buravura.put(TimeSignatureCharacter[i], i);
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

	public VBox getIndicatorCol() {
		return indicatorCol;
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
		return selectNote;
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
	
	public HBox[] getIndicatorRows() {
		return indicatorCol.getChildren().toArray(new HBox[0]);
	}
	
	public Circle[] getBeatIndicator(HBox[] rows) {
		Circle[] balls = new Circle[ getBeatCount() ];
		int idx = 0;
		for(HBox row : rows) {
			ObservableList<Node> list = row.getChildren();
			for(int i=0;i<list.size();i++) {
				balls[idx++] = (Circle)list.get(i);
			}
		}
		return balls;
	}
	
	public boolean isAccent(int idx) {
		if( ((accentArray>>>idx) & 1) == 1) return true;
		return false;
	}

	public void setIsAccent(int idx, boolean newValue) {
		if(newValue) accentArray |= 1L << idx;
		else accentArray &= ~(1L << idx) ;
	}
	
	public Label getTimeSignatureTime() {
		return timeSignatureTime;
	}

	public Label getTimeSignatureBeat() {
		return timeSignatureBeat;
	}
	
	public int getBuravuraValue(String str) {
		int n = 0;
		for(char c : str.toCharArray()) {
			n = (n<<3)+(n<<1) + buravura.get(c);
		}
		return n;
	}
	
	public int getBeatCount() {
		int note = selectNote.getValue();
		int time = getBuravuraValue(timeSignatureTime.getText());
		int beat = 4;//root.getBuravuraValue(timeSignatureBeat.getText());
		return note * time/beat;
	}
}