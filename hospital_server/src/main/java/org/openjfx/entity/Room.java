package org.openjfx.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class Room implements Serializable {
    static final long SerialVersionUID = -4862926644813433707L;
    private int roomNumber;
    private String procedureName;
    private String patientName;



    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "№ кабинета: " + roomNumber + " " +
                "| Название: " + procedureName + " " +
                "| Имя:" + patientName;
    }
}
