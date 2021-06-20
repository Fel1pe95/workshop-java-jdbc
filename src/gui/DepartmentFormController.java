package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); 
	
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
	public void onSaveAction(ActionEvent event) {
		if(entity ==null) {
			throw new IllegalStateException("Entity was null!");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangedListeners();
			Utils.currentStage(event).close();
			
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Erro saving object!", null, e.getMessage(), AlertType.ERROR);
		}
		
	}

	private void notifyDataChangedListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	private Department getFormData() {
		Department obj = new Department();
		ValidationException except = new ValidationException("Validation error");
		obj.setId(Utils.tryParseToInt(id.getText()));
		if(name.getText() == null || name.getText().trim().equals("")) {
	     except.addError("name", "Field can't be empty");
		}
		
		obj.setName(name.getText());
		  
		if(except.getErrors().size()>0) {
			throw except;
		}
		return obj;
	}

	@FXML
	public void onCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listenter) {
		dataChangeListeners.add(listenter);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Constraints.setTextFieldInteger(id);
		Constraints.setTextFieldMaxLength(name, 30);

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		id.setText(String.valueOf(entity.getId()));
		name.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if(fields.contains("name")) {
			error.setText(errors.get("name"));		}
	}
}
