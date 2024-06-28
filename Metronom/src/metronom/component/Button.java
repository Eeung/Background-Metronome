package metronom.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class Button extends JButton implements ActionListener {
private static final long serialVersionUID = 1L;
	
	private Action action;
	public static Dimension offsetButton = new Dimension(35,30);
	public Button(String inner_text, Action func) {
		action = func;
		setText(inner_text);
		addActionListener(this);
		setBorder( BorderFactory.createLineBorder(new Color(0,0,0),1) );
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		action.act();
	}
}