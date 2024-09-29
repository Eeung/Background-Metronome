package metronome.shortcut;

import java.util.HashMap;

public class KeyValue {
	private static HashMap<Integer, String> map = new HashMap<>();
	
	private static KeyValue I = new KeyValue();
	public static KeyValue getInstance() {
		return I;
	}
	
	public static HashMap<Integer, String> getKeyValuetoString() {
		return map;
	}
	private KeyValue() {
		map.put(8, "BackSpace");
		map.put(9, "Tab");
		map.put(13, "Enter");
		map.put(19, "Pause");
		map.put(20, "CAPS");
		map.put(21, "한/영");
		map.put(25, "한자");
		map.put(32, "Space");
		
		map.put(33, "PgUp");
		map.put(34, "PgDn");
		map.put(35, "End");
		map.put(36, "Home");
		map.put(37, "←");
		map.put(38, "↑");
		map.put(39, "→");
		map.put(40, "↓");
		map.put(44, "PrtSc");
		map.put(45, "Ins");
		map.put(46, "Del");
		
		map.put(48, "0");
		map.put(49, "1");
		map.put(50, "2");
		map.put(51, "3");
		map.put(52, "4");
		map.put(53, "5");
		map.put(54, "6");
		map.put(55, "7");
		map.put(56, "8");
		map.put(57, "9");
		
		map.put(65, "A");
		map.put(66, "B");
		map.put(67, "C");
		map.put(68, "D");
		map.put(69, "E");
		map.put(70, "F");
		map.put(71, "G");
		map.put(72, "H");
		map.put(73, "I");
		map.put(74, "J");
		map.put(75, "K");
		map.put(76, "L");
		map.put(77, "M");
		map.put(78, "N");
		map.put(79, "O");
		map.put(80, "P");
		map.put(81, "Q");
		map.put(82, "R");
		map.put(83, "S");
		map.put(84, "T");
		map.put(85, "U");
		map.put(86, "V");
		map.put(87, "W");
		map.put(88, "X");
		map.put(89, "Y");
		map.put(90, "Z");
		
		map.put(91, "LWin");
		map.put(92, "RWin");
		map.put(93, "Menu");
		map.put(96, "NUM 0");
		map.put(97, "NUM 1");
		map.put(98, "NUM 2");
		map.put(99, "NUM 3");
		map.put(100, "NUM 4");
		map.put(101, "NUM 5");
		map.put(102, "NUM 6");
		map.put(103, "NUM 7");
		map.put(104, "NUM 8");
		map.put(105, "NUM 9");
		map.put(106, "NUM *");
		map.put(107, "NUM +");
		map.put(109, "NUM -");
		map.put(110, "NUM .");
		map.put(111, "NUM /");
		
		map.put(112, "F1");
		map.put(113, "F2");
		map.put(114, "F3");
		map.put(115, "F4");
		map.put(116, "F5");
		map.put(117, "F6");
		map.put(118, "F7");
		map.put(119, "F8");
		map.put(120, "F9");
		map.put(121, "F10");
		map.put(122, "F11");
		map.put(123, "F12");
		map.put(124, "F13");
		map.put(125, "F14");
		map.put(126, "F15");
		map.put(127, "F16");
		map.put(128, "F17");
		map.put(129, "F18");
		map.put(130, "F19");
		map.put(131, "F20");
		map.put(132, "F21");
		map.put(133, "F22");
		map.put(134, "F23");
		map.put(135, "F24");
		
		map.put(144, "NumLk");
		map.put(145, "ScrLk");
		
		map.put(173, "VolMute");
		map.put(174, "VolDown");
		map.put(175, "VolUp");
		map.put(176, "MdiNext");
		map.put(177, "MdiPrev");
		map.put(178, "MdiStop");
		map.put(179, "MdiPlay");
		
		map.put(186, ";");
		map.put(187, "=");
		map.put(188, ",");
		map.put(189, "-");
		map.put(190, ".");
		map.put(191, "/");
		map.put(192, "~");
		
		map.put(219, "[");
		map.put(220, "\\");
		map.put(221, "]");
		map.put(222, "'");
	}
}
