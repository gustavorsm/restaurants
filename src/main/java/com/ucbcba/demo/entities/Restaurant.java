package com.ucbcba.demo.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Integer likes = 0;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @NotNull
    private Float latitude;

    @NotNull
    private Float longitude;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @ManyToOne()
    @JoinColumn(name = "nivelRestaurante_id")
    private NivelRestaurante nivelRestaurante;


    @ManyToMany
    @JoinTable(name = "restaurant_category", joinColumns = @JoinColumn(name = "restaurant_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="category_id", referencedColumnName = "id"))
    private Set<Category> categories;

    @OneToMany(mappedBy = "restaurant",cascade = CascadeType.ALL)
    private List<Photo> photo;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Integer getId() {

        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Photo> getPhoto() {
        return photo;
    }

    public void setPhoto(List<Photo> photos) {
        this.photo = photos;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public NivelRestaurante getNivelRestaurante() {
        return nivelRestaurante;
    }

    public void setNivelRestaurante(NivelRestaurante nivelRestaurante) {
        this.nivelRestaurante = nivelRestaurante;
    }
}
