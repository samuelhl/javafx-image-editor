package imageeditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
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
	private ImageView imageView = new ImageView();

	public void ImageChooserAction(ActionEvent event) throws IOException{
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(
				new ExtensionFilter("JPG Files", "*.jpg"),
				new ExtensionFilter("PNG Files", "*.png")
				);
		File selectedFile = fc.showOpenDialog(null);

		if(selectedFile != null){
			BufferedImage bufferedImage = ImageIO.read(selectedFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);

			//System.out.println("file is valid");
			//Image img = new Image("file:" + selectedFile.getAbsolutePath());
			//imageView.setImage(img);
		}else{
			System.out.println("file is not valid");
		}
	}
}
