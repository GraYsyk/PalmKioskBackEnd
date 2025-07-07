package com.varnix.PalmKioskBack.Dtos;

public class CommentDTO {
    private Long id;
    private String text;
    private Float rating;

    private Long userId;
    private String username;

    private Long itemId;

    public CommentDTO() {}

    public CommentDTO(Long id, String text,Float rating,Long userId, String username, Long itemId) {
        this.id = id;
        this.text = text;
        this.rating = rating;
        this.userId = userId;
        this.username = username;
        this.itemId = itemId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
