package ru.paracells.natlex.models;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Job {

    @Id
    private Long id;


    private String jobstate;

    private String jobname;

    public Job() {
    }

    public String getJobstate() {
        return jobstate;
    }

    public void setJobstate(String jobstate) {
        this.jobstate = jobstate;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
