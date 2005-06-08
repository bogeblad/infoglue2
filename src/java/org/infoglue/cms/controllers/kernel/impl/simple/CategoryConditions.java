package org.infoglue.cms.controllers.kernel.impl.simple;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.infoglue.cms.entities.management.CategoryVO;
import org.infoglue.cms.exception.SystemException;


/**
 * 
 */
interface ICategoryCondition {
	/**
	 * 
	 */
	String getWhereClauseOQL(final List bindings);
	
	/**
	 * 
	 */
	Collection getFromClauseTables();
	
	/**
	 * 
	 */
	boolean hasCondition();
}

/**
 * 
 */
interface ICategoryContainerCondition extends ICategoryCondition {
	/**
	 * 
	 */
	void add(ICategoryCondition condition);
	
	/**
	 * 
	 */
	void addCategory(final String attributeName, final CategoryVO categoryVO);
	
	/**
	 * 
	 */
	ICategoryContainerCondition and();

	/**
	 * 
	 */
	ICategoryContainerCondition or();
}

/**
 * 
 */
class CategoryCondition implements ICategoryCondition {
	private static final String SPACE  = " ";
	private static final String COMMA  = ",";
	private static final String AND    = "AND";
	private static final String OR     = "OR";
	
	private static final String CATEGORY_ALIAS_PREFIX         = "cat";
	private static final String CONTENT_CATEGORY_ALIAS_PREFIX = "ccat";
	private static final String CONTENT_VERSION_ALIAS         = "cv";

	private static final String CATEGORY_TABLE                = "cmcategory";
	private static final String CONTENT_CATEGORY_TABLE        = "cmcontentcategory";

	private static final String CATEGORY_CLAUSE   = "(" + CATEGORY_ALIAS_PREFIX + "{0}.active=1 " + AND + SPACE + CATEGORY_ALIAS_PREFIX + "{0}.categoryId={1} " + AND + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + "{0}.attributeName={2} " + AND + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + "{0}.categoryId = " + CATEGORY_ALIAS_PREFIX + "{0}.categoryId  " + AND + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + "{0}.ContVerId=" + CONTENT_VERSION_ALIAS + ".ContVerId)";
	//private static final String CATEGORY_CLAUSE = "(" + CATEGORY_ALIAS_PREFIX + "{0}.active=1 " + AND + SPACE + CATEGORY_ALIAS_PREFIX + "{0}.categoryId={1} " + AND + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + "{0}.attributeName={2} " + AND + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + "{0}.categoryId = " + CATEGORY_ALIAS_PREFIX + "{0}.categoryId  " + AND + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + "{0}.contentVersionId=" + CONTENT_VERSION_ALIAS + ".contentVersionId)";

	/**
	 * 
	 */
	private static int counter;
	
	/**
	 * 
	 */
	private String attributeName;
	
	/**
	 * 
	 */
	private CategoryVO categoryVO;
	
	/**
	 * 
	 */
	private Integer uniqueID;
	
	

	
	/**
	 * 
	 */
	private synchronized Integer createUniqueId() {
		return new Integer(counter++);
	}
	
	/**
	 * 
	 */
	CategoryCondition(final String attributeName, final CategoryVO categoryVO) {
		this.attributeName = attributeName;
		this.categoryVO    = categoryVO;
		this.uniqueID      = createUniqueId(); 
	}

	/**
	 * 
	 */
	public String getWhereClauseOQL(final List bindings) {
		final String categoryVariable = getBindingVariable(bindings);
		bindings.add(categoryVO.getId());
		final String nameVariable = getBindingVariable(bindings);
		bindings.add(attributeName);

		return MessageFormat.format(CATEGORY_CLAUSE, new Object[] { uniqueID, categoryVariable, nameVariable });
	}
	
	/**
	 * 
	 */
	private String getBindingVariable(final Collection bindings) {
		return "$" + (bindings.size() + 1);
	}

