package mvc2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
	private DataSource dataFactory;
	Connection conn;
	PreparedStatement pstmt;

	public BoardDAO() {
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context) ctx.lookup("java:/comp/env");
			dataFactory = (DataSource) envContext.lookup("jdbc/oracle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<ArticleVO> selectAllArticles(Map<String, Integer> pagingMap){
		List<ArticleVO> articlesList = new ArrayList<>();
		int section = (Integer)pagingMap.get("section");
		int pageNum=(Integer)pagingMap.get("pageNum");
		try{
		   conn = dataFactory.getConnection();
		   String query ="SELECT * FROM ( "
						+ "SELECT ROWNUM  as recNum, LVL,"
							+"articleno, parentno, title, id, writedate"
				                  +" FROM (select LEVEL as LVL, "
								+"articleno, parentno, title, id, writedate"
							   +" FROM t_board" 
							   +" START WITH parentno=0"
							   +" CONNECT BY PRIOR articleno = parentno"
							  +"  ORDER SIBLINGS BY articleno DESC)"
					+") "                        
					+" WHERE recNum BETWEEN( ? - 1 ) * 100 + ( ? - 1 ) * 10 + 1 AND ( ? - 1 ) * 100 + ? * 10";                
		 
		   pstmt= conn.prepareStatement(query);
		   pstmt.setInt(1, section);
		   pstmt.setInt(2, pageNum);
		   pstmt.setInt(3, section);
		   pstmt.setInt(4, pageNum);
		   ResultSet rs =pstmt.executeQuery();
		   while(rs.next()){
		      int level = rs.getInt("lvl");
		      int articleNO = rs.getInt("articleNO");
		      int parentNO = rs.getInt("parentNO");
		      String title = rs.getString("title");
		      String id = rs.getString("id");
		      Date writeDate= rs.getDate("writeDate");
		      ArticleVO article = new ArticleVO();
		      article.setLevel(level);
		      article.setArticleno(articleNO);
		      article.setParentno(parentNO);
		      article.setTitle(title);
		      article.setId(id);
		      article.setWritedate(writeDate);
		      articlesList.add(article);	
		   } 
		   rs.close();
		   pstmt.close();
		   conn.close();
	  }catch(Exception e){
	     e.printStackTrace();	
	  }
	  return articlesList;
    } 
	
	public List<ArticleVO> selectAllArticles() {
		List<ArticleVO> articlesList = new ArrayList<>();
		try {
			conn = dataFactory.getConnection();			
			String query = "SELECT LEVEL, articleno, parentno, title, content, id, writedate " 
			             + "FROM t_board "
					     + "START WITH parentno = 0 " 
			             + "CONNECT BY PRIOR articleno = parentno "
					     + "ORDER SIBLINGS BY articleno DESC";
				
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();		
			while (rs.next()) {
				int level = rs.getInt("level");
				int articleno = rs.getInt("articleno");
				int parentno = rs.getInt("parentno");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writedate = rs.getDate("writedate");
					
				ArticleVO article = new ArticleVO();
				article.setLevel(level);
				article.setArticleno(articleno);
				article.setParentno(parentno);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWritedate(writedate);
					
				articlesList.add(article);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articlesList;
	}

	private int getNewArticleNO() {
		try {
			conn = dataFactory.getConnection();
			
			String query = "SELECT MAX(articleno) FROM t_board ";
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery(query);
			
			if (rs.next())
				return (rs.getInt(1) + 1);
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int insertNewArticle(ArticleVO article) {
		int articleNO = getNewArticleNO();
		try {
			conn = dataFactory.getConnection();
			int parentNO = article.getParentno();
			String title = article.getTitle();
			String content = article.getContent();
			String id = article.getId();
			String imageFileName = article.getImagefilename();
//			System.out.println(imageFileName);
			/* 이미지 유무에 따라 쿼리 변경 */
			String query = "INSERT INTO t_board (articleNO, parentNO, title, content, id";
			if (imageFileName == null) {
				query += ") VALUES (?, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, articleNO);
				pstmt.setInt(2, parentNO);
				pstmt.setString(3, title);
				pstmt.setString(4, content);
				pstmt.setString(5, id);
			}
			else {
				query += ", imagefilename) VALUES (?, ?, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, articleNO);
				pstmt.setInt(2, parentNO);
				pstmt.setString(3, title);
				pstmt.setString(4, content);
				pstmt.setString(5, id);
				pstmt.setString(6, imageFileName);
			}
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return articleNO;
	}

	public ArticleVO selectArticle(int articleNO){
		ArticleVO article=new ArticleVO();
		try{
		conn = dataFactory.getConnection();
		String query ="SELECT articleno, parentno, title, content, imagefilename, id, writedate"
			         +" FROM t_board" 
			         +" WHERE articleno = ?";

		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, articleNO);
		ResultSet rs =pstmt.executeQuery();

		rs.next();
		int _articleNO =rs.getInt("articleno");
		int parentNO=rs.getInt("parentno");
		String title = rs.getString("title");
		String content =rs.getString("content");
	    String imageFileName = rs.getString("imagefilename"); 
		String id = rs.getString("id");
		Date writeDate = rs.getDate("writedate");

		article.setArticleno(_articleNO);
		article.setParentno (parentNO);
		article.setTitle(title);
		article.setContent(content);
		article.setImagefilename(imageFileName);
		article.setId(id);
		article.setWritedate(writeDate);
		rs.close();
		pstmt.close();
		conn.close();
		} catch(Exception e) {
			e.printStackTrace();	
		}
		return article;
	}

	public void updateArticle(ArticleVO article) {
		int articleNO = article.getArticleno();
		String title = article.getTitle();
		String content = article.getContent();
		String imageFileName = article.getImagefilename();
		try {
			conn = dataFactory.getConnection();
			String query = "UPDATE t_board SET title = ?, content = ?";
			if (imageFileName != null && imageFileName.length() != 0) {
				query += ", imagefilename=?";
			}
			query += " WHERE articleno=?";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
//			If image exists only
			if (imageFileName != null && imageFileName.length() != 0) {
				pstmt.setString(3, imageFileName);
				pstmt.setInt(4, articleNO);
			} else {
				pstmt.setInt(3, articleNO);
			}
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteArticle(int  articleNO) {
		try {
			conn = dataFactory.getConnection();
			String query = "DELETE FROM t_board ";
			query += " WHERE articleNO in (";
			query += " SELECT articleNO FROM  t_board ";
			query += " START WITH articleno = ?";
			query += " CONNECT BY PRIOR  articleno = parentno )";
			
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Integer> selectRemovedArticles(int  articleNO) {
		List<Integer> articleNOList = new ArrayList<Integer>();
		try {
			conn = dataFactory.getConnection();
			String query = "SELECT articleno FROM  t_board  ";
			query += " START WITH articleno = ?";
			query += " CONNECT BY PRIOR  articleno = parentno";

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				articleNO = rs.getInt("articleno");
				articleNOList.add(articleNO);
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleNOList;
	}

	public int selectTotArticles() {
		try {
			conn = dataFactory.getConnection();
			
			String query = "SELECT COUNT(articleno) FROM t_board ";
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return (rs.getInt(1));
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
