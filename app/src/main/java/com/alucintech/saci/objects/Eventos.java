package com.alucintech.saci.objects;

public class Eventos {

    int idEvento;
    String nombreEvento;
    String cicloEscolarEvento;
    String fechaInicioEvento;
    String fechaFinEvento;
    String descripcionEvento;
    String estadoEvento;
    int numEmpleadoAdministrador;
    String imagenEvento;

    public Eventos(int idEvento, String nombreEvento, String cicloEscolarEvento, String fechaInicioEvento, String fechaFinEvento,
                   String descripcionEvento, String estadoEvento, int numEmpleadoAdministrador, String imagenEvento) {
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.cicloEscolarEvento = cicloEscolarEvento;
        this.fechaInicioEvento = fechaInicioEvento;
        this.fechaFinEvento = fechaFinEvento;
        this.descripcionEvento = descripcionEvento;
        this.estadoEvento = estadoEvento;
        this.numEmpleadoAdministrador = numEmpleadoAdministrador;
        this.imagenEvento = imagenEvento;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public String getCicloEscolarEvento() {
        return cicloEscolarEvento;
    }

    public String getFechaInicioEvento() {
        return fechaInicioEvento;
    }

    public String getFechaFinEvento() {
        return fechaFinEvento;
    }

    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    public String getEstadoEvento() {
        return estadoEvento;
    }

    public int getNumEmpleadoAdministrador() {
        return numEmpleadoAdministrador;
    }

    public String getImagenEvento() {
        return imagenEvento;
    }
}
