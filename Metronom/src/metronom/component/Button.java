package metronom.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Button extends JButton implements ActionListener {
private static final long serialVersionUID = 1L;
	
	private Action action;
	public Button(String inner_text, Action func) {
		action = func;
		setText(inner_text);
		addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		action.act();
	}
}
