package org.infoglue.cms.applications.workflowtool.function;

import java.util.Locale;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.register.ContentType;
import org.infoglue.cms.applications.workflowtool.util.ContentFactory;
import org.infoglue.cms.applications.workflowtool.util.ContentValues;
import org.infoglue.cms.applications.workflowtool.util.ContentVersionValues;
import org.infoglue.cms.applications.workflowtool.util.PropertysetHelper;
import org.infoglue.cms.applications.workflowtool.util.RequestHelper;
import org.infoglue.cms.entities.management.ContentTypeDefinitionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.ConstraintExceptionBuffer;
import org.infoglue.cms.util.StringManager;
import org.infoglue.cms.util.StringManagerFactory;


import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ErrorPopulator extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String PROPERTYSET_ERROR_PREFIX = "error.";

	/**
	 * 
	 */
	private InfoGluePrincipal principal;

	/**
	 * 
	 */
	private LanguageVO language;

	/**
	 * 
	 */
	private ContentTypeDefinitionVO contentTypeDefinitionVO;

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
	private Locale locale;

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		clean(ps);
		populate(ps);
	}

	/**
	 * 
	 */
	private void clean(final PropertySet ps) {
		new PropertysetHelper(ps).removeKeys(PROPERTYSET_ERROR_PREFIX);
	}
	
	/**
	 * 
	 */
	private void populate(PropertySet ps) {
		final ConstraintExceptionBuffer ceb = new ContentFactory(contentTypeDefinitionVO, contentValues, contentVersionValues, principal, language).validate();
		for(ConstraintException e = ceb.toConstraintException(); e != null; e = e.getChainedException())
			populateError(ps, e);
	}

	/**
	 * 
	 */
	private void populateError(final PropertySet ps, final ConstraintException e) {
		ps.setString(getErrorKey(e), getErrorMessage(e));
	}
	
	/**
	 * 
	 */
	private String getErrorKey(final ConstraintException e) {
		return PROPERTYSET_ERROR_PREFIX + e.getFieldName();
	}

	/**
	 * 
	 */
	private String getErrorMessage(final ConstraintException e) {
	    final StringManager stringManager = StringManagerFactory.getPresentationStringManager("org.infoglue.cms.entities", locale);
	    return stringManager.getString(e.getErrorCode());
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		principal               = (InfoGluePrincipal)       getParameter(transientVars, PrincipalProvider.TRANSIENT_VARS_VARIABLE);
		language                = (LanguageVO)              getParameter(transientVars, LanguageProvider.TRANSIENT_VARS_VARIABLE);
		contentTypeDefinitionVO = (ContentTypeDefinitionVO) getParameter(transientVars, ContentType.TRANSIENT_VARIABLE_VARIABLE);
		contentValues           = (ContentValues)           getParameter(transientVars, ContentPopulator.TRANSIENT_VARS_CONTENT_VARIABLE);
		contentVersionValues    = (ContentVersionValues)    getParameter(transientVars, ContentPopulator.TRANSIENT_VARS_CONTENT_VERSION_VARIABLE);
		
		locale = new RequestHelper(transientVars).getLocale();
	}
}