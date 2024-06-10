package com.alucintech.saci.objects;

public class Carnet {

    int numFolio;
    int numeroSellosCarnet;
    String cicloEscolarCarnet;
    String fechaCreacionCarnet;
    int matriculaAlumno;
    String estadoCarnet;
    int claveCarnet;

    public Carnet(int numFolio, int numeroSellosCarnet, String cicloEscolarCarnet, String fechaCreacionCarnet, int matriculaAlumno, String estadoCarnet, int claveCarnet) {
        this.numFolio = numFolio;
        this.numeroSellosCarnet = numeroSellosCarnet;
        this.cicloEscolarCarnet = cicloEscolarCarnet;
        this.fechaCreacionCarnet = fechaCreacionCarnet;
        this.matriculaAlumno = matriculaAlumno;
        this.estadoCarnet = estadoCarnet;
        this.claveCarnet = claveCarnet;
    }

    public int getNumFolio() {
        return numFolio;
    }

    public int getNumeroSellosCarnet() {
        return numeroSellosCarnet;
    }

    public String getCicloEscolarCarnet() {
        return cicloEscolarCarnet;
    }

    public String getFechaCreacionCarnet() {
        return fechaCreacionCarnet;
    }

    public int getMatriculaAlumno() {
        return matriculaAlumno;
    }

    public String getEstadoCarnet() {
        return estadoCarnet;
    }

    public int getClaveCarnet() {
        return claveCarnet;
    }

    public void setNumFolio(int numFolio) {
        this.numFolio = numFolio;
    }

    public void setNumeroSellosCarnet(int numeroSellosCarnet) {this.numeroSellosCarnet = numeroSellosCarnet;}

    public void setCicloEscolarCarnet(String cicloEscolarCarnet) {this.cicloEscolarCarnet = cicloEscolarCarnet;}

    public void setFechaCreacionCarnet(String fechaCreacionCarnet) {this.fechaCreacionCarnet = fechaCreacionCarnet;}

    public void setMatriculaAlumno(int matriculaAlumno) {
        this.matriculaAlumno = matriculaAlumno;
    }

    public void setEstadoCarnet(String estadoCarnet) {
        this.estadoCarnet = estadoCarnet;
    }

    public void setClaveCarnet(int claveCarnet) {
        this.claveCarnet = claveCarnet;
    }
}
