package com.study.board3.repository;

import com.study.board3.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAll(Pageable pageable);
    Optional<Board> findById(Long boardId);
    Page<Board> findByTitleContaining(String title, Pageable pageable);

}
