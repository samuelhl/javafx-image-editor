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


public class Tooth {
	final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
	private double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;
	private double originalToothWidth, originalToothHeight, originalToothX, originalToothY, appFixedImgWidth, appFixedImgHeight;
	private ImageView imageV;
	private MainApp mainApp;
	int v;

	public Tooth(MainApp mainApp, MouseEvent e, Group toothGroup)
	{
		this.mainApp = mainApp;
		File file = new File("resources\\diente.png");
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
        originalToothX = e.getSceneX()-15;
        originalToothY = e.getSceneY()- 70;
        System.out.println((e.getSceneX()-15) + " ; " + (e.getSceneY()- 70));
        imageV.setLayoutX(originalToothX);
        imageV.setLayoutY(originalToothY);
        imageV.setCursor(Cursor.HAND);

        originalToothWidth = image.getWidth();
        originalToothHeight = image.getHeight();
        SetImageSize();

        // EVENT HANDLERS
        EventHandler<MouseEvent> toothOnMousePressedEventHandler = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	if (t.getButton().equals(MouseButton.PRIMARY))
            	{
            		orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();
                    orgTranslateX = ((Node)(t.getSource())).getTranslateX();
                    orgTranslateY = ((Node)(t.getSource())).getTranslateY();
            	} else if (t.getButton().equals(MouseButton.SECONDARY))
            	{
            		toothGroup.getChildren().remove((Node)t.getSource());
            	}
            }
        };

        EventHandler<MouseEvent> toothOnMouseDraggedEventHandler = new EventHandler<MouseEvent>()
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

        imageV.setOnMousePressed(toothOnMousePressedEventHandler);
        imageV.setOnMouseDragged(toothOnMouseDraggedEventHandler);

        toothGroup.getChildren().add(imageV);
	}

	private void SetImageSize()
	{
        double appOriginalImgWidth = mainApp.getOriginalImageWidth();
        double appOriginalImgHeight = mainApp.getOriginalImageHeight();
        appFixedImgWidth = mainApp.getFixedImageWidth();
        appFixedImgHeight = mainApp.getFixedImageHeight();

        double imgWidth = Utils.ComputeRatio(appOriginalImgWidth, appFixedImgWidth, originalToothWidth);
        double imgHeight = Utils.ComputeRatio(appOriginalImgHeight, appFixedImgHeight, originalToothHeight);

        imageV.setFitWidth(imgWidth);
        imageV.setFitHeight(imgHeight);
	}

	public double getOriginalImageWidth()
    {
    	return appFixedImgWidth;
    }

    public double getOriginalImageHeight()
    {
    	return appFixedImgHeight;
    }

	public double getOriginalToothWidth()
    {
    	return originalToothWidth;
    }

    public double getOriginalToothHeight()
    {
    	return originalToothHeight;
    }

    public double getOriginalToothX()
    {
    	return originalToothX;
    }

    public double getOriginalToothY()
    {
    	return originalToothY;
    }

    public ImageView getImageView()
    {
    	return imageV;
    }
}