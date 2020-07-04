package member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import member.MemberVO;

@WebServlet("/user/*")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MemberDAO memberDAO;
	MemberVO memberVO;
	
	public void init() throws ServletException {
		memberDAO = new MemberDAO();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)	
			throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response)	
			throws ServletException, IOException {
		String nextPage = "";	
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		HttpSession session;
		String action = request.getPathInfo();
		System.out.println("Member Action :" + action);
		try {
			/* 로그인 요청시 로그인 폼으로 이동 */
			if (action.equals("/login")) {
				nextPage = "/user0/loginForm.jsp";
			} 
			/* 로그인 제출시 로그인 시도 */
			else if (action.equals("/login.do")) {
				String id = request.getParameter("id");
				String pw = request.getParameter("pw");
				/* 로그인 성공 ? id : null */
				String loginId = memberDAO.getLoginId(id, pw);
				if (loginId != null) {
					session = request.getSession();
					session.setAttribute("id", loginId);				
				}
				
				nextPage = "/board/listArticles.do";
			}
			/* 회원가입 요청 */
			else if (action.equals("/register")) {	
				nextPage = "/user0/memberForm.jsp";
			}
			/* 회원가입 제출 시도 */
			else if (action.equals("/register.do")) {
				String id = request.getParameter("id");
				String pwd = request.getParameter("pwd");
				String name = request.getParameter("name");
				String email = request.getParameter("email");
				memberVO = new MemberVO(id, pwd, name, email);
				memberDAO.addMember(memberVO);
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('회원가입 완료. 로그인하세요.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;
			} 
			/* 로그아웃시 세션을 지움 */
			else if (action.equals("/logout.do")) {
				session = request.getSession();
				session.setAttribute("id", null);
				session.setMaxInactiveInterval(0);
				/* nextPage = "/board/listArticles.do"; */
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + " " + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;
			}
			
			/* 위의 nextPage를 기반으로 dispatch 실행 */
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
