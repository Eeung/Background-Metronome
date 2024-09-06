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
import metronome.sound.MetronomeStrategy;

public class indicatorSetting {
	private final static Controller root = Controller.getInstance();
	
	private static VBox indicatorCol = root.getIndicatorCol();
	private static FlowPane metronomeVisualPane = root.getMetronomeVisualPane();
	private static MetronomeStrategy player = (MetronomeStrategy) root.getPlayer();
	
	private final static Color activated_orange = Color.web("#ff940c");
	private final static Color deactivated_orange = Color.web("#a3630c");
	private final static Color activated_yellow = Color.web("#ffdf22");
	private final static Color deactivated_yellow = Color.web("#a39122");
	
	/** Get the event to turn the lights on and off on the accent beat */
	public static accent getAccentBeatEvent(Circle c) {
		return new accent(c);
	}
	
	/** Get the event to turn the lights on and off on the normal beat */
	public static normal getNormalBeatEvent(Circle c) {
		return new normal(c);
	}
	
	/** Get the event to change the type of clicked beat */
	public static change getChangeBeatEvent(Circle c) {
		return new change(c);
	}
	
	/** Get the event that returns focus to beats When another component takes it. */
	public static blur getBlurEvent() {
		return blur.getInstance();
	}
	
	/**
	 * Rebuild beat indicator when you change note or time signature.
	 * 
	 * @param 
	 * count The beat count (note * time signature).
	 */
	public static void rebuildIndicator(int count) {
		/** Create rows according to beat count. */
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
		
		/** Create balls and put event in them. */
		HBox[] rows = root.getIndicatorRows();
        for (int i = 0; i < count; i++) {
            Circle ball = new Circle(30);
            ball.setId( Integer.toString(i) );
            
            if (root.isAccent(i)) ball.focusedProperty().addListener(indicatorSetting.getAccentBeatEvent(ball)); // 공의 색상 설정
            else ball.focusedProperty().addListener(indicatorSetting.getNormalBeatEvent(ball));
            ball.setOnMouseClicked(indicatorSetting.getChangeBeatEvent(ball));

            /** Put each ball in the right line. */
            if (mode == 1) rows[(( (count+1)/2-1-i) >> 31) & 1].getChildren().add(ball);
            else if (mode == 0) rows[0].getChildren().add(ball);
            else rows[i / (count/4)].getChildren().add(ball);
        }
        
        adjustBallSizes(rows);
        player.setIndicator( root.getBeatIndicator(rows) );
	}
	
	/** The event of turning on/off accent beat. */
	private static class accent implements ChangeListener<Boolean> {
		private Circle ball;
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) ball.setFill(activated_orange);
            else ball.setFill(deactivated_orange);
		}
		
		private accent(Circle c) {
			ball = c;
			ball.setFill(deactivated_orange);
		}
	}
	/** The event of turning on/off normal beat. */
	private static class normal implements ChangeListener<Boolean> {
		private Circle ball;
		
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue) ball.setFill(activated_yellow);
            else ball.setFill(deactivated_yellow);
		}
		
		private normal(Circle c) {
			ball = c;
			ball.setFill(deactivated_yellow);
		}
	}
	
	/** The event of changing beat type. */
	private static class change implements EventHandler<MouseEvent> {
		private Circle ball;
		private int idx;
		@Override
		public void handle(MouseEvent arg0) {
			
			if(root.isAccent(idx)) {
				root.setIsAccent(idx, false);
				ball.focusedProperty().addListener( getNormalBeatEvent(ball) );
				if (root.isPlayed() && player.getBeat_sequence() == idx) ball.setFill(activated_yellow);
				else ball.setFill(deactivated_yellow);
			} else {
				root.setIsAccent(idx, true);
				ball.focusedProperty().addListener( getAccentBeatEvent(ball) );
				if (root.isPlayed() && player.getBeat_sequence() == idx) ball.setFill(activated_orange);
				else ball.setFill(deactivated_orange);
			}
		}
		
		private change(Circle c) {
			ball = c;
			idx = Integer.parseInt(ball.getId());
		}
	}
	
	/** The event of returning the focus to beat indicator. */
	private static class blur implements ChangeListener<Boolean>{
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if(!(newValue && root.isPlayed())) return;
			int idx = player.getBeat_sequence();
			if(idx>-1) root.getBeatIndicator( root.getIndicatorRows() )[idx].requestFocus();
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
	
	public static void setPlayer(MetronomeStrategy p) {
		player = p;
	}
}
