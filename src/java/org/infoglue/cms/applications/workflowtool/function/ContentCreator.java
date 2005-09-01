package org.infoglue.cms.applications.workflowtool.function;

import java.util.List;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.register.ContentType;
import org.infoglue.cms.applications.workflowtool.util.ContentFactory;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;
import org.infoglue.cms.applications.workflowtool.util.ContentVersionValues;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentTypeDefinitionController;
import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.security.InfoGluePrincipal;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentCreator extends ContentFunction {
	/**
	 * 
	 */
	public static final String TRANSIENT_VARS_FOLDER_VARIABLE = "create.folder";
	
	/**
	 * 
	 */
	private static final String STATUS_OK = "status.content.ok";
	
	/**
	 * 
	 */
	private static final String STATUS_NOK = "status.content.nok";
	
	/**
	 * 
	 */
	private InfoGluePrincipal principal;
	
	/**
	 * 
	 */
	private LanguageVO languageVO;
	
	/**
	 * 
	 */
	private ContentTypeDefinitionVO contentTypeDefinitionVO;
	
	/**
	 * 
	 */
	private Map categories;
	
	/**
	 * 
	 */
	private ContentValues contentValues;
	
	/**
	 * 
	 */
	private ContentVersionValues contentVersionValues;
	
	/**
	 * 
	 */
	private ContentVO parentFontentVO;
	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		create(ps);
	}

	/**
	 * 
	 */
	private void create(final PropertySet ps) throws WorkflowException {
		try {
			final ContentFactory factory = new ContentFactory(contentTypeDefinitionVO, contentValues, contentVersionValues, principal, languageVO);
			ContentVO newContentVO = null;
			if(getContentVO() == null)
				newContentVO = factory.create(parentFontentVO, categories, getDatabase());
			else
				newContentVO = factory.update(getContentVO(), categories, getDatabase());
			if(newContentVO != null)
				ps.setString(ContentProvider.RESULTSET_CONTENT_ID, newContentVO.getContentId().toString());
			setStatus(ps, (newContentVO != null) ? STATUS_OK : STATUS_NOK);
		} catch(ConstraintException e) {
			getLogger().debug(e.toString());
		} catch(Exception e) {
			throw new WorkflowException(e);
		}
	}

	/**
	 * 
	 */
	private List getContentTypeAttributes() {
		return ContentTypeDefinitionController.getController().getContentTypeAttributes(contentTypeDefinitionVO.getSchemaValue());
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		principal               = (InfoGluePrincipal)       getParameter(transientVars, PrincipalProvider.TRANSIENT_VARS_VARIABLE);
		contentTypeDefinitionVO = (ContentTypeDefinitionVO) getParameter(transientVars, ContentType.TRANSIENT_VARIABLE_VARIABLE);
		languageVO              = (LanguageVO)              getParameter(transientVars, LanguageProvider.TRANSIENT_VARS_VARIABLE);
		categories              = (Map)                     getParameter(transientVars, CategoryProvider.TRANSIENT_VARS_VARIABLE);
		contentValues           = (ContentValues)           getParameter(transientVars, ContentPopulator.TRANSIENT_VARS_CONTENT_VARIABLE);
		contentVersionValues    = (ContentVersionValues)    getParameter(transientVars, ContentPopulator.TRANSIENT_VARS_CONTENT_VERSION_VARIABLE);
		parentFontentVO         = (ContentVO)               getParameter(transientVars, TRANSIENT_VARS_FOLDER_VARIABLE);
	}
}
