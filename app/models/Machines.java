package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.db.ebean.Model.Finder;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity
public class Machines{
	
	@Id
    public Long id;
    
    public Double lat;
    public Double lon;
    
    public String address;
    public String status;
    
    public int total_capacity;
    public int num_items;
    
    public List<Containers> containers;
    
    public static Finder<Long,Machines> find = new Finder<Long,Machines>(
    	    Long.class, Machines.class);
}
