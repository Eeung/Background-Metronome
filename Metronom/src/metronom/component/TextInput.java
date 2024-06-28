package metronom.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class TextInput extends JTextField implements ActionListener {
	private static final long serialVersionUID = 1751069614495516875L;
	
	private Action action;
	public static final String DOUBLE = "(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*)(?:[Ee]([+-]?\\d+))?";
	public static final String INTEGER = "^[+|-]?\\d+";
	public TextInput(Action func){
		addActionListener(this);
		action = func;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
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
}