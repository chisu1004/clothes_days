package user;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import post.PostDAO;

public class UserDAO {
	private String ME_ID; 
	private String ME_PW;

	private String ME_NICK;

	private String ME_VERIFY;
	private String ME_PIC;
	private String ME_TOKEN;
	
	
	
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;
	
	public UserDAO() {
		try {
			String dbURL = "jdbc:mariadb://dlsxjsptb.cafe24.com:3306/dlsxjsptb?characterEncoding=UTF-8&serverTimezone=UTC";
			String dbID = "dlsxjsptb";
			String dbPwd = "cloth17203";
			Class.forName("org.mariadb.jdbc.Driver");
			con = DriverManager.getConnection(dbURL,dbID,dbPwd);
			stmt = con.createStatement();	
		} catch (Exception e) {		
			e.printStackTrace();
		}
		
	}
	
	public int loginRecord(UserDAO userDAO) {	
		try { 
			PreparedStatement pst = con.prepareStatement("INSERT LOGIN (LOG_ME_ID) VALUES (?)"); 
			pst.setString(1, userDAO.getME_ID()); 
			return pst.executeUpdate(); 
			} 
		catch (Exception e) {
			e.printStackTrace(); 
			return -1; 
		}
		
	}
	
	public int setToken(UserDAO userDAO) {
		try { 
			PreparedStatement pst = con.prepareStatement("UPDATE MEMBER SET ME_TOKEN = ? WHERE ME_ID = ?"); 
			pst.setString(1, userDAO.getME_TOKEN()); 
			pst.setString(2, userDAO.getME_ID()); 
			return pst.executeUpdate(); 
			} 
		catch (Exception e) {
			e.printStackTrace(); 
			return -1; 
		}		
	}
	
