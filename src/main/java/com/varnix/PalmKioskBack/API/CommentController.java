package com.varnix.PalmKioskBack.API;

import com.varnix.PalmKioskBack.Dtos.CommentDTO;
import com.varnix.PalmKioskBack.Entities.Comment;
import com.varnix.PalmKioskBack.Entities.Item;
import com.varnix.PalmKioskBack.Entities.User;
import com.varnix.PalmKioskBack.Exceptions.AppError;
import com.varnix.PalmKioskBack.Services.CommentService;
import com.varnix.PalmKioskBack.Services.ItemService;
import com.varnix.PalmKioskBack.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/comm")
public class CommentController {

    private final CommentService commentService;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService,  ItemService itemService,  UserService userService) {
        this.commentService = commentService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<List<CommentDTO>> getItemComments(@PathVariable Long itemId) {
        List<CommentDTO> comments = commentService.getCommentsByItemId(itemId);
        if (comments.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByUserId(@PathVariable long userId) {
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        if (comments.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(comments);
    }


    @PostMapping("/post/{itemId}")
    public ResponseEntity<?> createComment(@PathVariable Long itemId,
                                           @RequestBody CommentDTO commentDto,
                                           Principal principal) {

        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Comment text is required!"));
        }

        Optional<Item> itemOpt = itemService.findById(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Item not found"));
        }

        String username = principal.getName();

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "User not found"));
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setRating(commentDto.getRating());

        comment.setDate(LocalDateTime.now());
        comment.setItem(itemOpt.get());
        comment.setUser(userOpt.get());

        commentService.save(comment);

        CommentDTO responseDto = new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getRating(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getItem().getId()
        );

        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/del/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Principal principal) {
        Optional<Comment> commentOpt = commentService.getCommentById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Comment not found"));
        }

        Comment comment = commentOpt.get();
        String username = principal.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User not found"));

        boolean isAdmin = user.getRoles().contains("ROLE_ADMIN");
        boolean isOwner = comment.getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AppError(HttpStatus.FORBIDDEN.value(), "You can delete only your own comments"));
        }

        commentService.deleteComment(comment);

        return ResponseEntity.ok("Comment deleted successfully");
    }

}
