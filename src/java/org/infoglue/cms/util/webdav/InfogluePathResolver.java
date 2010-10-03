package org.infoglue.cms.util.webdav;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.FileResource;
import com.bradmcevoy.http.FolderResource;
import com.bradmcevoy.http.Resource;

/**
 * This class is responsible for parsing and getting the resource in question. Not sure this is the way to go.
 * 
 * @author mattiasbogeblad
 *
 */

public class InfogluePathResolver 
{
	public static Resource resolvePath(Path path, RepositoryResourceFactory resourceFactory)
	{
		Resource resource = null;
		
		//System.out.println("\n************************");
		int i=0;
		for(String part : path.getParts())
		{
			i++;
			System.out.println("part:" + part + ":" + path.getParts().length + ":" + i);
			if(part.startsWith("webdavedit") || (part.startsWith("repositories") && i < 3))
				resource = new AllRepositoryResource(resourceFactory); 
			else
			{
				System.out.println("resource:" + resource + ":" + (resource instanceof FolderResource));
				if(resource instanceof FolderResource)
				{
					FolderResource folderResource = (FolderResource)resource;
					resource = folderResource.child(part);
					System.out.println("resource child:" + resource);
				}
				else if(resource instanceof FileResource)
				{
					//System.out.println("resource was a file:" + resource);
				}
			}
		}
		
		//System.out.println("************************");		
		
		return resource;
	}
}
