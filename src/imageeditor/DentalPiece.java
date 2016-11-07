package imageeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class DentalPiece {

	final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
	private double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;
	private double originalDentalPieceWidth, originalDentalPieceHeight, originalDentalPieceX, originalDentalPieceY, appFixedImgWidth, appFixedImgHeight;
	private ImageView imageV;
	private MainApp mainApp;
	Group dentalPieceGroup;
	private Boolean rotar = false;
	private int ang=0;

	public DentalPiece(MainApp mainApp, double initX, double initY, Group dentalPieceGroup, String imageDir)
	{
		this.mainApp = mainApp;
		this.dentalPieceGroup = dentalPieceGroup;
		File file = new File(imageDir);
	    BufferedImage bufferedImage = null;

		try
		{
			bufferedImage = ImageIO.read(file);
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Image image = SwingFXUtils.toFXImage(bufferedImage, null);

        imageV = new ImageView(image);
        originalDentalPieceX = initX - 15;
        originalDentalPieceY = initY - 70;

        imageV.setLayoutX(originalDentalPieceX);
        imageV.setLayoutY(originalDentalPieceY);
        imageV.setCursor(Cursor.HAND);

        originalDentalPieceWidth = image.getWidth();
        originalDentalPieceHeight = image.getHeight();
        SetImageSize();

        imageV.setOnMousePressed(dentalPieceOnMousePressedEventHandler);
        imageV.setOnMouseDragged(dentalPieceOnMouseDraggedEventHandler);
        //imageV.setOnMouseReleased(dentalPieceOnMouseReleasedEventHandler);

        dentalPieceGroup.getChildren().add(imageV);
	}

	private void SetImageSize()
	{
        double appOriginalImgWidth = mainApp.getOriginalImageWidth();
        double appOriginalImgHeight = mainApp.getOriginalImageHeight();
        appFixedImgWidth = mainApp.getFixedImageWidth();
        appFixedImgHeight = mainApp.getFixedImageHeight();

        double imgWidth = Utils.ComputeRatio(appOriginalImgWidth, appFixedImgWidth, originalDentalPieceWidth);
        double imgHeight = Utils.ComputeRatio(appOriginalImgHeight, appFixedImgHeight, originalDentalPieceHeight);

        imageV.setFitWidth(imgWidth);
        imageV.setFitHeight(imgHeight);
	}

	public void setAngulo(int newAng)
    {
    	ang=newAng;
    }

	public int getAngulo()
    {
    	return ang;
    }

	public void setRotar(Boolean value)
    {
    	rotar=value;
    }

	public boolean getRotar()
    {
    	return rotar;
    }

	public double getOriginalImageWidth()
    {
    	return appFixedImgWidth;
    }

    public double getOriginalImageHeight()
    {
    	return appFixedImgHeight;
    }

	public double getOriginalDentalPieceWidth()
    {
    	return originalDentalPieceWidth;
    }

    public double getOriginalDentalPieceHeight()
    {
    	return originalDentalPieceHeight;
    }

    public double getOriginalDentalPieceX()
    {
    	return originalDentalPieceX;
    }

    public double getOriginalDentalPieceY()
    {
    	return originalDentalPieceY;
    }

    public ImageView getImageView()
    {
    	return imageV;
    }


 // EVENT HANDLERS
    EventHandler<MouseEvent> dentalPieceOnMousePressedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent t)
        {
        	if (t.getButton().equals(MouseButton.PRIMARY))
        	{
        		if (mainApp.getChooseRotate())
        		{
        			mainApp.SetRotateFalse();
        			rotar = true;
        		}
        		orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
                orgTranslateX = ((Node)(t.getSource())).getTranslateX();
                orgTranslateY = ((Node)(t.getSource())).getTranslateY();
                } else if (t.getButton().equals(MouseButton.SECONDARY))
        	{
        		dentalPieceGroup.getChildren().remove((Node)t.getSource());
        	}
        }
    };

    EventHandler<MouseEvent> dentalPieceOnMouseDraggedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent t)
        {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            ((Node)(t.getSource())).setTranslateX(newTranslateX);
            ((Node)(t.getSource())).setTranslateY(newTranslateY);
         }
    };

    EventHandler<MouseEvent> dentalPieceOnMouseReleasedEventHandler = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent t)
        {
        	boolean dragUndetected = t.isDragDetect();
        	if(!dragUndetected)
        	{
        		double posX = ((Node)(t.getSource())).getLayoutX();
        		double posY = ((Node)(t.getSource())).getLayoutY();

                originalDentalPieceX = posX;
                originalDentalPieceY = posY;
        	}
         }
    };
}
