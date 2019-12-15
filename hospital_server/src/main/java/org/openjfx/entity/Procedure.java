package org.openjfx.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class Procedure implements Serializable {
    private int idProcedur;
    private int cost;
    private String name;

    public int getIdProcedur() {
        return idProcedur;
    }

    public void setIdProcedur(int idProcedur) {
        this.idProcedur = idProcedur;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "№ процедуры: " + idProcedur + " " +
                "| Название: " + name  + " " +
                "| Цена: " + cost;
    }
}
