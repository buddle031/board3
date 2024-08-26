package com.study.board3.service;

import com.study.board3.dto.BoardDto;
import com.study.board3.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    Board save(BoardDto boardDto);
    List<Board> findAll();
    Page<Board> findPageAll(Pageable pageable);
    Page<Board> search(String title,Pageable pageable);
    Board findByBoardId(Long boardId);
    Board updateBoard(BoardDto boardDto);
    Integer passwordVerify(Long boardId, String password, String username);
    boolean deleteBoard(Long boardId, String memberUsername);
}
