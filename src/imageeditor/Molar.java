package imageeditor;
import javafx.scene.Group;

public class Molar extends DentalPiece {

	static String imageDir = "src\\resources\\MuelaI.png";

	public Molar(MainApp mainApp, double initX, double initY, Group dentalPieceGroup)
	{
		super(mainApp, initX, initY, dentalPieceGroup, imageDir);
	}

}