package postScrap;

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

public class PostScrapDAO {
	int PS_ID;
	int PS_PO_ID;
	String PS_DATE;
	String PS_ME_ID;
	
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;


	public PostScrapDAO() {
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
	
	
	public int addScrap(PostScrapDAO scrapDAO) {
		try {
	
			PreparedStatement pst = con
					.prepareStatement("INSERT POSTSCRAP (PS_PO_ID, PS_ME_ID) VALUES (?,?)");
			pst.setInt(1, scrapDAO.getPS_PO_ID());
			pst.setString(2, scrapDAO.getPS_ME_ID());	
	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int deleteScrap(PostScrapDAO scrapDAO) {
		try {
		
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM POSTSCRAP where PS_PO_ID = ? AND PS_ME_ID = ?");
			pst.setInt(1, scrapDAO.getPS_PO_ID());
			pst.setString(2, scrapDAO.getPS_ME_ID());	
	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int checkScrap(PostScrapDAO scrapDAO) { // 스크랩 여부 확인
		try {
	
			int res = 0;
			PreparedStatement pst = con
					.prepareStatement("SELECT count(PS_ID) FROM POSTSCRAP where PS_PO_ID = ? AND PS_ME_ID = ?");
			
			pst.setInt(1, scrapDAO.getPS_PO_ID());
			pst.setString(2, scrapDAO.getPS_ME_ID());		
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
	
	public JSONArray getUserScrap(PostScrapDAO scrapDAO) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select PS_PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC from POST p right join POSTSCRAP ps on p.PO_ID = ps.PS_PO_ID where PS_ME_ID = ? order BY PO_REG_DA DESC");
			pst.setString(1, scrapDAO.getPS_ME_ID());			
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
	

	public int getPS_ID() {
		return PS_ID;
	}

	public void setPS_ID(int pS_ID) {
		PS_ID = pS_ID;
	}

	public int getPS_PO_ID() {
		return PS_PO_ID;
	}

	public void setPS_PO_ID(int pS_PO_ID) {
		PS_PO_ID = pS_PO_ID;
	}

	public String getPS_DATE() {
		return PS_DATE;
	}

	public void setPS_DATE(String pS_DATE) {
		PS_DATE = pS_DATE;
	}

	public String getPS_ME_ID() {
		return PS_ME_ID;
	}

	public void setPS_ME_ID(String pS_ME_ID) {
		PS_ME_ID = pS_ME_ID;
	}
	
	
	

}
