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

function ComponentPropertyDefinition(name, type, entity, multiple, allowedContentTypeNames)
{
	this.name 		= name;
	this.type 		= type;
	this.entity		= entity;
	this.multiple	= multiple;
	this.allowedContentTypeNames = allowedContentTypeNames;
  
  	this.getName 					= getName;
  	this.getType 					= getType;
  	this.getEntity 					= getEntity;
  	this.getMultiple				= getMultiple;
  	this.getAllowedContentTypeNames = getAllowedContentTypeNames;

  	this.setName 					= setName;
  	this.setType 					= setType;
  	this.setEntity 					= setEntity;
  	this.setMultiple				= setMultiple;
  	this.setAllowedContentTypeNames = setAllowedContentTypeNames;
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

function getAllowedContentTypeNames()
{
	return this.allowedContentTypeNames;
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

function setAllowedContentTypeNames(allowedContentTypeNames)
{
	this.allowedContentTypeNames = allowedContentTypeNames;
}
