package com.varnix.PalmKioskBack.Dtos;

public class CommentDTO {
    private Long id;
    private String text;
    private String date; // Лучше как ISO-строка (например: "2024-06-27T12:34:56")

    private Long userId;
    private String username;

    private Long itemId;

    public CommentDTO() {}

    public CommentDTO(Long id, String text, String date, Long userId, String username, Long itemId) {
        this.id = id;
        this.text = text;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
