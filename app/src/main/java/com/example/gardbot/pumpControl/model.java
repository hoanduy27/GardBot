package com.example.gardbot.pumpControl;

public class model {
    String name, soilMoistureID;

    model()
    {
    }

    public model(String name, String soilMoistureID) {
        this.name = name;
        this.soilMoistureID = soilMoistureID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoilMoistureID() {
        return soilMoistureID;
    }

    public void setSoilMoistureID(String soilMoistureID) {
        this.soilMoistureID = soilMoistureID;
    }
}
