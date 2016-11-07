package imageeditor;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Point {
	private double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY, x, y;
	private double originalPointX, originalPointY,appFixedPointWidth, appFixedPointHeight;
	private MainApp mainApp;
	private Circle circl;
	Group pointGroup;

	public Point(MainApp mainApp, double initX, double initY, Group pointGroup) {
        this.mainApp = mainApp;
        this.pointGroup = pointGroup;
        Circle circle = new Circle();
        circle.setRadius(3.0f);
        circle.setFill(Color.RED);
        circle.setCenterX(initX);
        circle.setCenterY(initY - 42);
        circl = circle;
        originalPointX=initX;
        originalPointY=initY - 42;
        System.out.println(originalPointX  + ", " + originalPointY);
        appFixedPointWidth = mainApp.getFixedImageWidth();
        appFixedPointHeight = mainApp.getFixedImageHeight();
        circle.setCursor(Cursor.HAND);
        circle.setOnMousePressed(circleOnMousePressedEventHandler);
        circle.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        pointGroup.getChildren().add(circle);
        x = initX;
        y = initY - 45;
    }

	public double getOrgSceneX()
    {
    	return orgSceneX;
    }

	public double getOrgSceneY()
    {
    	return orgSceneY;
    }

	public double getorgTranslateX()
    {
    	return orgTranslateX;
    }

	public double getorgTranslateY()
    {
    	return orgTranslateY;
    }

	public Point getInstace()
    {
    	return this;
    }

	public Circle getCircle()
    {
    	return circl;
    }

	public double getOriginalPointWidth()
    {
    	return appFixedPointWidth;
    }

    public double getOriginalPointHeight()
    {
    	return appFixedPointHeight;
    }

    public double getOriginalPointX()
    {
    	return originalPointX;
    }

    public double getOriginalPointY()
    {
    	return originalPointY;
    }

	public double ComputeDistance(Point p)
	{
		Point2D point1 = new Point2D(orgSceneX, orgSceneY);
		Point2D point2 = new Point2D(p.getOrgSceneX(), p.getOrgSceneY());
		return point1.distance(point2);
	}

	public double ComputeAngle(Point p1, Point p2)
	{
		Point2D point1 = new Point2D(orgSceneX, orgSceneY);
		Point2D point2 = new Point2D(p1.getOrgSceneX(), p1.getOrgSceneY());
		Point2D point3 = new Point2D(p2.getOrgSceneX(), p2.getOrgSceneY());
		return point1.angle(point2, point3);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	// EVENT HANDLERS
    EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
        	if (t.getButton().equals(MouseButton.PRIMARY)) {

        		orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
                orgTranslateX = ((Node)(t.getSource())).getTranslateX();
                orgTranslateY = ((Node)(t.getSource())).getTranslateY();

                if(mainApp.getChooseSizeDistance())
        		{
        			mainApp.AddPointToComputeDistance(getInstace());
        		}
        		if(mainApp.getChooseSizeAngle())
        		{
        			mainApp.AddPointToComputeAngle(getInstace());
        		}

        	}
        	else if (t.getButton().equals(MouseButton.SECONDARY)) {
        		pointGroup.getChildren().remove((Node)t.getSource());
        	}
        }
    };

    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            ((Node)(t.getSource())).setTranslateX(newTranslateX);
            ((Node)(t.getSource())).setTranslateY(newTranslateY);
        }
    };

}