package controller;

import controller.DatabaseConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


@ManagedBean(name="user")
@SessionScoped
public class user {
	
	private String lastname;
	private String firstname;
    private String userid;
    private String email;
    private String address;
    private String phonenumber;
    private String password;
    public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	private String dbPassword;
    private String dbName;
    private String role;
    private String dbRole;
    private String Balance;
    


	DataSource ds;
	
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDbRole() {
		return dbRole;
	}
	public void setDbRole(String dbRole) {
		this.dbRole = dbRole;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String register()
	{
		

			
			int i = 0;
			//System.out.println("HELLO");

				System.out.println("HELLOkjhfkljwneflkjne");
				Connection con = DatabaseConnect.getConnection();
		        PreparedStatement ps = null;
				try {
					  	String sql = "INSERT INTO users(firstname, lastname, address, phonenumber, email, username, password, role,balance) VALUES(?,?,?,?,?,?,?,?,100000)";
						ps = con.prepareStatement(sql);
						
						 ps.setString(1, this.firstname);
			             ps.setString(2, this.lastname);
			             ps.setString(3, this.address);
			             ps.setString(4, this.phonenumber);
			             ps.setString(5, this.email);
			             ps.setString(6, this.userid);
			             ps.setString(7, this.password);
			             ps.setString(8, this.role);
			             
			             ps.executeUpdate();
			             System.out.println("Data Added Successfully");
			             i++;
			            		 
					}
				  catch (Exception e) {
						System.err.println("Exception: " + e.getMessage());
					} finally {
						try {
							con.close();
						} 
						catch (SQLException e) {
						}
					}

			if (i > 0) {
	         return "registerSuccess";
		        } 
			else
			return "failed";
		    }
	public String getupdateData()
	{
		ResultSet rs = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
        	con = DatabaseConnect.getConnection();
               String sql = "select firstname,lastname,address,phonenumber from users where username = '"
                        + userid + "'";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                rs.next();
                firstname = rs.getString("firstname");
                lastname = rs.getString("lastname");
                address = rs.getString("address");
                phonenumber = rs.getString("phonenumber");
          }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        return "update";
	}
	public void dbData(String uname) {
         if (uname != null) {
            ResultSet rs = null;
            Connection con = null;
	        PreparedStatement ps = null;
         
           
                try {
                	
					con = DatabaseConnect.getConnection();
                    if (con != null) {
                        String sql = "select uid,username,password,role,balance from users where username = '"
                                + userid + "'";
                        System.out.println(userid);
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rs.next();
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("userid", rs.getString("username"));
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("uid", rs.getString("uid"));
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("bal", rs.getString("balance"));
                        dbName = rs.getString("username");
                        //System.out.println(dbName);
                        dbPassword = rs.getString("password");
                        dbRole = rs.getString("role");
                                            
                    }
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }

        }
    }
	public String getBalance() {
		ResultSet rs = null;
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
        	con = DatabaseConnect.getConnection();
            String sql = "select balance from users where username = '"
                    + userid + "'";
			ps = con.prepareStatement(sql);
			 rs = ps.executeQuery();
		        rs.next();
		        Balance = rs.getString("balance");
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return Balance;
	}
	public void setBalance(String balance) {
		this.Balance = balance;
	}
	public String log() {
        dbData(userid);
        if (userid.equals(dbName) && password.equals(dbPassword) && role.equals("manager"))  {
        	 
            return "managerPanel";
        } 
        else if (userid.equals(dbName) && password.equals(dbPassword) && role.equals("admin"))
        		{
        	
        	return "adminPanel";
        		}
        else if (userid.equals(dbName) && password.equals(dbPassword) && role.equals("user"))
		{
        	
        	return "userPanel";
		}
        else {
            return "failureAttempt";
        }    
    }
	public String update() {
   	 
		int i = 0;
		//System.out.println("HELLO");
//		if(name != null && userid != null && email != null && address != null && password != null  )
//		{
			//System.out.println("HELLOkjhfkljwneflkjne");
			Connection con = DatabaseConnect.getConnection();;
	        PreparedStatement ps = null;
			try {
				  	String sql = "UPDATE `users`  SET firstname = '" + firstname + "', lastname = '" + lastname + "', address = '" + address + "', phonenumber = '" + phonenumber + "' , email =  '" + email + "' WHERE username = '" + this.userid + "' ";
					ps = con.prepareStatement(sql);
					System.out.println("Pranav Here afterwards");
					ps.executeUpdate();
		            System.out.println("Data Updated Successfully");
		            i++;
				}
			  catch (Exception e) {
					System.err.println("Exception: " + e.getMessage());
				} finally {
					try {
						con.close();
					} 
					catch (SQLException e) {
					}
				}
 //if bracket close
		if (i > 0) {
         return "updateSuccess";
	        } 
		else {
		return "failed";
	    }
	}

   
    public String logout() {
    	 FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
         return "index.xhtml?faces-redirect=true";
     
    }
}



