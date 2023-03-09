package post;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PostDAO {

	private int PO_ID;
	private String PO_CON;
	private String PO_ME_ID;
	private int PO_LIKE;
	private String PO_TAG;
	private String PO_CATE;
	private String PO_PIC;
	private String PO_REG_DA;
	
	

	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;


	public PostDAO() {
		try {
			String dbURL = "jdbc:mariadb://dlsxjsptb.cafe24.com:3306/dlsxjsptb?characterEncoding=UTF-8&serverTimezone=UTC";
			String dbID = "dlsxjsptb";
			String dbPwd = "cloth17203";
			Class.forName("org.mariadb.jdbc.Driver");
			con = DriverManager.getConnection(dbURL, dbID, dbPwd);
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public PostDAO(Connection con, Statement stmt) {
		try {
			this.con = con;
			this.stmt = stmt;
			String dbURL = "jdbc:mariadb://dlsxjsptb.cafe24.com:3306/dlsxjsptb?characterEncoding=UTF-8&serverTimezone=UTC";
			String dbID = "dlsxjsptb";
			String dbPwd = "cloth17203";
			Class.forName("org.mariadb.jdbc.Driver");
			con = DriverManager.getConnection(dbURL, dbID, dbPwd);
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int addPost(PostDAO postDAO) {
		try {
			PreparedStatement pst = con
					.prepareStatement("INSERT POST (PO_CON, PO_ME_ID, PO_TAG, PO_CATE, PO_PIC) VALUES (?,?,?,?,?)");
			pst.setString(1, postDAO.getPO_CON());
			pst.setString(2, postDAO.getPO_ME_ID());
			pst.setString(3, postDAO.getPO_TAG());
			pst.setString(4, postDAO.getPO_CATE());
			pst.setString(5, postDAO.getPO_PIC());
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	public int updatePost(PostDAO postDAO) {
		try {
			PreparedStatement pst = con
					.prepareStatement("UPDATE POST SET PO_CON = ?, PO_ME_ID = ?, PO_TAG = ?, PO_CATE = ?, PO_PIC = ? WHERE PO_ID = ?");
			pst.setString(1, postDAO.getPO_CON());
			pst.setString(2, postDAO.getPO_ME_ID());
			pst.setString(3, postDAO.getPO_TAG());
			pst.setString(4, postDAO.getPO_CATE());
			pst.setString(5, postDAO.getPO_PIC());
			pst.setInt(6, postDAO.getPO_ID());
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public ArrayList<PostDAO> getUserPO_PIC(PostDAO postDAO) {
		try {
			int jCount = 0;
			ArrayList<PostDAO> mList = new ArrayList<PostDAO>();
			PreparedStatement pst = con.prepareStatement("select PO_PIC, PO_ID from POST where PO_ME_ID = ?");
			pst.setString(1, postDAO.getPO_ME_ID());		
						
			rs = pst.executeQuery();
			
			while (rs.next()) {
				PostDAO post = new PostDAO();
				post.setPO_PIC(rs.getString(1));
				post.setPO_ID(rs.getInt(2));
				mList.add(post);
				jCount++;
			}		
			
			pst.close();	
			return mList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	

	
	public int countLike(PostDAO post) { // 게시글 좋아요 갯수 세기
		try {
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("SELECT PO_LIKE FROM POST where PO_ID = ?");
			
			pst.setInt(1, post.getPO_ID());	
			rs = pst.executeQuery();
			if (rs.next()) {
				res = rs.getInt(1);
			}
	
			return res;			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}	
	
	public int deletePost(PostDAO post) {
		try {
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM POST WHERE PO_ID = ?");
			pst.setInt(1, post.getPO_ID());		
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int deleteAllPost(PostDAO post) {
		try {
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM POST WHERE PO_ME_ID = ?");
			pst.setString(1, post.getPO_ME_ID());		
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	public JSONArray getMonthPost(PostDAO postDAO) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC, ME_PIC, ME_NICK, PO_ME_ID from POST p left join MEMBER m ON p.PO_ME_ID = m.ME_ID where PO_CATE = ? order BY PO_REG_DA DESC");
			pst.setString(1, postDAO.getPO_CATE());			
			JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String PO_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("PO_REG_DA", PO_REG_DA);
				jobj.put("PO_TAG", rs.getString(3));
				jobj.put("PO_CATE", rs.getString(4));
				jobj.put("PO_PIC", rs.getString(5));			
				jobj.put("ME_PIC", rs.getString(6));
				jobj.put("ME_NICK", rs.getString(7));
				jobj.put("PO_ME_ID", rs.getString(8));
				jArray.add(jCount, jobj);
				jCount++;
			}		
			pst.close();	
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public JSONArray getRecommendPost(PostDAO postDAO) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_CON, PO_TAG, PO_CATE, PO_PIC, PO_ME_ID, PO_LIKE, ME_NICK, ME_PIC from POST p join MEMBER m on p.PO_ME_ID = m.ME_ID where PO_TAG LIKE CONCAT('%' , ? , '%')");
			pst.setString(1, postDAO.getPO_TAG());			
			JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성
			rs = pst.executeQuery();
			if (rs.next()) {			
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String PO_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("PO_REG_DA", PO_REG_DA);
				jobj.put("PO_CON", rs.getString(3));
				jobj.put("PO_TAG", rs.getString(4));
				jobj.put("PO_CATE", rs.getString(5));
				jobj.put("PO_PIC", rs.getString(6));
				jobj.put("PO_ME_ID", rs.getString(7));
				jobj.put("PO_LIKE", rs.getInt(8));
				jobj.put("ME_NICK", rs.getString(9));
				jobj.put("ME_PIC", rs.getString(10));
				jArray.add(0, jobj);			
			}		
			pst.close();	
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	
	public int countPost(PostDAO postDAO) {
		try {
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("select count(*) from POST where PO_ME_ID = ?");
			pst.setString(1, postDAO.getPO_ME_ID());
			rs = pst.executeQuery();
			if (rs.next()) {
				res = rs.getInt(1);				
			}		
		
			pst.close();	
		
			return res;
		} catch (Exception e) {
			e.printStackTrace();	
		
			return -1;
		}
	}
	

	public JSONArray getUserPost(PostDAO postDAO) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC, PO_ME_ID from POST where PO_ME_ID = ? order BY PO_REG_DA DESC");
			pst.setString(1, postDAO.getPO_ME_ID());			
			JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String PO_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("PO_REG_DA", PO_REG_DA);
				jobj.put("PO_TAG", rs.getString(3));
				jobj.put("PO_CATE", rs.getString(4));
				jobj.put("PO_PIC", rs.getString(5));
				jArray.add(jCount, jobj);
				jCount++;
			}
		
			pst.close();				
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public JSONArray getPost(PostDAO postDAO) {
		try {
			
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_CON, PO_TAG, PO_CATE, PO_PIC, PO_ME_ID, PO_LIKE, ME_NICK, ME_PIC from POST p join MEMBER m on p.PO_ME_ID = m.ME_ID where PO_ID = ?");
			pst.setInt(1, postDAO.getPO_ID());		
			JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성	
			rs = pst.executeQuery();
			if (rs.next()) {			
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String PO_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("PO_REG_DA", PO_REG_DA);
				jobj.put("PO_CON", rs.getString(3));
				jobj.put("PO_TAG", rs.getString(4));
				jobj.put("PO_CATE", rs.getString(5));
				jobj.put("PO_PIC", rs.getString(6));
				jobj.put("PO_ME_ID", rs.getString(7));
				jobj.put("PO_LIKE", rs.getInt(8));
				jobj.put("ME_NICK", rs.getString(9));
				jobj.put("ME_PIC", rs.getString(10));
				jArray.add(0, jobj);				
			}	
		
			pst.close();	
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

	public PostDAO getPostPicture(PostDAO postDAO) {
		try {
			
			PreparedStatement pst = con.prepareStatement("select PO_PIC from POST where PO_ID = ?");
			pst.setInt(1, postDAO.getPO_ID());	
		
			rs = pst.executeQuery();
			if (rs.next()) {			
				postDAO.setPO_PIC(rs.getString(1));		
			}			
			pst.close();	
			return postDAO;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getAllPost() {
		PostDAO dao = new PostDAO();
		JSONArray jArray = new JSONArray();
		int jCount = 0;
		try {					
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC, ME_PIC, ME_NICK, PO_ME_ID from POST p left join MEMBER m ON p.PO_ME_ID = m.ME_ID order BY PO_REG_DA DESC");			
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String PO_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("PO_REG_DA", PO_REG_DA);
				jobj.put("PO_TAG", rs.getString(3));
				jobj.put("PO_CATE", rs.getString(4));
				jobj.put("PO_PIC", rs.getString(5));			
				jobj.put("ME_PIC", rs.getString(6));
				jobj.put("ME_NICK", rs.getString(7));
				jobj.put("PO_ME_ID", rs.getString(8));
				jArray.add(jCount, jobj);
				jCount++;
			}			
			pst.close();
	
		} catch (Exception e) {
			e.printStackTrace();		
		}				
		return jArray;
	}
	
	public JSONArray getBestPost() {
		JSONArray jArray = new JSONArray(); 
		int jCount = 0;
		try {					
			PreparedStatement pst = con.prepareStatement("select PO_ID, ME_PIC, ME_NICK from POST p left join MEMBER m ON p.PO_ME_ID = m.ME_ID WHERE PO_REG_DA BETWEEN DATE_ADD(NOW(), INTERVAL -1 WEEK) AND NOW() order BY PO_LIKE DESC LIMIT 5");					
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));
				jobj.put("ME_PIC", rs.getString(2));
				jobj.put("ME_NICK", rs.getString(3));			
				jArray.add(jCount, jobj);
				jCount++;
			}			
			pst.close();			
		} catch (Exception e) {
			e.printStackTrace();		
		}				
		return jArray;
	}
	
		public JSONArray getOneBestPost(PostDAO post) {
	
		JSONArray jArray = new JSONArray();
		int jCount = 0;
		try {					
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC, ME_PIC, ME_NICK, PO_ME_ID from POST p left join MEMBER m ON p.PO_ME_ID = m.ME_ID where PO_ID = ? order BY PO_REG_DA DESC");		
			pst.setInt(1, post.getPO_ID());		
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("PO_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String PO_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("PO_REG_DA", PO_REG_DA);
				jobj.put("PO_TAG", rs.getString(3));
				jobj.put("PO_CATE", rs.getString(4));
				jobj.put("PO_PIC", rs.getString(5));			
				jobj.put("ME_PIC", rs.getString(6));
				jobj.put("ME_NICK", rs.getString(7));
				jobj.put("PO_ME_ID", rs.getString(8));
				jArray.add(jCount, jobj);
				jCount++;
			}			
			pst.close();
	
		} catch (Exception e) {
			e.printStackTrace();		
		}				
		return jArray;
	}
		

		public void closeConnection() {
			try {
				this.con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	

	public String getPO_CON() {
		return PO_CON;
	}

	public void setPO_CON(String pO_CON) {
		PO_CON = pO_CON;
	}

	public String getPO_ME_ID() {
		return PO_ME_ID;
	}

	public void setPO_ME_ID(String pO_ME_ID) {
		PO_ME_ID = pO_ME_ID;
	}

	public int getPO_LIKE() {
		return PO_LIKE;
	}

	public void setPO_PIC(String pO_PIC) {
		PO_PIC = pO_PIC;
	}

	public String getPO_PIC() {
		return PO_PIC;
	}

	public void setPO_LIKE(int pO_LIKE) {
		PO_LIKE = pO_LIKE;
	}

	public String getPO_TAG() {
		return PO_TAG;
	}

	public void setPO_TAG(String pO_TAG) {
		PO_TAG = pO_TAG;
	}

	public String getPO_CATE() {
		return PO_CATE;
	}

	public void setPO_CATE(String pO_CATE) {
		PO_CATE = pO_CATE;
	}

	public int getPO_ID() {
		return PO_ID;
	}

	public void setPO_ID(int pO_ID) {
		PO_ID = pO_ID;
	}
	
	public String getPO_REG_DA() {
		return PO_REG_DA;
	}

	public void setPO_REG_DA(String pO_REG_DA) {
		PO_REG_DA = pO_REG_DA;
	}


}