
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import metronome.Controller;

public class Main extends Application {
	public static void main(String[] args){
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

		primaryStage.setTitle("메트로놈"); 
		primaryStage.setScene(scene);
		primaryStage.show(); // 창 띄우기
		
		// 설정: 창 닫기 이벤트 처리
		primaryStage.setOnCloseRequest(event -> System.exit(0) );
	}
}