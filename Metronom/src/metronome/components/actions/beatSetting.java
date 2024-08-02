package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class beatSetting  {
	private static final Controller root = Controller.getInstance();
	private static VBox metronomeVisualPane = root.getMetronomeVisualPane();
	
	public static select getBeatSelectEvent() {
		return select.getInstance();
	}
	
	private static class select implements ChangeListener<Integer> {
		@Override
		public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
			
			metronomeVisualPane.getChildren().clear();
			metronomeVisualPane.getChildren().add(createFlowPane());
			if(newValue >9) {
				metronomeVisualPane.getChildren().add(createFlowPane());
				if(newValue>19 && (newValue&3)==0) {
					metronomeVisualPane.getChildren().add(createFlowPane());
					metronomeVisualPane.getChildren().add(createFlowPane());
					
					int cnt = newValue/4;
					FlowPane[] rows = root.getIndicatorRows();
					for(int i=0;i<4;i++) {
						for(int j=0;j<cnt;j++) {
							Circle ball = new Circle(30);
							ball.setId( Integer.toString(j) );
							if(root.isAccent(i*4+j)) ball.focusedProperty().addListener( indicatorSetting.getAccentBeatEvent(ball) ); // 공의 색상 설정
							else ball.focusedProperty().addListener( indicatorSetting.getNormalBeatEvent(ball) );
							ball.setOnMouseClicked( indicatorSetting.getChangeBeatEvent(ball) );
							
							rows[i].getChildren().add(ball);
						}
					}
				} else {
					FlowPane[] rows = root.getIndicatorRows();
					int cnt = newValue/2 - 1;
					for(int i=0;i<newValue;i++) {
						Circle ball = new Circle(30);
						ball.setId( Integer.toString(i) );
						if(root.isAccent(i)) ball.focusedProperty().addListener( indicatorSetting.getAccentBeatEvent(ball) ); // 공의 색상 설정
						else ball.focusedProperty().addListener( indicatorSetting.getNormalBeatEvent(ball) );
						ball.setOnMouseClicked( indicatorSetting.getChangeBeatEvent(ball) );
						
						rows[ ((cnt-i)>>31) & 1].getChildren().add(ball);
					}
				}
			} else {
				FlowPane[] rows = root.getIndicatorRows();
				for(int i=0;i<newValue;i++) {
					Circle ball = new Circle(30);
					ball.setId( Integer.toString(i) );
					if(root.isAccent(i)) ball.focusedProperty().addListener( indicatorSetting.getAccentBeatEvent(ball) ); // 공의 색상 설정
					else ball.focusedProperty().addListener( indicatorSetting.getNormalBeatEvent(ball) );
					ball.setOnMouseClicked( indicatorSetting.getChangeBeatEvent(ball) );
					
					rows[0].getChildren().add(ball);
				}
			}
			SoundPlayer.setBit(newValue);
			SoundPlayer.setIndicator( root.getBeatIndicator( root.getIndicatorRows() ) );
			if(root.isPlayed()) root.getBeatIndicator( root.getIndicatorRows() )[SoundPlayer.getBeat_sequence()].requestFocus();
		}
		
		private FlowPane createFlowPane() {
			FlowPane flow = new FlowPane();
			flow.setMaxSize(-1, -1);
			flow.setMinSize(-1, -1);
			flow.setPrefSize(-1, -1);
			flow.setAlignment(Pos.CENTER);
			flow.setHgap(5);
			return flow;
		}
		
		//Singleton Pattern
		private static select I = new select();
		public static select getInstance() {
			return I;
		}
		private select() {
		}
	}
}
