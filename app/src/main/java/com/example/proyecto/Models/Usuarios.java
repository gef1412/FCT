package com.example.proyecto.Models;

import java.util.List;

//Esta clase servirá para obtener los datos de Firebase,
//Es fundamental que el nombre de los nodos y los atributos de la clase sean iguales
public class Usuarios {

    private String ID, nombre, apellido, edad, email, password, foto, type;
    private List<String> asignaturas;

    public Usuarios(String ID, String nombre, String apellido, String edad, String email, String password, String foto, String type, List<String> asignaturas) {
        this.ID = ID;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.password = password;
        this.foto = foto;
        this.type = type;
        this.asignaturas = asignaturas;
    }

    public Usuarios(){

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(List<String> asignaturas) {
        this.asignaturas = asignaturas;
    }
}
