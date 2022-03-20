package form.login.v5.loginformversion5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Account {
    public static final File ACCOUNTS_FOLDER = new File("C:\\LoginFormV5\\Accounts");

    private String id, name, password;

    public Account(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void writeToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FOLDER + "\\" + id + ".properties"));

            writer.write("id=" + id);
            writer.newLine();
            writer.write("name=" + name);
            writer.newLine();
            writer.write("password=" + password);
            writer.newLine();

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
