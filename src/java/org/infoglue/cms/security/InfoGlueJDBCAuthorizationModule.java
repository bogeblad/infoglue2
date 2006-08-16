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

package org.infoglue.cms.security;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.BaseController;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupController;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleController;
import org.infoglue.cms.controllers.kernel.impl.simple.SystemUserController;
import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.Group;
import org.infoglue.cms.entities.management.GroupVO;
import org.infoglue.cms.entities.management.Role;
import org.infoglue.cms.entities.management.RoleVO;
import org.infoglue.cms.entities.management.SystemUser;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.util.CmsPropertyHandler;

/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against the ordinary infoglue database.
 */

public class InfoGlueJDBCAuthorizationModule extends BaseController implements AuthorizationModule
{
    private final static Logger logger = Logger.getLogger(InfoGlueJDBCAuthorizationModule.class.getName());

	private Properties extraProperties = null;
	private Database transactionObject 	= null;

    protected String connectionName = "cds";
    protected String connectionPassword = "cds";
    protected String connectionURL = "jdbc:jtds:sqlserver://localhost:1433;DatabaseName=cds;SelectMethod=Cursor";
    //protected Connection connection = null;
    protected Driver driver = null;
    protected String driverName = "net.sourceforge.jtds.jdbc.Driver";

	/**
	 * Gets is the implementing class can update as well as read 
	 */
	
	public boolean getSupportUpdate() 
	{
		return false;
	}

	/**
	 * Gets is the implementing class can delete as well as read 
	 */
	
	public boolean getSupportDelete()
	{
		return false;
	}
	
	/**
	 * Gets is the implementing class can create as well as read 
	 */
	
	public boolean getSupportCreate()
	{
		return false;
	}

	/**
     * Open (if necessary) and return a database connection for use by
     * this class.
     *
     * @exception SQLException if a database error occurs
     */
    protected Connection getConnection() throws SQLException 
    {
    	Connection conn = null;
    	
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
        
        conn = driver.connect(connectionURL, props);
        conn.setAutoCommit(false);
        
        return (conn);

    }

	/**
	 * Gets an authorized InfoGluePrincipal. If the user has logged in with the root-account
	 * we immediately return - otherwise we populate it.
	 */
	
	public InfoGluePrincipal getAuthorizedInfoGluePrincipal(String userName) throws Exception
	{
		System.out.println("getAuthorizedInfoGluePrincipal with userName:" + userName);
		
	    if(userName == null || userName.equals(""))
	    {
	        logger.warn("userName was null or empty - fix your templates:" + userName);
	        return null;
	    }
	    
		InfoGluePrincipal infogluePrincipal = null;
		
		String administratorUserName = CmsPropertyHandler.getAdministratorUserName();
		String administratorEmail 	 = CmsPropertyHandler.getAdministratorEmail();
		
		final boolean isAdministrator = (userName != null && userName.equalsIgnoreCase(administratorUserName)) ? true : false;
		if(isAdministrator)
		{
			infogluePrincipal = new InfoGluePrincipal(userName, "System", "Administrator", administratorEmail, new ArrayList(), new ArrayList(), isAdministrator);
		}
		else
		{	
			List roles = new ArrayList();
			List groups = new ArrayList();
			
			ResultSet rs = null;
			Connection conn = null;
			PreparedStatement ps = null;
			
			try 
			{
				String sql = "SELECT * from CDS_USER, CDS_ROLE_USER, CDS_ROLE where CDS_ROLE_USER.CDS_USER = CDS_USER.ID AND CDS_ROLE_USER.CDS_ROLE = CDS_ROLE.ID AND CDS_USER.USER_NAME = ?";
				
				conn = getConnection();
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, userName);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					String roleName = rs.getString("ROLE_NAME");
					String description = rs.getString("ROLE_DESCRIPTION");
					
					InfoGlueRole infoGlueRole = new InfoGlueRole(roleName, description);
					System.out.println("Adding infoGlueRole:" + infoGlueRole.getName());
					roles.add(infoGlueRole);
				}
				
				infogluePrincipal = new InfoGluePrincipal(userName, userName, userName, "undefined", roles, groups, isAdministrator);
				System.out.println("infogluePrincipal created:" + infogluePrincipal.getName());
			} 
			catch (Exception e) 
			{
				getLogger().info("An error occurred trying to get jdbc user for " + userName + ":" + e);
				throw new SystemException(e.getMessage());
			}
			finally
			{
				if (rs != null) 
				{
					try 
					{
						rs.close();
					} 
					catch (SQLException e) {}
				}
				if (ps != null) 
				{
					try 
					{
						ps.close();
					} 
					catch (SQLException e) {}
				}
				if (conn != null) 
				{
					try 
					{
						conn.close();
					} 
					catch (Exception ex) {}
				}
			}

