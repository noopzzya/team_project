package mem.model;

import java.sql.*;
import java.util.Vector;
import javax.naming.*;
import javax.sql.*;

public class MemberDAO {
	
	// 싱글톤 생성
	private static MemberDAO instance = null;

	// 외부에서 인스턴스 생성을 막기 위해 private 생성자 선언
	private MemberDAO() {}
	
	// 싱글톤 객체 생성
	public static MemberDAO getInstance() {
	
		if(instance == null) {
			synchronized (MemberDAO.class) {
				instance = new MemberDAO();
			}
		}
		return instance;
	}
	
	private Connection getConnection() {
		
		Connection conn = null;

		try {
			Context initContext = new InitialContext();
			Context envContext = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/myoracle");
			conn = ds.getConnection();			
		} catch (Exception e) {
			System.out.println("Connection 생성 실패!");
		}
		return conn;
	}
	
	// idCheck
	public boolean idCheck(String id) {
		
		boolean result = true;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = getConnection();
			
			String sql = "select * from member where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(!rs.next()) result = false;
			
		} catch (SQLException ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try {
					rs.close();
				}catch (SQLException ex) {
					ex.printStackTrace();
				}
			if(pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			if(conn != null)
				try {
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
		}
				
		return result;
	} // end idCheck
	
	// zipcodeRead (동으로 검색)
	public Vector<ZipCodeVO> zipcodeRead(String dong){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Vector<ZipCodeVO> vecList = new Vector<ZipCodeVO>();
		
		try {
			
			conn = getConnection();
			
			String sql = "select * from zipcode where dong like '" +dong+ "%'" ;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ZipCodeVO tempZipcode = new ZipCodeVO();
				tempZipcode.setZipcode(rs.getString("zipcode"));
				tempZipcode.setSido(rs.getString("sido"));
				tempZipcode.setGugun(rs.getString("gugun"));
				tempZipcode.setDong(rs.getString("dong"));
				tempZipcode.setRi(rs.getString("ri"));
				tempZipcode.setRi(rs.getString("bunji"));
				vecList.addElement(tempZipcode);				
			}
			
		} catch(SQLException ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try{
					rs.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
		}
		
		return vecList;
	} // end zipcodeRead
	
	// 실제 데이터베이스 회원 데이터를 저장하기 위해 메소드 추가
	public boolean memberInsert(MemberVO vo) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		boolean flag = false;
		
		try {
			
			conn = getConnection();
			
			String sql = "insert into member values(?,?,?,?,?,?,?,?,?)";
			
			System.out.println(vo.getId());
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getId());
			pstmt.setString(2, vo.getPass());
			pstmt.setString(3, vo.getName());
			pstmt.setString(4, vo.getPhone());
			pstmt.setString(5, vo.getEmail());
			pstmt.setString(6, vo.getZipcode());
			pstmt.setString(7, vo.getAddress1());
			pstmt.setString(8, vo.getAddress2());
			pstmt.setString(9, vo.getUserlike());
			
			int count = pstmt.executeUpdate();
			if(count > 0) flag = true;
			
		} catch(SQLException ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try{
					rs.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
		}
		
		return flag;
	} // end 메소드
	
	// 아이디 비번 비교한 결과를 정수형으로 리턴해주는 메소드
	// 1:로그인 성공, 0:비밀번호 오류, -1:아이디없음
	public int loginCheck(String id, String pass) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int check = -1;
		
		try {
			
			conn = getConnection();
			
			// id로 pass 조회
			String sql = "select pass from member where id=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			
			if(rs.next()) { // dbPass와 입력한 비밀번호 일치확인
				String dbPass = rs.getString("pass");
				if(pass.equals(dbPass)) check = 1;
				else check = 0;
			}
			
		} catch(Exception ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try{
					rs.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
		}
		
		return check;
	} // end loginCheck
	
