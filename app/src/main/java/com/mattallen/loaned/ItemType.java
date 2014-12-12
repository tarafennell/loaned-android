package com.mattallen.loaned;

/**
 * Created by Tara on 12/12/2014.
 */
public class ItemType {

    private int itemTypeID,iconId;
    private String name;

    /**
     *
     * @param itemTypeID
     * @param name
     * @param iconId
     */
    public ItemType(int itemTypeID, String name, int iconId){
        this.itemTypeID = itemTypeID;
        this.name = name;
        this.iconId = iconId;
    }

    /**
     * gets the icons resource id
     * @return
     */
    public int getIconId() {
        return iconId;
    }

    /**
     * sets the icons resource id
     * @param iconId
     */
    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    /**
     * gets Types Name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * sets the Types Name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getItemTypeID() {
        return itemTypeID;
    }
}