package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import models.Brands;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity
public class Products{
	
	@Id
    public int item_sku;
    public String item_name;
    public String subtitle;
    public String category;
    public String item_img;
    public String detail_img;
    public String thumbnail;
    public String price;
    public String item_description;
    public String package_type;
    public Brands brand;
    
    public static Finder<Long,Products> find = new Finder<Long,Products>(
    	    Long.class, Products.class);
}
