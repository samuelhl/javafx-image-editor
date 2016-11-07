package imageeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

import org.math.plot.Plot2DPanel;

/**
 * Image Editor
 */
public class MainApp extends Application
{
	private int ang = 0;
	private int i = 0;
	private double fixedImageWidth = 500;
	private ImageView myImageView;
    private ScrollPane scrollPane = new ScrollPane();
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(fixedImageWidth/4);
    boolean chooseAddPoint = false;
    boolean chooseAddTooth = false;
    boolean chooseAddContour = false;
    boolean chooseRotate = false;
    boolean chooseDelete = false;
    boolean chooseSizeDistance = false;
    boolean chooseSizeAngle = false;
    Group root;
    List<Tooth> toothList = new ArrayList<Tooth>();
    List<Point> pointListContour = new ArrayList<Point>();
    double imageOriginalWidth, imageOriginalHeight, fixedImageHeight;
    Button btnLessAngle = new Button("-");
    Button btnMoreAngle = new Button("+");
    Group contourGroup = new Group();

    @Override
    public void start(Stage primaryStage) throws Exception
    {

    	//File options
        Button btnLoad = new Button("Open image...");
        btnLoad.setOnAction(btnLoadEventListener);

        //Add options
        Button btnAddPoint = new Button("Point");
        btnAddPoint.setOnAction(btnPointEventListener);
        Button btnAddTooth = new Button("Tooth");
        btnAddTooth.setOnAction(btnToothEventListener);
        Button btnAddContour = new Button("Contour");
        btnAddContour.setOnAction(btnContourEventListener);

        //Edit options
        Button btnEditRotate = new Button("Rotate");
        btnEditRotate.setOnAction(btnMoveEventListener);
        Button btnEditDelete = new Button("Delete");
        btnEditDelete.setOnAction(btnDeleteEventListener);

        //Size options
        Button btnSizeDistance = new Button("Distance");
        btnSizeDistance.setOnAction(btnDistanceEventListener);
        Button btnSizeAngle = new Button("Angle");
        btnSizeAngle.setOnAction(btnAngleEventListener);

        //Rotate control
        btnLessAngle.setPrefWidth(25);
        btnLessAngle.setDisable(true);
        btnLessAngle.setOnAction(btnLessAngEventListener);
        btnMoreAngle.setPrefWidth(25);
        btnMoreAngle.setDisable(true);
        btnMoreAngle.setOnAction(btnMoreAngEventListener);

        myImageView = new ImageView();

        //File options
    	HBox hBoxFile = new HBox(5);
    	hBoxFile.getChildren().addAll(btnLoad);
    	//Add options
    	HBox hBoxAdd = new HBox(5);
    	hBoxAdd.getChildren().addAll(btnAddPoint, btnAddTooth, btnAddContour);
    	//Edit options
    	HBox hBoxEdit = new HBox(5);
    	hBoxEdit.getChildren().addAll(btnEditRotate, btnEditDelete);
    	//Size options
    	HBox hBoxSize = new HBox(5);
    	hBoxSize.getChildren().addAll(btnSizeDistance, btnSizeAngle);
    	//Rotate control
    	HBox hBoxAngle = new HBox(5);
    	hBoxAngle.getChildren().addAll(btnLessAngle, btnMoreAngle);

    	//All options
    	HBox hBoxOption = new HBox(10);
    	hBoxOption.getChildren().addAll(hBoxFile, hBoxAdd, hBoxEdit, hBoxSize, hBoxAngle);

        VBox rootBox = new VBox(20);
        rootBox.getChildren().addAll(hBoxOption, scrollPane);

        this.root = new Group();
        Group toothGroup = new Group();
        Group pointGroup = new Group();

        root.getChildren().addAll(myImageView, pointGroup, toothGroup, contourGroup);

        Scene scene = new Scene(rootBox, 600, 300);

        //ScrollPane size
        scrollPane.setPrefSize(1024,1024);
        scrollPane.setStyle("-fx-background-color:transparent;");

        zoomProperty.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable arg0)
            {
            	fixedImageWidth= (zoomProperty.get() * 4);
                fixedImageHeight = Utils.ComputeRatio(imageOriginalWidth, imageOriginalHeight, fixedImageWidth);
                if 	(!chooseRotate)
                {
                myImageView.setFitWidth(fixedImageWidth);
                myImageView.setFitHeight(fixedImageHeight);
                }

                for(Tooth t : toothList)
                {
                	ImageView imgV = t.getImageView();
                	if 	(!chooseRotate)
                	{
                	imgV.setFitWidth(Utils.ComputeRatio(imageOriginalWidth, fixedImageWidth, t.getOriginalToothWidth()));
                	imgV.setFitHeight(Utils.ComputeRatio(imageOriginalHeight, fixedImageHeight, t.getOriginalToothHeight()));
                	imgV.setLayoutX(Utils.ComputeRatio(t.getOriginalImageWidth(), fixedImageWidth, t.getOriginalToothX()));
                	imgV.setLayoutY(Utils.ComputeRatio(t.getOriginalImageHeight(), fixedImageHeight, t.getOriginalToothY()));
                	}
                	else
                	imgV.setRotate(ang);
                }
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>()
        {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                    ang+=10;
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                    ang-=10;
                }
            }
        });

        // IMPORTANT EVENT HANDLER
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) ->
        {
        	boolean dragUndetected = e.isDragDetect();
        	if (e.getButton().equals(MouseButton.PRIMARY) && dragUndetected)
        	{
            	if (this.chooseAddPoint) {
            		Point point = new Point(e, pointGroup);
            	} else if (this.chooseAddTooth) {
            		Tooth tooth = new Tooth(this, e, toothGroup);
            		toothList.add(tooth);
            	} else if (this.chooseAddContour) {
            		if(i<14){
	            		Point point = new Point(e, contourGroup);
	            		pointListContour.add(point);
	            		i++;
            		}else{
            			System.out.println(pointListContour);
            			Contour contorno = new Contour(pointListContour, contourGroup);
            			//Plot2DPanel plot = new Plot2DPanel();

						//contorno.pointList = pointListContour;
						//System.out.println(contorno.xc);
						//plot.addLinePlot("Interpolacion Spline", contorno.xc, contorno.yc);
            			chooseAddContour = false;
            		}
            	} else if (this.chooseRotate) {
            		//
            	} else if (this.chooseSizeDistance) {
            		// SELECCIONAR DOS PUNTOS Y CALCULAR DISTANCIA
            	} else if (this.chooseSizeAngle) {
            		// SELECCIONAR TRES PUNTOS Y CALCULAR ÁNGULO
            	}
            }
        });

        primaryStage.setTitle("Image Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    public double getFixedImageWidth()
    {
    	return fixedImageWidth;
    }

    public double getFixedImageHeight()
    {
    	return fixedImageHeight;
    }

    public double getOriginalImageWidth()
    {
    	return imageOriginalWidth;
    }

    public double getOriginalImageHeight()
    {
    	return imageOriginalHeight;
    }

    EventHandler<ActionEvent> btnLoadEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilterIMAGE = new FileChooser.ExtensionFilter("Image files", "*.JPG", "*.JPEG", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterIMAGE);

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);

            try
            {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageOriginalWidth = image.getWidth();
                imageOriginalHeight = image.getHeight();

                myImageView.setImage(image);
                myImageView.preserveRatioProperty().set(true);
                myImageView.setFitWidth(fixedImageWidth);
                fixedImageHeight = Utils.ComputeRatio(imageOriginalWidth, imageOriginalHeight, fixedImageWidth);
                myImageView.setFitHeight(fixedImageHeight);
                scrollPane.setContent(root);
                System.out.println(fixedImageWidth + " ; " + fixedImageHeight);
            } catch (IOException ex)
            {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    };

    EventHandler<ActionEvent> btnPointEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner punto");
            chooseAddPoint = true;
            chooseAddTooth = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseDelete = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnToothEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner muela");
            chooseAddPoint = false;
            chooseAddTooth = true;
            chooseAddContour = false;
            chooseRotate = false;
            chooseDelete = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnContourEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner contorno");
            i = 0;

            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddContour = true;
            chooseRotate = false;
            chooseDelete = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnMoveEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Rotar objeto");
            btnLessAngle.setDisable(false);
            btnMoreAngle.setDisable(false);
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddContour = false;
            chooseRotate = true;
            chooseDelete = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnDeleteEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t) {
            System.out.println("Borrar objeto");
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseDelete = true;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnDistanceEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t) {
            System.out.println("Distancia");
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseDelete = false;
            chooseSizeDistance = true;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnAngleEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Angulo");
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseDelete = false;
            chooseSizeDistance = false;
            chooseSizeAngle = true;
        }
    };

    EventHandler<ActionEvent> btnLessAngEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {

        }
    };

    EventHandler<ActionEvent> btnMoreAngEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {

        }
    };
}