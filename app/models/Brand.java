package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;



@Entity
@Table(name = "brands")
public class Brand {
	
  @Id	
  public int id;
  public String name;
  public String logo;
  public String description;

  public static Finder<Long,Brand> find = new Finder<Long,Brand>(
  	    Long.class, Brand.class);
}
