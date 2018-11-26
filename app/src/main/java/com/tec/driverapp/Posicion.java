package com.tec.driverapp;

/**
 * Clase utilizada para manejar la posición de los marcadores
 */
public class Posicion {
    private double lat;
    private double lon;

    public Posicion(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
