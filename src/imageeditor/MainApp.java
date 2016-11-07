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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Image Editor
 */
public class MainApp extends Application
{
	private double getDeltaY = 0;
	private int i = 0;
	private double fixedImageWidth = 500;
	private ImageView myImageView;
    private ScrollPane scrollPane = new ScrollPane();
    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(fixedImageWidth/4);
    private boolean chooseAddPoint = false;
    private boolean chooseAddTooth = false;
    private boolean chooseAddToothS = false;
    private boolean chooseAddMolar = false;
    private boolean chooseAddMolarS = false;
    private boolean chooseAddContour = false;
    private boolean chooseRotate = false;
    private boolean chooseSizeDistance = false;
    private boolean chooseSizeAngle = false;
    private Group root;
    private List<DentalPiece> dentalPieceList = new ArrayList<DentalPiece>();
    private List<Point> pointsList = new ArrayList<Point>();
    private List<Point> pointsToComputeDistance = new ArrayList<Point>();
    private List<Point> pointsToComputeAngle = new ArrayList<Point>();
    private List<Point> pointListContour = new ArrayList<Point>();
    private double imageOriginalWidth, imageOriginalHeight, fixedImageHeight;
    private TextField TextDistance = new TextField();
    private TextField TextAngle = new TextField();

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
        btnAddTooth.setStyle("-fx-background-image: url('src\\image\\diente.png')");
        btnAddTooth.setOnAction(btnToothEventListener);

        Button btnAddToothS = new Button("Upper Tooth");
        btnAddToothS.setStyle("-fx-background-image: url('src\\image\\diente.png')");
        btnAddToothS.setOnAction(btnToothSEventListener);

        Button btnAddMolar = new Button("Molar");
        btnAddMolar.setStyle("-fx-background-image: url('src\\image\\molar.png')");
        btnAddMolar.setOnAction(btnMolarEventListener);

        Button btnAddMolarS = new Button("Upper Molar");
        btnAddMolarS.setStyle("-fx-background-image: url('src\\image\\molar.png')");
        btnAddMolarS.setOnAction(btnMolarSEventListener);

        Button btnAddContour = new Button("Contour");
        btnAddContour.setOnAction(btnContourEventListener);

        //Edit options
        Button btnEditRotate = new Button("Rotate");
        btnEditRotate.setOnAction(btnMoveEventListener);

        //Size options
        Button btnSizeDistance = new Button("Distance");
        TextDistance.setPrefWidth(50);
        btnSizeDistance.setOnAction(btnDistanceEventListener);

        Button btnSizeAngle = new Button("Angle");
        TextAngle.setPrefWidth(50);
        btnSizeAngle.setOnAction(btnAngleEventListener);

        myImageView = new ImageView();

        //File options
    	HBox hBoxFile = new HBox(5);
    	hBoxFile.getChildren().addAll(btnLoad);
    	//Add options
    	HBox hBoxAdd = new HBox(5);
    	hBoxAdd.getChildren().addAll(btnAddPoint, btnAddTooth, btnAddToothS, btnAddMolar, btnAddMolarS, btnAddContour);
    	//Edit options
    	HBox hBoxEdit = new HBox(5);
    	hBoxEdit.getChildren().addAll(btnEditRotate);
    	//Size options
    	HBox hBoxSize = new HBox(5);
    	hBoxSize.getChildren().addAll(btnSizeDistance, TextDistance, btnSizeAngle, TextAngle);

    	//All options
    	HBox hBoxOption = new HBox(10);
    	hBoxOption.getChildren().addAll(hBoxFile, hBoxAdd, hBoxEdit, hBoxSize);

        VBox rootBox = new VBox(20);
        rootBox.getChildren().addAll(hBoxOption, scrollPane);

        this.root = new Group();
        Group toothGroup = new Group();
        Group molarGroup = new Group();
        Group pointGroup = new Group();
        Group contourGroup = new Group();
        Group lines = new Group();
        root.getChildren().addAll(myImageView, pointGroup, toothGroup, molarGroup, contourGroup, lines);

        Scene scene = new Scene(rootBox, 800, 500);

        //ScrollPane size
        scrollPane.setPrefSize(1024,1024);
        scrollPane.setStyle("-fx-background-color:transparent;");

