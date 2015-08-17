package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity
public class Containers {
    public int id;
    public int machine_id;
    public int position;
    public int num_items;
    public int total_capacity;
    public int item_sku;
    public Products product;
    public String status;
    public int slot;

    public static Finder<Long,Containers> find = new Finder<Long,Containers>(
    	    Long.class, Containers.class);
}
