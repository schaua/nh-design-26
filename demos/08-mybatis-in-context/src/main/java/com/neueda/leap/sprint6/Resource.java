package com.neueda.leap.sprint6;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// The JPA equivalent of Module 7's plain Resource class - same four
// columns, but this one IS the persistence model, not just a query
// result shape. @Entity puts it under Hibernate's management: once
// loaded, this object is tracked, and changes to it get written back
// on flush - MyBatis's Resource had none of that.
@Entity
@Table(name = "resources")
public class Resource {

    // JPA has logic of automatically mapping fields with camelCase names to snake_case column names,
    // so we only need @Column when the names differ, like for resourceId, mediaId, and authorId.
    @Id
    @Column(name = "resourceid")
    private String resourceId;

    private String title;

    @Column(name = "mediaid")
    private String mediaId;

    @Column(name = "authorid")
    private String authorId;

    private boolean available;

    protected Resource() {
        // JPA requires a no-args constructor to build entities via reflection
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public boolean isAvailable() {
        return available;
    }
}
