package metronome;

public class OffsetActions {
	static int Min = 0;
	static int Max = Integer.MAX_VALUE;
	
	static void setRange(int max, int min) {
		Min = min;
		Max = max;
	}
	
	static int offsetUp(int offset, int scale) {
		offset = offset >= (Max-scale) ? Max : offset + scale;
		return offset;
	}
	
	static int offsetDown(int offset, int scale) {
		offset = offset <= (Min+scale) ? Min : offset - scale;
		return offset;
	}
}
