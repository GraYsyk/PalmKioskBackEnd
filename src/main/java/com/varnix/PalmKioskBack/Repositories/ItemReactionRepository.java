package com.varnix.PalmKioskBack.Repositories;

import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Entities.ItemReaction;
import com.varnix.PalmKioskBack.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemReactionRepository extends JpaRepository<ItemReaction, Long> {
    Optional<ItemReaction> findByItemAndUser(Item item, User user);
    long countByItemAndLiked(Item item, boolean liked);
}
