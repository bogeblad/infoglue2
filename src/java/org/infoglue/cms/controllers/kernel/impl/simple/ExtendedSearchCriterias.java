package org.infoglue.cms.controllers.kernel.impl.simple;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;

/**
 * 
 */
public class ExtendedSearchCriterias {
	/**
	 * 
	 */
	public static final int NO_DATE_CRITERIA_TYPE   = 0;
	public static final int FROM_DATE_CRITERIA_TYPE = 1;
	public static final int TO_DATE_CRITERIA_TYPE   = 2;
	public static final int BOTH_DATE_CRITERIA_TYPE = 3;

	/**
	 * 
	 */
	private Integer stateId;
	
	/**
	 * 
	 */
	private String freetext;
	
	/**
	 * 
	 */
	private List xmlAttributes;
	
	/**
	 * 
	 */
	private LanguageVO languageVO;
	
	/**
	 * 
	 */
	private List contentTypeDefinitionVOs;
	
	/**
	 * 
	 */
	private CategoryConditions categories;
	
	/**
	 * 
	 */
	private Timestamp fromDate;
	
	/**
	 * 
	 */
	private Timestamp toDate;
	
	
	
	/**
	 * 
	 */
	public ExtendedSearchCriterias(final int stateId) 
	{
		super();
		this.stateId = new Integer(stateId);
	}
	
	/**
	 * 
	 */
	public void setFreetext(final String freetext, final List xmlAttributes)
	{
		this.freetext = freetext;
		if(xmlAttributes != null)
			this.xmlAttributes = new ArrayList(xmlAttributes);
	}
	
	/**
	 * 
	 */
	public void setLanguage(final LanguageVO languageVO)
	{
		this.languageVO = languageVO;
	}
	
	/**
	 * 
	 */
	public void setContentTypeDefinitions(final ContentTypeDefinitionVO contentTypeDefinitionVO)
	{
		if(contentTypeDefinitionVO != null)
		{
			contentTypeDefinitionVOs = new ArrayList();
			contentTypeDefinitionVOs.add(contentTypeDefinitionVO);
		}
	}
	
	/**
	 * 
	 */
	public void setContentTypeDefinitions(final List contentTypeDefinitionVOs)
	{
		if(contentTypeDefinitionVOs != null)
			this.contentTypeDefinitionVOs = new ArrayList(contentTypeDefinitionVOs);
	}
	
	/**
	 * 
	 */
	public void setCategoryConditions(final CategoryConditions categories)
	{
		this.categories = categories;
	}
	
	/**
	 * 
	 */
	public void setDates(final Date from, final Date to)
	{
		this.fromDate = (from == null) ? null : new Timestamp(from.getTime());
		this.toDate   = (to == null)   ? null : new Timestamp(to.getTime());
	}
	
	/**
	 * 
	 */
	public boolean hasFreetextCritera() 
	{
		return freetext != null && freetext.length() > 0 && xmlAttributes != null && !xmlAttributes.isEmpty();
	}

	/**
	 * 
	 */
	public boolean hasLanguageCriteria() 
	{
		return languageVO != null;
	}
	
	/**
	 * 
	 */
	public boolean hasContentTypeDefinitionVOsCriteria() 
	{
		return contentTypeDefinitionVOs != null && !contentTypeDefinitionVOs.isEmpty();
	}
	
	/**
	 * 
	 */
	public boolean hasCategoryConditions() 
	{
		return categories != null && categories.hasCondition();
	}
	
	/**
	 * 
	 */
	public int getDateCriteriaType()
	{
		if(toDate == null && fromDate == null)
			return NO_DATE_CRITERIA_TYPE;
		if(toDate != null && fromDate == null)
			return NO_DATE_CRITERIA_TYPE;
		if(toDate == null && fromDate != null)
			return FROM_DATE_CRITERIA_TYPE;
		return BOTH_DATE_CRITERIA_TYPE;
	}
	
	/**
	 * 
	 */
	public Integer getStateId()
	{
		return this.stateId;
	}
	
	/**
	 * 
	 */
	public String getFreetext()
	{
		return this.freetext;
	}

	/**
	 * 
	 */
	public List getXmlAttributes()
	{
		return this.xmlAttributes;
	}

	/**
	 * 
	 */
	public LanguageVO getLanguage()
	{
		return this.languageVO;
	}
	
	/**
	 * 
	 */
	public List getContentTypeDefinitions()
	{
		return this.contentTypeDefinitionVOs;
	}

	/**
	 * 
	 */
	public CategoryConditions getCategories()
	{
		return this.categories;
	}

	/**
	 * 
	 */
	public Timestamp getFromDate()
	{
		return this.fromDate;
	}
	/**
	 * 
	 */

	public Timestamp getToDate()
	{
		return this.toDate;
	}
}
