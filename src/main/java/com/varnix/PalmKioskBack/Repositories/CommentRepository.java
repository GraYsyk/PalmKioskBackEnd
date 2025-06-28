package com.varnix.PalmKioskBack.Repositories;

import com.varnix.PalmKioskBack.Entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(long id);
    List<Comment> findCommentsByUserId(long id);
    List<Comment> findCommentsByItemId(long id);
}
