package org.infoglue.cms.applications.workflowtool.function;

import java.util.List;
import java.util.Map;

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
	public static final String FOLDER_PARAMETER = "create.folder";
	
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
		principal               = (InfoGluePrincipal)       getParameter(transientVars, PrincipalProvider.PRINCIPAL_PARAMETER);
		contentTypeDefinitionVO = (ContentTypeDefinitionVO) getParameter(transientVars, ContentTypeDefinitionProvider.CONTENT_TYPE_DEFINITION_PARAMETER);
		languageVO              = (LanguageVO)              getParameter(transientVars, LanguageProvider.LANGUAGE_PARAMETER);
		categories              = (Map)                     getParameter(transientVars, CategoryProvider.CATEGORIES_PARAMETER);
		contentValues           = (ContentValues)           getParameter(transientVars, ContentPopulator.CONTENT_VALUES_PARAMETER);
		contentVersionValues    = (ContentVersionValues)    getParameter(transientVars, ContentPopulator.CONTENT_VERSION_VALUES_PARAMETER);
		parentFontentVO         = (ContentVO)               getParameter(transientVars, FOLDER_PARAMETER);
	}
}
