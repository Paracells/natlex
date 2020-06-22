package ru.paracells.natlex.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
public class Section implements Serializable {

    @Id
    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "geo_section", referencedColumnName = "name", nullable = false, insertable = false, updatable = false)
    private List<GeologicalClass> geologicalСlasses = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GeologicalClass> getGeologicalСlasses() {
        return geologicalСlasses;
    }

    public void setGeologicalСlasses(List<GeologicalClass> geologicalСlasses) {
        this.geologicalСlasses = geologicalСlasses;
    }
    @Override
    public String toString() {
        return "Section{" +
                " name = '" + name + '\'' +
                ", geologicalClasses = " + geologicalСlasses.toString() +
                '}';
    }
}
