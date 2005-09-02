package org.infoglue.cms.applications.workflowtool.function;

import java.util.List;
import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.LanguageController;
import org.infoglue.cms.entities.management.LanguageVO;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class LanguageProvider extends InfoglueFunction {
	/**
	 * 
	 */
	public static final String LANGUAGE_PARAMETER   = "language";

	/**
	 * 
	 */
	private static final String PROPERTYSET_LANGUAGE_ID  = "languageId";

	/**
	 * 
	 */
	private static final String ARGUMENT_LANGUAGE_CODE = "code";

	/**
	 * 
	 */
	private static final String LANGUAGE_ID_IDENTIFIER = "languageId";

	
	
	/**
	 * 
	 */
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		populate(transientVars, args, ps);
	}
	
	/**
	 * 
	 */
	private void populate(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		LanguageVO language = null;

		if(ps.exists(PROPERTYSET_LANGUAGE_ID))
			language = getLanguageWithID(ps.getString(PROPERTYSET_LANGUAGE_ID));

		if(language == null && transientVars.containsKey(LANGUAGE_ID_IDENTIFIER))
			language = getLanguageWithID((String) transientVars.get(LANGUAGE_ID_IDENTIFIER));

		if(language == null && args.containsKey(ARGUMENT_LANGUAGE_CODE))
			language = getLanguageWithCode((String) args.get(ARGUMENT_LANGUAGE_CODE));

		if(language == null)
			language = getAnyLanguage();
		
		populate(transientVars, ps, language);
	}
	
	/**
	 * 
	 */
	private void populate(final Map transientVars, final PropertySet ps, final LanguageVO language) {
		if(language == null && ps.exists(PROPERTYSET_LANGUAGE_ID))
			ps.remove(PROPERTYSET_LANGUAGE_ID);
		if(language != null) {
			transientVars.put(LANGUAGE_PARAMETER, language);
			ps.setString(PROPERTYSET_LANGUAGE_ID, language.getId().toString());
		}
	}
	
	/**
	 * 
	 */
	public LanguageVO getAnyLanguage() throws WorkflowException {
		try {
			
			final List languages = LanguageController.getController().getLanguageVOList(getDatabase());
			if(!languages.isEmpty())
				return (LanguageVO) languages.get(0);
			throw new WorkflowException("No languages found...");
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException("Language.getAnyLanguage() : " + e);
		}
	}

	/**
	 * 
	 */
	public LanguageVO getLanguageWithID(final String languageId) throws WorkflowException {
		try {
			return LanguageController.getController().getLanguageVOWithId(new Integer(languageId), getDatabase());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException("Language.getLanguageWithID() : " + e);
		}
	}

	/**
	 * 
	 */
	public LanguageVO getLanguageWithCode(final String code) throws WorkflowException {
		try {
			return LanguageController.getController().getLanguageVOWithCode(code, getDatabase());
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException("Language.getLanguageWithCode() : " + e);
		}
	}
}