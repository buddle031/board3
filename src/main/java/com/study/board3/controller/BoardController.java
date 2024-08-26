package com.study.board3.controller;

import com.study.board3.dto.BoardDto;
import com.study.board3.entity.Board;
import com.study.board3.entity.Member;
import com.study.board3.event.ViewsEvent;
import com.study.board3.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@SessionAttributes("member")
@Tag(name = "Board", description = "Board Management API")
public class BoardController {

    private final BoardService boardService;
    private final ApplicationEventPublisher eventPublisher;

    @Operation(summary = "Get list of boards with pagination and search",
            description = "Retrieve a paginated list of boards with optional search by title.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Board.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("/board")
    public String board(
            Model model,
            @PageableDefault(size = 4) Pageable pageable,
            @RequestParam(name = "title", required = false, defaultValue="") String title,
            HttpSession session
    ){
        Page<Board> boards = boardService.search(title, pageable);
        List<Board> boardAll = boards.getContent();

        getSession(model, session);

        int currentPage = boards.getPageable().getPageNumber() + 1;
        int totalPage = boards.getTotalPages();

        int visiblePages = 3;
        int startPage = Math.max(1, currentPage - visiblePages / 2);
        int endPage = Math.min(totalPage, startPage + visiblePages - 1);

        if (boardAll != null) {
            model.addAttribute("boardAll", boardAll);
        }
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("page", boards);

        return "board/board";
    }

    @Operation(summary = "Get form to write a new board",
            description = "Retrieve the form for creating a new board.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Form retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @GetMapping("/boardWrit")
    public String write(Model model, HttpSession session){
        getSession(model, session);
        model.addAttribute("board", new BoardDto());
        return "board/writeboard";
    }

    @Operation(summary = "Create a new board",
            description = "Create a new board entry with the provided data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Board created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/boardWrit")
    @ResponseBody
    public ResponseEntity<?> writing(@Valid @RequestBody BoardDto boardDto, BindingResult result){
        try {
            if (result.hasErrors()) {
                Map<String, String> errorMessage = new HashMap<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errorMessage.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
            // 데이터베이스에 BoardDto 저장
            boardService.save(boardDto);
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류발생");
        }
    }

    private void getSession(Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
        } else {
            model.addAttribute("loginMember", null);
        }
    }

    @Operation(summary = "Get details of a specific board",
            description = "Retrieve details of a board by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Board.class))),
                    @ApiResponse(responseCode = "404", description = "Board not found")
            })
    @GetMapping("/board/{boardId}")
    public String boardInfo(@PathVariable(name = "boardId") Long boardId, Model model, HttpSession session){
        Board byBoardId = boardService.findByBoardId(boardId);
        eventPublisher.publishEvent(new ViewsEvent(byBoardId)); //조회시 카운터 증가

        getSession(model, session);

        model.addAttribute("board", byBoardId);
        return "board/boardInfo";
    }

    @Operation(summary = "Get form to update an existing board",
            description = "Retrieve the form for updating a board by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Form retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Board not found")
            })
    @GetMapping("/board/update/{id}")
    public String updateGetBoard(@PathVariable(name = "id") Long id, Model model){
        Board byBoardId = boardService.findByBoardId(id);
        model.addAttribute("board", byBoardId);
        return "board/updateBoard";
    }

    @Operation(summary = "Update an existing board",
            description = "Update board details with the provided data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Board updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PutMapping("/board/update")
    @ResponseBody
    public ResponseEntity<?> updateBoardAfter(@RequestBody BoardDto boardDto){
        try {
            Board board = boardService.updateBoard(boardDto);
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생");
        }
    }

    @Operation(summary = "Verify password for board modification",
            description = "Verify the password for a board entry to allow modification.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password verified successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/password/verify")
    @ResponseBody
    public ResponseEntity<?> verifyPassword(@RequestBody BoardDto boardDto){
        try {
            String password = boardDto.getPassword();
            String dtoUsername = boardDto.getMemberDto().getUsername();
            Long boardId = boardDto.getId();

            Integer integer = boardService.passwordVerify(boardId, password, dtoUsername);

            if (integer == 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("등록한 사용자만 수정할 수 있습니다");
            } else if (integer == 1) {
                return ResponseEntity.ok(integer);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 일치하지 않습니다");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류발생");
        }
    }

    @Operation(summary = "Delete a board",
            description = "Delete a board entry by its ID after verifying the password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Board deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @DeleteMapping("/board/{id}")
    @ResponseBody
    public ResponseEntity<?> boardDelete(@RequestBody BoardDto boardDto){
        System.out.println("DELETE 요청 수신: " + boardDto);
        try {
            Long id = boardDto.getId();
            String memberUsername = boardDto.getMemberDto().getUsername();
            boolean deleteBoard = boardService.deleteBoard(id, memberUsername);
            if (!deleteBoard) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("등록된 사용자만 삭제가능");
            }
            return ResponseEntity.ok("삭제성공");
        } catch (EntityActionVetoException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물이 존재하지 않습니다");
        }
    }
}
