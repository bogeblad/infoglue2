package org.infoglue.cms.applications.workflowtool.function;

import java.util.Map;

import org.infoglue.cms.entities.content.ContentVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.structure.SiteNodeVO;
import org.infoglue.cms.util.CmsPropertyHandler;
import org.infoglue.cms.util.URLHelper;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class PreviewProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String SITENODE_PARAMETER = "previewSiteNode";
	
	/**
	 * 
	 */
	public static final String PROPERTYSET_PREVIEW_URL_VARIABLE = "previewURL";

	/**
	 * 
	 */
	private ContentVO content;

	/**
	 * 
	 */
	private LanguageVO language;

	/**
	 * 
	 */
	private SiteNodeVO previewSiteNode;

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(ps);
	}

	/**
	 * 
	 */
	private void populate(PropertySet ps) throws WorkflowException {
		final String baseURL   = CmsPropertyHandler.getProperty("previewDeliveryUrl");
		final URLHelper helper = new URLHelper(baseURL, content.getId(), previewSiteNode.getId(), language.getId());
		ps.setString(PROPERTYSET_PREVIEW_URL_VARIABLE, helper.getURL());
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		content         = (ContentVO)  getParameter(transientVars, ContentFunction.CONTENT_PARAMETER);
		language        = (LanguageVO) getParameter(transientVars, LanguageProvider.LANGUAGE_PARAMETER);
		previewSiteNode = (SiteNodeVO) getParameter(transientVars, SITENODE_PARAMETER);
	}
}
