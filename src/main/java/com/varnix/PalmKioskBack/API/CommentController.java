package com.varnix.PalmKioskBack.API;

import com.varnix.PalmKioskBack.Dtos.CommentDTO;
import com.varnix.PalmKioskBack.Services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comm")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<List<CommentDTO>> getUserComments(@PathVariable Long itemId) {
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
}
