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

package org.infoglue.cms.util.workflow;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;

import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.query.Expression;
import com.opensymphony.workflow.query.FieldExpression;
import com.opensymphony.workflow.query.NestedExpression;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;
import com.opensymphony.workflow.query.WorkflowQuery;
import com.opensymphony.workflow.spi.SimpleStep;
import com.opensymphony.workflow.spi.SimpleWorkflowEntry;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.opensymphony.workflow.spi.jdbc.JDBCWorkflowStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

import java.util.*;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;


/**
 * JDBC implementation just overiding the init-method of the default JDBCWorkflowStore as it demanded DataStores.
 *
 * @author Mattias Bogeblad
 */
public class InfoGlueMySQLJDBCWorkflowStore extends InfoGlueJDBCWorkflowStore 
{

    //~ Instance fields ////////////////////////////////////////////////////////

    private String _stepSequenceIncrement = null;
    private String _stepSequenceRetrieve = null;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void init(Map props) throws StoreException 
    {
        super.init(props);
        _stepSequenceIncrement 	= (String) props.get("step.sequence.increment");
        _stepSequenceRetrieve 	= (String) props.get("step.sequence.retrieve");
    }

    protected long getNextStepSequence(Connection c) throws SQLException 
    {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = c.prepareStatement(_stepSequenceIncrement);
            stmt.executeUpdate();
            rset = stmt.executeQuery(_stepSequenceRetrieve);

            rset.next();

            long id = rset.getLong(1);

            return id;
        } 
        finally 
        {
            cleanup(null, stmt, rset);
        }
    } 
} 

