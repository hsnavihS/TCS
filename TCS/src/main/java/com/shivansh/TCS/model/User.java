package com.shivansh.TCS.model;

import jakarta.persistence.*;
import java.util.List;

import com.shivansh.TCS.enums.UserRole;

@Entity
@Table(name = "users")
public class User {

    // attributes: id (Long), name (String), email (String), password (String), role
    // (UserRole), events (many to many: List<Event>)

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private Long phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToMany
    @JoinTable(name = "event_user", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<Event> events;

    // constructors

    public User() {
    }

    public User(String name, String email, String password, UserRole role, Long phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPhone() {
        return phone;
    }

    public Long setPhone(Long phone) {
        return this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    @PreRemove
    private void removeUserFromEvents() {
        for (Event event : events) {
            event.getPeople().remove(this);
        }
    }
}