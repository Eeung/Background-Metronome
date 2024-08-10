package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class indicatorSetting {
	private final static Controller root = Controller.getInstance();
	private static VBox indicatorCol = root.getIndicatorCol();
	private static FlowPane metronomeVisualPane = root.getMetronomeVisualPane();
	
	public static accent getAccentBeatEvent(Circle c) {
		return new accent(c);
	}
	
	public static normal getNormalBeatEvent(Circle c) {
		return new normal(c);
	}
	
	public static change getChangeBeatEvent(Circle c) {
		return new change(c);
	}
	
	public static blur getBlurEvent() {
		return blur.getInstance();
	}
	
	public static void rebuildIndicator() {
		int count = root.getBeatCount();
		
		int mode = 0;
		indicatorCol.getChildren().clear();
		indicatorCol.getChildren().add(createHBox());
		if(count >9) {
			mode = 1;
			indicatorCol.getChildren().add(createHBox());
			if(count>19 && (count&3)==0) {
				mode = 2;
				indicatorCol.getChildren().add(createHBox());
				indicatorCol.getChildren().add(createHBox());
			}
		}
		
		HBox[] rows = root.getIndicatorRows();
        for (int i = 0; i < count; i++) {
            Circle ball = new Circle(30);
            ball.setId( Integer.toString(i) );
            
            if (root.isAccent(i)) ball.focusedProperty().addListener(indicatorSetting.getAccentBeatEvent(ball)); // 공의 색상 설정
            else ball.focusedProperty().addListener(indicatorSetting.getNormalBeatEvent(ball));
            ball.setOnMouseClicked(indicatorSetting.getChangeBeatEvent(ball));

            if (mode == 1)
            	rows[(( (count+1)/2-1-i) >> 31) & 1].getChildren().add(ball);
            else if (mode == 0)
            	rows[0].getChildren().add(ball);
            else rows[i / (count/4)].getChildren().add(ball);
        }
        
        adjustBallSizes(rows);
	}
	//주황색 #a3630c -> #ff940c
	//노란색 #a39122 -> #ffdf22
	private static class accent implements ChangeListener<Boolean> {
		private Circle ball;
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) ball.setFill(Color.web("#ff940c"));
            else ball.setFill(Color.web("#a3630c"));
		}
		
		private accent(Circle c) {
			ball = c;
			ball.setFill(Color.web("#a3630c"));
		}
	}
	
	private static class normal implements ChangeListener<Boolean> {
		private Circle ball;
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) ball.setFill(Color.web("#ffdf22"));
            else ball.setFill(Color.web("#a39122"));
		}
		
		private normal(Circle c) {
			ball = c;
			ball.setFill(Color.web("#a39122"));
		}
	}
	
	private static class change implements EventHandler<MouseEvent> {
		private Circle ball;
		private int idx;
		@Override
		public void handle(MouseEvent arg0) {
			if(root.isAccent(idx)) {
				root.setIsAccent(idx, false);
				ball.focusedProperty().addListener( getNormalBeatEvent(ball) );
				if (root.isPlayed() && SoundPlayer.getBeat_sequence() == idx) ball.setFill(Color.web("#ffdf22"));
				else ball.setFill(Color.web("#a39122"));
			} else {
				root.setIsAccent(idx, true);
				ball.focusedProperty().addListener( getAccentBeatEvent(ball) );
				if (root.isPlayed() && SoundPlayer.getBeat_sequence() == idx) ball.setFill(Color.web("#ff940c"));
				else ball.setFill(Color.web("#a3630c"));
			}
		}
		
		private change(Circle c) {
			ball = c;
			idx = Integer.parseInt(ball.getId());
		}
	}
	
	private static class blur implements ChangeListener<Boolean>{
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if(!(newValue && root.isPlayed())) return;
			root.getBeatIndicator( root.getIndicatorRows() )[SoundPlayer.getBeat_sequence()].requestFocus();
		}
		
		//Singleton Pattern
		static blur I = new blur();
		public static blur getInstance() {
			return I;
		}
		private blur() {
		}
	}
	
	public static void adjustBallSizes(HBox[] rows) {
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
	
	private static HBox createHBox() {
		HBox flow = new HBox();
		flow.setMaxSize(-1, -1);
		flow.setMinSize(-1, -1);
		flow.setPrefSize(-1, -1);
		flow.setAlignment(Pos.CENTER);
		flow.setSpacing(5);
		return flow;
	}
}
