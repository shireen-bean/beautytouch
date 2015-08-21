package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model.Finder;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity

@Table(name = "sales_customer")
public class Customer {

  @Id
  public long sales_id;
  public String customer_email;
  public String customer_phone;
  
  public static Finder<Long,Customer> find = new Finder<Long,Customer>(
      Long.class, Customer.class);
}
