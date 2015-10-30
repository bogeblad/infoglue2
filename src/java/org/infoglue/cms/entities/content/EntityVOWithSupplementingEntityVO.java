/**
 * 
 */
package org.infoglue.cms.entities.content;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.kernel.BaseGloballyIdentifyableEntity;
import org.infoglue.cms.util.ConstraintExceptionBuffer;

/**
 * @author Erik Stenbäcka
 *
 */
public class EntityVOWithSupplementingEntityVO
{
	private BaseGloballyIdentifyableEntity entity;
	private BaseGloballyIdentifyableEntity supplementingEntity;

	public BaseGloballyIdentifyableEntity getEntity()
	{
		return entity;
	}
	public void setEntity(BaseGloballyIdentifyableEntity entity)
	{
		this.entity = entity;
	}
	public BaseGloballyIdentifyableEntity getSupplementingEntity()
	{
		return supplementingEntity;
	}
	public void setSupplementingEntity(BaseGloballyIdentifyableEntity supplementingEntity)
	{
		this.supplementingEntity = supplementingEntity;
	}



	public boolean getIsEntityContent()
	{
		return entity instanceof ContentVO;
	}
	public boolean getIsEntityExternal()
	{
		return entity instanceof IdOnlyBaseEntityVO;
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
			sb.append(entity.getUUId());
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
			sb.append(supplementingEntity.getUUId());
			sb.append("}");
		}
		else
		{
			sb.append(", --null--");
		}
		sb.append("]");

		return sb.toString();
	}
	
	public static class IdOnlyBaseEntityVO implements BaseGloballyIdentifyableEntity
	{
		private String uuId;

		public IdOnlyBaseEntityVO(String uuId)
		{
			this.uuId = uuId;
		}

		//@Override
		public String getUUId()
		{
			return uuId;
		}
		
	}
}
