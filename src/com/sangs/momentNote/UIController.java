package com.sangs.momentNote;

import org.fxmisc.richtext.StyleClassedTextArea;

import com.sangs.momentNote.UIModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.function.IntFunction;

import org.fxmisc.richtext.LineNumberFactory;


public class UIController {
	
	dbDriver driver = new dbDriver();
	
	@FXML
	private VBox mainBody;
	
	private UIModel model;
	private Boolean notagAdd;
	
	//DISPLAY/EDIT Area
	@FXML private StyleClassedTextArea noteText;
	
	//SEARCHTAB
	@FXML private TextField searchTagField;
	@FXML private Button searchTagBtn;
	@FXML private ListView<String> searchList;
	@FXML private Label noteLabel;
	@FXML private Button setEditing;
	@FXML private Button saveEditing;
	
	//ADD TAB
	@FXML private StyleClassedTextArea addText;
	@FXML private TextField addNameField;
	@FXML private Button addSubmitBtn;
	@FXML private Button addTagBtn;
	@FXML private Button remTagBtn;
	@FXML private TextField addTagField;
	@FXML private ListView<String> addList;
	
	//Create observable list of tags, ListView UI
	private ObservableList<String> tagSet = FXCollections.observableArrayList("");
	private ObservableList<String> selectionSet = FXCollections.observableArrayList("");
	private String currentNote;
	
	
	
	public UIController(UIModel model){
		this.model = model;
		this.notagAdd = true;
	}
	
	
	@FXML
	public void setTextArea() { //initialize display area
		addLineNo(noteText);
		addLineNo(addText);
		addList.setItems(tagSet);
		addList.setStyle("-fx-background: transparent;");
		searchList.setItems(selectionSet);
		searchList.setStyle("-fx-background: transparent;");
		saveEditing.setVisible(false);
		

	}
	
	//addsLine numbers to the text areas using RichTextFX
	//optionally adds arrow to the current cursor location
	@FXML
	public void addLineNo(StyleClassedTextArea textArea) {
        IntFunction<Node> numberFactory = LineNumberFactory.get(textArea);
        //IntFunction<Node> arrowFactory = new ArrowFactory(codeArea.currentParagraphProperty());
        IntFunction<Node> graphicFactory = line -> {
            HBox hbox = new HBox( 
                    numberFactory.apply(line));
            //      arrowFactory.apply(line); 
            hbox.setAlignment(Pos.CENTER_LEFT);
			return hbox;
        };
        
        textArea.setParagraphGraphicFactory(graphicFactory);
        textArea.moveTo(0, 0);
	}
	
	//Adds tags to TagList
	@FXML
	public void addTag() {
		String tag = addTagField.getText();
		addTagField.clear();
		if (tag.length() > 0 && !tagSet.contains(tag)) {
			if (notagAdd) {
				tagSet.clear();
				notagAdd = false;
			}
			tagSet.add(tag);
			/*tagSet.forEach(x->{
				System.out.println(x);
			}); */
		}
	}
	
	//Removes tags in the add/tab
	@FXML
	public void removeTag() {
		tagSet.remove(addList.getSelectionModel().getSelectedItem());
		if (tagSet.size() == 0) {
			tagSet.add("");
			notagAdd=true;
		}
	}
	
	@FXML
	public void newEntry() {
		String name = addNameField.getText(); //Check to make sure no entries are empty
		String content = addText.getText();
		if (name.length() == 0 || content.length() == 0) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Invalid Entry");
			alert.setContentText("One or more entries has no content");
			alert.show(); 
			return;
		}
		
		System.out.println(content); 
		driver.insert(name, content); //Add note entry and tag relationships to database
		driver.addTags(name, tagSet);
		
		
	}
	
	@FXML
	public void searchEntry() { //retrieve ObservableList from Database driver object
		String searchTitle = searchTagField.getText();
		selectionSet = driver.selections(searchTitle);
		searchList.setItems(selectionSet);
		
		
		if (selectionSet.size() == 1) { //If there is only one result append the contents of the note to the textARea
			currentNote = selectionSet.get(0);
			noteText.replaceText(0, noteText.getLength(), driver.loadContent(currentNote));
			noteLabel.setText(currentNote);
		}
		
		
		
	}
	 
	@FXML
	public void editMode() { //Switch from display mode to edit mode
		noteText.setEditable(true);
		saveEditing.setVisible(true);
		setEditing.setVisible(false);
		noteText.setStyle("-fx-background-color: #555");
		noteText.setStyle("-fx-text-fill: #999");
	}
	
	@FXML
	public void saveEdit() { //Switch from edit mode to display mode, save changes
		noteText.setEditable(false);
		saveEditing.setVisible(false);
		setEditing.setVisible(true);
		noteText.setStyle("-fx-background-color: #444");
		
		String content = noteText.getText();
		if (content.length() == 0) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Invalid Entry");
			alert.setContentText("One or more entries has no content");
			alert.show(); 
				return;
		}
			
		System.out.println(content);
		driver.editNote(currentNote, content);
					
	}
}
	
	
		

