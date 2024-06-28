package metronom.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class TextInput extends JTextField implements KeyListener {
	private static final long serialVersionUID = 1751069614495516875L;
	
	private Action action;
	public static final String DOUBLE = "(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*)(?:[Ee]([+-]?\\d+))?";
	public static final String INTEGER = "^[+|-]?\\d+";
	
	public TextInput(Action func){
		addKeyListener(this);
		action = func;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		action.act();
	}
	
	public void checkNum(String change, String Type) {
		String text = this.getText();
		if(!text.matches(Type)) {
			setText(change);
			return;
		}
	}
	public void checkRange(int min, int max){
		double input = Double.parseDouble(getText());
		if(input<min) setText(Integer.toString(min));
		else if (input>max) setText(Integer.toString(max));
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
}