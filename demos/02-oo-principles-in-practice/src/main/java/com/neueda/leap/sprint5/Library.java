package com.neueda.leap.sprint5;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private final List<LibraryResource> resources = new ArrayList<>();

    public void addResource(LibraryResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }

        resources.add(resource);
    }

    public LibraryResource findById(String id) {
        for (LibraryResource resource : resources) {
            if (resource.getId().equals(id)) {
                return resource;
            }
        }

        return null;
    }

    public List<LibraryResource> getAvailableResources() {
        List<LibraryResource> available = new ArrayList<>();

        for (LibraryResource resource : resources) {
            if (!resource.isLoaned()) {
                available.add(resource);
            }
        }

        return List.copyOf(available);
    }
}
