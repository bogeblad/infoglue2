/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
 */

package org.infoglue.cms.util;

import org.apache.catalina.realm.*;

import java.util.*;
import java.sql.*;
import java.security.Principal;


public class InfoGlueCMSJDBCRealm extends JDBCRealm 
{
	private String sql = null;
	
    /**
     * Return a PreparedStatement configured to perform the SELECT required
     * to retrieve user roles for the specified username.
     *
     * @param dbConnection The database connection to be used
     * @param username Username for which roles should be retrieved
     *
     * @exception SQLException if a database error occurs
     */
    protected PreparedStatement roles(Connection dbConnection, String username) throws SQLException 
    {
    	try
    	{
			if (preparedRoles == null) 
	        {
	            StringBuffer sb = new StringBuffer("SELECT r.name FROM cmRole r, cmSystemUser u, cmSystemUserRole ur WHERE r.roleId = ur.roleId AND ur.systemUserId = u.systemUserId AND u.userName = ?");
	            this.sql = sb.toString();
	            CmsLogger.logInfo("Roles SQL:" + this.sql + ":" + username);
	            log("Roles SQL:" + this.sql + ":" + username);
	            preparedRoles = dbConnection.prepareStatement(this.sql);
	        }
	
	        preparedRoles.setString(1, username);
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace(System.out);
    		e.printStackTrace(System.err);
    		e.printStackTrace();
    		throw e;
    	}
    	
        return (preparedRoles);

    }
    
    /**
     * Open (if necessary) and return a database connection for use by
     * this Realm.
     *
     * @exception SQLException if a database error occurs
     */
    protected Connection open() throws SQLException 
    {
		System.out.println("Getting connection for Realm: " + connectionURL + " - " + connectionName + ":" + connectionPassword + "(" + driverName + ")");
		log("Getting connection for Realm: " + connectionURL + " - " + connectionName + ":" + connectionPassword + "(" + driverName + ")");
			
        // Do nothing if there is a database connection already open
        if (dbConnection != null)
            return (dbConnection);

        // Instantiate our database driver if necessary
        if (driver == null) 
        {
            try 
            {
                Class clazz = Class.forName(driverName);
                driver = (Driver) clazz.newInstance();
            } 
            catch (Throwable e) 
            {
                throw new SQLException(e.getMessage());
            }
        }

        // Open a new connection
        Properties props = new Properties();
        
        if (connectionName != null)
            props.put("user", connectionName);
        
        if (connectionPassword != null)
            props.put("password", connectionPassword);
        
        dbConnection = driver.connect(connectionURL, props);
        dbConnection.setAutoCommit(false);
        
        try
        {
			String sql = "SELECT * FROM cmSystemUser";
			PreparedStatement stmt = dbConnection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) 
			{
				String userName = rs.getString("userName").trim();
				System.out.println("[JDBCREALM-DEBUG] userName:" + userName);
				System.err.println("[JDBCREALM-DEBUG] userName:" + userName);
				log("[JDBCREALM-DEBUG] userName:" + userName);
			}
			rs.close();
        }
        catch(Exception e)
        {
			System.out.println("[JDBCREALM-DEBUG] An exception occurred when testing to get all users:" + e.getMessage());
			System.err.println("[JDBCREALM-DEBUG] An exception occurred when testing to get all users:" + e.getMessage());
			log("[JDBCREALM-DEBUG] An exception occurred when testing to get all users:" + e.getMessage());
        	e.printStackTrace(System.out);
			e.printStackTrace(System.err);
        }	
        				
        return (dbConnection);
    }


    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param dbConnection The database connection to be used
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     *
     * @exception SQLException if a database error occurs
     */
    
    public synchronized Principal authenticate(Connection dbConnection, String username, String credentials) throws SQLException 
    {
		try
		{
	        // Look up the user's credentials
	        String dbCredentials = null;
	        PreparedStatement stmt = credentials(dbConnection, username);
	        
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) 
	        {
	            dbCredentials = rs.getString(1).trim();
	        }
	        
	        rs.close();
	        
	        if (dbCredentials == null) 
	        {
	        	return (null);
	        }
	
	        // Validate the user's credentials
	        boolean validated = false;
	        if (hasMessageDigest()) 
	        {
	            // Hex hashes should be compared case-insensitive
	            validated = (digest(credentials).equalsIgnoreCase(dbCredentials));
	        } 
	        else
	            validated = (digest(credentials).equals(dbCredentials));
	
	        if (validated) 
	        {
	            if (debug >= 2)
	                log(sm.getString("jdbcRealm.authenticateSuccess", username));
	        } 
	        else 
	        {
	            if (debug >= 2)
	                log(sm.getString("jdbcRealm.authenticateFailure", username));
	            
	            return (null);
	        }
	
	        // Accumulate the user's roles
	        ArrayList list = new ArrayList();
	        stmt = roles(dbConnection, username);
	        rs = stmt.executeQuery();
	        while (rs.next()) 
	        {
	            list.add(rs.getString(1).trim());
	        }
	        
	        rs.close();
	        dbConnection.commit();
		
		    // Create and return a suitable Principal for this user
	        return (new GenericPrincipal(this, username, credentials, list));
		}
		catch(SQLException e)
		{
			System.out.println("Out: An error occurred when trying to authenticate: " + e.getMessage());
			System.out.println("Out: SQL used: " + this.sql);
			System.out.println("Out: Connection:" + dbConnection.getMetaData().getURL());
			System.err.println("Err: An error occurred when trying to authenticate: " + e.getMessage());
			System.err.println("Err: SQL used: " + this.sql);
			System.err.println("Err: Connection:" + dbConnection.getMetaData().getURL());
			log("Log: An error occurred when trying to authenticate: " + e.getMessage());
			log("Log: SQL used: " + this.sql);
			log("Log: Connection:" + dbConnection.getMetaData().getURL());
			e.printStackTrace(System.out);
			throw e;
		}
    }


    /**
     * Return a PreparedStatement configured to perform the SELECT required
     * to retrieve user credentials for the specified username.
     *
     * @param dbConnection The database connection to be used
     * @param username Username for which credentials should be retrieved
     *
     * @exception SQLException if a database error occurs
     */
    protected PreparedStatement credentials(Connection dbConnection, String username) throws SQLException 
    {

        if (preparedCredentials == null) {
            StringBuffer sb = new StringBuffer("SELECT ");
            sb.append(userCredCol);
            sb.append(" FROM ");
            sb.append(userTable);
            sb.append(" WHERE ");
            sb.append(userNameCol);
            sb.append(" = ?");
            
            System.out.println("Credentials SQL:" + sb.toString() + ":" + username);
	        log("Credentials SQL:" + sb.toString() + ":" + username);
	            
            preparedCredentials = dbConnection.prepareStatement(sb.toString());
        }

        preparedCredentials.setString(1, username);
        return (preparedCredentials);

    }


}
