package com.alucintech.saci.objects;

public class Actividades {
    int id;
    String nombreActividad;
    String descripcionActividad;
    String tipoActividad;
    String fechaActividad;
    String horarioInicio;
    String horarioFin;
    String lugarActividad;
    int espaciosDisponibles;
    String modalidadActividad;
    String enlaceVirtual;
    String imagenActividad;
    String ponenteActividad;
    int idEvento;
    int numEmpleadoAdministrador;
    String estadoActividad;

    public Actividades(int id, String nombreActividad, String descripcionActividad, String tipoActividad, String fechaActividad, String horarioInicio,
                       String horarioFin, String lugarActividad, int espaciosDisponibles, String modalidadActividad, String enlaceVirtual,
                       String imagenActividad, String ponenteActividad, int idEvento, int numEmpleadoAdministrador, String estadoActividad) {
        this.id = id;
        this.nombreActividad = nombreActividad;
        this.descripcionActividad = descripcionActividad;
        this.tipoActividad = tipoActividad;
        this.fechaActividad = fechaActividad;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.lugarActividad = lugarActividad;
        this.espaciosDisponibles = espaciosDisponibles;
        this.modalidadActividad = modalidadActividad;
        this.enlaceVirtual = enlaceVirtual;
        this.imagenActividad = imagenActividad;
        this.ponenteActividad = ponenteActividad;
        this.idEvento = idEvento;
        this.numEmpleadoAdministrador = numEmpleadoAdministrador;
        this.estadoActividad = estadoActividad;
    }

    public int getId() {
        return id;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public String getDescripcionActividad() {
        return descripcionActividad;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public String getFechaActividad() {
        return fechaActividad;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public String getHorarioFin() {
        return horarioFin;
    }

    public String getLugarActividad() {
        return lugarActividad;
    }

    public int getEspaciosDisponibles() {
        return espaciosDisponibles;
    }

    public String getModalidadActividad() {
        return modalidadActividad;
    }

    public String getEnlaceVirtual() {
        return enlaceVirtual;
    }

    public  String getImagenActividad(){
        return imagenActividad;
    }

    public String getPonenteActividad(){
        return ponenteActividad;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public int getNumEmpleadoAdministrador() {
        return numEmpleadoAdministrador;
    }

    public String getEstadoActividad(){
        return  estadoActividad;
    }
}
