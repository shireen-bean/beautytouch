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
@Table(name = "products")
public class Product{
	
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
    public Integer brand_id;
    public Brand brand;
    
    public static Finder<Long,Product> find = new Finder<Long,Product>(
    	    Long.class, Product.class);
}
