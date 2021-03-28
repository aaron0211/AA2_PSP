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

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppController implements Initializable {

    public TableView tvList, tvFav;
    public Button btSearch, btAll, btAdd, btInfo, btExport, btExpZip, btWeb;
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
        piProgress.setVisible(true);

        lstCountries = FXCollections.observableArrayList();
        tvList.setItems(lstCountries);

        lstFav = FXCollections.observableArrayList();
        tvFav.setItems(lstFav);

        fijarColumnas();
        cargarTodos();
    }

    private void cargarTodos(){
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

    private void cargarSpanish(){
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
    private void removeFav(ActionEvent event){
        lstFav.remove(tvFav.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void getAll(ActionEvent event){
        piProgress.setVisible(true);
        switch (accion){
            case ALL:
                piProgress.setVisible(true);
                cargarTodos();
                accion = Accion.SPANISH;
                btAll.setText("Buscar idioma Español");
                break;
            case SPANISH:
                piProgress.setVisible(true);
                cargarSpanish();
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
        if (exportar() != null) AlertUtils.mostrarInfo("Archivo exportado con exito");
    }

    @FXML
    private void expzip(ActionEvent event){
        CompletableFuture.supplyAsync(() -> exportar())
                .thenAccept(value -> zip(value));
    }

    @FXML
    private void webclient(ActionEvent event){
        FXMLLoader loader = new FXMLLoader();
        WebClientController controller = new WebClientController();
        loader.setLocation(R.getUI("panelWeb.fxml"));
        loader.setController(controller);
        VBox vBox = null;
        try {
            vBox = loader.load();
        } catch (IOException e) {
            AlertUtils.mostrarAlerta("No se ha podido conectar");
        }

        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    private File exportar(){
        File fichero = null;
        try{
            List<Country> countryList = tvFav.getItems();
            if (countryList.size()<1) {
                AlertUtils.mostrarAlerta("Ningun elemento en la tabla");
                return null;
            }

            FileChooser fileChooser = new FileChooser();
            fichero = fileChooser.showSaveDialog(btExport.getScene().getWindow());
            if (fichero == null) {
                AlertUtils.mostrarAlerta("Fallo al exportar");
                return null;
            }

            FileWriter fileWriter = new FileWriter(fichero);
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
            for (Country country: countryList){
                printer.printRecord(country.getName(),country.getCapital(),
                        country.getRegion(),country.getSubregion(),
                        country.getPopulation(),country.getFlag());
            }
            printer.close();
        }catch (IOException ioe){
            AlertUtils.mostrarAlerta("Error al exportar los datos");
        }
        return fichero;
    }

    private void zip(File fichero){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("Compressed.zip");
            ZipOutputStream zipOut = new ZipOutputStream(fileOutputStream);
            FileInputStream fileInputStream = new FileInputStream(fichero);
            ZipEntry zipEntry = new ZipEntry(fichero.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >=0){
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fileInputStream.close();
            fileOutputStream.close();
        }catch (IOException ioe){
            AlertUtils.mostrarAlerta("Fallo de compresion");
        }
    }
}
