package models;

import java.sql.Timestamp;

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
@Table(name = "events")
public class Event{
	
	@Id
    public long id;
	public long machine_id;
	public String event;
	public Integer product_sku;
	public Timestamp time;
    
    public static Finder<Long,Event> find = new Finder<Long,Event>(
    	    Long.class, Event.class);
}
