package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity
@Table(name = "hooks")
public class Hook {
    public int id;
    public Long machine_id;
    public Integer item_sku;
    public Product product;
    public String status;

    public static Finder<Long,Hook> find = new Finder<Long,Hook>(
    	    Long.class, Hook.class);
}
