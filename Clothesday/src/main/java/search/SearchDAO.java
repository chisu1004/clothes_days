package search;

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
import reply.ReplyDAO;

public class SearchDAO {
	int SE_ID;
	String SE_CON;
	String SE_DA;
	String SE_ME_ID;
	
	
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;
	
	public SearchDAO() {
		try {
			String dbURL = "jdbc:mariadb://dlsxjsptb.cafe24.com:3306/dlsxjsptb?characterEncoding=UTF-8";
			String dbID = "dlsxjsptb";
			String dbPwd = "cloth17203";
			Class.forName("org.mariadb.jdbc.Driver");
			con = DriverManager.getConnection(dbURL,dbID,dbPwd);
			stmt = con.createStatement();
		} catch (Exception e) {		
			e.printStackTrace();
		}		
	}
	
	public JSONArray getSearchResult(SearchDAO se) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select PO_ID, PO_REG_DA, PO_TAG, PO_CATE, PO_PIC, ME_PIC, ME_NICK, PO_ME_ID, PO_CON from POST p left join MEMBER m ON p.PO_ME_ID = m.ME_ID where PO_CON LIKE CONCAT('%' , ? , '%') order BY PO_REG_DA DESC");
			pst.setString(1, se.getSE_CON());			
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
				jobj.put("PO_CON", rs.getString(9));
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
	
	
	public JSONArray getRecentSearch(SearchDAO se) {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select SE_CON from SEARCH where SE_ME_ID = ? order BY SE_DA DESC LIMIT 9");
			pst.setString(1, se.getSE_ME_ID());			
			JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("SE_CON", rs.getString(1));	
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
	
	
	public int addSearchRecord(SearchDAO se) {		
		try {			
			PreparedStatement pst = con
					.prepareStatement("INSERT SEARCH (SE_CON, SE_ME_ID) VALUES (?,?)");
			pst.setString(1, se.getSE_CON());
			pst.setString(2, se.getSE_ME_ID());		
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
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
	
	
	
	
	
	public int getSE_ID() {
		return SE_ID;
	}
	public void setSE_ID(int sE_ID) {
		SE_ID = sE_ID;
	}
	public String getSE_CON() {
		return SE_CON;
	}
	public void setSE_CON(String sE_CON) {
		SE_CON = sE_CON;
	}
	public String getSE_DA() {
		return SE_DA;
	}
	public void setSE_DA(String sE_DA) {
		SE_DA = sE_DA;
	}
	public String getSE_ME_ID() {
		return SE_ME_ID;
	}
	public void setSE_ME_ID(String sE_ME_ID) {
		SE_ME_ID = sE_ME_ID;
	}


}
