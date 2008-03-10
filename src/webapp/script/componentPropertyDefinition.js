//////////////////////////////////////////////////////
// File: ComponentPropertyDefinition.js
//
// Author: Mattias Bogeblad
// 
// Purpose: To have a generic ComponentPropertyDefinition.
//
//////////////////////////////////////////////////////


/**
 * ComponentPropertyDefinition object
 */

function ComponentPropertyDefinition(name, type, entity, multiple, assetBinding, allowedContentTypeNames, description, defaultValue, WYSIWYGEnabled, WYSIWYGToolbar, dataProvider, dataProviderParameters)
{
	this.name 						= name;
	this.type 						= type;
	this.entity						= entity;
	this.multiple					= multiple;
	this.assetBinding 				= assetBinding;
	this.allowedContentTypeNames 	= allowedContentTypeNames;
	this.description				= description;
	this.defaultValue				= defaultValue;
	this.WYSIWYGEnabled				= WYSIWYGEnabled;
	this.WYSIWYGToolbar				= WYSIWYGToolbar;
	this.dataProvider 				= dataProvider;
	this.dataProviderParameters		= dataProviderParameters;
	this.options					= new Vector(0);
	  
  	this.getName 					= getName;
  	this.getType 					= getType;
  	this.getEntity 					= getEntity;
  	this.getMultiple				= getMultiple;
  	this.getAssetBinding			= getAssetBinding;
  	this.getAllowedContentTypeNames = getAllowedContentTypeNames;
	this.getDescription				= getDescription;
	this.getOptions					= getOptions;
	this.getDefaultValue			= getDefaultValue;
	this.getWYSIWYGEnabled			= getWYSIWYGEnabled;
	this.getWYSIWYGToolbar			= getWYSIWYGToolbar;
	this.getDataProvider			= getDataProvider;
	this.getDataProviderParameters	= getDataProviderParameters;
	
  	this.setName 					= setName;
  	this.setType 					= setType;
  	this.setEntity 					= setEntity;
  	this.setMultiple				= setMultiple;
  	this.setAssetBinding			= setAssetBinding;
  	this.setAllowedContentTypeNames = setAllowedContentTypeNames;
	this.setDescription				= setDescription;
	this.setDefaultValue			= setDefaultValue;
	this.setWYSIWYGEnabled			= setWYSIWYGEnabled;
	this.setWYSIWYGToolbar			= setWYSIWYGToolbar;
	this.setDataProvider			= setDataProvider;
	this.setDataProviderParameters	= setDataProviderParameters;
}
  
function getName()
{
  	return this.name;
}

function getType()
{
  	return this.type;
}

function getEntity()
{
  	return this.entity;
}

function getMultiple()
{
  	return this.multiple;
}

function getAssetBinding()
{
  	return this.assetBinding;
}

function getAllowedContentTypeNames()
{
	return this.allowedContentTypeNames;
}

function getDescription()
{
	return this.description;
}

function getOptions()
{
	return this.options;
}

function setName(name)
{
  	this.name = name;
}

function setType(type)
{
  	this.type = type;
}

function setEntity(entity)
{
	this.entity = entity;
}

function setMultiple(multiple)
{
	this.multiple = multiple;
}

function setAssetBinding(assetBinding)
{
  	this.assetBinding = assetBinding;
}

function setAllowedContentTypeNames(allowedContentTypeNames)
{
	this.allowedContentTypeNames = allowedContentTypeNames;
}

function setDescription(description)
{
	this.description = description;
}

function getDefaultValue()
{
	return this.defaultValue;
}

function setDefaultValue(defaultValue)
{
	this.defaultValue = defaultValue;
}
	
function getWYSIWYGEnabled()
{
	return this.WYSIWYGEnabled;
}

function setWYSIWYGEnabled(WYSIWYGEnabled)
{
	this.WYSIWYGEnabled = WYSIWYGEnabled;
}

function getWYSIWYGToolbar()
{
	return this.WYSIWYGToolbar;
}

function setWYSIWYGToolbar(WYSIWYGToolbar)
{
	this.WYSIWYGToolbar = WYSIWYGToolbar;
}

function getDataProvider()
{
	return this.dataProvider;
}

function setDataProvider(dataProvider)
{
	this.dataProvider = dataProvider;
}

function getDataProviderParameters()
{
	return this.dataProviderParameters;
}

function setDataProviderParameters(dataProviderParameters)
{
	this.dataProviderParameters = dataProviderParameters;
}
