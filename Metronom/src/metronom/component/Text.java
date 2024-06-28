package metronom.component;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Text extends JLabel {
	private static final long serialVersionUID = 1L;

	public Text() {}
	public Text(String str) {
		super(str);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}
}