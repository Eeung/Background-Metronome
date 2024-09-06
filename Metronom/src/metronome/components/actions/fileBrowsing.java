package metronome.components.actions;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import metronome.Controller;
import metronome.sound.MusicPlayer;

public class fileBrowsing {
	private static final Controller root = Controller.getInstance();
	private static Stage stage;
	private static Label musicFilePath = root.getMusicFilePath();
	
	public static browse getFileBrowsing(Stage s) {
		stage = s;
		return browse.getInstance();
	}
	
	private static class browse implements EventHandler<ActionEvent>{
		
		@Override
		public void handle(ActionEvent arg0) {			
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("음악 파일 선택");
	        
	        // 확장자 필터 설정
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav", "*.flac");
	        fileChooser.getExtensionFilters().add(extFilter);
	        
	        // 파일 탐색기를 열어 파일 선택
	        File selectedFile = fileChooser.showOpenDialog(stage);
	        
	        // 선택된 파일 처리
	        if (selectedFile != null) {
	        	String path = selectedFile.getAbsolutePath();
	        	MusicPlayer.getInstance().setFilePath(path);
	        	int ch = path.lastIndexOf('\\');
	        	musicFilePath.setText(path.substring(ch+1));
	        	
	        } else {
	            System.out.println("파일이 선택되지 않았습니다.");
	        }
	        
	        
		}
		
		static browse I = new browse();
		static browse getInstance() {
			return I;
		}
	}
}
