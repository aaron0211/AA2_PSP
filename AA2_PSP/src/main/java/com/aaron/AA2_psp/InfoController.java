package com.aaron.AA2_psp;

import com.aaron.AA2_psp.domain.Country;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoController implements Initializable {

    public WebView wvImagen = new WebView();
    public Label lbName, lbCapital, lbRegion, lbPopulation;

    private Country country;

    public InfoController(Country country){
        this.country = country;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbName.setText(country.getName());
        lbCapital.setText(country.getCapital());
        lbRegion.setText(country.getRegion());
        lbPopulation.setText(String.valueOf(country.getPopulation()));
        wvImagen.getEngine().load(country.getFlag());
        wvImagen.setFontScale(0.1);
    }
}
