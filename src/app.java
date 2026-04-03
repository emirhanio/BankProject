package src;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.*;


public class app extends Application {
    public void start(Stage stage)
    {
        Button b = new Button("Click me");
        FlowPane fp = new FlowPane();
        fp.getChildren().add(b);
        Scene sc = new Scene(fp);

        stage.setScene(sc);
        stage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
