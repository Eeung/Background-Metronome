package metronom;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.*;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import metronom.component.Button;
import metronom.component.Radios;
import metronom.component.Slider;
import metronom.component.Text;
import metronom.component.TextInput;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;
	
	Button play, stop;
	Button offsetUp, offsetDown, offsetMoreUp, offsetMoreDown;
	TextInput bpmInput;
	Text offset;
	Radios radio;
	Slider bpmSlider, volumeSlider;
	GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(false);
	
	SoundPlayer player = new SoundPlayer(4);
	
	private boolean isPlayed = false;
	private int offsetScale = 1;
	private long startMilisec = 0;
	public Window() {
		setTitle("매트로놈");
		setSize(640,480);
		
		play = new Button("재생", () -> {
			play.setEnabled(false);
			stop.setEnabled(true);
			
			int bit = Integer.parseInt(radio.getSelected().getText());
			player.setBit(bit);
			
			bpmSetUp();
			offsetSetUp();
			
			player.start();
			isPlayed = true;
			startMilisec = System.currentTimeMillis();
		} );
		play.setBounds(5, 5, 100, 40);
		add(play);
		
		stop = new Button("일시정지", () -> {
			play.setEnabled(true);
			stop.setEnabled(false);
			player.scheduleCancel();
			player = new SoundPlayer(4);
			isPlayed = false;
		} );
		stop.setEnabled(false);
		stop.setBounds(5, 55, 100, 40);
		add(stop);
		
		bpmInput = new TextInput(() -> {
			double bpm = bpmSetUp();
			bpmSlider.setValue(bpm);
		} );
		bpmInput.setBounds(155,5,100,30);
		bpmInput.setText("120");
		add(bpmInput);
		
		offsetMoreDown = new Button("◀◀", () -> {
			offset.setText( Integer.toString(OffsetActions.offsetDown(Integer.parseInt(offset.getText()) , 10 * offsetScale)));
			offsetSetUp();
		});
		offsetMoreDown.setBounds(new Rectangle(new Point(155,40), Button.offsetButton));
		add(offsetMoreDown);
		
		offsetDown = new Button("◀", () -> {
			offset.setText( Integer.toString(OffsetActions.offsetDown(Integer.parseInt(offset.getText()) , 1 * offsetScale)));
			offsetSetUp();
		});
		offsetDown.setBounds(new Rectangle(new Point(195,40), Button.offsetButton));
		add(offsetDown);
		
		offset = new Text("0");
		offset.setBounds(235,40,60,30);
		add(offset);
		
		offsetUp = new Button("▶", () -> {
			offset.setText( Integer.toString(OffsetActions.offsetUp(Integer.parseInt(offset.getText()) , 1 * offsetScale)));
			offsetSetUp();
		});
		offsetUp.setBounds(new Rectangle(new Point(295,40), Button.offsetButton));
		add(offsetUp);
		
		offsetMoreUp = new Button("▶▶", () -> {
			offset.setText( Integer.toString(OffsetActions.offsetUp(Integer.parseInt(offset.getText()) , 10 * offsetScale)));
			offsetSetUp();
		});
		offsetMoreUp.setBounds(new Rectangle(new Point(335,40), Button.offsetButton));
		add(offsetMoreUp);
		
		bpmSlider = new Slider(30,480,120);
		bpmSlider.setAction(() -> {
			int bpm = bpmSlider.getValue();
			if(bpmSlider.isMoved) {
				bpmInput.setText(Integer.toString(bpm));
				player.setBpm(bpm);
			}
			bpmSlider.isMoved = true;
		} );
		bpmSlider.setBounds(5, 100,500,50);
		bpmSlider.setTickSpacing(30, 15);
		add(bpmSlider);
		
		volumeSlider = new Slider(0,100,100);
		volumeSlider.setAction(() -> {
			int volume = volumeSlider.getValue();
			player.setVolume(volume);
		} );
		volumeSlider.setBounds(5, 160, 50, 200);
		volumeSlider.setOrientation(SwingConstants.VERTICAL);
		volumeSlider.setTickSpacing(25, 5);
		add(volumeSlider);
		
		radio = new Radios("4","6","8","12","16","24");
		JRadioButton[] buttons = radio.getRButtons();
		for(int i=0;i<buttons.length;i++) {
			JRadioButton bt = buttons[i];
			bt.setBounds(75+(i*55), 160, 50, 30);
			add(bt);
		}
		
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			
			@Override 
			public void keyPressed(GlobalKeyEvent e) {
				//System.out.println(e);
				if(isFocused() && e.isShiftPressed())
					offsetScale = 100;
				
				if(isPlayed) {
					if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_OEM_5) {
						long cur = System.currentTimeMillis();
						offset.setText(Long.toString(cur-startMilisec));
						offsetSetUp();
					}
					if(e.getVirtualKeyCode() == GlobalKeyEvent.VK_F5) {
						stop.doClick();
						play.doClick();
					}
				} else {
					if(e.isControlPressed() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_RETURN)
						play.doClick();
				}
				
			}
			
			@Override 
			public void keyReleased(GlobalKeyEvent e) {
				//System.out.println(e);
				if(isFocused() && e.getVirtualKeyCode() == GlobalKeyEvent.VK_LSHIFT)
					offsetScale = 1;
				
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
		player.setBpm(bpm);
		return bpm;
	}
	private int offsetSetUp() {
		int off = Integer.parseInt(offset.getText());
		player.setOffset(off);
		return off;
	}
}