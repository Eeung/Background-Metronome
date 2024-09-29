package metronome;

import java.net.*;
import java.util.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import metronome.components.actions.*;
import metronome.shortcut.showShortcutView;
import metronome.sound.*;

public class Controller implements Initializable {
	/** Buravura 폰트 저장할 맵 */
	private static HashMap<Character,Integer> buravura = new HashMap<>(10);
	private char[] buravuraCharacter = {'','','','','','','','','',''};
	// Singleton Pattern
	private static Controller instance;
	public Controller() {
		instance = this;
		
		for(int i=0;i<10;i++)
			buravura.put(buravuraCharacter[i], i);
	}
	public static Controller getInstance() {
	    return instance;
	}
	
	@FXML
	private FlowPane metronomeVisualPane;
	@FXML
	private VBox indicatorCol;
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
	/** 오차 없이 bpm저장하기 위한 레이블 */	@FXML
	private Label bpmValue;
	/** 실제 표기되는 bpm */	@FXML 
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
	@FXML
	private Menu SettingMenu;
	@FXML
	private MenuItem KeySetting;
	@FXML
	private RadioMenuItem djmaxQuickTry;
	@FXML
	private RadioMenuItem ez2onQuickTry;
	
	public Stage stage;
	/** 창의 포커스 상태 */
	private boolean isFocused = false;
	/** 지금 하고 있는 게임 선택 */
	private String whatGame;
	
	private MusicStrategy player = MetronomePlayer.getInstance();
	/** 매트로놈 재생 상태 */
	private boolean isPlayed = false;
	/** 매트로놈 재생할 때의 시간 */
	private long startTime;
	
	/** 백그라운드에서 키보드 입력을 받기 위함  */
	private GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	/** 인디케이터의 악센트 여부를 비트마스킹 */
	private long accentArray = Settings.getAccentBeat();
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//ObservableList<Integer> notes = FXCollections.observableArrayList(new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32});
		/** 비트 종류들(사실은 음표임) */
		ObservableList<Integer> notes = FXCollections.observableArrayList(new Integer[] {4,6,8,12,16,24,32});
		selectNote.setItems(notes);
		
		/** 정수 값인 bpm을 실수 값으로 변환 */
		bpmValue.setText( Integer.toString(Settings.getBpm()) );
		int bpm = Integer.parseInt( bpmValue.getText() );
		StringBuilder sb = new StringBuilder();
		sb.append( bpm/100 );
		int remain = bpm%100;
		/** 소수부분 */
		if(remain>0) {
			double num = remain/100.0;
			String str = Double.toString(num);
			sb.append(str.substring(1));
		}
		bpmText.setText( sb.toString() );
		
		offsetText.setText( Integer.toString(Settings.getOffset()) );
		
		/** 각 컴포넌트들의 이벤트를 설정 */
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
		bpmSlider.setValue( Settings.getBpm()/100.0 );
		
		offsetDecrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.NormalDecrease) );
		offsetMoreDecrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.MoreDecrease) );
		offsetIncrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.NormalIncrease) );
		offsetMoreIncrease.setOnMouseClicked( offsetSetting.getButtonEvent( offsetSetting.MoreIncrease) );
		
		volumeSlider.valueProperty().addListener( VolumeSetting.getSliderEvnet() );
		volumeSlider.focusedProperty().addListener( indicatorSetting.getBlurEvent() );
		volumeSlider.setValue( Settings.getVolume() );
		
		selectNote.valueProperty().addListener( noteSetting.getBeatSelectEvent() );
		selectNote.getSelectionModel().select( Settings.getNote() );
		
		
		timeSignatureTime.setText( getBuravuraString(Settings.getTime()) );
		timeClickJone.setOnMouseClicked(timeSignatureSetting.getTimeSignatureEvent(
				timeSignatureSetting.NUMERATOR, new String[]{"","","",""} , timeSignatureTime.getText() ));
		timeSignatureBeat.setText( getBuravuraString(Settings.getBeat()) );
		beatClickJone.setOnMouseClicked(timeSignatureSetting.getTimeSignatureEvent(
				timeSignatureSetting.DENOMINATOR, new String[]{"",""} , timeSignatureBeat.getText() ));
		indicatorSetting.rebuildIndicator(getBeatCount());
		
		metronomeVisualPane.widthProperty().addListener((observable, oldValue, newValue) -> indicatorSetting.adjustBallSizes( getIndicatorRows() ));
		metronomeVisualPane.heightProperty().addListener((observable, oldValue, newValue) -> indicatorSetting.adjustBallSizes( getIndicatorRows() ));
		
		KeySetting.setOnAction( (e) -> {
			keyboardHook.shutdownHook();
			new showShortcutView();
			keyboardHook = new GlobalKeyboardHook(false);
			keyboardHook.addKeyListener(keyboardEvent.getInstance());
		} );
		
		djmaxQuickTry.setOnAction(new quickTrySetting("djmax"));
		ez2onQuickTry.setOnAction(new quickTrySetting("ez2on"));
		gameSelect(Settings.getGame());
		
		keyboardHook.addKeyListener(keyboardEvent.getInstance());
	}
	
	public void setStage(Stage stage) {
        this.stage = stage;
        /** 포커스 이벤트 설정 */
        this.stage.focusedProperty().addListener((observable,oldValue,newValue) -> {
            if (newValue) isFocused = true;
            else isFocused = false;
        });
    }
	public FlowPane getMetronomeVisualPane() {
		return metronomeVisualPane;
	}
	public VBox getIndicatorCol() {
		return indicatorCol;
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
	/*public void setPlayer(MusicStrategy pl) {
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
	}*/
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
	public void setAccent(int idx, boolean newValue) {
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
	public String getBuravuraString(int value) {
		StringBuilder sb = new StringBuilder();
		while(value>0) {
			sb.insert(0, buravuraCharacter[value%10]);
			value /= 10;
		}
		return sb.toString();
	}
	public int getBeatCount() {
		int note = selectNote.getValue();
		int time = getBuravuraValue(timeSignatureTime.getText());
		int beat = getBuravuraValue(timeSignatureBeat.getText());
		if((note * time)%beat > 0) return -1;
		return note * time/beat;
	}
	
	public void setGame(String name) {
		whatGame = name;
	}
	
	public String getGame() {
		return whatGame;
	}
	private void gameSelect(String game) {
		switch(game) {
		case "djmax":
			djmaxQuickTry.setSelected(true);
			djmaxQuickTry.fire();
			break;
		case "ez2on":
			ez2onQuickTry.setSelected(true);
			ez2onQuickTry.fire();
		}
	}
}