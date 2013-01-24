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

import org.sprintsmart.roadmap.model.Label;
import org.sprintsmart.roadmap.model.ProductBacklog;
import org.sprintsmart.roadmap.model.UserStory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class JiraConnector
{
  private List<UserStory> stories = new ArrayList<UserStory>();  
  private Map<String, Color> label2Color = new HashMap<String,Color>();
  
  public JiraConnector(ProductBacklog productBacklogConfig) 
  {
    for( Label label : productBacklogConfig.getLabelThemes().getLabel() )
    {
      if( label.getColor() != null )
      {
        label2Color.put(label.getValue(), Color.valueOf(label.getColor()));        
      }
      else if( label.getWebColor() != null )
      {
        label2Color.put(label.getValue(), Color.web(label.getWebColor()));        
      }
    }
    
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
        String storyName = jiraItem.getElementsByTagName("key").item(0).getTextContent();
        String summary = jiraItem.getElementsByTagName("summary").item(0).getTextContent();
        String storySize = xpath.evaluate(productBacklogConfig.getStorySizeXPath(), jiraItem);
        
        NodeList labels = jiraItem.getElementsByTagName("label");
        List<String> labelList = new ArrayList<String>();
        if( labels != null )
        {
          for( int l=0; l < labels.getLength(); l++ )
          {
            String label = labels.item(l).getTextContent();
            labelList.add(label);
            if( label2Color.containsKey(label) )
            {
              storyColor = label2Color.get(label);
            }
          }
        }

        //Don't add a story if it hasn't been estimated
        if( storySize != null && storySize.trim().length() > 0 )
        {
          stories.add(new UserStory(Double.valueOf(storySize).intValue(), storyColor, storyName, summary, labelList));          
        }
      }
    } 
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public List<UserStory> getStories()
  {
    return stories;
  }
}
