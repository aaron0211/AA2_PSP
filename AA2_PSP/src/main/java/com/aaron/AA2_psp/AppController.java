package com.aaron.AA2_psp;

import com.aaron.AA2_psp.domain.Country;
import com.aaron.AA2_psp.service.CountrieService;
import com.aaron.AA2_psp.util.AlertUtils;
import com.aaron.AA2_psp.util.R;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class AppController implements Initializable {

    public TableView tvList, tvFav;
    public Button btSearch, btAll, btAdd, btInfo, btExport, btExpZip;
    public TextField tfSearch;
    public ProgressIndicator piProgress;

    private ObservableList<Country> lstCountries;
    private ObservableList<Country> lstFav;

    private enum Accion{
        ALL,SPANISH
    }

    private Accion accion;

    CountrieService countrieService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countrieService = new CountrieService();
        accion = Accion.SPANISH;
        tfSearch.setPromptText("Buscar pais");
        piProgress.setProgress(-1);

        lstCountries = FXCollections.observableArrayList();
        tvList.setItems(lstCountries);

        lstFav = FXCollections.observableArrayList();
        tvFav.setItems(lstFav);

        fijarColumnas();
        cargarLista();
    }

    private void cargarLista(){
        tvList.getItems().clear();

        countrieService.getAll()
                .flatMap(Observable::from)
                .doOnCompleted(()-> {
                    System.out.println("Listado descargado");
                    piProgress.setVisible(false);
                })
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(country -> lstCountries.add(country));
    }

    private void fijarColumnas(){
        Field[] fields = Country.class.getDeclaredFields();
        for (Field field: fields){
            if (field.getName().equals("flag")) continue;
            TableColumn<Country,String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvList.getColumns().add(column);
        }
        tvList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Field[] fieldes = Country.class.getDeclaredFields();
        for (Field field: fieldes){
            if (field.getName().equals("subregion") || field.getName().equals("population") ||field.getName().equals("flag")) continue;
            TableColumn<Country,String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvFav.getColumns().add(column);
        }
        tvFav.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void addFav(ActionEvent event){
        Country country = (Country) tvList.getSelectionModel().getSelectedItem();
        if (country==null){
            AlertUtils.mostrarAlerta("Selecciona un pais de la lista");
            return;
        }
        lstFav.add(country);
    }

    @FXML
    private void getAll(ActionEvent event){
        piProgress.setVisible(true);
        switch (accion){
            case ALL:
                tvList.getItems().clear();
                countrieService.getAll()
                        .flatMap(Observable::from)
                        .doOnCompleted(()-> {
                            System.out.println("Listado descargado");
                            piProgress.setVisible(false);
                        })
                        .doOnError(throwable -> System.out.println(throwable.getMessage()))
                        .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                        .subscribe(country -> lstCountries.add(country));
                accion = Accion.SPANISH;
                btAll.setText("Buscar idioma Español");
                break;
            case SPANISH:
                tvList.getItems().clear();
                countrieService.getSpanish()
                        .flatMap(Observable::from)
                        .doOnCompleted(()-> {
                            System.out.println("Listado descargado");
                            piProgress.setVisible(false);
                        })
                        .doOnError(throwable -> System.out.println(throwable.getMessage()))
                        .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                        .subscribe(country -> lstCountries.add(country));
                accion = Accion.ALL;
                btAll.setText("Buscar todos");
                break;
        }
    }

    @FXML
    private void search(ActionEvent event){
        piProgress.setVisible(true);
        String search = tfSearch.getText();
        tvList.getItems().clear();
        countrieService.getAll()
                .flatMap(Observable::from)
                .filter(country -> country.getName().startsWith(search))
                .doOnCompleted(()-> {
                    System.out.println("Listado descargado");
                    piProgress.setVisible(false);
                })
                .doOnError(throwable -> System.out.println(throwable.getMessage()))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(country -> lstCountries.add(country));

        tfSearch.setText("");
    }

    @FXML
    private void info(ActionEvent event) throws IOException {
        Country country = (Country) tvList.getSelectionModel().getSelectedItem();
        if (country == null){
            AlertUtils.mostrarAlerta("Tienes que seleccionar un pais de la lista");
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        InfoController controller = new InfoController(country);
        loader.setLocation(R.getUI("panelInfo.fxml"));
        loader.setController(controller);
        VBox vBox = loader.load();

        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void export(ActionEvent event){
        try{
            FileChooser fileChooser = new FileChooser();
            File fichero = fileChooser.showSaveDialog(btExport.getScene().getWindow());
            if (fichero == null) return;

            FileWriter fileWriter = new FileWriter(fichero);
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            List<Country> countryList = tvFav.getItems();
            for (Country country: countryList){
                printer.printRecord(country.getName(),country.getCapital(),
                        country.getRegion(),country.getSubregion(),
                        country.getPopulation(),country.getFlag());
            }
            printer.close();
        }catch (IOException ioe){
            AlertUtils.mostrarAlerta("Error al exportar los datos");
        }
        AlertUtils.mostrarInfo("Archivo exportado con exito");
    }

    @FXML
    private void expzip(ActionEvent event){

    }
}