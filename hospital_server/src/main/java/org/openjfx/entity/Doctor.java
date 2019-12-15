package org.openjfx.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class Doctor implements Serializable {
    private int idDoctor;
    private String name;
    private String specialization;

    public int getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(int idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "№ доктора: " + idDoctor + " " +
                "| Имя и фамилия: " + name  + " " +
                "| Сспециализация: " + specialization;
    }
}
