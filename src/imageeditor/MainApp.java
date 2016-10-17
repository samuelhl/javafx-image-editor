package imageeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Image Editor
 */
public class MainApp extends Application {

	private ImageView myImageView;
    private ScrollPane scrollPane = new ScrollPane();
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    @Override
    public void start(Stage primaryStage) throws Exception {

        Button btnLoad = new Button("Open image...");
        btnLoad.setOnAction(btnLoadEventListener);

        myImageView = new ImageView();

        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                myImageView.setFitWidth(zoomProperty.get() * 4);
                myImageView.setFitHeight(zoomProperty.get() * 3);
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        VBox rootBox = new VBox();
        rootBox.getChildren().addAll(btnLoad, scrollPane);

        //Group root = new Group();
        //root.getChildren().addAll(myImageView, rootBox);

        Scene scene = new Scene(rootBox, 300, 300);

        primaryStage.setTitle("Image Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    EventHandler<ActionEvent> btnLoadEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilterIMAGE = new FileChooser.ExtensionFilter("Image files", "*.JPG", "*.JPEG", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterIMAGE);

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);

            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                myImageView.setImage(image);
                myImageView.preserveRatioProperty().set(true);
                scrollPane.setContent(myImageView);
            } catch (IOException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    };
}