	/**
	 * 
	 */
	public Collection getFromClauseTables() {
		final Collection result = new ArrayList();
		result.add(CATEGORY_TABLE + SPACE + CATEGORY_ALIAS_PREFIX + uniqueID);
		result.add(CONTENT_CATEGORY_TABLE + SPACE + CONTENT_CATEGORY_ALIAS_PREFIX + uniqueID);
		return result;
	}

	/**
	 * 
	 */
	public boolean hasCondition() { return true; }
}

/**
 * 
 */
public class CategoryConditions implements ICategoryContainerCondition {
	private static final String LEFT   = "(";
	private static final String RIGHT  = ")";
	private static final String SPACE  = " ";
	private static final String AND    = "AND";
	private static final String OR     = "OR";

	/**
	 * 
	 */
	private List children = new ArrayList();
	
	/**
	 * 
	 */
	private String delimiter;
	
	
	
	/**
	 * 
	 */
	private CategoryConditions(final String delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * 
	 */
	public void add(final ICategoryCondition condition) {
		if(condition != null)
			children.add(condition);
	}
	
	/**
	 * 
	 */
	public void addCategory(final String attributeName, final CategoryVO categoryVO) {
		children.add(new CategoryCondition(attributeName, categoryVO));
	}
	
	/**
	 * 
	 */
	public ICategoryContainerCondition and() {
		final ICategoryContainerCondition container = createAndConditions();
		add(container);
		return container;
	}

	/**
	 * 
	 */
	public ICategoryContainerCondition or() {
		final ICategoryContainerCondition container = createOrConditions();
		add(container);
		return container;
	}

	/**
	 * 
	 */
	public static CategoryConditions createAndConditions() { return new CategoryConditions(AND); }
	
	/**
	 * 
	 */
	public static CategoryConditions createOrConditions() { return new CategoryConditions(OR); }
	
	/**
	 * 
	 */
	public static CategoryConditions parse(final String s) { return new ConditionsParser().parse(s); }
	
	/**
	 * 
	 */
	public String getWhereClauseOQL(final List bindings) {
		final StringBuffer sb = new StringBuffer();
		int counter = 0;
		for(Iterator i=children.iterator(); i.hasNext(); ) {
			ICategoryCondition condition = (ICategoryCondition) i.next();
			if(condition.hasCondition()) {
				if(counter++ > 0)
					sb.append(SPACE + delimiter + SPACE);
				sb.append(condition.getWhereClauseOQL(bindings));
			}
		}
		return (counter > 1) ? (LEFT + sb.toString() + RIGHT) : sb.toString();
	}
	
	/**
	 * 
	 */
	public Collection getFromClauseTables() {
		final List result = new ArrayList();
		for(Iterator i=children.iterator(); i.hasNext(); ) {
			ICategoryCondition condition = (ICategoryCondition) i.next();
			result.addAll(condition.getFromClauseTables());
		}
		return result;
	}

	/**
	 * 
	 */
	public boolean hasCondition() { 
		for(Iterator i=children.iterator(); i.hasNext(); ) {
			ICategoryCondition condition = (ICategoryCondition) i.next();
			if(condition.hasCondition())
				return true;
		}
		return false;
	}
}

/**
 * 
 */
class ConditionsParser {
	private static final String AND_START           = "{";
	private static final String AND_END             = "}";
	private static final String OR_START            = "[";
	private static final String OR_END              = "]";
	private static final String CONDITION_DELIMITER = ",";
	private static final String CATEGORY_DELIMITER  = "=";
	
	
	/**
	 * 
	 */
	ConditionsParser() {}
	
	/**
	 * 
	 */
	public CategoryConditions parse(final String s) {
		final String parseString = (s == null ? "" : s);
		final StringTokenizer st = new StringTokenizer(AND_START + parseString + AND_END, AND_START + AND_END + OR_START + OR_END + CONDITION_DELIMITER, true);
		final List tokens = tokensToList(st);
		
		final CategoryConditions conditions = createContainer(tokens);
		parse(conditions, tokens);
		return conditions;
	}
	