	public ArrayList<String> getTokenStringArray() {
		try {
			 ArrayList<String> token = new ArrayList<String>();			
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select ME_TOKEN from MEMBER where ME_TOKEN IS NOT NULL");				
			rs = pst.executeQuery();
			while (rs.next()) {			
				 token.add(rs.getString(1));							
			}		
			pst.close();	
			return token;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public JSONArray getTokenJSONArray() {
		try {
			int jCount = 0;
			PreparedStatement pst = con.prepareStatement("select ME_TOKEN from MEMBER where ME_TOKEN IS NOT NULL");	
			JSONArray jArray = new JSONArray(); // 객체 리스트를 담을 JSON 배열 생성
			rs = pst.executeQuery();
			while (rs.next()) {			
				jArray.add(rs.getString(1));				
			}		
			pst.close();	
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	


	public int setPassword(UserDAO user) {
		try {
			PreparedStatement pst = con
					.prepareStatement("UPDATE MEMBER SET ME_PW = ? WHERE ME_ID = ?");
			pst.setString(1, user.getME_PW());	
			pst.setString(2, user.getME_ID());		
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int withdrawal(UserDAO user) {
		try {
			PreparedStatement pst = con
					.prepareStatement("DELETE FROM MEMBER WHERE ME_ID = ?");
			pst.setString(1, user.getME_ID());		
			return pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	
	public boolean checkId(String ME_ID, String ME_VERIFY) {
		try {
			PreparedStatement pst = con.prepareStatement("SELECT * FROM MEMBER WHERE ME_ID = ?");
			pst.setString(1, ME_ID);
			rs = pst.executeQuery();
			if(rs.next()) {
				return false;
			}else {				
				return true;
			}
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		return false;
		
	}
	
	

	public int checkNick(String ME_NICK) {
		try {
			PreparedStatement pst = con.prepareStatement("SELECT * FROM MEMBER WHERE ME_NICK = ?");
			pst.setString(1, ME_NICK);
			rs = pst.executeQuery();
			if(rs.next()) {
				return 0;
			}else {				
				return 1;
			}
		} catch (SQLException e) {			
			e.printStackTrace();
			return 0;
		}		
	}
		
	
	
	
	// -2: id �뾾�쓬 / -1: �꽌踰� �삤瑜� / 0: 鍮꾨�踰덊샇 ��由� / 1: �꽦怨�
	public int login(String ME_ID, String ME_PW) {
		try { 
			PreparedStatement pst = con.prepareStatement("SELECT ME_PW FROM MEMBER WHERE ME_ID = ?"); 
			pst.setString(1, ME_ID); 
			rs = pst.executeQuery();
			if (rs.next()) { 		
				return rs.getString(1).equals(ME_PW) ? 1 : 0; 
				} else {				
					return -2; 
					} 
			} catch (Exception e) { e.printStackTrace(); return -1; }
	}	
	
	

	public int join(UserDAO userDAO) {	
		try { 
			PreparedStatement pst = con.prepareStatement("INSERT MEMBER (ME_ID, ME_PW, ME_NICK) VALUES (?,?,?)"); 
			pst.setString(1, userDAO.getME_ID()); 
			pst.setString(2, userDAO.getME_PW()); 
			pst.setString(3, userDAO.getME_NICK());	
			return pst.executeUpdate(); 
			} 
		catch (Exception e) {
			e.printStackTrace(); 
			return -1; 
		}
	}
	
	public UserDAO getUser(String ME_ID) {
		try { 
			PreparedStatement pst = con.prepareStatement("SELECT ME_ID, ME_PW, ME_NICK FROM MEMBER where ME_ID = ?"); 
			pst.setString(1, ME_ID);
			rs = pst.executeQuery();
			UserDAO userDAO = new UserDAO();
			if(rs.next()) {				
				userDAO.setME_ID(rs.getString(1));
				userDAO.setME_PW(rs.getString(2));		
				userDAO.setME_NICK(rs.getString(3));		
				return userDAO;
			} else{ 

				return userDAO;				
				}				
			}
			 catch (Exception e) {
				e.printStackTrace(); 
		}
		return null; 
	}	
	
	
	public UserDAO getProfile(UserDAO dao) {
		try { 
			String ME_ID = dao.getME_ID();
			PreparedStatement pst = con.prepareStatement("SELECT ME_PIC, ME_NICK, ME_ID FROM MEMBER where ME_ID = ?"); 
			pst.setString(1, ME_ID);		
			rs = pst.executeQuery();
			if(rs.next()) {				
				dao.setME_PIC(rs.getString(1));		
				dao.setME_NICK(rs.getString(2));				
			}
						
			} catch (Exception e) {
				e.printStackTrace(); 				
		}		
		
		return dao; 
	}
	
	
	public int setProfile(UserDAO dao) {
		try { 			
			PreparedStatement pst = con.prepareStatement("UPDATE MEMBER SET ME_PIC = ? where ME_ID = ?"); 
			pst.setString(1, dao.getME_PIC());
			pst.setString(2, dao.getME_ID());			
			return pst.executeUpdate();								
			} catch (Exception e) {
				e.printStackTrace(); 	
				return 0;
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
	
	
	
	public String getME_ID() {
		return ME_ID;
	}
	public void setME_ID(String mE_ID) {
		ME_ID = mE_ID;
	}
	public String getME_PW() {
		return ME_PW;
	}
	public void setME_PW(String mE_PW) {
		ME_PW = mE_PW;
	}

	public String getME_NICK() {
		return ME_NICK;
	}
	public void setME_NICK(String ME_NICK) {
		this.ME_NICK = ME_NICK;
	}

	
	public String getME_VERIFY() {
		return ME_VERIFY;
	}

	public void setME_VERIFY(String mE_VERIFY) {
		ME_VERIFY = mE_VERIFY;
	}

	public String getME_PIC() {
		return ME_PIC;
	}

	public void setME_PIC(String mE_PIC) {
		ME_PIC = mE_PIC;
	}
	
	
	public String getME_TOKEN() {
		return ME_TOKEN;
	}

	public void setME_TOKEN(String mE_TOKEN) {
		ME_TOKEN = mE_TOKEN;
	}

	
}