package edu.ucsb.cs156.team02.entities;


import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsb_requirements")
public class UCSBRequirement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // This establishes that many todos can belong to one user
  // Only the user_id is stored in the table, and through it we
  // can access the user's details

  private int courseCount;
  private int unit;
  private boolean inactive;
  private String requirementCode;
  private String requirementTranslation;
  private String collegeCode;
  private String objCode;

  /* public UCSBRequirement(long id, int cc, int unit, boolean inactive, String rc, String rt, String collegeCode, String oc)
  {
    this.id = id;
    this.courseCount = cc;
    this.unit = unit;
    this.inactive = inactive.
    this.requirementCode = rc;
    this.requirementTranslation = rt;
    this.collegeCode = collegeCode;
    this.objCode = oc;
  } */
}