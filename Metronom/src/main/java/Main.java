
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import metronome.Controller;
import metronome.Settings;

public class Main extends Application {
	public static void main(String[] args){
		Settings.getSettingsFromFile();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
		
		Parent root = loader.load(); 
		Controller controller = loader.getController();
		Scene scene = new Scene(root);
		primaryStage.setMinWidth(640);
		primaryStage.setMinHeight(550);
		
		controller.setStage(primaryStage);

		primaryStage.getIcons().add(new Image("file:src/main/resources/Icon/metronome_32px.png"));
		primaryStage.setTitle("메트로놈"); 
		primaryStage.setScene(scene);
		primaryStage.show(); // 창 띄우기
		
		// 설정: 창 닫기 이벤트 처리
		primaryStage.setOnCloseRequest(event -> {
			Settings.setSettingFile();
			Platform.exit();
			System.exit(0);
		} );
	}
}