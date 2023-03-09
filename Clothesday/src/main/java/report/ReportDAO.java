package report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import reply.ReplyDAO;

public class ReportDAO {
	
	int RP_ID;
	int RP_PO_ID;
	String RP_CON;
	String RP_ME_ID;
	
	String RP_VIL_ID;
	String RP_DA;
	
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stmt = null;
	
	public ReportDAO() {
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
	

	public int addReport(ReportDAO dao) {		
		try {
			
			PreparedStatement pst = con
					.prepareStatement("INSERT REPORT (RP_VIL_ID, RP_ME_ID, RP_CON, RP_PO_ID) VALUES (?,?,?,?)");
			pst.setString(1, dao.getRP_VIL_ID());
			pst.setString(2, dao.getRP_ME_ID());	
			pst.setString(3, dao.getRP_CON());	
			pst.setInt(4, dao.getRP_PO_ID());	
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
	

	
	public int getRP_ID() {
		return RP_ID;
	}

	public void setRP_ID(int rP_ID) {
		RP_ID = rP_ID;
	}

	public int getRP_PO_ID() {
		return RP_PO_ID;
	}

	public void setRP_PO_ID(int rP_PO_ID) {
		RP_PO_ID = rP_PO_ID;
	}

	public String getRP_CON() {
		return RP_CON;
	}

	public void setRP_CON(String rP_CON) {
		RP_CON = rP_CON;
	}

	public String getRP_ME_ID() {
		return RP_ME_ID;
	}

	public void setRP_ME_ID(String rP_ME_ID) {
		RP_ME_ID = rP_ME_ID;
	}

	public String getRP_VIL_ID() {
		return RP_VIL_ID;
	}

	public void setRP_VIL_ID(String rP_VIL_ID) {
		RP_VIL_ID = rP_VIL_ID;
	}

	public String getRP_DA() {
		return RP_DA;
	}

	public void setRP_DA(String rP_DA) {
		RP_DA = rP_DA;
	}

}
