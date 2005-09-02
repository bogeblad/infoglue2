package org.infoglue.cms.applications.workflowtool.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infoglue.cms.controllers.kernel.impl.simple.ContentStateController;
import org.infoglue.cms.controllers.kernel.impl.simple.ContentVersionController;
import org.infoglue.cms.controllers.kernel.impl.simple.PublicationController;
import org.infoglue.cms.entities.content.ContentVersionVO;
import org.infoglue.cms.entities.management.LanguageVO;
import org.infoglue.cms.entities.publishing.PublicationVO;
import org.infoglue.cms.security.InfoGluePrincipal;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class ContentPublisher extends ContentFunction {
	/**
	 * 
	 */
	private static final String STATUS_OK = "status.publish.ok";
	
	/**
	 * 
	 */
	private static final String STATUS_NOK = "status.publish.nok";
	
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
	protected void doExecute(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		if(getContentVO() != null)
			publish(ps);
		else
			setStatus(ps, STATUS_NOK);
	}
	
	/**
	 * 
	 */
	private void publish(final PropertySet ps) throws WorkflowException {
		try {
			final ContentVersionVO contentVersionVO = ContentVersionController.getContentVersionController().getLatestActiveContentVersionVO(getContentVO().getId(), language.getId(), getDatabase());
			if(contentVersionVO.getStateId().equals(ContentVersionVO.WORKING_STATE)) {
				final List events = new ArrayList();
				ContentStateController.changeState(contentVersionVO.getContentVersionId(), ContentVersionVO.PUBLISH_STATE, "Auto", principal, getContentVO().getId(), getDatabase(), events);
				PublicationController.getController().createAndPublish(createPublicationVO(), events, principal, getDatabase());
				setStatus(ps, STATUS_OK);
			} else {
				setStatus(ps, STATUS_NOK);
			}
		} catch(Exception e) {
			setStatus(ps, STATUS_NOK);
			throw new WorkflowException(e);
		}
	}
	
	/**
	 * 
	 */
	private PublicationVO createPublicationVO() {
	    PublicationVO publicationVO = new PublicationVO();
	    publicationVO.setName("Workflow publication by " + principal.getName());
	    publicationVO.setDescription("Workflow publication by " + principal.getName());
	    publicationVO.setRepositoryId(getContentVO().getRepositoryId());
		return publicationVO;
	}

	/**
	 * 
	 */
	protected void initialize(final Map transientVars, final Map args, final PropertySet ps) throws WorkflowException {
		super.initialize(transientVars, args, ps);
		principal = (InfoGluePrincipal) getParameter(transientVars, PrincipalProvider.PRINCIPAL_PARAMETER);
		language =  (LanguageVO)        getParameter(transientVars, LanguageProvider.LANGUAGE_PARAMETER);
	}
}