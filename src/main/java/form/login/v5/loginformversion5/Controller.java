package form.login.v5.loginformversion5;

import static form.login.v5.loginformversion5.Controller.MessageLocation.*;
import static form.login.v5.loginformversion5.Controller.MessageType.*;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private VBox loginPane, registerPane, editPane, userInfoVBox;

    // Login
    @FXML
    private TextField lIdField;
    @FXML
    private PasswordField lPasswordField;
    @FXML
    private Label lStateLabel;

    // Register
    @FXML
    private TextField rIdField, rNameField;
    @FXML
    private PasswordField rPasswordField, rcPasswordField;
    @FXML
    private Label rStateLabel;

    // Main
    @FXML
    private Label idLabel, nameLabel;

    // Edit
    @FXML
    private TextField eIdField, eNameField;
    @FXML
    private PasswordField ePasswordField, ecPasswordField;
    @FXML
    private Label eStateLabel;

    private boolean isUniqueId = false, checked = false;

    private ArrayList<Account> accounts;
    private Account currentAccount;

    // write message to state label
    private void stateMessage(String message, MessageLocation location, MessageType type) {
        Label messageLabel = null;

        switch (location) {
            case LOGIN:
                messageLabel = lStateLabel;
                break;
            case REGISTER:
                messageLabel = rStateLabel;
                break;
            case EDIT:
                messageLabel = eStateLabel;
                break;
        }

        messageLabel.setText(message);

        switch (type) {
            case ERROR:
                messageLabel.setStyle("-fx-text-fill: red;");
                break;
            case SUCCESS:
                messageLabel.setStyle("-fx-text-fill: green;");
                break;
        }
    }

    enum MessageType {
        ERROR, SUCCESS
    }

    enum MessageLocation {
        LOGIN, REGISTER, EDIT
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rIdField.setOnKeyPressed(e -> {
            isUniqueId = false;
            checked = false;
        });
        eIdField.setOnKeyPressed(e -> {
            isUniqueId = false;
            checked = false;
        });

        // bind width, height property to Scene's width, height property
        mainPane.prefWidthProperty().bind(Main.widthProperty());
        mainPane.prefHeightProperty().bind(Main.heightProperty());
        loginPane.prefWidthProperty().bind(Main.widthProperty());
        loginPane.prefHeightProperty().bind(Main.heightProperty());
        registerPane.prefWidthProperty().bind(Main.widthProperty());
        registerPane.prefHeightProperty().bind(Main.heightProperty());
        editPane.prefWidthProperty().bind(Main.widthProperty());
        editPane.prefHeightProperty().bind(Main.heightProperty());
        userInfoVBox.prefHeightProperty().bind(Main.heightProperty());

        accounts = new ArrayList<>();

        loadAccounts();
    }

    // Load accounts from files
    private void loadAccounts() {
        try {
            Properties ap = System.getProperties();

            for (File file : Objects.requireNonNull(Account.ACCOUNTS_FOLDER.listFiles())) {
                InputStream is = new FileInputStream(file);
                ap.load(is);

                if (ap.getProperty("id") != null) {
                    accounts.add(new Account(ap.getProperty("id"), ap.getProperty("name"), ap.getProperty("password")));
                }

                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void login() {
        String id = lIdField.getText(), password = lPasswordField.getText();

        if (id.equals("")) {
            stateMessage("Please enter ID.", LOGIN, ERROR);
            lIdField.requestFocus();
            return;
        } else if (password.equals("")) {
            stateMessage("Please enter password.", LOGIN, ERROR);
            lPasswordField.requestFocus();
            return;
        }

        for (Account account : accounts) {
            if (id.equals(account.getId())) {
                if (password.equals(HexString.toNormalString(account.getPassword()))) {
                    showMain();
                    clearFields();

                    currentAccount = account;

                    idLabel.setText(id);
                    nameLabel.setText(account.getName());

                    lStateLabel.setText("");
                } else {
                    stateMessage("Passwords do not match.", LOGIN, ERROR);
                    lPasswordField.requestFocus();
                }

                return;
            }
        }

        stateMessage("ID '" + id + "' doesn't exists.", LOGIN, ERROR);
        lIdField.requestFocus();
    }

    private void clearFields() {
        lIdField.clear();
        lPasswordField.clear();
        rIdField.clear();
        rPasswordField.clear();
        rcPasswordField.clear();
        rNameField.clear();
        eIdField.clear();
        ePasswordField.clear();
        ecPasswordField.clear();
        eNameField.clear();
    }

    @FXML
    protected void register() {
        String id = rIdField.getText(), name = rNameField.getText(), password = rPasswordField.getText(), cPassword =  rcPasswordField.getText();

        if (id.equals("")) {
            stateMessage("Please enter ID.", REGISTER, ERROR);
            rIdField.requestFocus();
            return;
        } else if (password.equals("")) {
            stateMessage("Please enter password.", REGISTER, ERROR);
            rPasswordField.requestFocus();
            return;
        } else if (cPassword.equals("")) {
            stateMessage("Please enter password (Confirm Password).", REGISTER, ERROR);
            rcPasswordField.requestFocus();
            return;
        } else if (name.equals("")) {
            stateMessage("Please enter password (Confirm Password).", REGISTER, ERROR);
        }

        if (!isUniqueId) {
            if (!checked) {
                stateMessage("Please do a duplicate ID check.", REGISTER, ERROR);
                return;
            }

            stateMessage("ID '" + id + "' is already in use.", REGISTER, ERROR);
            return;
        }

        if (!password.equals(cPassword)) {
            stateMessage("Passwords do not match.", REGISTER, ERROR);
            return;
        }

        Account newAccount = new Account(id, name, HexString.toHexString(password));
        accounts.add(newAccount);
        newAccount.writeToFile();

        rStateLabel.setText("");

        showLoginPane();
        clearFields();
    }

    @FXML
    protected void logout() {
        currentAccount = null;
        loginPane.setTranslateY(Main.heightProperty().get());
        loginPane.setVisible(true);

        Timeline t = new Timeline();
        t.getKeyFrames().clear();
        t.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), new KeyValue(loginPane.translateYProperty(), 0, Interpolator.SPLINE(0, 0, 0, 1))));
        t.play();

        clearFields();
    }

    // ID Duplicate Check
    @FXML
    protected void rCheckId() {
        String id = rIdField.getText();

        if (id.equals("")) {
            stateMessage("Please enter ID.", REGISTER, ERROR);
            return;
        }

        for (Account account : accounts) {
            if (id.equals(account.getId())) {
                stateMessage("ID '" + id + "' already exists.", REGISTER, ERROR);
                checked = true;
                return;
            }
        }

        isUniqueId = true;
        checked = true;
        stateMessage("You can use this ID.", REGISTER, SUCCESS);
    }

    @FXML
    protected void eCheckId() {
        String id = eIdField.getText();

        if (id.equals("")) {
            stateMessage("Please enter ID.", EDIT, ERROR);
            return;
        }

        for (Account account : accounts) {
            if (account == currentAccount) {
                continue;
            }

            if (id.equals(account.getId())) {
                stateMessage("ID '" + id + "' already exists.", EDIT, ERROR);
                checked = true;
                return;
            }
        }

        isUniqueId = true;
        checked = true;
        stateMessage("You can use this ID.", EDIT, SUCCESS);
    }

    @FXML
    protected void edit() {
        String newId = eIdField.getText(), newPassword = ePasswordField.getText(), cPassword = ecPasswordField.getText(), newName = eNameField.getText();

        if (newId.equals("")) {
            stateMessage("Please enter ID.", EDIT, ERROR);
            eIdField.requestFocus();
            return;
        } else if (newPassword.equals("")) {
            stateMessage("Please enter password.", EDIT, ERROR);
            ePasswordField.requestFocus();
            return;
        } else if (cPassword.equals("")) {
            stateMessage("Please enter password (Confirm Password).", EDIT, ERROR);
            ecPasswordField.requestFocus();
            return;
        }

        if (!isUniqueId) {
            if (!checked) {
                stateMessage("Please do a duplicate ID check.", EDIT, ERROR);
                return;
            }

            stateMessage("ID '" + newId + "' is already in use.", EDIT, ERROR);
            return;
        }

        if (!newPassword.equals(cPassword)) {
            stateMessage("Passwords do not match.", EDIT, ERROR);
            return;
        }

        delete();

        currentAccount = new Account(newId, newName, HexString.toHexString(newPassword));
        accounts.add(currentAccount);
        currentAccount.writeToFile();

        idLabel.setText(newId);
        nameLabel.setText(newName);

        showMainE();
        clearFields();
    }

    @FXML
    protected void showEditPane() {
        checked = true;
        isUniqueId = true;

        editPane.setVisible(true);
        editPane.setTranslateY(-Main.heightProperty().get());

        Timeline t = new Timeline();
        t.getKeyFrames().clear();
        t.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), new KeyValue(editPane.translateYProperty(), 0, Interpolator.SPLINE(0, 0, 0, 1))));
        t.play();

        eStateLabel.setText("");

        eIdField.setText(currentAccount.getId());
        ePasswordField.setText(HexString.toNormalString(currentAccount.getPassword()));
        eNameField.setText(currentAccount.getName());
    }

    @FXML
    protected void showRegisterPane() {
        checked = false;
        isUniqueId = false;

        registerPane.setVisible(true);
        registerPane.setTranslateX(Main.widthProperty().get());

        Timeline t = new Timeline();
        t.getKeyFrames().clear();
        t.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), new KeyValue(registerPane.translateXProperty(), 0, Interpolator.SPLINE(0, 0, 0, 1))));
        t.setOnFinished(e -> loginPane.setVisible(false));
        t.play();

        clearFields();
        lStateLabel.setText("");
    }

    @FXML
    protected void showLoginPane() {
        loginPane.setVisible(true);

        Timeline t = new Timeline();
        t.getKeyFrames().clear();
        t.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), new KeyValue(registerPane.translateXProperty(), Main.widthProperty().get(), Interpolator.SPLINE(0, 0, 0, 1))));
        t.setOnFinished(e -> registerPane.setVisible(false));
        t.play();

        clearFields();
        rStateLabel.setText("");
    }

    @FXML
    protected void deleteAccount() {
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Delete this account?");

        confirmDelete.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                delete();
                logout();
            }
        });
    }

    private void delete() {
        accounts.remove(currentAccount);

        File accountFile = new File(Account.ACCOUNTS_FOLDER + "\\" + currentAccount.getId() + ".properties");
        boolean deleted = accountFile.delete();

        if (!deleted) {
            // if file is not deleted, write "Deleted Account".
            try {
                FileWriter writer = new FileWriter(accountFile);
                writer.write("Deleted Account");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        currentAccount = null;
    }

    private void showMain() {
        Timeline t = new Timeline();
        t.getKeyFrames().clear();
        t.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), new KeyValue(loginPane.translateYProperty(), Main.heightProperty().get(), Interpolator.SPLINE(0, 0, 0, 1))));
        t.setOnFinished(e -> loginPane.setVisible(false));
        t.play();
    }

    @FXML
    protected void showMainE() {
        Timeline t = new Timeline();
        t.getKeyFrames().clear();
        t.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), new KeyValue(editPane.translateYProperty(), -Main.heightProperty().get(), Interpolator.SPLINE(0, 0, 0, 1))));
        t.setOnFinished(e -> editPane.setVisible(false));
        t.play();
    }
}
