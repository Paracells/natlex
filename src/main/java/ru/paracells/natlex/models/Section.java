package ru.paracells.natlex.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "section")
public class Section implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @Column(name = "jobid")
    private Integer jobid;

    @JsonIgnore
    @Column(name = "jobstate")
    private String jobstate;
/*
    // name - это как колонка будет выглядить в таблице
    // referencedColumnName - это ссылка на таблицу, с которой связываемся
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "jobstate")
    private Job job;*/

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id", referencedColumnName = "name")
    List<GeologicalClass> geoClasses = new ArrayList<>();

    public Section() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   /* public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }*/

    public List<GeologicalClass> getGeoClasses() {
        return geoClasses;
    }

    public void setGeoClasses(List<GeologicalClass> geoClasses) {
        this.geoClasses = geoClasses;
    }

    public Integer getJobid() {
        return jobid;
    }

    public void setJobid(Integer jobid) {
        this.jobid = jobid;
    }

    public String getJobstate() {
        return jobstate;
    }

    public void setJobstate(String jobstate) {
        this.jobstate = jobstate;
    }

    @Override
    public String toString() {
        return "Section{" +
                "name='" + name + '\'' +
                ", jobstate=" + jobstate +
                ", geoClasses=" + geoClasses +
                '}';
    }
}
