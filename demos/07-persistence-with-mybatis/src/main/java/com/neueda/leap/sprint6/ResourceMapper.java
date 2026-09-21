package com.neueda.leap.sprint6;

import java.util.List;

import org.apache.ibatis.annotations.Select;

// Annotation-based configuration: the SQL lives right next to the method it
// belongs to, in the Java file. Good for short, simple queries - there's
// nowhere else to look.
public interface ResourceMapper {

    // #{resourceId} is a PARAMETERISED placeholder, not string concatenation -
    // MyBatis turns this into a JDBC PreparedStatement with a bound
    // parameter, the same protection against SQL injection Sprint 3 covered
    // for raw JDBC/SQL.
    @Select("SELECT resourceId, title, authorId, mediaId, available FROM resources WHERE resourceId = #{resourceId}")
    Resource findByResourceId(String resourceId);

    @Select("SELECT resourceId, title, authorId, mediaId, available FROM resources")
    List<Resource> findAll();
}
