module com.example.stamper {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires javafx.swing;



    opens com.example.stamper to javafx.fxml;
    exports com.example.stamper;
}