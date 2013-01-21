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
      label2Color.put(label.getValue(), Color.valueOf(label.getColor()));      
    }
    
    try
    {
      URL url = new URL(productBacklogConfig.getRssFeed());

      InputStream is = url.openStream();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document xmlDoc = db.parse(is);
      NodeList itemNodes = xmlDoc.getElementsByTagName("item");
      for( int i=0; i < itemNodes.getLength(); i++ )
      {
        Element item = (Element) itemNodes.item(i);
        Color storyColor = Color.GRAY;
        String storyName = item.getElementsByTagName("key").item(0).getTextContent();
        NodeList labels = item.getElementsByTagName("label");
        if( labels != null )
        {
          for( int l=0; l < labels.getLength(); l++ )
          {
            String label = labels.item(l).getTextContent();
            if( label2Color.containsKey(label) )
            {
              storyColor = label2Color.get(label);
            }
          }
        }

        stories.add(new UserStory(5, storyColor, storyName));
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
