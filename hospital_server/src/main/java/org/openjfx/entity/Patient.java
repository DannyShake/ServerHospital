package org.openjfx.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class Patient implements Serializable {
    private int idPatient;
    private String name;
    private String gender;
    private String age;

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "№ пациента: " + idPatient + " " +
                "| Имя и фамилия : " + name  + " " +
                "| Возраст " + age  + " " +
                "| Пол " + gender;
    }
}
