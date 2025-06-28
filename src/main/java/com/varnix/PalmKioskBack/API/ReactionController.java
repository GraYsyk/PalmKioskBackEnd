package com.varnix.PalmKioskBack.API;

import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Services.ItemService;
import com.varnix.PalmKioskBack.Services.ReactionService;
import com.varnix.PalmKioskBack.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReactionController {
    private final ReactionService reactionService;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ReactionController(ReactionService reactionService, ItemService itemService, UserService userService) {
        this.reactionService = reactionService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping("/{itemId}/like")
    public ResponseEntity<?> like(@PathVariable Long itemId, @AuthenticationPrincipal UserDetails userDetails) {
        return handleReaction(itemId, userDetails, true);
    }

    @PostMapping("/{itemId}/dislike")
    public ResponseEntity<?> dislike(@PathVariable Long itemId, @AuthenticationPrincipal UserDetails userDetails) {
        return handleReaction(itemId, userDetails, false);
    }

    private ResponseEntity<?> handleReaction(Long itemId, UserDetails userDetails, boolean liked) {
        Item item = itemService.findById(itemId).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        reactionService.react(item, user, liked);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{itemId}/reactions")
    public ResponseEntity<?> getReactionStats(@PathVariable Long itemId) {
        Item item = itemService.findById(itemId).orElse(null);
        if (item == null) return ResponseEntity.notFound().build();

        long likes = reactionService.countLikes(item);
        long dislikes = reactionService.countDislikes(item);

        return ResponseEntity.ok(new ReactionStatsDTO(likes, dislikes));
    }

    private static class ReactionStatsDTO {
        public long likes;
        public long dislikes;

        public ReactionStatsDTO(long likes, long dislikes) {
            this.likes = likes;
            this.dislikes = dislikes;
        }
    }
}
