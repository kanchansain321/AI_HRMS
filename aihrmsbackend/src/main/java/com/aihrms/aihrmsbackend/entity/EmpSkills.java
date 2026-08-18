package com.aihrms.aihrmsbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name="skills")
public class EmpSkills {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "resume_id",nullable = false)
    private Integer resume;
    @Column(name = "skill_name",length = 100)
    private String skillName;


}
