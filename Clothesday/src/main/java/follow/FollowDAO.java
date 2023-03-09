package follow;

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

import postScrap.PostScrapDAO;
import user.UserDAO;

public class FollowDAO {

	private String FO_FOL_ID;
	private String FO_ME_ID;
	private String FO_DA;

	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;

	public FollowDAO() {
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
	
	public int checkFollow(FollowDAO fo) { // 팔로우 여부 확인
		try {
	
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("SELECT count(FO_ME_ID) FROM FOLLOW where FO_FOL_ID = ?");			
			pst.setString(1, fo.getFO_FOL_ID());			
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
	
	
	public int addFollow(FollowDAO fo) {
		try {
	
			PreparedStatement pst = con
					.prepareStatement("INSERT FOLLOW (FO_ME_ID, FO_FOL_ID) VALUES (?,?)");
			pst.setString(1, fo.getFO_ME_ID());
			pst.setString(2, fo.getFO_FOL_ID());	
	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int unFollow(FollowDAO fo) {
		try {
		
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM FOLLOW where FO_ME_ID = ? AND FO_FOL_ID = ?");
			pst.setString(1, fo.getFO_ME_ID());
			pst.setString(2, fo.getFO_FOL_ID());		
	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int countFollowing(FollowDAO fo) { // 내가 팔로우한
		try {
			
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("SELECT count(FO_ME_ID) FROM FOLLOW where FO_FOL_ID = ?");			
			pst.setString(1, fo.getFO_FOL_ID());			
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
	
	public int countFollower(FollowDAO fo) { // 나를 팔로우한
		try {
	
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("SELECT count(FO_FOL_ID) FROM FOLLOW where FO_ME_ID = ?");			
			pst.setString(1, fo.getFO_ME_ID());			
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
	
	public JSONArray getFollower(FollowDAO fo) {
		try { 
			int jCount = 0;
			JSONArray jArray = new JSONArray();
			String FO_ME_ID = fo.getFO_ME_ID();
			PreparedStatement pst = con.prepareStatement("SELECT FO_FOL_ID ,ME_PIC, ME_NICK FROM MEMBER m join FOLLOW f on m.ME_ID = f.FO_FOL_ID where FO_ME_ID = ? order BY FO_DA DESC"); 
			pst.setString(1, FO_ME_ID);		
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();		
				jobj.put("FO_FOL_ID", rs.getString(1));
				jobj.put("ME_PIC", rs.getString(2));
				jobj.put("ME_NICK", rs.getString(3));	
				jArray.add(jCount, jobj);
				jCount++;
			}
			return jArray; 
						
			} catch (Exception e) {
				e.printStackTrace(); 
				return null; 
		}
	}
	
	public JSONArray getFollowing(FollowDAO fo) {
		try { 
			int jCount = 0;
			JSONArray jArray = new JSONArray();
			String FO_FOL_ID = fo.getFO_FOL_ID();
			PreparedStatement pst = con.prepareStatement("SELECT FO_ME_ID ,ME_PIC, ME_NICK FROM MEMBER m join FOLLOW f on m.ME_ID = f.FO_ME_ID where FO_FOL_ID = ? order BY FO_DA DESC"); 
			pst.setString(1, FO_FOL_ID);		
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();		
				jobj.put("FO_ME_ID", rs.getString(1));
				jobj.put("ME_PIC", rs.getString(2));
				jobj.put("ME_NICK", rs.getString(3));	
				jArray.add(jCount, jobj);
				jCount++;
			}
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
	
	
	
	
	
	
	public String getFO_FOL_ID() {
		return FO_FOL_ID;
	}

	public void setFO_FOL_ID(String fO_FOL_ID) {
		FO_FOL_ID = fO_FOL_ID;
	}

	public String getFO_ME_ID() {
		return FO_ME_ID;
	}

	public void setFO_ME_ID(String fO_ME_ID) {
		FO_ME_ID = fO_ME_ID;
	}

	public String getFO_DA() {
		return FO_DA;
	}

	public void setFO_DA(String fO_DA) {
		FO_DA = fO_DA;
	}

}
