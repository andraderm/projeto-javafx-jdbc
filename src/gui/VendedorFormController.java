package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class VendedorFormController implements Initializable{
	
	private Vendedor entity;
	private VendedorService service;
	private DepartamentoService departamentoService;
	
	private List<DataChangeListener> dataChangeListener = new ArrayList<>();

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpDataNascimento;
	
	@FXML
	private TextField txtSalario;
	
	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorDataNascimento;
	
	@FXML
	private Label labelErrorSalario;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	private ObservableList<Departamento> obsList;
	
	public void setVendedor(Vendedor entity) {
		this.entity = entity;
	}
	
	public void setServices(VendedorService service, DepartamentoService departamentoService) {
		this.service = service;
		this.departamentoService = departamentoService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		 for (DataChangeListener listener : dataChangeListener) {
			 listener.onDataChanged();
		 }
	}

	private Vendedor getFormData() {
		Vendedor obj = new Vendedor();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Field can't be empty");
		}
		obj.setNome(txtNome.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 100);
		Constraints.setTextFieldDouble(txtSalario);
		Constraints.setTextFieldMaxLength(txtEmail, 100);
		Utils.formatDatePicker(dpDataNascimento, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entity.getSalario()));
		if (entity.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(entity.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		}
		comboBoxDepartamento.setValue(entity.getDepartamento());
	}
	
	public void loadAssociatedObjects() {
		if (departamentoService == null) {
			throw new IllegalStateException("DepartamentoService was null");
		}
		List<Departamento> list = departamentoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("nome")) {
			labelErrorNome.setText(errors.get("nome"));
		}
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}

}
