package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Mutter;
import model.PostMutterLogic;
import model.User;

/**
 * Servlet implementation class Main
 */
@WebServlet("/Main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// つぶやきリストをアプリケーションスコープから取得
		ServletContext application = this.getServletContext();
		List<Mutter> mutterList = (List<Mutter>) application.getAttribute("mutterList");
		// 取得できなかった場合はつぶやきリストを作成して保存
		if(mutterList == null) {
			mutterList = new ArrayList<>();
			application.setAttribute("mutterList", mutterList);
		}

		// ログインしているか確認
		HttpSession session = request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");

		if(loginUser == null) {
			// ログインしていない場合
			response.sendRedirect("/docoTsubu/");
		} else {
			// フォワード
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/main.jsp");
			dispatcher.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// リクエストパラメータを取得
		request.setCharacterEncoding("UTF-8");
		String text = request.getParameter("text");

		// 入力値チェック
		if(text != null && text.length() != 0) {
			// アプリケーションスコープに保存されたつぶやきリストを取得
			ServletContext application = this.getServletContext();
			List<Mutter> mutterList = (List<Mutter>) application.getAttribute("mutterList");

			// セッションスコープ内のユーザ情報を取得
			HttpSession session = request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");

			// つぶやきをリストに追加
			Mutter mutter = new Mutter(loginUser.getName(), text);
			PostMutterLogic postMutterLogic = new PostMutterLogic();
			postMutterLogic.execute(mutter, mutterList);

			// アプリケーションスコープにリストを保存
			application.setAttribute("mutterList", mutterList);
		}

		// メイン画面にフォワード
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/main.jsp");
		dispatcher.forward(request, response);
	}
}
