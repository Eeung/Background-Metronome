package metronom.component;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Slider extends JSlider implements ChangeListener {
	private static final long serialVersionUID = 1L;

	private Action action = () -> {};
	public boolean isMoved = true;
	public Slider(int min, int max, int value) {
		setModel(new DefaultBoundedRangeModel(value, 0, min, max));
		addChangeListener(this);
	}
	public void setTickSpacing(int major, int minor) {
		setMajorTickSpacing(major);
		setMinorTickSpacing(minor);
		setPaintTicks(true);
		setPaintLabels(true);
	}
	public void setAction(Action func) {
		action = func;
	}
	
	public void setValue(double num) {
		isMoved = false;
		super.setValue((int) num);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		action.act();
	}
}
