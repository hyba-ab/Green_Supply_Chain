package entities;


 // Base class for entities in the supply chain.
 
public class SupplyChainEntity {
    private String entityID;
    private String name;
    private String location;

    public SupplyChainEntity(String entityID, String name, String location) {
        this.entityID = entityID;
        this.name = name;
        this.location = location;
    }

    
    //Returns the entity ID
     
    public String getEntityID() {
        return entityID;
    }

    
     //Returns a string with details about the entity.
    
    public String getDetails() {
        return "Entity ID: " + entityID + ", Name: " + name + ", Location: " + location;
    }
}