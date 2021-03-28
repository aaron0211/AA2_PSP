package com.aaron.AA2_psp;

import com.aaron.AA2_psp.domain.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class WebClientController implements Initializable {

    public TableView tvWebclient;
    public Button   btConnect;

    private WebClient webClient;
    private ObservableList<Country> lstWeb;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        webClient = WebClient.create("http://localhost:8080");
        lstWeb = FXCollections.observableArrayList();
        tvWebclient.setItems(lstWeb);

        fijarColumnas();
    }

    @FXML
    public void connect(ActionEvent event){
        Flux<Country> countryFlux = webClient.get()
                .uri("/countries")
                .retrieve()
                .bodyToFlux(Country.class);

        countryFlux.doOnError((System.out::println))
                .subscribeOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
                .doOnComplete(()-> System.out.println("Terminado"))
                .subscribe((country)-> lstWeb.add(country));
    }

    private void fijarColumnas(){
        Field[] fields = Country.class.getDeclaredFields();
        for (Field field: fields){
            if (field.getName().equals("capital") || field.getName().equals("subregion") || field.getName().equals("flag")) continue;
            TableColumn<Country,String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvWebclient.getColumns().add(column);
        }
        tvWebclient.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
