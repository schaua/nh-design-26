package com.neueda.leap.sprint6;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryResourcesRepository implements ResourcesRepository {

    private static final Map<String, Resource> Shelf = Map.of(
            "978-0-13-539857-9", new Resource("978-0-13-539857-9", ResourceType.BOOK)
    );

    @Override
    public Resource findByISBN(String iSBN) {
        // For demonstration purposes, we'll just return a fixed resource type.
        // In a real implementation, you would look up the resource by its ISBN.
        // 978-0-13-539857-9
        return Shelf.get(iSBN);
    }
}
