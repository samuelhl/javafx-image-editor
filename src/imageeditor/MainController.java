package imageeditor;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {
	@FXML
	private MenuItem imageChooser;
	@FXML
	private ImageView imageView;

	public void ImageChooserAction(ActionEvent event){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(
				new ExtensionFilter("JPG Files", "*.jpg"),
				new ExtensionFilter("PNG Files", "*.png")
				);
		File selectedFile = fc.showOpenDialog(null);

		if(selectedFile != null){
			System.out.println("file is valid");
			Image img = new Image("file:" + selectedFile.getAbsolutePath());
			imageView.setImage(img);
		}else{
			System.out.println("file is not valid");
		}
	}
}
