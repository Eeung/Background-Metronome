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
import metronome.Settings;
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
	
	/**
	 * 박자표나 비트(note)가 바뀔 때, 인디케이터를 재생성한다.
	 * 
	 * @param 
	 * 비트 수 (note * 박자표).
	 */
	public static void rebuildIndicator(int count) {
		/** 비트 수에 따른 줄 생성 */
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
		
		/** 각각의 원을 생성하고 이벤트 삽입 */
		HBox[] rows = root.getIndicatorRows();
        for (int i = 0; i < count; i++) {
            Circle ball = new Circle(30);
            ball.setId( Integer.toString(i) );
            
            if (root.isAccent(i)) ball.focusedProperty().addListener(indicatorSetting.getAccentBeatEvent(ball)); // 공의 색상 설정
            else ball.focusedProperty().addListener(indicatorSetting.getNormalBeatEvent(ball));
            ball.setOnMouseClicked(indicatorSetting.getChangeBeatEvent(ball));

            /** 올바른 줄에 원을 넣기 */
            if (mode == 1) rows[(( (count+1)/2-1-i) >> 31) & 1].getChildren().add(ball);
            else if (mode == 0) rows[0].getChildren().add(ball);
            else rows[i / (count/4)].getChildren().add(ball);
        }
        
        adjustBallSizes(rows);
        player.setIndicator( root.getBeatIndicator(rows) );
	}
	
	/** 악센트 비트가 켜고 꺼지는 이벤트 클래스 */
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
	/** 일반 비트가 켜고 꺼지는 이벤트 클래스 */
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
	
	/** 각 비트의 재생 타입을 바꾸는 이벤트클래스 */
	private static class change implements EventHandler<MouseEvent> {
		private Circle ball;
		private int idx;
		@Override
		public void handle(MouseEvent arg0) {
			
			if(root.isAccent(idx)) {
				ball.focusedProperty().addListener( getNormalBeatEvent(ball) );
				if (root.isPlayed() && player.getBeat_sequence() == idx) ball.setFill(activated_yellow);
				else ball.setFill(deactivated_yellow);
				root.setAccent(idx, false);
				
				/** 세팅 클래스와 동기화 */
				Settings.setAccentBeat(idx, false);
				
			} else {
				ball.focusedProperty().addListener( getAccentBeatEvent(ball) );
				if (root.isPlayed() && player.getBeat_sequence() == idx) ball.setFill(activated_orange);
				else ball.setFill(deactivated_orange);
				root.setAccent(idx, true);
				
				/** 세팅 클래스와 동기화 */
				Settings.setAccentBeat(idx, true);
				
			}
		}
		
		private change(Circle c) {
			ball = c;
			idx = Integer.parseInt(ball.getId());
		}
	}
	
	/** 다른 컴포넌트가 포커스를 가져갈 때, 비트 인디케이터로 포커스를 돌려주는 이벤트 클래스 */
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
