package rpc;

import java.io.Console;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();

		try {
			HttpSession session = request.getSession(false);//false means: if the request currently dont have a session, then we will not automatically create/connect new ones  
			JSONObject object = new JSONObject();
			if(session != null) {
				String userId = session.getAttribute("user_id").toString();
				object.put("status", "OK").put("name", connection.getFullname(userId));
			}else {
				object.put("status", "Invalid session");
				response.setStatus(403);
			}
			RpcHelper.writeJsonObject(response, object);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		
		try {
			JSONObject object = RpcHelper.readJSONObject(request);
			String userId = object.getString("user_id");
			//System.out.println(object.toString(4));
			//System.out.println(userId);
			String password = object.getString("password");
			
			JSONObject result = new JSONObject();
			if(connection.verifyLogin(userId, password)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				session.setMaxInactiveInterval(600);
				result.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} else {
				 result.put("status", "User doesn't exist");
				 response.setStatus(401);
			}
			RpcHelper.writeJsonObject(response, result);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

}
