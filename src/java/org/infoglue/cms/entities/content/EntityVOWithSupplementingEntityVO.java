/**
 * 
 */
package org.infoglue.cms.entities.content;

import org.infoglue.cms.entities.kernel.BaseEntityVO;

/**
 * @author Erik Stenbäcka
 *
 */
public class EntityVOWithSupplementingEntityVO
{
	private BaseEntityVO entity;
	private BaseEntityVO supplementingEntity;

	public BaseEntityVO getEntity()
	{
		return entity;
	}
	public void setEntity(BaseEntityVO entity)
	{
		this.entity = entity;
	}
	public BaseEntityVO getSupplementingEntity()
	{
		return supplementingEntity;
	}
	public void setSupplementingEntity(BaseEntityVO supplementingEntity)
	{
		this.supplementingEntity = supplementingEntity;
	}



	public boolean getIsEntityContent()
	{
		return entity instanceof ContentVO;
	}
	public boolean getIsSupplementingEntityAsset()
	{
		return supplementingEntity instanceof DigitalAssetVO;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (entity != null)
		{
			sb.append("Entity: {type:");
			sb.append(entity.getClass().getSimpleName());
			sb.append(",");
			sb.append(entity.getId());
			sb.append("}");
		}
		else
		{
			sb.append("--null--");
		}
		if (supplementingEntity != null)
		{
			sb.append(", Supplementing: {type:");
			sb.append(supplementingEntity.getClass().getSimpleName());
			sb.append(",");
			sb.append(supplementingEntity.getId());
			sb.append("}");
		}
		else
		{
			sb.append(", --null--");
		}
		sb.append("]");

		return sb.toString();
	}
}
