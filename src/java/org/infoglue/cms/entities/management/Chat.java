package org.infoglue.cms.entities.management;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Chat
{
    static LinkedList messages = new LinkedList();

    public List addMessage(String userName, String text)
    {
        if (text != null && text.trim().length() > 0)
        {
            messages.addFirst(new Message(messages.size(), userName, text));
            //while (messages.size() > 10)
            //{
            //messages.removeLast();
                //}
        }

        return messages;
    }

    public List getMessages()
    {
        return messages;
    }

    public List getMessages(int lastIndex)
    {
    	//if(lastIndex + 1 == messages.size() - 1)
    	//	return new ArrayList();
    	
    	System.out.println("lastIndex:" + lastIndex + ":" + messages.size());
        return messages.subList(0, messages.size() - (lastIndex + 1));
    }

}