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
package org.sprintsmart.roadmap;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

import org.sprintsmart.roadmap.model.Label;
import org.sprintsmart.roadmap.model.ProductBacklog;

/**
 * @author Nate Good
 *
 */
public class BacklogContext
{
  private ProductBacklog backlog;
  private Map<String, Color> label2Color = new HashMap<String,Color>();
  
  /**
   * 
   */
  public BacklogContext(ProductBacklog backlog) 
  {
    this.backlog = backlog;
    for( Label label : backlog.getLabelThemes().getLabel() )
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
  }
  
  /**
   * @return the backlog
   */
  public ProductBacklog getBacklog()
  {
    return backlog;
  }
  
  public Color getColorForLabel(String pLabel)
  {
    return label2Color.get(pLabel);
  }
  
}
