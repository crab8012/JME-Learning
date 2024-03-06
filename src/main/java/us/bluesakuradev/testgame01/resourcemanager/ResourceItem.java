package us.bluesakuradev.testgame01.resourcemanager;

import java.util.Formatter;

/**
 * A representation of every available resource in the game.
 * Contains an id, path, name, and resource type
 */
public class ResourceItem {
     private int id;
     private String path;
     private String name;
     private ResourceManager.RESOURCE_TYPE resourceType;

     public ResourceItem(int id, String path){
         this.id = id;
         this.path = path;
         this.name = path;
         this.resourceType = ResourceManager.RESOURCE_TYPE.UNKNOWN;
     }

     public ResourceItem(int id, String path, String name){
         this.id = id;
         this.path = path;
         this.name = name;
         this.resourceType = ResourceManager.RESOURCE_TYPE.UNKNOWN;
     }
    public ResourceItem(int id, String path, String name, ResourceManager.RESOURCE_TYPE type){
        this.id = id;
        this.path = path;
        this.name = name;
        this.resourceType = type;
    }

     public int getId(){
         return this.id;
     }
     public String getPath(){
         return this.path;
     }
     public String getName(){
         return this.name;
     }
     public ResourceManager.RESOURCE_TYPE getResourceType(){
         return this.resourceType;
     }
}
