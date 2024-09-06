package metronome;

import java.net.*;
import java.util.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import javafx.application.Platform;
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
import metronome.components.actions.*;
import metronome.sound.*;

public class Controller implements Initializable {
	/** Buravura font to integer */
	private static HashMap<Character,Integer> buravura = new HashMap<>(10);
	
	/**Instances of singleton pattern */
	private static Controller instance;
	public Controller() {
		instance = this;
		char[] TimeSignatureCharacter = {'','','','','','','','','',''};
		for(int i=0;i<10;i++)
			buravura.put(TimeSignatureCharacter[i], i);
	}
	
	public static Controller getInstance() {
	    return instance;
	}
	
	
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
	private Button browseFile;
	@FXML
	private ComboBox<Integer> selectNote;
	@FXML
	private Slider bpmSlider;
	@FXML
	private Slider volumeSlider;
	/** To store bpm without errors */	@FXML
	private Label bpmValue;
	/** Actual bpm figures displayed */	@FXML 
	private Label bpmText;
	@FXML
	private Label offsetText;
	@FXML
	private Label musicFilePath;
	@FXML
	private Label timeSignatureTime;
	@FXML
	private Label timeSignatureBeat;
	@FXML
	private Rectangle timeClickJone;
	@FXML
	private Rectangle beatClickJone;
	
	private Stage stage;
	/** Window's Focus Status */
	private boolean isFocused = false;
	
	private MusicStrategy player = MetronomePlayer.getInstance();
	/** Sound Playback Status */
	private boolean isPlayed = false;
	/** Start time in milliseconds when play sound */
	private long startTime;
	
	/** The event to receive keyboard input in background  */
	private GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	/** The variable for bitmasking the type of beat indicator */
	private long accentArray = 0x00_00_00_00_00_00_00_01L;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//ObservableList<Integer> notes = FXCollections.observableArrayList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32});
		/** Types of notes (Influenced by speed) */
		ObservableList<Integer> notes = FXCollections.observableArrayList(new Integer[] {4,6,8,12,16,24,32});
		/** Setting ComboBox */
		selectNote.setItems(notes);
		
		/** Convert bpm from int to float text */
		int bpm = Integer.parseInt( bpmValue.getText() );
		StringBuilder sb = new StringBuilder();
		sb.append( bpm/100 );
		int remain = bpm%100;
		/** decimal places */
		if(remain>0) {
			double num = remain/100.0;
			String str = Double.toString(num);
			sb.append(str.substring(1));
		}
		bpmText.setText( sb.toString() );
		
		/** Set events for each component */
		playSound.setOnAction( playAndStop.getPlay() );
		stopSound.setOnAction( playAndStop.getStop() );
		
		bpmLessDecrease.setOnMouseClicked( bpmSetting.getButtonEvent( bpmLessDecrease ) );
		bpmDecrease.setOnMouseClicked( bpmSetting.getButtonEvent( bpmDecrease ) );
		bpmMoreDecrease.setOnMouseClicked( bpmSetting.getButtonEvent( bpmMoreDecrease ) );
		bpmLessIncrease.setOnMouseClicked( bpmSetting.getButtonEvent( bpmLessIncrease ) );
		bpmIncrease.setOnMouseClicked( bpmSetting.getButtonEvent( bpmIncrease ) );
		bpmMoreIncrease.setOnMouseClicked( bpmSetting.getButtonEvent( bpmMoreIncrease ) );
		
		bpmSlider.valueProperty().addListener( bpmSetting.getSliderEvent( bpmSlider ) );
		bpmSlider.focusedProperty().addListener( indicatorSetting.getBlurEvent() );
		
		offsetDecrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.NormalDecrease) );
		offsetMoreDecrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.MoreDecrease) );
		offsetIncrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.NormalIncrease) );
		offsetMoreIncrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.MoreIncrease) );
		
		volumeSlider.valueProperty().addListener( VolumeSetting.getSliderEvnet() );
		volumeSlider.focusedProperty().addListener( indicatorSetting.getBlurEvent() );
		
		selectNote.valueProperty().addListener( noteSetting.getBeatSelectEvent() );
		selectNote.getSelectionModel().select( Integer.valueOf(4) );
		
		timeClickJone.setOnMouseClicked(timeSignatureSetting.getTimeSignatureEvent(timeSignatureSetting.NUMERATOR, new String[]{"","","",""} , 1 ));
		beatClickJone.setOnMouseClicked(timeSignatureSetting.getTimeSignatureEvent(timeSignatureSetting.DENOMINATOR, new String[]{"",""} ));
		
		keyboardHook.addKeyListener(keyboardEvent.getInstance());
		
		metronomeVisualPane.widthProperty().addListener((observable, oldValue, newValue) -> indicatorSetting.adjustBallSizes( getIndicatorRows() ));
		metronomeVisualPane.heightProperty().addListener((observable, oldValue, newValue) -> indicatorSetting.adjustBallSizes( getIndicatorRows() ));
		
		//대망의 음악모드 설정
		selectMedia.setOnAction( modeSelecting.getModeSelectEvent(mediaVisualPane, mediaControlPane, MusicPlayer.getInstance()) );
		selectMetronome.setOnAction( modeSelecting.getModeSelectEvent(metronomeVisualPane, metronomeControlPane, MetronomePlayer.getInstance()) );
		
		Platform.runLater(() -> browseFile.setOnAction(fileBrowsing.getFileBrowsing(stage)));
		;
	}
	
	public void setStage(Stage stage) {
        this.stage = stage;
        /** Listener to detect the window's focus status */
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
	public MusicStrategy getPlayer() {
		return player;
	}
	public void setPlayer(MusicStrategy pl) {
		player = pl;
		
		playAndStop.setPlayer(pl);
		offsetSetting.setPlayer(pl);
		VolumeSetting.setPlayer(pl);
		if(pl instanceof MetronomeStrategy) {
			MetronomeStrategy temp = (MetronomeStrategy) pl;
			bpmSetting.setPlayer(temp);
			indicatorSetting.setPlayer(temp);
			noteSetting.setPlayer(temp);
			timeSignatureSetting.setPlayer(temp);
		}
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
			for(int i=0;i<list.size();i++)
				balls[idx++] = (Circle)list.get(i);
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
		int beat = getBuravuraValue(timeSignatureBeat.getText());
		if((note * time)%beat > 0) return -1;
		return note * time/beat;
	}
	public Label getMusicFilePath() {
		return musicFilePath;
	}
}