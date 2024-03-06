package us.bluesakuradev.testgame01.resourcemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ResourceManager {
    // This is the base of the resource manager. It contains common base methods that
    // are in charge of reading through asset directories and making connections to
    // repeatedly and reliably load and provide access to all sorts of assets.

    ArrayList<ResourceItem> resources = new ArrayList<>();
    HashMap<String, RESOURCE_TYPE> resourceMap = new HashMap<>();

    /**
     * Load in resource items from a directory in the filesystem.
     * @param baseFolder The directory to start loading items in from.
     */
    public void loadResourcesFromFilesystem(String baseFolder){

    }

    /**
     * Loads the resource map from an external file.
     * @param resourceTypeMap The json-formatted file containing the resource type map.
     */
    public void loadResourceTypeMap(String resourceTypeMap){

    }

    /**
     * Load in previously loaded resource items.
     * Called by loadResourcesFromFilesystem using a default listFile to facilitate reliable resource management.
     * @param listFile The json-formatted list file containing information about previously loaded resources.
     */
    public void loadResourcesFromResourceList(String listFile){

    }

    /**
     * Save the currently loaded resources to a file.
     * @param listFile The path to a new json-formatted resource list file.
     */
    public void saveResourcesToResourceList(String listFile){

    }

    /**
     * Find an already-loaded resource by name.
     * @param name The name of the resource to locate.
     * @return The ResourceItem representation, or null if not loaded.
     */
    public ResourceItem findLoadedResourceByName(String name){
        for(ResourceItem i : resources){
            if(i.getName().equals(name)){
                return i;
            }
        }
        return null;
    }

    /**
     * Find an already-loaded resource by id.
     * @param id The id of the resource to locate.
     * @return The ResourceItem representation, or null if not loaded
     */
    public ResourceItem findLoadedResourceById(int id){
        for(ResourceItem i : resources){
            if(i.getId() == id){
                return i;
            }
        }
        return null;
    }

    /**
     * Return a list of loaded resources based on the requested resource type
     * @param type The type of resource to list.
     * @return An ArrayList of resource items matching the requested type.
     */
    public ArrayList<ResourceItem> listLoadedResourceByType(RESOURCE_TYPE type){
        ArrayList<ResourceItem> filteredResourceList = new ArrayList<>();
        for(ResourceItem i : resources){
            if(i.getResourceType().equals(type)){
                filteredResourceList.add(i);
            }
        }

        return filteredResourceList;
    }

    enum RESOURCE_TYPE{
        MUSIC,
        SFX,
        VIDEO,
        MODEL,
        TEXTURE,
        UNKNOWN
    }
}
