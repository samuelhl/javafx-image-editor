package imageeditor;
import javafx.scene.Group;

public class Molar_S extends DentalPiece {

	static String imageDir = "src\\resources\\MuelaS.png";

	public Molar_S(MainApp mainApp, double initX, double initY, Group dentalPieceGroup)
	{
		super(mainApp, initX, initY, dentalPieceGroup, imageDir);
	}

}