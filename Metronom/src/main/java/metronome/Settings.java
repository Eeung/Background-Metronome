package metronome;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Settings {
	private static Properties prop = new Properties();
	
	//세팅 기본값
	
	private static int bpm = 12000;
	
	private static int offset = 0;
	
	private static Integer note = 8;
	
	private static long accentBeat = 
			0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000011L;
	
	private static int volume = 50;
	
	private static int time = 4;
	
	private static int beat = 4;
	
	// djmax , ez2on
	private static String game = "djmax";
	
	private static List<Integer> quickOffsetSetting = List.of(162, 220);
	private static String quickOffsetSettingString = "LCtrl + \\";
	
	//----------------------------------------------------------------------------
	
	/** 세팅 파일에서 저장된 값을 불러오기 */
	public static void getSettingsFromFile() {
		try (FileInputStream input = new FileInputStream("src/main/resources/Setting.properties")) {
		    prop.load(input);
		    bpm = Integer.parseInt(prop.getProperty("bpm"));
		    offset = Integer.parseInt(prop.getProperty("offset"));
		    note = Integer.parseInt( prop.getProperty("note") );
		    accentBeat = Long.parseLong( prop.getProperty("accentBeat") );
		    volume = Integer.parseInt( prop.getProperty("volume") );
		    time = Integer.parseInt( prop.getProperty("time") );
		    beat = Integer.parseInt( prop.getProperty("beat") );
		    game = prop.getProperty("game");
		    
		    quickOffsetSetting = asList( prop.getProperty("quickOffsetSetting") );
		    quickOffsetSettingString = prop.getProperty("quickOffsetSettingString");
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	}
	
	/** 프로그램의 지금 설정을 세팅 파일에 저장하기 */
	public static void setSettingFile() {
		try (FileOutputStream output = new FileOutputStream("src/main/resources/Setting.properties")) {
		    prop.setProperty("bpm", Integer.toString(bpm) );
		    prop.setProperty("offset", Integer.toString(offset));
		    prop.setProperty("note", Integer.toString(note) );
		    prop.setProperty("accentBeat", Long.toString(accentBeat));
		    prop.setProperty("volume", Integer.toString(volume) );
		    prop.setProperty("time", Integer.toString(time));
		    prop.setProperty("beat", Integer.toString(beat) );
		    prop.setProperty("game", game);
		    
		    prop.setProperty("quickOffsetSetting", listToString(quickOffsetSetting) );
		    prop.setProperty("quickOffsetSettingString", quickOffsetSettingString);
		    prop.store(output, null);
		} catch (IOException io) {
		    io.printStackTrace();
		}
	}
	
	/** 문자열과 리스트를 변환하는 메소드들 */
	private static List<Integer> asList(String got){
		String[] keys = got.split(",");
		LinkedList<Integer> list = new LinkedList<>();
		for(String key : keys) {
			list.add( Integer.parseInt(key) );
		}
		return list;
	}
	
	private static String listToString(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
		for(int val : list) {
			sb.append(val).append(",");
		}
		sb.setLength(sb.length()-1);
		return sb.toString();
	}
	
	//----------------------------------------------------------------------------
	
	public static int getBpm() {
		return bpm;
	}
	public static void setBpm(int b) {
		bpm = b;
	}
	public static int getOffset() {
		return offset;
	}
	public static void setOffset(int o) {
		offset = o;
	}
	public static Integer getNote() {
		return note;
	}
	public static void setNote(Integer n) {
		note = n;
	}
	public static long getAccentBeat() {
		return accentBeat;
	}
	public static void setAccentBeat(int idx, boolean newValue) {
		if(newValue) accentBeat |= 1L << idx;
		else accentBeat &= ~(1L << idx) ;
	}
	public static int getVolume() {
		return volume;
	}
	public static void setVolume(int v) {
		volume = v;
	}
	public static int getTime() {
		return time;
	}
	public static void setTime(int t) {
		time = t;
	}
	public static int getBeat() {
		return beat;
	}
	public static void setBeat(int b) {
		beat = b;
	}
	public static String getGame() {
		return game;
	}
	public static void setGame(String g) {
		game = g;
	}
	public static List<Integer> getQuickOffsetSetting() {
		return quickOffsetSetting;
	}
	public static void setQuickOffsetSetting(List<Integer> qO) {
		quickOffsetSetting = qO;
	}
	public static String getQuickOffsetSettingString() {
		return quickOffsetSettingString;
	}
	public static void setQuickOffsetSettingString(StringBuilder qOS) {
		quickOffsetSettingString = qOS.toString();
	}
}
