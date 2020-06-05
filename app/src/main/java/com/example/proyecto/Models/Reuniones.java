package com.example.proyecto.Models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reuniones {
    private String ID;
    private String asignaturas;
    private String fecha;
    private String hora;
    private String grupo;

    public Reuniones(String ID, String asignaturas, String fecha, String hora, String grupo) {
        this.ID = ID;
        this.asignaturas = asignaturas;
        this.fecha = fecha;
        this.hora = hora;
        this.grupo = grupo;
    }

    public Reuniones(){

    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(String asignaturas) {
        this.asignaturas = asignaturas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}