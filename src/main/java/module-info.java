module form.login.v5.loginformversion5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;


    opens form.login.v5.loginformversion5 to javafx.fxml;
    exports form.login.v5.loginformversion5;
}