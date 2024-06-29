package metronom.component;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

public class Radios extends ButtonGroup {
	private static final long serialVersionUID = 1L;
	
	private JRadioButton[] button;
	
	public Radios(String... bts) {
		button = new JRadioButton[bts.length];
		for(int i=0; i<bts.length;i++) {
			String name = bts[i];
			JRadioButton bt = new JRadioButton(name);
			button[i] = bt;
			add(bt);
		}
		button[0].setSelected(true);
	}
	
	public JRadioButton[] getRButtons() {
		return button;
	}
	
	public JRadioButton getSelected() {
		for(JRadioButton bt : button) 
			if(bt.isSelected()) 
				return bt;
		return null;
	}
}
