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

function ComponentPropertyDefinition(name, type, entity, multiple, assetBinding, allowedContentTypeNames, description)
{
	this.name 						= name;
	this.type 						= type;
	this.entity						= entity;
	this.multiple					= multiple;
	this.assetBinding 				= assetBinding;
	this.allowedContentTypeNames 	= allowedContentTypeNames;
	this.description				= description;
	this.options					= new Vector(0);
	  
  	this.getName 					= getName;
  	this.getType 					= getType;
  	this.getEntity 					= getEntity;
  	this.getMultiple				= getMultiple;
  	this.getAssetBinding			= getAssetBinding;
  	this.getAllowedContentTypeNames = getAllowedContentTypeNames;
	this.getDescription				= getDescription;
	this.getOptions					= getOptions;
	
  	this.setName 					= setName;
  	this.setType 					= setType;
  	this.setEntity 					= setEntity;
  	this.setMultiple				= setMultiple;
  	this.setAssetBinding			= setAssetBinding;
  	this.setAllowedContentTypeNames = setAllowedContentTypeNames;
	this.setDescription				= setDescription;
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
