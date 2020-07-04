package mvc2;

import java.sql.Date;

public class ArticleVO {
	private int level;
	private int articleno;
	private int parentno;
	private String title;
	private String content;
	private String imagefilename;
	private String id;
	private Date writedate;

	public ArticleVO() {	
	}
	
	public ArticleVO(int level, int articleno, int parentno, String title, 
					 String content, String imagefilename, String id) {
		super();
		this.level = level;
		this.articleno = articleno;
		this.parentno = parentno;
		this.title = title;
		this.content = content;
		this.imagefilename = imagefilename;
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getArticleno() {
		return articleno;
	}

	public void setArticleno(int articleno) {
		this.articleno = articleno;
	}

	public int getParentno() {
		return parentno;
	}

	public void setParentno(int parentno) {
		this.parentno = parentno;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImagefilename() {
		return imagefilename;
	}

	public void setImagefilename(String imagefilename) {
//		try {
//		//�����̸��� Ư�����ڰ� ���� ��� ���ڵ�.
//		this.imagefilename = URLEncoder.encode(imagefilename, "UTF-8");
//	} catch (UnsupportedEncodingException e) {
//		e.printStackTrace();
//	}
	
		this.imagefilename = imagefilename;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getWritedate() {
		return writedate;
	}

	public void setWritedate(Date writedate) {
		this.writedate = writedate;
	}
}
