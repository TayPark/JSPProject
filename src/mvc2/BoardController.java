package mvc2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String ARTICLE_IMAGE_REPO = "D:\\web_programming\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;

	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = "";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		HttpSession session = null;
		String action = request.getPathInfo();
		System.out.println("Board Action:" + action);
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			/* 기본은 글 리스트로 이동 */
			if (action == null) {
				/* section과 pageNum을 HttpRequest로부터 받아옴. 값은 articleMaps에서 추출 */
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				/* 삼항 연산자로 section, pageNum 조정 */
				int section = Integer.parseInt(((_section == null) ? "1" : _section));
				int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
				/* K(String)-V(Integer) 형태로 paging하기 위해 변수 설정  */
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				/* pagingMap을 기반으로 글 리스트 불러옴 */
				Map articlesMap = boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				/* 프론트에 넘겨줌 */
				request.setAttribute("articlesMap", articlesMap);
				/* 세션값 임시. 나중에 지울 것. */
				session = request.getSession();
				session.setAttribute("id", "안녕");
				
				nextPage = "/board0/listArticles.jsp";
				/* /listArticles.do로 들어왔을때도 위와 동일하게 동작 */
			} else if (action.equals("/listArticles.do")) {
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				int section = Integer.parseInt(((_section == null) ? "1" : _section));
				int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
				Map pagingMap = new HashMap();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				Map articlesMap = boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				request.setAttribute("articlesMap", articlesMap);
				request.setAttribute("session", session);
				nextPage = "/board0/listArticles.jsp";
				/* 새 글 작성 */
			} else if (action.equals("/articleForm.do")) {
				nextPage = "/board0/articleForm.jsp";
				/* 글 작성에서 작성완료를 눌렀을 때 동작 */
			} else if (action.equals("/addArticle.do")) {
				int articleNO = 0;
				/* 프론트에서 넘겨온 값으로 title, content, imgFileName 설정 */
				Map<String, String> articleMap = upload(request, response);
				session = request.getSession();
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				/* VO에 넣고 addArticle 실행 */
				articleVO.setParentno(0);
				articleVO.setId((String) session.getAttribute("id"));
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImagefilename(imageFileName);
				articleNO = boardService.addArticle(articleVO);
				/* 
				 * 파일이 있을 경우에만 동작. 
				 * temp에 들어간 이미지를 글 번호의 디렉토리로 이동
				 * */
				if (imageFileName != null && imageFileName.length() != 0) {
					
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('작성완료');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");

				return;
				/* 글 보기 요청 */
			} else if (action.equals("/viewArticle.do")) {
				/* 글 번호를 불러와서 viewArticle 요청 */
				String articleNO = request.getParameter("articleNO");
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				/* 리턴값 프론트로 전달 */
				request.setAttribute("article", articleVO);
				nextPage = "/board0/viewArticle.jsp";
				/* 글 수정 요청 */
			} else if (action.equals("/modArticle.do")) {
				Map<String, String> articleMap = upload(request, response);
				/* 글 수정요청을 받아옴 */
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleno(articleNO);
				/* 로컬 변수에 매핑 */
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				/* VO에 넣고 modArticle 요청 */
				articleVO.setParentno(0);
				articleVO.setId((String) request.getAttribute("id"));
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImagefilename(imageFileName);
				boardService.modArticle(articleVO);
				/* 이미지가 있을 경우 동작 */
				if (imageFileName != null && imageFileName.length() != 0) {
					/* 이전 파일을 새로운 파일로 대체  */
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					;
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('수정완료.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
				/* 글 삭제 요청 */
			} else if (action.equals("/removeArticle.do")) {
				/* HttpRequest로부터 articleNO 받아와서 removeArticle 수행 */
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				List<Integer> articleNOList = boardService.removeArticle(articleNO);
				/* articleNO와 일치하는 이미지들 삭제 */
				for (int _articleNO : articleNOList) {
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					if (imgDir.exists()) {
						FileUtils.deleteDirectory(imgDir);
					}
				}

				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('삭제완료.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;
				/* 답글 달기 폼으로 이동 */
			} else if (action.equals("/replyForm.do")) {
//				System.out.println(request.getParameter("parentNO"));
				/* 세션에 부모 글 번호를 넣어 답글을 달 수 있도록 함 */
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				session = request.getSession();
				session.setAttribute("parentNO", parentNO);
				nextPage = "/board0/replyForm.jsp";
				/* 답글 달기 완료 요청 */
			} else if (action.equals("/addReply.do")) {
				/* 세션의 부모 글 번호를 받아  */
				session = request.getSession();
				System.out.println("ParentNO: " + (Integer) session.getAttribute("parentNO"));
				int parentNO = (Integer) session.getAttribute("parentNO");
				session.removeAttribute("parentNO");
				/* 글 쓰기와 같은 방식으로 HttpRequest로부터 정보 받아와서 로컬변수에 매핑 */
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				/* VO에 세팅 */
				articleVO.setParentno(parentNO);
				articleVO.setId((String) session.getAttribute("id"));
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImagefilename(imageFileName);
				/* 새로운 글 번호  */
				int articleNO = boardService.addReply(articleVO);
				/* 글 쓰기의 이미지 삽입과 동일 */
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('답변 완료.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
			} 
			/* 위의 nextPage를 기반으로 dispatch 실행 */
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* 업로드 메서드 */
	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		/* 이미지 저장 위치 디렉토리 객체화 */
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		/* 파일 업로드를 위한 세팅 */
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			/* 프론트 http request 파싱 */
			List items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				/* 파일 갯수만큼 동작, FileItem 객체가 파일 데이터(true)인지 폼 데이터(false)인지 확인. */
				FileItem fileItem = (FileItem) items.get(i);
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					/* 프론트에서 multipart/form-data를 받았으므로 이미지는 아래 else문 실행 */
				} else {
					/* 파일이 정상적으로 존재하다면 */
					if (fileItem.getSize() > 0) {
						/* 실제 파일 이름과 경로 설정 */
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}
						/* 파일 이름 추출하여 객체화 */
						String fileName = fileItem.getName().substring(idx + 1);
						articleMap.put(fileItem.getFieldName(), fileName);
						/* 파일 객체를 디스크에 저장 */
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}
}
