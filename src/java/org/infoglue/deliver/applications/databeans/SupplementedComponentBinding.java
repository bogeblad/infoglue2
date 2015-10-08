/**
 * 
 */
package org.infoglue.deliver.applications.databeans;

/**
 * @author Erik StenbÃ¤cka <stenbacka@gmail.com>
 */
public class SupplementedComponentBinding extends ComponentBinding
{
	private String supplementingEntityId;
	private String supplementingAssetKey;

	public SupplementedComponentBinding(String entityId, String assetKey)
	{
		this.supplementingEntityId = entityId;
		this.supplementingAssetKey = assetKey;
	}

	public String getSupplementingEntityId()
	{
		return supplementingEntityId;
	}

	public void setSupplementingEntityId(String supplementingEntityId)
	{
		this.supplementingEntityId = supplementingEntityId;
	}

	public String getSupplementingAssetKey()
	{
		return supplementingAssetKey;
	}

	public void setSupplementingAssetKey(String supplementingAssetKey)
	{
		this.supplementingAssetKey = supplementingAssetKey;
	}
}
