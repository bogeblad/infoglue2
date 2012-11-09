package org.infoglue.cms.util.workflow.hibernate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.infoglue.cms.util.CmsPropertyHandler;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

public class LongRawOrByteBinaryType extends net.sf.hibernate.type.BinaryType 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public Object get(java.sql.ResultSet rs, java.lang.String name) throws net.sf.hibernate.HibernateException, java.sql.SQLException
	{
		try
		{
			
		Object o = null;
		
		//System.out.println("Name: " + name);
		
		String columnTypeName = "";
		try
		{
			for(int i=1; i <= rs.getMetaData().getColumnCount(); i++)
			{
				String columnName = rs.getMetaData().getColumnName(i);
				//System.out.println("columnName:" + columnName + ":" + rs.getMetaData().getColumnTypeName(i));
				if(columnName.equals(name))
				{
					columnTypeName = rs.getMetaData().getColumnTypeName(i);
					break;
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

    	//System.out.println("columnTypeName: " + columnTypeName); 
    	if(CmsPropertyHandler.getDatabaseEngine().indexOf("oracle") > -1 && columnTypeName != null && columnTypeName.indexOf("RAW") == -1)
    	{
    		//System.out.println("Getting as blob");
	        Blob blob = rs.getBlob(name);
	        //System.out.println("blob:" + blob);
        	if(blob != null)
        	{
        		try
        		{
        			InputStream in = blob.getBinaryStream();
        			ByteArrayOutputStream baos = new ByteArrayOutputStream();
        			byte[] buffer = new byte[(int)blob.length()];
        			InputStream is = in;
	                while (is.read(buffer) > 0) {
	                	baos.write(buffer);
	                }
	                baos.flush();
	                String s = baos.toString();
	                //System.out.println("S: " + s + "...");
	                o = s.getBytes();
        		}
        		catch (Exception e) 
        		{
        			e.printStackTrace();
				}
        	}
        	else
        	{
        		o = null;
        	}
    	}
    	else
    	{
    		//System.out.println("Getting as raw bytes");
    		o = rs.getBytes(name);
    	}
    	
    	return o;
    	
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	/*
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException 
	{
		System.out.println("nullSafeSet:" + value);
		super.nullSafeSet(st, value, index);
	}
	*/

	public Class returnedClass() {
		return byte[].class;
	}

	public int[] sqlTypes() {
		return new int[] {Types.VARBINARY};
	}

	/*
	public int[] sqlTypes() {
        return new int[] {
        		 Types.,
        };
    }

    public Class getReturnedClass() {
        return Money.class;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException 
    {
        assert names.length == 2;
        BigDecimal amount = BigDecimalType.INSTANCE.get( names[0] ); // already handles null check
        Currency currency = CurrencyType.INSTANCE.get( names[1] ); // already handles null check
        return amount == null && currency == null
                ? null
                : new Money( amount, currency );
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException 
    {
        if ( value == null ) 
        {
            BigDecimalType.INSTANCE.set( st, null, index );
            CurrencyType.INSTANCE.set( st, null, index+1 );
        }
        else 
        {
            final Money money = (Money) value;
            BigDecimalType.INSTANCE.set( st, money.getAmount(), index );
            CurrencyType.INSTANCE.set( st, money.getCurrency(), index+1 );
        }
    }
    */
}