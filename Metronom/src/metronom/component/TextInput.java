package metronom.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class TextInput extends JTextField implements ActionListener {
	private static final long serialVersionUID = 1751069614495516875L;
	
	private Action action;
	public TextInput(Action func){
		addActionListener(this);
		action = func;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		action.act();
	}
	public void checkDouble() {
		String text = this.getText();
		if(!text.matches("(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*)(?:[Ee]([+-]?\\d+))?")) {
			setText("120");
			return;
		}
	}
}