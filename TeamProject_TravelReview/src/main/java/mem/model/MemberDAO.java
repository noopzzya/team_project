package mem.model;

import java.sql.*;
import java.util.Vector;

import javax.naming.*;
import javax.sql.*;

public class MemberDAO {

	// 싱글톤 생성
	private static MemberDAO instance = null;
	
	public MemberDAO() {}
	
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
				}catch (SQLException ex) {}
			if(pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException ex) {}
			if(conn != null)
				try {
					conn.close();
				} catch (SQLException ex) {}
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
			
			String sql = "select * from zipcode where don like '"+dong+"%'";
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
		
		return flag;
	} // end 메소드
	
	// 아이디 비번 비교한 결과를 정수형으로 리턴
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
				vo.setPhone(rs.getString("phone"));
				vo.setEmail(rs.getString("zipcode"));
				vo.setZipcode(rs.getString("address1"));
				vo.setZipcode(rs.getString("address2"));
				vo.setUserlike(rs.getString("userlike"));
			}
			
		} catch(Exception ss) {
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
		
		return vo;
	} // end getMember
	
	
	
}
