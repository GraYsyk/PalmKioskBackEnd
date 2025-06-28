package com.varnix.PalmKioskBack.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_reactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "user_id"})
})
public class ItemReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean liked;

    public ItemReaction() {
    }

    public ItemReaction(Item item, User user, boolean liked) {
        this.item = item;
        this.user = user;
        this.liked = liked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
