package imageeditor;
import javafx.scene.Group;

public class Tooth_S extends DentalPiece {

	static String imageDir = "src\\image\\DienteS.png";

	public Tooth_S(MainApp mainApp, double initX, double initY, Group dentalPieceGroup)
	{
		super(mainApp, initX, initY, dentalPieceGroup, imageDir);
	}

}