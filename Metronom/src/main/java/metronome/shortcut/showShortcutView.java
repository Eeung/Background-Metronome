package metronome.shortcut;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class showShortcutView {	
	public showShortcutView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/keySettingView.fxml"));
			
			Parent root = loader.load();
			
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			
			keySettingController controller = loader.getController();
			controller.setStage(stage);
			
			stage.setTitle("단축키 설정"); 
			stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
