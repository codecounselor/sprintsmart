/*
    Copyright (C) 2012-2013 Nate Good

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.sprintsmart.roadmap.connectors;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.sprintsmart.roadmap.BacklogContext;
import org.sprintsmart.roadmap.model.ProductBacklog;
import org.sprintsmart.roadmap.model.StoryStatus;
import org.sprintsmart.roadmap.model.UserStory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JiraConnector
{
  private List<UserStory> stories = new ArrayList<UserStory>();  
  
  
  private static Map<String, StoryStatus> jiraStatus2Status = new HashMap<String, StoryStatus>();
  static
  {
    jiraStatus2Status.put("Open", StoryStatus.OPEN);
    jiraStatus2Status.put("In Progress", StoryStatus.IN_PROGRESS);
    jiraStatus2Status.put("Complete", StoryStatus.COMPLETE);
    jiraStatus2Status.put("Resolved", StoryStatus.COMPLETE);
  }
  
  public JiraConnector(BacklogContext backlogContext) 
  {    
    ProductBacklog productBacklogConfig = backlogContext.getBacklog();
    try
    {
      XPath xpath = XPathFactory.newInstance().newXPath();
      URL url = new URL(productBacklogConfig.getRssFeed());

      InputStream is = url.openStream();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document xmlDoc = db.parse(is);
      NodeList itemNodes = xmlDoc.getElementsByTagName("item");
      for( int i=0; i < itemNodes.getLength(); i++ )
      {
        Element jiraItem = (Element) itemNodes.item(i);
        Color storyColor = Color.GRAY;
        String storyName = getItemAttribute(jiraItem, "key");
        String status = getItemAttribute(jiraItem, "status");
        String summary = getItemAttribute(jiraItem, "summary");
        String storySize = xpath.evaluate(productBacklogConfig.getStorySizeXPath(), jiraItem);
        
        NodeList labels = jiraItem.getElementsByTagName("label");
        List<String> labelList = new ArrayList<String>();
        if( labels != null )
        {
          for( int l=0; l < labels.getLength(); l++ )
          {
            String label = labels.item(l).getTextContent();
            labelList.add(label);
            Color colorForLabel = backlogContext.getColorForLabel(label);
            if( colorForLabel != null )
            {
              storyColor = colorForLabel;
            }
          }
        }

        //Don't add a story if it hasn't been estimated
        if( storySize != null && storySize.trim().length() > 0 )
        {
          int intValue = Double.valueOf(storySize).intValue();
          //for now we won't render these stories, eventually they can be represented but must extend the sprint/release markers
          if( intValue > 0 )
          {
            stories.add(new UserStory(Double.valueOf(storySize).intValue(), storyColor, storyName, summary, labelList, jiraStatus2Status.get(status)));            
          }
        }
      }
    } 
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * @param jiraItem
   * @param string
   * @return
   */
  private String getItemAttribute(Element jiraItem, String attributeName)
  {
    String value = "";
    Node item = jiraItem.getElementsByTagName(attributeName).item(0);
    if( item != null )
    {
      value = item.getTextContent();      
    }
    return value;
  }

  public List<UserStory> getStories()
  {
    return stories;
  }
}
