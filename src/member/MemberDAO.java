package member;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import member.MemberVO;

public class MemberDAO {
	private DataSource dataFactory;
	private Connection conn;
	private PreparedStatement pstmt;

	public MemberDAO() {
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context) ctx.lookup("java:/comp/env");
			dataFactory = (DataSource) envContext.lookup("jdbc/oracle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addMember(MemberVO m) {
		try {
			conn = dataFactory.getConnection();
			
			String id = m.getId();
			String pwd = m.getPwd();
			String name = m.getName();
			String email = m.getEmail();

			String query = "INSERT INTO t_member(id, pwd, name, email) VALUES(?, ? ,? ,?)";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getLoginId(String id, String pw) {
		String _id = null;
		try {
			conn = dataFactory.getConnection();
			
			String sql = "SELECT COUNT(*) FROM t_member WHERE id=? AND pwd=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			if(rs.getInt("COUNT(*)") == 1) {
				System.out.println("Login detected: " + id);
				_id = id;
			} else {
				System.out.println("Login detected, but no account.");
				_id = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _id;
	}

	public MemberVO findMember(String _id) {
		MemberVO memInfo = null;
		try {
			conn = dataFactory.getConnection();
			
			String query = "SELECT * FROM t_member WHERE id=?";
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, _id);
			
			ResultSet rs = pstmt.executeQuery();
			
			rs.next();
			String id = rs.getString("id");
			String pwd = rs.getString("pwd");
			String name = rs.getString("name");
			String email = rs.getString("email");
			Date joinDate = rs.getDate("joinDate");
			
			memInfo = new MemberVO(id, pwd, name, email, joinDate);
			
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return memInfo;
	}
}

























