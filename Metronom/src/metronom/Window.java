package metronom;

import javax.swing.*;

import metronom.component.Button;
import metronom.component.Slider;
import metronom.component.TextInput;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	
	Button play, stop;
	TextInput bpmInput;
	Slider bpmSlider;
	SoundPlayer thread = new SoundPlayer();
	public Window() {
		
		setTitle("매트로놈");
		setSize(640,480);
		
		play = new Button("재생", () -> {
			play.setEnabled(false);
			stop.setEnabled(true);
			
			bpmInput.checkDouble();
			
			thread.setBpm(Double.parseDouble(bpmInput.getText()));
			thread.start();
		} );
		play.setBounds(5, 5, 100, 40);
		add(play);
		
		stop = new Button("일시정지", () -> {
			play.setEnabled(true);
			stop.setEnabled(false);
			thread.interrupt();
			thread = new SoundPlayer();
		} );
		stop.setEnabled(false);
		stop.setBounds(5, 55, 100, 40);
		add(stop);
		
		bpmInput = new TextInput(() -> {
			bpmInput.checkDouble();
			double bpm = Double.parseDouble(bpmInput.getText());
			thread.setBpm(bpm);
			bpmSlider.setValue(bpm);
		} );
		bpmInput.setBounds(155,5,100,40);
		bpmInput.setText("120");
		add(bpmInput);
	
		bpmSlider = new Slider(0,480,120);
		bpmSlider.setBounds(5, 100,500,50);
		bpmSlider.setTickSpacing(60, 30);
		bpmSlider.setAction(() -> {
			int bpm = bpmSlider.getValue();
			if(bpmSlider.isMoved) {
				bpmInput.setText(Integer.toString(bpm));
				thread.setBpm(bpm);
			}
			bpmSlider.isMoved = true;
		} );
		add(bpmSlider);
		
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}