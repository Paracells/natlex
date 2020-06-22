package ru.paracells.natlex.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "geologicalclasses")
public class GeologicalClass implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;

    public GeologicalClass(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @JsonIgnore
    @Column(name = "geo_section")
    private String geo_section;

    public GeologicalClass() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGeo_section() {
        return geo_section;
    }

    public void setGeo_section(String geo_section) {
        this.geo_section = geo_section;
    }

    @Override
    public String toString() {
        return "GeologicalClass{ " +
                "name = '" + name + '\'' +
                ", code = '" + code + '\'' +
                '}';
    }
}
