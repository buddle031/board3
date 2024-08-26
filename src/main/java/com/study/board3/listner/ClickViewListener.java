package com.study.board3.listner;

import com.study.board3.entity.Board;
import com.study.board3.event.ViewsEvent;
import com.study.board3.service.BoardService;
import com.study.board3.service.BoardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClickViewListener implements ApplicationListener<ViewsEvent> {
    private final BoardServiceImpl boardService;

    @Override
    public void onApplicationEvent(ViewsEvent event){
        Board board = event.getBoard();
        boardService.countViews(board.getId());
    }
}
