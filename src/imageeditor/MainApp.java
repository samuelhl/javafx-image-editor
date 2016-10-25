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
import javafx.scene.layout.HBox;
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

    	//File options
        Button btnLoad = new Button("Open image...");
        btnLoad.setOnAction(btnLoadEventListener);

        //Add options
        Button btnAddPoint = new Button("Point");
        btnAddPoint.setOnAction(btnPointEventListener);
        Button btnAddTooth = new Button("Tooth");
        btnAddTooth.setOnAction(btnToothEventListener);

        //Edit options
        Button btnEditMove = new Button("Move");
        btnEditMove.setOnAction(btnMoveEventListener);
        Button btnEditDelete = new Button("Delete");
        btnEditDelete.setOnAction(btnDeleteEventListener);

        //Size options
        Button btnSizeDistance = new Button("Distance");
        btnSizeDistance.setOnAction(btnDistanceEventListener);
        Button btnSizeAngle = new Button("Angle");
        btnSizeAngle.setOnAction(btnAngleEventListener);

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

        //File options
    	HBox hBoxFile = new HBox();
    	hBoxFile.getChildren().addAll(btnLoad);
    	//Add options
    	HBox hBoxAdd = new HBox(2);
    	hBoxAdd.getChildren().addAll(btnAddPoint, btnAddTooth);
    	//Edit options
    	HBox hBoxEdit = new HBox(2);
    	hBoxEdit.getChildren().addAll(btnEditMove, btnEditDelete);
    	//Size options
    	HBox hBoxSize = new HBox(2);
    	hBoxSize.getChildren().addAll(btnSizeDistance, btnSizeAngle);

    	//All options
    	HBox hBoxOption = new HBox(4);
    	hBoxOption.getChildren().addAll(hBoxFile, hBoxAdd, hBoxEdit, hBoxSize);

        VBox rootBox = new VBox();
        rootBox.getChildren().addAll(hBoxOption, scrollPane);

        //Group root = new Group();
        //root.getChildren().addAll(myImageView, rootBox);

        Scene scene = new Scene(rootBox, 600, 300);

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

    EventHandler<ActionEvent> btnPointEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            System.out.println("Poner punto");
        }
    };

    EventHandler<ActionEvent> btnToothEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            System.out.println("Poner muela");
        }
    };

    EventHandler<ActionEvent> btnMoveEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            System.out.println("Mover objeto");
        }
    };

    EventHandler<ActionEvent> btnDeleteEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            System.out.println("Borrar objeto");
        }
    };

    EventHandler<ActionEvent> btnDistanceEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            System.out.println("Distancia");
        }
    };

    EventHandler<ActionEvent> btnAngleEventListener
    = new EventHandler<ActionEvent>(){

        @Override
        public void handle(ActionEvent t) {
            System.out.println("Angulo");
        }
    };
}