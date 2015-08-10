package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This declares a model object for persistence usage. Model objects are generally anaemic structures that represent
 * the database entity. Behaviour associated with instances of a model class are also captured, but behaviours
 * associated with collections of these model objects belong to the PersonRepository e.g. findOne, findAll etc.
 * Play Java will synthesise getter and setter methods for us and therefore keep JPA happy (JPA expects them).
 */
@Entity
public class ActivityLogModel{
    public int machineId;
    public String entryType;
    public String event;
    public int productSku;
    public boolean jammed;
    public int traffic;
    public int salesId;
    public int salesSku;
    public String salesPrice;
    public Date date;
}
