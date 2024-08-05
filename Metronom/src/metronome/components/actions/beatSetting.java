package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class beatSetting  {
	private static final Controller root = Controller.getInstance();
	private static VBox indicatorCol = root.getIndicatorCol();
	private static FlowPane metronomeVisualPane = root.getMetronomeVisualPane();
	
	
	public static select getBeatSelectEvent() {
		return select.getInstance();
	}
	
	public static void adjustBallSizes(HBox[] rows) {
		select.adjustBallSizes(rows);
	}
	
	private static class select implements ChangeListener<Integer> {
		@Override
		public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
			
			indicatorCol.getChildren().clear();
			indicatorCol.getChildren().add(createHBox());
			if(newValue >9) {
				indicatorCol.getChildren().add(createHBox());
				if(newValue>19 && (newValue&3)==0) {
					indicatorCol.getChildren().add(createHBox());
					indicatorCol.getChildren().add(createHBox());
				}
			}
			HBox[] rows = root.getIndicatorRows();
	        int cnt = newValue > 19 && (newValue & 3) == 0 ? newValue / 4 : ((newValue > 9 ? (newValue + 1) / 2 - 1 : 0));

	        for (int i = 0; i < newValue; i++) {
	            Circle ball = new Circle(30);
	            ball.setId( Integer.toString(i) );
	            
	            if (root.isAccent(i)) ball.focusedProperty().addListener(indicatorSetting.getAccentBeatEvent(ball)); // 공의 색상 설정
	            else ball.focusedProperty().addListener(indicatorSetting.getNormalBeatEvent(ball));
	            ball.setOnMouseClicked(indicatorSetting.getChangeBeatEvent(ball));

	            if (newValue > 19 && (newValue & 3) == 0)
	            	rows[i / cnt].getChildren().add(ball);
	            else if (newValue > 9)
	            	rows[((cnt - i) >> 31) & 1].getChildren().add(ball);
	            else rows[0].getChildren().add(ball);
	        }
	        
	        rows = root.getIndicatorRows();
	        adjustBallSizes( rows );
			SoundPlayer.setIndicator( root.getBeatIndicator(rows) );
			SoundPlayer.setBit(newValue);
		}
		
		private HBox createHBox() {
			HBox flow = new HBox();
			flow.setMaxSize(-1, -1);
			flow.setMinSize(-1, -1);
			flow.setPrefSize(-1, -1);
			flow.setAlignment(Pos.CENTER);
			flow.setSpacing(5);
			return flow;
		}
		
		private static void adjustBallSizes(HBox[] rows) {
			Circle[] balls = root.getBeatIndicator(rows);
			double radius = balls[0].getRadius();
			int ballCnt = rows[0].getChildren().size();
			double dwid = (metronomeVisualPane.getWidth()-(2*radius+5)*ballCnt) / ballCnt;
			double dhei = (metronomeVisualPane.getHeight()-(2*radius+5)*rows.length) / rows.length;
	        
	    	double min = Math.min(dwid, dhei)/2;
	    	double newRadius = radius+min>30 ? 30 : radius+min;
	    	for(Circle ball : balls) {
	    		ball.setRadius(newRadius);
	    	}
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
