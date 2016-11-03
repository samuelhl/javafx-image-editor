package imageeditor;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Point {
	double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;

	public Point(MouseEvent e, Group pointGroup) {
		Circle circle = new Circle(e.getSceneX(), e.getSceneY() - 42, 5, Color.RED);
        circle.setCursor(Cursor.HAND);

        // EVENT HANDLERS
        EventHandler<MouseEvent> circleOnMousePressedEventHandler =
        new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
            	if (t.getButton().equals(MouseButton.PRIMARY)) {
            		orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();
                    orgTranslateX = ((Node)(t.getSource())).getTranslateX();
                    orgTranslateY = ((Node)(t.getSource())).getTranslateY();
            	} else if (t.getButton().equals(MouseButton.SECONDARY)) {
            		pointGroup.getChildren().remove((Node)t.getSource());
            	}
            }
        };

        EventHandler<MouseEvent> circleOnMouseDraggedEventHandler =
        new EventHandler<MouseEvent>() {

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

        circle.setOnMousePressed(circleOnMousePressedEventHandler);
        circle.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        pointGroup.getChildren().add(circle);
	}

}