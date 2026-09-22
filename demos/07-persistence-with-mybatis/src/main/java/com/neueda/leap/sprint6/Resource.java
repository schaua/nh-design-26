package com.neueda.leap.sprint6;

// A plain result-mapping record - MyBatis populates this by matching column
// names (or an explicit mapping) to these fields. No JPA annotations, no
// entity lifecycle, no persistence context - just a query result, mapped.
public record Resource(String resourceId, String title, String mediaId, String authorId, boolean available) {}

