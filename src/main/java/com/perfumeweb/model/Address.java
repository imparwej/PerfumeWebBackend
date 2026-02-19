package com.perfumeweb.model;

import jakarta.persistence.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // address type
    @Column(nullable = false)
    private String type; // HOME | WORK | OTHER

    // receiver info
    private String name;
    private String phone;
    private String altPhone;
    private String landmark;

    // location
    private String line;
    private String city;
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // getters + setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAltPhone() { return altPhone; }
    public void setAltPhone(String altPhone) { this.altPhone = altPhone; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getLine() { return line; }
    public void setLine(String line) { this.line = line; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