        zoomProperty.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable arg0)
            {
                if  (!chooseRotate)
                {
                    fixedImageWidth= (zoomProperty.get() * 4);
                    fixedImageHeight = Utils.ComputeRatio(imageOriginalWidth, imageOriginalHeight, fixedImageWidth);

                    myImageView.setFitWidth(fixedImageWidth);
                    myImageView.setFitHeight(fixedImageHeight);
                }
                else
                    zoomProperty.set(fixedImageWidth/4);

                for(DentalPiece t : dentalPieceList)
                {

                    ImageView imgV = t.getImageView();
                    if  (!chooseRotate)
                    {
                    imgV.setFitWidth(Utils.ComputeRatio(imageOriginalWidth, fixedImageWidth, t.getOriginalDentalPieceWidth()));
                    imgV.setFitHeight(Utils.ComputeRatio(imageOriginalHeight, fixedImageHeight, t.getOriginalDentalPieceHeight()));
                    imgV.setLayoutX(Utils.ComputeRatio(t.getOriginalImageWidth(), fixedImageWidth, t.getOriginalDentalPieceX()));
                    imgV.setLayoutY(Utils.ComputeRatio(t.getOriginalImageHeight(), fixedImageHeight, t.getOriginalDentalPieceY()));
                    }
                    else if (t.getRotar())
                    {
                        if ( getDeltaY > 0)
                        {
                            t.setAngulo(t.getAngulo()+10);
                            imgV.setRotate(t.getAngulo());
                        }
                        else
                        {
                            t.setAngulo(t.getAngulo()-10);
                            imgV.setRotate(t.getAngulo());
                        }
                    }
                }


                for(Point p : pointsList)
                {
                    Circle cir = p.getCircle();

                    if  (!chooseRotate)
                    {
                    cir.setCenterX(Utils.ComputeRatio(p.getOriginalPointWidth(), fixedImageWidth, p.getOriginalPointX()));
                    cir.setCenterY(Utils.ComputeRatio(p.getOriginalPointHeight(), fixedImageHeight, p.getOriginalPointY()));
                    }
                }

                for(Point p : pointListContour)
                {
                    Circle cir = p.getCircle();

                    if  (!chooseRotate)
                    {
                    cir.setCenterX(Utils.ComputeRatio(p.getOriginalPointWidth(), fixedImageWidth, p.getOriginalPointX()));
                    cir.setCenterY(Utils.ComputeRatio(p.getOriginalPointHeight(), fixedImageHeight, p.getOriginalPointY()));
                    }
                }

            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>()
        {
            @Override
            public void handle(ScrollEvent event)
            {
                if (event.getDeltaY() > 0)
                {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                    getDeltaY=event.getDeltaY();
                } else if (event.getDeltaY() < 0)
                {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                    getDeltaY=event.getDeltaY();
                }
            }
        });

        // IMPORTANT EVENT HANDLER
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) ->
        {
        	boolean dragUndetected = e.isDragDetect();
        	if (e.getButton().equals(MouseButton.PRIMARY) && dragUndetected)
        	{
            	if (this.chooseAddPoint)
                {
                    Point point = new Point(this, e.getSceneX(), e.getSceneY(), pointGroup);
                    pointsList.add(point);
                }
                else if (this.chooseAddTooth)
                {
                    Tooth tooth = new Tooth(this, e.getSceneX(), e.getSceneY(), toothGroup);
                    dentalPieceList.add(tooth);
                }
                else if (this.chooseAddToothS)
                {
                    Tooth_S tooths = new Tooth_S(this, e.getSceneX(), e.getSceneY(), toothGroup);
                    dentalPieceList.add(tooths);
                }
                else if (this.chooseAddMolar)

                {
                    Molar molar = new Molar(this, e.getSceneX(), e.getSceneY(), molarGroup);
                    dentalPieceList.add(molar);
                }
                else if (this.chooseAddMolarS)
                {
                    Molar_S molars = new Molar_S(this, e.getSceneX(), e.getSceneY(), molarGroup);
                    dentalPieceList.add(molars);
                }
                else if (this.chooseAddContour) {
            		if(i<14){
	            		Point point = new Point(this, e.getSceneX(), e.getSceneY(), pointGroup);
	            		pointListContour.add(point);
	            		i++;
            		}else{
            			System.out.println(pointListContour);
            			Contour contorno = new Contour(pointListContour);

            			int n = contorno.XC.length;

            			Line line;
            			for (i=0 ; i<n-1 ; i++){
            				line = new Line(contorno.YC[i], contorno.XC[i], contorno.YC[i+1], contorno.XC[i+1]);
            				line.setStyle("-fx-stroke: red;");
            				lines.getChildren().add(line);
            			}
            			chooseAddContour = false;
            		}
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

    public boolean getChooseRotate()
    {
        return chooseRotate;
    }

    public boolean getChooseSizeDistance()
    {
        return chooseSizeDistance;
    }

    public boolean getChooseSizeAngle()
    {
        return chooseSizeAngle;
    }

    public void SetRotateFalse()
    {
         for(DentalPiece t : dentalPieceList)
         {
             t.setRotar(false);
         }
    }

    public void AddPointToComputeDistance(Point p)
    {
        double distance = 0;

        if(pointsToComputeDistance.size() == 0)
        {
            pointsToComputeDistance.add(p);
        }
        else if(pointsToComputeDistance.size() == 1)
        {
            pointsToComputeDistance.add(p);
            distance = pointsToComputeDistance.get(0).ComputeDistance(pointsToComputeDistance.get(1));
        }
        else
        {
            pointsToComputeDistance.set(0, pointsToComputeDistance.get(1));
            pointsToComputeDistance.set(1, p);
            distance = pointsToComputeDistance.get(0).ComputeDistance(pointsToComputeDistance.get(1));
        }

        distance = Math.round(distance);
        TextDistance.setPromptText(Double.toString(distance));
        System.out.println(distance);
    }

    public void AddPointToComputeAngle(Point p)
    {
        double angle = 0;

        if(pointsToComputeAngle.size() < 2)
        {
            pointsToComputeAngle.add(p);
        }
        else if(pointsToComputeAngle.size() == 2)
        {
            pointsToComputeAngle.add(p);
            angle = pointsToComputeAngle.get(1).ComputeAngle(pointsToComputeAngle.get(0), pointsToComputeAngle.get(2));
        }
        else
        {
            pointsToComputeAngle.set(0, pointsToComputeAngle.get(1));
            pointsToComputeAngle.set(1, pointsToComputeAngle.get(2));
            pointsToComputeAngle.set(2, p);
            angle = pointsToComputeAngle.get(1).ComputeAngle(pointsToComputeAngle.get(0), pointsToComputeAngle.get(2));

        }

        angle = Math.round(angle);
        TextAngle.setPromptText(Double.toString(angle));
        System.out.println(angle);
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
            SetRotateFalse();
            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = true;
            chooseAddTooth = false;
            chooseAddMolar = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnToothEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner diente");
            SetRotateFalse();
            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = true;
            chooseAddMolar = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnToothSEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner diente");
            SetRotateFalse();
            chooseAddPoint = false;
            chooseAddToothS = true;
            chooseAddMolarS = false;
            chooseAddTooth = false;
            chooseAddMolar = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnMolarEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner molar");
            SetRotateFalse();
            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddMolar = true;
            chooseAddContour = false;
            chooseRotate = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

    EventHandler<ActionEvent> btnMolarSEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner molar");
            SetRotateFalse();
            chooseAddMolar = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddMolarS = true;
            chooseAddContour = false;
            chooseRotate = false;
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
            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddMolar = false;
            chooseAddContour = false;
            chooseRotate = true;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };


    EventHandler<ActionEvent> btnDistanceEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t) {
            System.out.println("Distancia");
            SetRotateFalse();
            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddMolar = false;
            chooseAddContour = false;
            chooseRotate = false;
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
            SetRotateFalse();
            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddMolar = false;
            chooseAddContour = false;
            chooseRotate = false;
            chooseSizeDistance = false;
            chooseSizeAngle = true;
        }
    };

    EventHandler<ActionEvent> btnContourEventListener = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            System.out.println("Poner contorno");
            i = 0;

            chooseAddMolarS = false;
            chooseAddToothS = false;
            chooseAddPoint = false;
            chooseAddTooth = false;
            chooseAddMolar = false;
            chooseAddContour = true;
            chooseRotate = false;
            chooseSizeDistance = false;
            chooseSizeAngle = false;
        }
    };

}