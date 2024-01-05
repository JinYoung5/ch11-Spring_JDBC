package kr.spring.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.spring.board.dao.BoardDAO;
import kr.spring.board.vo.BoardVO;

@Service
public class BoardServiceImpl implements BoardService{
	@Autowired
	private BoardDAO boardDAO;
	
	@Override
	public void insertBoard(BoardVO board) {
		boardDAO.insertBoard(board);
	}

	@Override
	public int getBoardCount() {
		return boardDAO.getBoardCount();
	}

	@Override
	public List<BoardVO> getBoardList(int startRow, int endRow) {
		return boardDAO.getBoardList(startRow, endRow);
	}

	@Override
	public BoardVO getBoard(int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBoard(BoardVO board) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteBoard(int num) {
		// TODO Auto-generated method stub
		
	}

}
