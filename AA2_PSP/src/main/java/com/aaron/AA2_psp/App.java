package com.aaron.AA2_psp;

import com.aaron.AA2_psp.util.R;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        AppController controller = new AppController();
        loader.setLocation(R.getUI("panel.fxml"));
        loader.setController(controller);
        VBox vBox = loader.load();

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