	/**
	 * 
	 */
	private void parse(CategoryConditions conditions, final List tokens) {
		if(tokens.isEmpty() || isContainerEndToken(tokens))
			return;
		if(isContainerStartToken(tokens))
			parseContainer(conditions, tokens);
		else if(isConditionDelimiterToken(tokens))
			parseConditionDelimiter(conditions, tokens);
		else
			parseCategory(conditions, tokens);
		
		parse(conditions, tokens);
	}
	
	/**
	 * 
	 */
	private void parseContainer(CategoryConditions conditions, final List tokens) {
		final CategoryConditions newConditions = createContainer(tokens);
		
		final String startToken = (String) tokens.remove(0);
		parse(newConditions, tokens);
		matchContainerTokens(startToken, tokens);
		conditions.add(newConditions);
	}
	
	/**
	 * 
	 */
	private void parseConditionDelimiter(CategoryConditions conditions, final List tokens) {
		if(!conditions.hasCondition())
			throw new IllegalArgumentException("ConditionsParser.parseConditionDelimiter() - empty condition.");
		tokens.remove(0);
	}
	
	/**
	 * 
	 */
	private void parseCategory(CategoryConditions conditions, final List tokens) {
		final String token = (String) tokens.remove(0);
		final List terms = tokensToList(new StringTokenizer(token, CATEGORY_DELIMITER, true));
		if(terms.size() != 3)
			throw new IllegalArgumentException("ConditionsParser.parseCategory() - illegal category syntax.");
		
		final String attributeName  = (String) terms.get(0);
		final String path           = (String) terms.get(2);
		
		try {
			final CategoryVO categoryVO = CategoryController.getController().findByPath(path); 
			if(categoryVO == null)
				throw new IllegalArgumentException("ConditionsParser.parseCategory() - no such category [" + path + "].");
			conditions.addCategory(attributeName, categoryVO);
		} catch(SystemException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("ConditionsParser.parseCategory() - unknown category path [" + path + "].");
		}
	}
	
	/**
	 * 
	 */
	private CategoryConditions createContainer(final List tokens) {
		if(tokens.size() < 2)
			throw new IllegalArgumentException("ConditionsParser.createContainer() - no trailing container delimiter.");

		final String startToken = (String) tokens.get(0);
		final String endToken   = (String) tokens.get(tokens.size() - 1);

		if(AND_START.equals(startToken))
			return CategoryConditions.createAndConditions();
		if(OR_START.equals(startToken))
			return CategoryConditions.createOrConditions();
		
		throw new IllegalArgumentException("ConditionsParser.createContainer() - illegal state.");
	}

	/**
	 * 
	 */
	private boolean isContainerStartToken(final List tokens) {
		if(tokens.isEmpty())
			return false;
		final String token = (String) tokens.get(0);
		return AND_START.equals(token) || OR_START.equals(token);
	}

	/**
	 * 
	 */
	private boolean isContainerEndToken(final List tokens) {
		if(tokens.isEmpty())
			return false;
		final String token = (String) tokens.get(0);
		return AND_END.equals(token) || OR_END.equals(token);
	}
	
	/**
	 * 
	 */
	private boolean isConditionDelimiterToken(final List tokens) {
		if(tokens.isEmpty())
			return false;
		final String token = (String) tokens.get(0);
		return CONDITION_DELIMITER.equals(token);
	}
	
	/**
	 * 
	 */
	private void matchContainerTokens(final String startToken, final List tokens) {
		if(tokens.isEmpty())
			throw new IllegalArgumentException("ConditionsParser.matchContainerTokens() - no closing container token.");
		final String endToken = (String) tokens.remove(0);
		if(startToken.equals(AND_START) && !endToken.equals(AND_END))
			throw new IllegalArgumentException("ConditionsParser.matchContainerTokens() - no matching closing container token.");
		if(startToken.equals(OR_START) && !endToken.equals(OR_END))
			throw new IllegalArgumentException("ConditionsParser.matchContainerTokens() - no matching closing container token.");
	}
	
	/**
	 * 
	 */
	private List tokensToList(final StringTokenizer st) {
		final List result = new ArrayList();
		while(st.hasMoreElements())
			result.add(st.nextElement());
		return result;
	}
}