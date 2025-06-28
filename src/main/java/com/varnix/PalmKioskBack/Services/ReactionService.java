package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Entities.ItemReaction;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Repositories.ItemReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReactionService {

    private final ItemReactionRepository itemReactionRepository;

    @Autowired
    public ReactionService(ItemReactionRepository itemReactionRepository) {
        this.itemReactionRepository = itemReactionRepository;
    }

    public void react(Item item, User user, boolean liked) {
        var existingReaction = itemReactionRepository.findByItemAndUser(item, user);
        if (existingReaction.isPresent()) {
            ItemReaction reaction = existingReaction.get();
            reaction.setLiked(liked);
            itemReactionRepository.save(reaction);
        } else {
            itemReactionRepository.save(new ItemReaction(item, user, liked));
        }
    }

    public long countLikes(Item item) {
        return itemReactionRepository.countByItemAndLiked(item, true);
    }

    public long countDislikes(Item item) {
        return itemReactionRepository.countByItemAndLiked(item, false);
    }
}
