package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {

	@FXML
	private TextField id;
	@FXML
	private TextField name;
	@FXML
	private Button save;
	@FXML
	private Button cancel;
	@FXML
	private Label error;

	@FXML
	public void onSaveAction() {
		System.out.println("onSaveAction");
	}

	@FXML
	public void onCancelAction() {
		System.out.println("onCancelAction");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Constraints.setTextFieldInteger(id);
		Constraints.setTextFieldMaxLength(name, 30);

	}

}
