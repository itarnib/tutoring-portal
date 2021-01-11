package com.tutoring.portal.service;

import com.tutoring.portal.model.Comment;
import com.tutoring.portal.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Returns list with all comments from the database.
     * @return comments list
     */
    public List<Comment> getAllComments() {
        return new ArrayList<>(commentRepository.findAll());
    }

    /**
     * Returns comment with provided ID or null, if comment wasn't found.
     * @param id comment's ID
     * @return comment with provided ID
     */
    public Comment getCommentById(int id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }

    /**
     * Saves provided comment and returns it.
     * @param comment comment
     * @return saved comment
     */
    public Comment saveComment(Comment comment) {
        comment.setTimestamp(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /**
     * Deletes comment with provided ID.
     * @param id comment's ID
     */
    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }
}
