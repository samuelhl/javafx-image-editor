package imageeditor;
import javafx.scene.Group;

public class Tooth extends DentalPiece {

	static String imageDir = "src\\resources\\DienteI.png";

	public Tooth(MainApp mainApp, double initX, double initY, Group dentalPieceGroup)
	{
		super(mainApp, initX, initY, dentalPieceGroup, imageDir);
	}

}