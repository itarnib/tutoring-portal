package com.tutoring.portal.repository;

import com.tutoring.portal.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for Comment.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

}