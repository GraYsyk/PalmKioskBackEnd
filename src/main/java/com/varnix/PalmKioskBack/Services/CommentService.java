package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Dtos.CommentDTO;
import com.varnix.PalmKioskBack.Entities.Comment;
import com.varnix.PalmKioskBack.Repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> getCommentsByItemId(long userId) {
        List<Comment> comments = commentRepository.findCommentsByItemId(userId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setText(comment.getText());
            dto.setDate(sdf.format(comment.getDate()));
            dto.setUserId(comment.getUser().getId());
            dto.setUsername(comment.getUser().getUsername());
            dto.setItemId(comment.getItem().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByUserId(long userId) {
        List<Comment> comments = commentRepository.findCommentsByUserId(userId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setText(comment.getText());
            dto.setDate(sdf.format(comment.getDate()));
            dto.setUserId(comment.getUser().getId());
            dto.setUsername(comment.getUser().getUsername());
            dto.setItemId(comment.getItem().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}
