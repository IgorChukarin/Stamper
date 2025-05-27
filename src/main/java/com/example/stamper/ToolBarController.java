package com.example.stamper;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

import java.io.File;

public class ToolBarController {

    @FXML
    private ToolBar toolBar;

    @FXML
    public void initialize() {
        String rootFolderPath = "entities";

        File rootFolder = new File(rootFolderPath);

        if (rootFolder.exists() && rootFolder.isDirectory()) {
            System.out.println("root folder exists!");
            File[] files = rootFolder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        Button btn = new Button(f.getName());

                        toolBar.getItems().add(btn);
                    }
                }
            }
        } else {
            System.out.println("Where the fuck is: " + rootFolderPath);
        }
    }
}
