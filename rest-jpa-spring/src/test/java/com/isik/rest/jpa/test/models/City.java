package com.isik.rest.jpa.test.models;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class City extends BaseEntity {

    @Column
    private String name;

    @Column
    private String code;

    public City() {
    }

    public City(String id) {
        setId(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
