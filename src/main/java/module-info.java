module com.example.stamper {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;


    opens com.example.stamper to javafx.fxml;
    exports com.example.stamper;
    exports org.example;
    opens org.example to javafx.fxml;
}