package src;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class PropertyDemo extends Application {

    public void start(Stage stage) throws Exception
    {
        Button b = new Button("_Click me" );
        b.setTextFill(Color.BLUE);
        b.setMnemonicParsing(true);
        Tooltip tp = new Tooltip("Click this button to save data");
        b.setTooltip(tp);
        //b.setStyle("-fx-border-color:yellow;-fx-background-color:green");
        Alert a = new Alert(AlertType.INFORMATION,"BUTTON IS CLICKED");
        Button b2= new Button("Button 2");

        b.setOnAction(e->a.show());


        FlowPane fp = new FlowPane(b);

        fp.getChildren().add(b2);
        b2.setTranslateX(200);
        Scene sc = new Scene(fp,400,400);
        stage.setScene(sc);
        stage.show();

    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
