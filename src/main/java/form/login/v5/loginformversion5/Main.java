package form.login.v5.loginformversion5;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    // Scene's width and height
    private static final DoubleProperty width = new SimpleDoubleProperty(), height = new SimpleDoubleProperty();

    public static DoubleProperty widthProperty() {
        return width;
    }

    public static DoubleProperty heightProperty() {
        return height;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("LoginForm.fxml"))));

        width.bind(scene.widthProperty());
        height.bind(scene.heightProperty());

        stage.setScene(scene);
        stage.setTitle("Login Form - Version 5");
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
