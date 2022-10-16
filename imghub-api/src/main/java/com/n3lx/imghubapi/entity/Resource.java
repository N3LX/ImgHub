package com.n3lx.imghubapi.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private ZonedDateTime uploadTime;

    private TimeZone uploadTimeZone;

    @Column(unique = true)
    private String resourceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ZonedDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(ZonedDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public TimeZone getUploadTimeZone() {
        return uploadTimeZone;
    }

    public void setUploadTimeZone(TimeZone uploadTimeZone) {
        this.uploadTimeZone = uploadTimeZone;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

}