	// 정보수정, 세션에 저장된 회원정보를 얻어오는 메소드를 이용하여 가져옴
	public MemberVO getMember(String id) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		MemberVO vo = null;
		
		try {
			
			conn = getConnection();
			
			String sql = "select * from member where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) { // 해당 id 회원이 존재한다면
				vo = new MemberVO();
				vo.setId(rs.getString("id"));
				vo.setPass(rs.getString("pass"));
				vo.setName(rs.getString("name"));
				vo.setEmail(rs.getString("email"));
				vo.setPhone(rs.getString("phone"));
				vo.setZipcode(rs.getString("zipcode"));
				vo.setAddress1(rs.getString("address1"));
				vo.setAddress2(rs.getString("address2"));
				vo.setUserlike(rs.getString("userlike"));
			}
			
		} catch(Exception ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try{
					rs.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){
					ex.printStackTrace();
				}
		}
		
		return vo;
	} // end getMember
	
	
	// 정보수정 클릭 시 데이터베이스 수정처리 할 메소드 구현
	public void updateMember(MemberVO vo) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			
			conn = getConnection();
			String sql = "update member set pass=?, email=?, phone=?, zipcode=?, address1=?, address2=? where id=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, vo.getPass());
			pstmt.setString(2, vo.getEmail());
			pstmt.setString(3, vo.getPhone());
			pstmt.setString(4, vo.getZipcode());
			pstmt.setString(5, vo.getAddress1());
			pstmt.setString(6, vo.getAddress2());
			pstmt.setString(7, vo.getId());
			
			pstmt.executeUpdate();
			
		} catch (Exception ss) {
			ss.printStackTrace();
		} finally {
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){}
		}
	} // end updateMember
	
	// 회원탈퇴 버튼 클릭 시 데이터베이스에 회원데이터에서 삭제되는 메소드 구현
	public int deleteMember(String id, String pass) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String dbPass = "";
		int result = -1;
		
		try {
			
			conn = getConnection();
			
			String sql = "select pass from member where id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id); // 바인딩변수 처리
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dbPass = rs.getString("pass");
				if(dbPass.equals(pass)) { // 본인확인
					sql = "delete from member where id=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, id);
					pstmt.executeUpdate();
					result = 1; // 회원탈퇴 성공
				} else { // 본인확인 실패
					result = 0;
				}
			}
			
		} catch (Exception ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try{
					rs.close();
				}catch(SQLException ex){}
			
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){}
		}
		
		return result;
	} // end deleteMember

	// searchId
	public String searchId(String name, String email) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String id = null;
		
		try {
			
			conn = getConnection();
			
			String sql = "select id from member where name=? and email=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			rs = pstmt.executeQuery();
			
			if(rs.next())
				id = rs.getString("id");
			
		} catch (Exception ss) {
			ss.printStackTrace();
		} finally {
			if(rs != null)
				try{
					rs.close();
				}catch(SQLException ex){}
			
			if(pstmt != null)
				try{
					pstmt.close();
				}catch(SQLException ex){}
			
			if(conn != null)
				try{
					conn.close();
				}catch(SQLException ex){}
		}
		
		return id;
	} // end searchId
	
	// searchPass
		public String searchPass(String name, String email, String id) {
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			String pass = null;
			
			try {
				
				conn = getConnection();
				
				String sql = "select pass from member where name=? and email=? and id=?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, name);
				pstmt.setString(2, email);
				pstmt.setString(3, id);
				rs = pstmt.executeQuery();
				
				if(rs.next())
					pass = rs.getString("pass");
				
			} catch (Exception ss) {
				ss.printStackTrace();
			} finally {
				if(rs != null)
					try{
						rs.close();
					}catch(SQLException ex){}
				
				if(pstmt != null)
					try{
						pstmt.close();
					}catch(SQLException ex){}
				
				if(conn != null)
					try{
						conn.close();
					}catch(SQLException ex){}
			}
			
			return pass;
		} // end searchPass
}
