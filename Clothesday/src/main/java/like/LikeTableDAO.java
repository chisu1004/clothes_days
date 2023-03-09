package like;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import post.PostDAO;
import postScrap.PostScrapDAO;

public class LikeTableDAO {
	
	
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;
	
	int LIKE_PO_ID;
	String LIKE_ME_ID;


	


	public LikeTableDAO() {
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
	

	public int checkPostLike(LikeTableDAO like) { // 게시글 좋아요 여부 확인
		try {
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("SELECT count(LIKE_PO_ID) FROM LIKETABLE where LIKE_PO_ID = ? AND LIKE_ME_ID = ?");
			
			pst.setInt(1, like.getLIKE_PO_ID());
			pst.setString(2, like.getLIKE_ME_ID());		
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
	
	
	

	public int addLikeTable(LikeTableDAO like) {
		try {
			PreparedStatement pst = con
					.prepareStatement("INSERT LIKETABLE (LIKE_ME_ID, LIKE_PO_ID) VALUES (?,?)");
			pst.setString(1, like.getLIKE_ME_ID());
			pst.setInt(2, like.getLIKE_PO_ID());
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int addPO_LIKE(LikeTableDAO like) {
		try {
			PreparedStatement pst = con
					.prepareStatement("update POST set PO_LIKE = PO_LIKE + 1 where PO_ID = ?");	
			pst.setInt(1, like.getLIKE_PO_ID());

			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int deletePO_LIKE(LikeTableDAO like) {
		try {
			PreparedStatement pst = con
					.prepareStatement("update POST set PO_LIKE = PO_LIKE - 1 where PO_ID = ?");	
			pst.setInt(1, like.getLIKE_PO_ID());	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//회원탈퇴시 좋아요 삭제
	public int deleteAllUserPO_LIKE(LikeTableDAO like) {
		try {
			PreparedStatement pst = con
					.prepareStatement("update POST p join LIKETABLE li on p.PO_ID = li.LIKE_PO_ID set PO_LIKE = PO_LIKE - 1 where LIKE_ME_ID = ?");	
			pst.setString(1, like.getLIKE_ME_ID());	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	

	public int deleteLike(LikeTableDAO like) {
		try {
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM LIKETABLE where LIKE_PO_ID = ? AND LIKE_ME_ID = ?");
			pst.setInt(1, like.getLIKE_PO_ID());
			pst.setString(2, like.getLIKE_ME_ID());	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public JSONArray getAllLikePost(LikeTableDAO like) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select LIKE_PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC from POST p right join LIKETABLE l on p.PO_ID = l.LIKE_PO_ID where LIKE_ME_ID = ? order BY PO_REG_DA DESC");
			pst.setString(1, like.getLIKE_ME_ID());			
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
	
	
	public void closeConnection() {
		try {
			this.con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public int getLIKE_PO_ID() {
		return LIKE_PO_ID;
	}



	public void setLIKE_PO_ID(int lIKE_PO_ID) {
		LIKE_PO_ID = lIKE_PO_ID;
	}



	public String getLIKE_ME_ID() {
		return LIKE_ME_ID;
	}



	public void setLIKE_ME_ID(String lIKE_ME_ID) {
		LIKE_ME_ID = lIKE_ME_ID;
	}





}
