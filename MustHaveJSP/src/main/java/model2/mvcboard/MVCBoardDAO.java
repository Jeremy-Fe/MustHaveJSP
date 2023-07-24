package model2.mvcboard;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import common.DBConnPool;

public class MVCBoardDAO extends DBConnPool { // 커넥션 풀 상속
	public MVCBoardDAO() {
		super();
	}
	
	// 검색 조건에 맞는 게시물의 개수를 반환합니다.
	public int selectCount(Map<String, Object> map) {
		int totalCount = 0;
		
		// 쿼리문 준비
		String query = "SECET COUNT(*) FROM mvcboard";
		// 검색 조건이 있다면  WHERE절로 추가
		if(map.get("serrchWord") != null) {
			query += " WHERE " + map.get("searchField") + " LIKE '%" + map.get("searchWord") + "%'";
		}
		try {
			stmt = con.createStatement(); // 쿼리문 생성
			rs = stmt.executeQuery(query); // 쿼리문 실행
			rs.next();
			totalCount = rs.getInt(1); // 검색된 게시물 개수 저장 
			
			
		} catch (Exception e) {
			System.out.println("게시물 카운트 중 예외 발생");
			e.printStackTrace();
		}
		
		return totalCount; // 게시물 개수를 서블릿으로 반환
	}
	
	// 검색 조건에 맞는 게시물 목록을 반환합니다(페이징 기능 지원).
	public List<MVCBoardDTO> selectListPage(Map<String, Object> map) {
		List<MVCBoardDTO> board = new Vector<MVCBoardDTO>();
		
		// 쿼리문 준비
		String query = " SELECT * FROM ( "
					+ "		SELECT Tb.*, ROWNUM rNum FROM ( "
					+ " 		SELECT * FROM mvcboard ";
		
		// 검색 조건이 있다면 WHERE절로 추가
		if(map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " LIKE '%" + map.get("searchWord") + "%' ";
		}
		
		query += "		ORDER BY idx DESC "
				+ "	) Tb "
				+ ") "
				+ " WHERE rNum BETWEEN ? AND ?"; // 게시물 구간은 인파라미터로...
		
		try {
			pstmt = con.prepareStatement(query); // 동적 쿼리문 생성
			pstmt.setString(1, map.get("start").toString()); // 인파라미터 설정
			pstmt.setString(2, map.get("end").toString());
			rs = pstmt.executeQuery(); // 쿼리문 실행
			
			// 반환된 게시물 목록을 List 컬렉션에 추가
			while (rs.next()) {
				MVCBoardDTO dto = new MVCBoardDTO();
				
				dto.setIdx(rs.getString(1));
				dto.setName(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setPostdate(rs.getDate(5));
				dto.setOfile(rs.getString(6));
				dto.setSfile(rs.getString(7));
				dto.setDowncount(rs.getInt(8));
				dto.setPass(rs.getString(9));
				dto.setVisitcount(rs.getInt(10));
				
				board.add(dto);
			}
		} catch (Exception e) {
			
		}
		
		return board; // 목록 반환
	}
	
	// 게시글 테이터를 받아 DB에 추가합니다.
	public int insertWrite(MVCBoardDTO dto) {
		int result = 0;

		try {
			String query = "INSERT INTO mvcboard ("
					+ "idx, name, title, content, ofile, sfile, pass) "
					+ " VALUES ( "
					+ "seq_board_num.NEXTVAL, ?, ?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getTitle());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getOfile());
			pstmt.setString(5, dto.getSfile());
			pstmt.setString(6, dto.getPass());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("게시물 입력 중 예외 발생");
			e.printStackTrace();
		}

		return result;
	}
	
	
}
