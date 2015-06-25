package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;



@Entity
public class Brand {
  public int id;
  public String name;
  public String logo;
  public String description;
}
