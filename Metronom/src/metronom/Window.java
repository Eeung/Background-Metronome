package metronom;

import javax.swing.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import metronom.component.Button;
import metronom.component.Slider;
import metronom.component.TextInput;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	
	Button play, stop;
	TextInput bpmInput, offsetInput;
	Slider bpmSlider, volumeSlider;
	SoundPlayer thread = new SoundPlayer();
	GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	private boolean isPlayed = false;
	public Window() {
		setTitle("매트로놈");
		setSize(640,480);
		
		play = new Button("재생", () -> {
			
			play.setEnabled(false);
			stop.setEnabled(true);
			
			bpmSetUp();
			offsetSetUp();
			
			thread.start();
			isPlayed = true;
		} );
		play.setBounds(5, 5, 100, 40);
		add(play);
		
		stop = new Button("일시정지", () -> {
			play.setEnabled(true);
			stop.setEnabled(false);
			thread.scheduleCancel();
			thread = new SoundPlayer();
			isPlayed = false;
		} );
		stop.setEnabled(false);
		stop.setBounds(5, 55, 100, 40);
		add(stop);
		
		bpmInput = new TextInput(() -> {
			System.out.println("bpm!");
			double bpm = bpmSetUp();
			bpmSlider.setValue(bpm);
		} );
		bpmInput.setBounds(155,5,100,30);
		bpmInput.setText("120");
		add(bpmInput);
		
		offsetInput = new TextInput(() -> {
			System.out.println("offset!");
			offsetSetUp();
		} );
		offsetInput.setBounds(155,40,100,30);
		offsetInput.setText("0");
		add(offsetInput);
	
		bpmSlider = new Slider(30,480,120);
		bpmSlider.setAction(() -> {
			int bpm = bpmSlider.getValue();
			if(bpmSlider.isMoved) {
				bpmInput.setText(Integer.toString(bpm));
				thread.setBpm(bpm);
			}
			bpmSlider.isMoved = true;
		} );
		bpmSlider.setBounds(5, 100,500,50);
		bpmSlider.setTickSpacing(30, 15);
		add(bpmSlider);
		
		volumeSlider = new Slider(0,100,100);
		volumeSlider.setAction(() -> {
			int volume = volumeSlider.getValue();
			thread.setVolume(volume);
		} );
		volumeSlider.setBounds(5, 160, 50, 200);
		volumeSlider.setOrientation(SwingConstants.VERTICAL);
		volumeSlider.setTickSpacing(25, 5);
		add(volumeSlider);
		
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			
			@Override 
			public void keyPressed(GlobalKeyEvent e) {
				//System.out.println(e);
				if(!isPlayed && e.isShiftPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_RETURN) {
					play.doClick();
				}
			}
			
			@Override 
			public void keyReleased(GlobalKeyEvent e) {
				//System.out.println(e);
				if(isPlayed && e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
					stop.doClick();
				}
			}
			
		});
		
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private double bpmSetUp() {
		bpmInput.checkNum("120", TextInput.DOUBLE);
		bpmInput.checkRange(1, Integer.MAX_VALUE);
		double bpm = Double.parseDouble(bpmInput.getText());
		thread.setBpm(bpm);
		return bpm;
	}
	private int offsetSetUp() {
		offsetInput.checkNum("0", TextInput.INTEGER);
		offsetInput.checkRange(-50000, Integer.MAX_VALUE);
		int offset = Integer.parseInt(offsetInput.getText());
		thread.setOffset(offset);
		return offset;
	}
}