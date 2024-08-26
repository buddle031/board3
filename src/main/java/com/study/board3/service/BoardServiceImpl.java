package com.study.board3.service;

import com.study.board3.dto.BoardDto;
import com.study.board3.entity.Board;
import com.study.board3.entity.Member;
import com.study.board3.repository.BoardRepository;
import com.study.board3.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService{

    @Autowired
    private final BoardRepository boardRepository;
    @Autowired
    private final MemberService memberService;

    @Override
    public Board save(BoardDto boardDto){
        String username = boardDto.getMemberDto().getUsername();
        Member byUsername = memberService.findByUsername(username);
        Board build = Board.builder()
                .title(boardDto.getTitle())
                .dateTime(LocalDateTime.now())
                .writer(byUsername.getUsername())
                .password(boardDto.getPassword())
                .content(boardDto.getContent())
                .count(0)
                .member(byUsername)
                .views(0)
                .build();

        Board save = boardRepository.save(build);
        return save;
    }

    @Override
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Override
    public Page<Board> findPageAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Override
    public Board findByBoardId(Long boardId){
        try{
            return boardRepository.findById(boardId)
                    .orElseThrow(()->new RuntimeException("Board not found with ID: "+boardId));
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error while finding Board by ID", e);
        }
    }

    public Board countViews(Long boardId){
        Board byBoardId = findByBoardId(boardId);
        Integer views = byBoardId.getViews();
        byBoardId.setViews(++views);
        return boardRepository.save(byBoardId);
    }

    @Override
    public Board updateBoard(BoardDto boardDto) {
        Board byBoardId = findByBoardId(boardDto.getId());
        byBoardId.update(boardDto.getTitle(), boardDto.getContent());
        return boardRepository.save(byBoardId);
    }


    @Override
    public Integer passwordVerify(Long boardId, String password, String username){
        Board byBoardId = findByBoardId(boardId);
        String boardPassword = byBoardId.getPassword();
        Member memberUsername = memberService.findByUsername(username);

        if (boardPassword.equals(password)) {
            if(memberUsername == null|| !(byBoardId.getWriter()).equals(username)){
                return 2;
            }
            return 1;
        }
        return 0;
    }

    @Override
    public boolean deleteBoard(Long boardId, String memberUsername) {
        Member byUsername = memberService.findByUsername(memberUsername);
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (boardOptional.isPresent()) {
            String writer = boardOptional.get().getWriter();
            if (byUsername != null && writer.equals(memberUsername)) {
                boardRepository.deleteById(boardId);
                return true;
            }
        } else {
            throw new EntityNotFoundException("게시물이 존재하지 않습니다. ID: " + boardId);
        }
        return false;
    }

    @Override
    public Page<Board> search(String title,Pageable pageable) {
        return boardRepository.findByTitleContaining(title,pageable);
    }

}
