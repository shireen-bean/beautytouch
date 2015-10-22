package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "ratings")
public class Rating{
	
	@Id
	public int id;
    public int item_sku;
    public long sales_id;
    public int rating;
    
    public static Finder<Long,Rating> find = new Finder<Long,Rating>(
    	    Long.class, Rating.class);
}
