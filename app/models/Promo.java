package models;

import java.math.BigDecimal;

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
@Table(name = "promos")
public class Promo{
	
	
	public int id;
	public short status;
	@Id
	public String code;
	public BigDecimal threshold;
	public int percent_discount;
	public int flat_discount;
	public long num_uses;
    
    public static Finder<Long,Promo> find = new Finder<Long,Promo>(
    	    Long.class, Promo.class);
}
