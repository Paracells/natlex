package ru.paracells.natlex.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "geoclasses")
public class GeologicalClass implements Serializable {


    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;


   /* @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;*/


    public GeologicalClass() {
    }

    /*  public Section getSection() {
          return section;
      }

      public void setSection(Section section) {
          this.section = section;
      }
  */

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
}
