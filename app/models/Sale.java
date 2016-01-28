package models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import models.Brand;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity

@Table(name = "sales")
public class Sale {

  @Id
  public long id;
  public long machine_id;
  public BigDecimal sales_total;
  public Timestamp time;
  public String promo_code;
  
  public static Finder<Long,Sale> find = new Finder<Long,Sale>(
      Long.class, Sale.class);
}
