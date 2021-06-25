package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField id;
	@FXML
	private TextField name;
	@FXML
	private Button save;
	@FXML
	private TextField email;
	@FXML
	private DatePicker birthDate;
	@FXML
	private TextField baseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Button cancel;
	@FXML
	private Label error;
	@FXML
	private Label errorEmail;
	@FXML
	private Label errorBirthDate;
	@FXML
	private Label errorBaseSalary;

	private ObservableList<Department> obsList;

	@FXML
	public void onSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangedListeners();
			Utils.currentStage(event).close();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro saving object!", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangedListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Seller getFormData() {
		Seller obj = new Seller();
		ValidationException except = new ValidationException("Validation error");
		obj.setId(Utils.tryParseToInt(id.getText()));
		if (name.getText() == null || name.getText().trim().equals("")) {
			except.addError("name", "Field can't be empty");
		}
		obj.setName(name.getText());
		
		if (email.getText() == null || email.getText().trim().equals("")) {
			except.addError("email", "Field can't be empty");
		}
		obj.setEmail(email.getText());
		
		if(birthDate.getValue() == null) {
			except.addError("birthDate", "Field can't be empty");
		}else {
		Instant instant = Instant.from(birthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
		obj.setBirthDate(Date.from(instant));
		}
		
		if (baseSalary.getText() == null || baseSalary.getText().trim().equals("")) {
			except.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(baseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());
			
		if (except.getErrors().size() > 0) {
			throw except;
		}
		return obj;
	}

	@FXML
	public void onCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentServices) {
		this.service = service;
		this.departmentService = departmentServices;
	}

	public void subscribeDataChangeListener(DataChangeListener listenter) {
		dataChangeListeners.add(listenter);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Constraints.setTextFieldInteger(id);
		Constraints.setTextFieldMaxLength(name, 70);
		Constraints.setTextFieldDouble(baseSalary);
		Constraints.setTextFieldMaxLength(email, 70);
		Utils.formatDatePicker(birthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		id.setText(String.valueOf(entity.getId()));
		name.setText(entity.getName());
		email.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		baseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			birthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociateObjects() {

		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		error.setText((fields.contains("name") ? errors.get("name") : ""));
		
		errorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
	
		errorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
		
		errorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
			
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
