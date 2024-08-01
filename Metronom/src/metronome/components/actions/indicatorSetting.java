package metronome.components.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import metronome.Controller;
import metronome.sound.SoundPlayer;

public class indicatorSetting {
	private final static Controller root = Controller.getInstance();
	
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
			root.getBeatIndicator()[SoundPlayer.getBeat_sequence()].requestFocus();
		}
		
		//Singleton Pattern
		static blur I = new blur();
		public static blur getInstance() {
			return I;
		}
		private blur() {
		}
	}
}
