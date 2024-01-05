package kr.spring.board.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kr.spring.board.vo.BoardVO;

@Repository
public class BoardDAOImpl implements BoardDAO{
	private static final String INSERT_SQL = "INSERT INTO aboard (num,writer,title,passwd,content,reg_date) VALUES (aboard_seq.nextval,?,?,?,?,SYSDATE)";
	private static final String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM aboard";
	private static final String SELECT_LIST_SQL = "SELECT * FROM (SELECT a.*,rownum rnum FROM (SELECT * FROM aboard ORDER BY reg_date DESC)a) WHERE rnum >=? AND rnum <= ?";
	
	private RowMapper<BoardVO> rowMapper = new RowMapper<BoardVO>() {
		public BoardVO mapRow(ResultSet rs, int rowNum) throws SQLException{
			BoardVO board = new BoardVO();
			board.setNum(rs.getInt("num"));
			board.setWriter(rs.getString("writer"));
			board.setTitle(rs.getString("title"));
			board.setPasswd(rs.getString("passwd"));
			board.setContent(rs.getString("content"));
			board.setReg_date(rs.getDate("reg_date"));
			
			return board;
		}
	};
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void insertBoard(BoardVO board) {
		jdbcTemplate.update(INSERT_SQL, new Object[] {board.getWriter(), board.getTitle(), board.getPasswd(), board.getContent()});
	}

	@Override
	public int getBoardCount() {
		return jdbcTemplate.queryForObject(SELECT_COUNT_SQL, Integer.class);
	}

	@Override
	public List<BoardVO> getBoardList(int startRow, int endRow) {
		List<BoardVO> list = jdbcTemplate.query(SELECT_LIST_SQL, new Object[] {startRow,endRow},rowMapper);
		
		return list;
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