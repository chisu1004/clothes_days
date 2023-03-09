package reply;

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

public class ReplyDAO {
	
	int RE_ID;
	int RE_PO_ID;
	String RE_CON;
	String RE_ME_ID;
	String RE_REG_DA;
	String RE_CHA_DA;
	
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;
	
	public ReplyDAO() {
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
	
	
	public JSONArray getPostReply(ReplyDAO reply) {
		ReplyDAO dao = new ReplyDAO();
		JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성
		int jCount = 0;
		try {					
			PreparedStatement pst = con.prepareStatement("select RE_ID, RE_REG_DA, RE_CON, ME_PIC, ME_NICK, RE_ME_ID from REPLY r left join MEMBER m on r.RE_ME_ID = m.ME_ID WHERE RE_PO_ID = ? order BY RE_REG_DA DESC");	
			pst.setInt(1, reply.getRE_PO_ID());
			rs = pst.executeQuery();
			while (rs.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("RE_ID", rs.getInt(1));			
				Timestamp stamp = rs.getTimestamp(2);	
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
				String RE_REG_DA = format2.format(new Date(stamp.getTime()));	
				jobj.put("RE_REG_DA", RE_REG_DA);		
				jobj.put("RE_CON", rs.getString(3));
				jobj.put("ME_PIC", rs.getString(4));
				jobj.put("ME_NICK", rs.getString(5));
				jobj.put("RE_ME_ID", rs.getString(6));
				jArray.add(jCount, jobj);
				jCount++;
			}	
			pst.close();	
		} catch (Exception e) {
			e.printStackTrace();		
		}				
		return jArray;
				
	}
	
	public int addReply(ReplyDAO dao) {		
		try {
			
			PreparedStatement pst = con
					.prepareStatement("INSERT REPLY (RE_CON, RE_ME_ID, RE_PO_ID) VALUES (?,?,?)");
			pst.setString(1, dao.getRE_CON());
			pst.setString(2, dao.getRE_ME_ID());	
			pst.setInt(3, dao.getRE_PO_ID());	
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}	
	
	}
	
	public int deleteReply(ReplyDAO reply) {
		try {
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM REPLY WHERE RE_ID = ?");
			pst.setInt(1, reply.getRE_ID());		
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
	
	
	
	public int getRE_ID() {
		return RE_ID;
	}

	public void setRE_ID(int rE_ID) {
		RE_ID = rE_ID;
	}

	public int getRE_PO_ID() {
		return RE_PO_ID;
	}

	public void setRE_PO_ID(int rE_PO_ID) {
		RE_PO_ID = rE_PO_ID;
	}

	public String getRE_ME_ID() {
		return RE_ME_ID;
	}

	public void setRE_ME_ID(String rE_ME_ID) {
		RE_ME_ID = rE_ME_ID;
	}

	public String getRE_REG_DA() {
		return RE_REG_DA;
	}

	public void setRE_REG_DA(String rE_REG_DA) {
		RE_REG_DA = rE_REG_DA;
	}

	public String getRE_CHA_DA() {
		return RE_CHA_DA;
	}

	public void setRE_CHA_DA(String rE_CHA_DA) {
		RE_CHA_DA = rE_CHA_DA;
	}

	public String getRE_CON() {
		return RE_CON;
	}

	public void setRE_CON(String rE_CON) {
		RE_CON = rE_CON;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