			System.out.println("returning from getAuthorizedInfoGluePrincipal with userName:" + userName);
			
			/*
			
			Database db = CastorDatabaseService.getDatabase();

			try 
			{
				beginTransaction(db);
				
				SystemUser systemUser = SystemUserController.getController().getReadOnlySystemUserWithName(userName, db);
				if(systemUser != null)
				{
					Iterator roleListIterator = systemUser.getRoles().iterator();
					while(roleListIterator.hasNext())
					{
						Role role = (Role)roleListIterator.next();
						InfoGlueRole infoGlueRole = new InfoGlueRole(role.getRoleName(), role.getDescription());
						roles.add(infoGlueRole);
					}
	
					Iterator groupListIterator = systemUser.getGroups().iterator();
					while(groupListIterator.hasNext())
					{
					    Group group = (Group)groupListIterator.next();
						InfoGlueGroup infoGlueGroup = new InfoGlueGroup(group.getGroupName(), group.getDescription());
						groups.add(infoGlueGroup);
					}
	
					infogluePrincipal = new InfoGluePrincipal(userName, systemUser.getFirstName(), systemUser.getLastName(), systemUser.getEmail(), roles, groups, isAdministrator);
				}
				else
				{
				    logger.warn("Could not find user with userName '" + userName + "' - fix your template logic.");
				    infogluePrincipal = null;
				}
				
				commitTransaction(db);
			} 
			catch (Exception e) 
			{
				getLogger().info("An error occurred trying to get SystemUser for " + userName + ":" + e);
				rollbackTransaction(db);
				throw new SystemException(e.getMessage());
			}
			*/
		}

		return infogluePrincipal;
	}

	/**
	 * Gets an authorized InfoGlueRole.
	 */
	
	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName) throws Exception
	{
		InfoGlueRole infoglueRole = null;

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try 
		{
			String sql = "SELECT * from CDS_ROLE where CDS_ROLE.ROLE_NAME = ?";

			conn = getConnection();
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, roleName);
			
			rs = ps.executeQuery();
			while(rs.next())
			{
				String description = rs.getString("ROLE_DESCRIPTION");
				
				infoglueRole = new InfoGlueRole(roleName, description);
			}
			
			System.out.println("Role created:" + infoglueRole.getName());
		} 
		catch (Exception e) 
		{
			getLogger().info("An error occurred trying to get jdbc user for " + roleName + ":" + e);
			throw new SystemException(e.getMessage());
		}
		finally
		{
			if (rs != null) 
			{
				try 
				{
					rs.close();
				} 
				catch (SQLException e) {}
			}
			if (ps != null) 
			{
				try 
				{
					ps.close();
				} 
				catch (SQLException e) {}
			}
			if (conn != null) 
			{
				try 
				{
					conn.close();
				} 
				catch (Exception ex) {}
			}
		}
				
		return infoglueRole;
	}

	/**
	 * Gets an authorized InfoGlueGroup.
	 */
	
	public InfoGlueGroup getAuthorizedInfoGlueGroup(String groupName) throws Exception
	{
		InfoGlueGroup infoglueGroup = null;
				
		return infoglueGroup;
	}

	
	/**
	 * This method gets a users roles
	 */
	
	public List authorizeUser(String userName) throws Exception
	{
		List roles = new ArrayList();
		List groups = new ArrayList();
		
		String administratorUserName = CmsPropertyHandler.getAdministratorUserName();
		
		boolean isAdministrator = userName.equalsIgnoreCase(administratorUserName) ? true : false;
		if(isAdministrator)
			return roles;
		
		if(transactionObject == null)
		{
			List roleVOList = RoleController.getController().getRoleVOList(userName);
			Iterator roleVOListIterator = roleVOList.iterator();
			while(roleVOListIterator.hasNext())
			{
				RoleVO roleVO = (RoleVO)roleVOListIterator.next();
				InfoGlueRole infoGlueRole = new InfoGlueRole(roleVO.getRoleName(), roleVO.getDescription());
				roles.add(infoGlueRole);
			}
	
			List groupVOList = GroupController.getController().getGroupVOList(userName);
			Iterator groupVOListIterator = groupVOList.iterator();
			while(groupVOListIterator.hasNext())
			{
			    GroupVO groupVO = (GroupVO)groupVOListIterator.next();
				InfoGlueGroup infoGlueGroup = new InfoGlueGroup(groupVO.getGroupName(), groupVO.getDescription());
				groups.add(infoGlueGroup);
			}
		}
		else
		{
			Collection roleList = RoleController.getController().getRoleList(userName, transactionObject);
			Iterator roleListIterator = roleList.iterator();
			while(roleListIterator.hasNext())
			{
				Role role = (Role)roleListIterator.next();
				InfoGlueRole infoGlueRole = new InfoGlueRole(role.getRoleName(), role.getDescription());
				roles.add(infoGlueRole);
			}
	
			Collection groupList = GroupController.getController().getGroupList(userName, transactionObject);
			Iterator groupListIterator = groupList.iterator();
			while(groupListIterator.hasNext())
			{
			    Group group = (Group)groupListIterator.next();
				InfoGlueGroup infoGlueGroup = new InfoGlueGroup(group.getGroupName(), group.getDescription());
				groups.add(infoGlueGroup);
			}
		}
		
		return groups;
	}

	/**
	 * This method gets a list of roles
	 */
	
	public List getRoles() throws Exception
	{
		List roles = new ArrayList();
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try 
		{
			String sql = "SELECT * from CDS_ROLE ORDER BY ROLE_NAME";

			conn = getConnection();
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			while(rs.next())
			{
				String roleName = rs.getString("ROLE_NAME");
				String description = rs.getString("ROLE_DESCRIPTION");
				
				InfoGlueRole infoGlueRole = new InfoGlueRole(roleName, description);
				roles.add(infoGlueRole);
				
				System.out.println("Role created:" + infoGlueRole.getName());
			}
		} 
		catch (Exception e) 
		{
			getLogger().info("An error occurred trying to get all roles:" + e);
			throw new SystemException(e.getMessage());
		}
		finally
		{
			if (rs != null) 
			{
				try 
				{
					rs.close();
				} 
				catch (SQLException e) {}
			}
			if (ps != null) 
			{
				try 
				{
					ps.close();
				} 
				catch (SQLException e) {}
			}
			if (conn != null) 
			{
				try 
				{
					conn.close();
				} 
				catch (Exception ex) {}
			}
		}
		
		return roles;
	}

    public List getGroups() throws Exception
    {
        List groups = new ArrayList();
					
		return groups;
    }

    
	/**
	 * This method gets a list of users
	 */
	
	public List getUsers() throws Exception
	{
		List users = new ArrayList();
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try 
		{
			String sql = "SELECT * from CDS_USER, CDS_ROLE_USER, CDS_ROLE where CDS_ROLE_USER.CDS_USER = CDS_USER.ID AND CDS_ROLE_USER.CDS_ROLE = CDS_ROLE.ID ORDER BY CDS_USER.USER_NAME";
			//String sql = "SELECT * from CDS_USER ORDER BY USER_NAME";

			conn = getConnection();
			
			ps = conn.prepareStatement(sql);
			
			String oldUserName = null;
			
			List roles = new ArrayList();
			List groups = new ArrayList();
			
			rs = ps.executeQuery();
			while(rs.next())
			{
				String userName = rs.getString("USER_NAME");

				if(oldUserName != null && userName.equals(oldUserName))
				{
					String roleName = rs.getString("ROLE_NAME");
					String description = rs.getString("ROLE_DESCRIPTION");
				
					InfoGlueRole infoGlueRole = new InfoGlueRole(roleName, description);
					roles.add(infoGlueRole);
				}
				else
				{
					if(oldUserName == null)
						oldUserName = userName;
					
					InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(oldUserName, oldUserName, oldUserName, "Undefined", roles, groups, false);
					users.add(infoGluePrincipal);
					roles = new ArrayList();
					groups = new ArrayList();
					
					System.out.println("User read:" + infoGluePrincipal.getName());
				}
			}
		} 
		catch (Exception e) 
		{
			getLogger().info("An error occurred trying to get all roles:" + e);
			throw new SystemException(e.getMessage());
		}
		finally
		{
			if (rs != null) 
			{
				try 
				{
					rs.close();
				} 
				catch (SQLException e) {}
			}
			if (ps != null) 
			{
				try 
				{
					ps.close();
				} 
				catch (SQLException e) {}
			}
			if (conn != null) 
			{
				try 
				{
					conn.close();
				} 
				catch (Exception ex) {}
			}
		}
		
		return users;
	}

	public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleIds) throws Exception
	{
		return getUsers();
	}
	
	public List getUsers(String roleName) throws Exception
	{
		return getRoleUsers(roleName);
	}

    public List getRoleUsers(String roleName) throws Exception
    {
        getLogger().info("roleName:" + roleName);
		List users = new ArrayList();
		
		if(transactionObject == null)
		{
			List systemUserVOList = RoleController.getController().getRoleSystemUserVOList(roleName);
			Iterator systemUserVOListIterator = systemUserVOList.iterator();
			while(systemUserVOListIterator.hasNext())
			{
				SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
				InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), new ArrayList(), new ArrayList(), false);
				users.add(infoGluePrincipal);
			}
		}
		else
		{
			List systemUserVOList = RoleController.getController().getRoleSystemUserVOList(roleName, transactionObject);
			Iterator systemUserVOListIterator = systemUserVOList.iterator();
			while(systemUserVOListIterator.hasNext())
			{
				SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
				InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), new ArrayList(), new ArrayList(), false);
				users.add(infoGluePrincipal);
			}
		}
		
		return users;
	}

    public List getGroupUsers(String groupName) throws Exception
    {
        getLogger().info("groupName:" + groupName);
		List users = new ArrayList();
		
		if(transactionObject == null)
		{
			List systemUserVOList = GroupController.getController().getGroupSystemUserVOList(groupName);
			Iterator systemUserVOListIterator = systemUserVOList.iterator();
			while(systemUserVOListIterator.hasNext())
			{
				SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
				InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), new ArrayList(), new ArrayList(), false);
				users.add(infoGluePrincipal);
			}
		}
		else
		{
			List systemUserVOList = GroupController.getController().getGroupSystemUserVOList(groupName, transactionObject);
			Iterator systemUserVOListIterator = systemUserVOList.iterator();
			while(systemUserVOListIterator.hasNext())
			{
				SystemUserVO systemUserVO = (SystemUserVO)systemUserVOListIterator.next();
				InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(systemUserVO.getUserName(), systemUserVO.getFirstName(), systemUserVO.getLastName(), systemUserVO.getEmail(), new ArrayList(), new ArrayList(), false);
				users.add(infoGluePrincipal);
			}
		}

		return users;
    }

	public void createInfoGluePrincipal(SystemUserVO systemUserVO) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support creation of users yet...");
	}

	public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support updating of users yet...");
	}

	public void updateInfoGluePrincipalPassword(String userName) throws Exception 
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support updates of users yet...");
	}

	public void updateInfoGluePrincipalPassword(String userName, String oldPassword, String newPassword) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support updates of user password yet...");
	}
	
	public void deleteInfoGluePrincipal(String userName) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support deletion of users yet...");
	}
	
	public void createInfoGlueRole(RoleVO roleVO) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support creation of users yet...");
	}

	public void updateInfoGlueRole(RoleVO roleVO, String[] userNames) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support updates of users yet...");
	}

	public void deleteInfoGlueRole(String roleName) throws Exception
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support deletion of roles yet...");
	}

	public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames, String[] groupNames) throws Exception 
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support deletion of roles yet...");
	}

	public void createInfoGlueGroup(GroupVO groupVO) throws Exception 
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support deletion of roles yet...");
	}

	public void updateInfoGlueGroup(GroupVO roleVO, String[] userNames) throws Exception 
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support deletion of roles yet...");
	}

	public void deleteInfoGlueGroup(String groupName) throws Exception 
	{
		throw new SystemException("The JDBC BASIC Authorization module does not support deletion of roles yet...");
	}

	public Properties getExtraProperties()
	{
		return extraProperties;
	}

	public void setExtraProperties(Properties extraProperties)
	{
		this.extraProperties = extraProperties;
	}
	
    public Object getTransactionObject()
    {
        return this.transactionObject;
    }

    public void setTransactionObject(Object transactionObject)
    {
        this.transactionObject = (Database)transactionObject; 
    }

	public BaseEntityVO getNewVO()
	{
		return null;
	}


}
