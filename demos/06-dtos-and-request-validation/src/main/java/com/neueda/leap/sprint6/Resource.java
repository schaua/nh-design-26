package com.neueda.leap.sprint6;

public class Resource {
    private String iSBN;
    private ResourceType type;
    boolean available;

    public Resource(String iSBN, ResourceType type) {
        this.iSBN = iSBN;
        this.type = type;
        this.available = true;
    }

    public String getISBN() {
        return iSBN;
    }

    public ResourceType getType() {
        return type;
    }

    public void loanResource() {
        this.available = false;
    }

    public void returnResource() {
        this.available = true;
    }

    public boolean isAvailable() {
        return available;
    }
}
