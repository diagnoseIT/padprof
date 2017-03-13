package gui;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import analysis.AnalysisExecution;
import guiController.MainGui;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * This class manages all the in and outputs of the GUI
 * @author Alexander Bran
 *
 */
public class GuiController {

	private MainGui mainGui;

	@FXML
	private Button selectHotspotData;

	@FXML
	private Button selectProblemData;

	@FXML
	private Button selectComparisonData;

	@FXML
	private Button start;

	@FXML
	private TextField hotspotPath;

	@FXML
	private TextField problemPath;

	@FXML
	private TextField comparisonPath;

	@FXML
	private TextArea resultBox;
	
	@FXML
	private TextField cthCpuPercentage;
	
	@FXML
	private TextField cthMethodCount;
	
	@FXML
	private TextField epMethodPercentage;
	
	@FXML
	private TextField epBlockedThreadsPercentage;
	
	@FXML
	private TextField wcMethodPercentage;
	
	@FXML
	private CheckBox cthBox;
	
	@FXML
	private CheckBox epBox;
	
	@FXML
	private CheckBox wcBox;
	
	@FXML
	private ComboBox<String> analysisSelector;
	
	private PrintStream ps;

	private List<File> selectedComparisonFiles = new ArrayList<>();
	private List<File> selectedProblemFiles = new ArrayList<>();
	private File hotspotFile;
	private ObservableList<String> analysisMethods = 
		    FXCollections.observableArrayList(
		            "min",
		            "max",
		            "average"
		        );
	@FXML
	private void initialize() {
		ps = new PrintStream(new Console(resultBox));
		analysisSelector.setItems(analysisMethods);

	}

	/**
	 * shows the FileChosser for the hotspot file
	 * (only one file can be selected)
	 */
	@FXML
	private void hotspotFileSelector() {

		hotspotFile = mainGui.getSingleFile();
		if (hotspotFile != null) {
			hotspotPath.setText(hotspotFile.getAbsolutePath());

		}
	}

	/**
	 * shows the FileChosser for the problem files
	 * (multiple files can be selected)
	 * Should be two (All Threads + By Thread data).
	 */
	@FXML
	private void problemFileSelector() {
		selectedProblemFiles = mainGui.getMultipleFiles();
		if (!selectedProblemFiles.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (File file : selectedProblemFiles) {
				builder.append("\"");
				builder.append(file.getAbsolutePath());
				builder.append("\" ");
			}
			problemPath.setText(builder.toString());
		}
	}

	/**
	 * shows the FileChosser for the comparison files
	 * (multiple files can be selected)
	 * Should be always an even number (All Threads + By Thread data per snapshot)
	 */
	@FXML
	private void comparisonFileSelector() {
		selectedComparisonFiles = mainGui.getMultipleFiles();
		if (!selectedComparisonFiles.isEmpty()) {

			StringBuilder builder = new StringBuilder();
			for (File file : selectedComparisonFiles) {
				builder.append("\"");
				builder.append(file.getAbsolutePath());
				builder.append("\" ");
			}
			comparisonPath.setText(builder.toString());
		}
	}
	
	/**
	 * makes the texFields for the Circuitous Treasure Hunt strategy visible.
	 */
	@FXML
	private void triggerCTH(){
		if(cthBox.isSelected()){
			cthCpuPercentage.setDisable(false);
			cthMethodCount.setDisable(false);
			analysisSelector.setDisable(false);
		} else {
			cthCpuPercentage.setDisable(true);
			cthMethodCount.setDisable(true);
			analysisSelector.setDisable(true);
		}
	}
	
	/**
	 * makes the texFields for the Extensive Processing strategy visible.
	 */
	@FXML
	private void triggerEP(){
		if(epBox.isSelected()){
			epMethodPercentage.setDisable(false);
			epBlockedThreadsPercentage.setDisable(false);
		} else {
			epMethodPercentage.setDisable(true);
			epBlockedThreadsPercentage.setDisable(true);
		}
	}
	
	/**
	 * makes the texFields for the Wrong Cache strategy visible.
	 */
	@FXML
	private void triggerWC(){
		if(wcBox.isSelected()){
			wcMethodPercentage.setDisable(false);
		} else {
			wcMethodPercentage.setDisable(true);
		}
	}

	/**
	 * This method is executed when "Start Analysis" is pressed.
	 * It runs the complete analysis.
	 */
	@FXML
	private void startClicked() {
		System.setOut(ps);
		System.setErr(ps);
	
		if(isValid()){
			AnalysisExecution exec = new AnalysisExecution(hotspotFile, selectedProblemFiles, selectedComparisonFiles);
			exec.doPrepearation();
			if(cthBox.isSelected()){
				exec.cthAnalysis(1.00+(Double.valueOf(cthMethodCount.getText())/100), Integer.valueOf(cthCpuPercentage.getText()), analysisSelector.getValue().toString());
			}
			if(epBox.isSelected()){
				exec.epAnalysis(1.00+(Double.valueOf(epBlockedThreadsPercentage.getText())/100), Integer.valueOf(epMethodPercentage.getText()));
				//TODO: Implement
			}
			if(wcBox.isSelected()){
				exec.wcAnalysis(Integer.valueOf(wcMethodPercentage.getText()));
				//TODO: Implement.
			}

		} else {
			//if something is wrong with the entries, show a error dialog
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Entries wrong");
			alert.setHeaderText("Your Entries have a mistake! Please check!");
		

			alert.showAndWait();
		}
		
		

	}
	
	/**
	 * checks if all entries that are needed for the detection are there
	 * @return true if everything is all right
	 */
	private boolean isValid(){
		boolean isValid = true;
		if(hotspotPath.getText() == null || problemPath.getText() == null || comparisonPath.getText() == null)
			isValid = false;
		
		if(cthBox.isSelected() || epBox.isSelected() || wcBox.isSelected()){
			if(cthBox.isSelected()){
				if (cthMethodCount.getText() == null || cthCpuPercentage.getText() == null || analysisSelector.getValue() == null){
					isValid = false;
				} 
				
			}
			
			if(epBox.isSelected()){
				if (epMethodPercentage.getText() == null || epBlockedThreadsPercentage.getText() == null){
					isValid = false;
				} 
				
			}
			
			if(wcBox.isSelected()){
				if (wcMethodPercentage.getText() == null){
					isValid = false;
				} 
				
			}
			
		} else {
			isValid = false;
		}
		
		return isValid;
	}

	public void setMain(MainGui mainGui) {
		this.mainGui = mainGui;

	}

	/**
	 * class for the output.
	 * Puts all the console output in a textField.
	 * 
	 *
	 */
	public class Console extends OutputStream {
		private TextArea console;

		public Console(TextArea console) {
			this.console = console;
		}

		public void appendText(String valueOf) {
			Platform.runLater(() -> console.appendText(valueOf));
		}

		public void write(int b) throws IOException {
			appendText(String.valueOf((char) b));
		}
	}
}
