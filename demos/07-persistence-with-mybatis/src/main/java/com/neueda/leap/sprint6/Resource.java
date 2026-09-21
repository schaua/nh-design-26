package com.neueda.leap.sprint6;

// A plain result-mapping class - MyBatis populates this by matching column
// names (or an explicit mapping) to these fields. No JPA annotations, no
// entity lifecycle, no persistence context - just a query result, mapped.
public class Resource {
    private String resourceId;
    private String title;
    private String mediaId;
    private String authorId;
    private boolean available;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
