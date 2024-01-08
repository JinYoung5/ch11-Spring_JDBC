package kr.spring.board.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.util.PageUtil;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	//로그 처리(로그 대상 지정)
	private static final Logger log = LoggerFactory.getLogger(BoardController.class);
	
	/*
	 * 로그 레벨
	 * FATAL : 가장 심각한 오류
	 * ERROR : 일반적인 오류
	 * WARN	 : 주의를 요하는 경우
	 * INFO	 : 런타임시 관심있는 내용
	 * DEBUG : 시스템 흐름과 관련된 상세 정보
	 * TRACE : 가장 상세한 정보
	 * 
	 * DEBUG,INFO 로만 하면 로그파일이 많이 쌓여서 나중에는 ERROR 레벨까지 낮춰야됨
	 * LOG레벨 지정은 log4j.xml 파일에서 설정(우클릭-> open with -> xml editor
	 */
	
	//자바빈(VO) 초기화
	@ModelAttribute
	public BoardVO initCommand() {
		return new BoardVO();
	}
	
	@GetMapping("/insert.do")
	public String form() {
		return "insertForm";
	}
	
	@PostMapping("/insert.do")
	public String submit(@Valid BoardVO boardVO, BindingResult result) {
		//System.out.println("BoardVO : " + boardVO);				//웹페이지를 만들때는 println 사용하지 않고 로그 처리
		log.debug("BoardVO : " + boardVO);							//DEBUG 형태로 출력
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form();
		}
		
		//글 등록
		boardService.insertBoard(boardVO);
		
		return "redirect:/list.do";
	}
	
	@RequestMapping("/list.do")
	public ModelAndView process(
	@RequestParam(value="pageNum",defaultValue = "1") int currnetPage){
		
		int count = boardService.getBoardCount();
		
		log.debug("pageNum : " + currnetPage);
		log.debug("count : " + count);
		
		PageUtil page = new PageUtil(currnetPage, count, 10,10,"list.do"); //페이지 처리
		
		List<BoardVO> list = null;	//페이지 불러오기
		if(count > 0) {
			list = boardService.getBoardList(page.getStartRow(), page.getEndRow());
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("selectList");			//뷰이름 지정
		mav.addObject("count",count);
		mav.addObject("list",list);
		mav.addObject("page",page.getPage());	//페이지 처리
		
		return mav;
	}
	
	@RequestMapping("/detail.do")
	public ModelAndView viewDetail(@RequestParam int num) { //필수적으로 num을 받게 명시 못받으면 에러
		log.debug("num : " + num);
		
		BoardVO board = boardService.getBoard(num);
								//뷰이름,속성명,속성값
		return new ModelAndView("selectDetail","board",board);
	}
	
	@GetMapping("/update.do")
	public String formUpdate(@RequestParam int num, Model model) {
		//모델에 데이터 저장		속성명		속성값
		model.addAttribute("boardVO", boardService.getBoard(num));
		
		return "updateForm";
	}
	@PostMapping("/update.do")
	public String submitUpadte(@Valid BoardVO boardVO, BindingResult result) {
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return "updateForm";  //위쪽의 formUpdate를 불러올수 없어서 updateForm으로 명시
		}
		//DB에 저장된 비밀번호 구하기
		BoardVO db_board = boardService.getBoard(boardVO.getNum());
		
		//비밀번호 일치 여부 체크
		if(!db_board.getPasswd().equals(boardVO.getPasswd())) {
								//에러필드, 에러코드
			result.rejectValue("passwd", "invalidPassword");
			return "updateForm";
		}
		
		//글 수정
		boardService.updateBoard(boardVO);
		
		return "redirect:/list.do";
	}
	
	@GetMapping("/delete.do")
	public String formDelete(@RequestParam int num, Model model) {
		BoardVO boardVO = new BoardVO();	//인증을 위해 자바빈 생성
		boardVO.setNum(num);
		
		model.addAttribute("boardVO",boardVO);
		
		return "deleteForm";
	}
	
	@PostMapping("/delete.do")
	public String submitDelete(@Valid BoardVO boardVO, BindingResult result) {
		//비밀번호만 유효성 체크 결과를 확인
		if(result.hasFieldErrors("passwd")) {
			return "deleteForm";
		}
		
		//DB에 저장된 비밀번호 구하기
		BoardVO db_board = boardService.getBoard(boardVO.getNum());
		//비밀번호 일치 여부 체크
		if(!db_board.getPasswd().equals(boardVO.getPasswd())) {
			result.rejectValue("passwd", "invalidPassword");
			return "deleteForm";
		}
		
		//글 삭제
		boardService.deleteBoard(boardVO.getNum());
		
		return "redirect:/list.do";
	}
	
}
