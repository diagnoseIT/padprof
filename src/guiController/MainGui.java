package guiController;

import java.io.File;
import java.io.IOException;
import java.util.List;

import gui.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**This class is responsible for showing the graphical user interface
 * @author Alexander Bran
 *
 */
public class MainGui extends Application {

	private Stage primaryStage;
	private AnchorPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("PADprof");
		
		initRootLayout();
	}
	
	
	/**
	 * opens a file chooser in order to select one file 
	 * (used for the hotspot file selector)
	 * @return chosen file
	 */
	public File getSingleFile(){
		FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("YourKit Export data (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        
        

        // Show open file dialog
        File file = fileChooser.showOpenDialog(primaryStage);
        
       
        return file;
    }
	
	/**
	 * opens a fileChooser in order to select multiple files
	 * @return
	 */
	public List<File> getMultipleFiles(){
		FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("YourKit Export data (*csv, *.xml)", "*.xml", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        
        

        // Show open file dialog
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
        
        
        return selectedFiles;
    }
	
	
	

	/**
	 * Initializes the root layout.
	 */
	private void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainGui.class.getResource("/gui/MainWindow.fxml"));
			rootLayout = (AnchorPane) loader.load();
			GuiController controller = loader.getController();
			controller.setMain(this);
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	   /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
	public static void main(String[] args) {
		launch(args);
	}
}
