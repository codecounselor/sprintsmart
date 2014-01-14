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
package org.sprintsmart.roadmap.model;

import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * 
 * @author Nate Good
 */
public class UserStory
{
  private int size;
  private Color color;
  private String text;
  private String summary;
  private List<String> labels;
  private Image statusImage;
  
  private static Image statusImgNotStarted;
  private static Image statusImgInProgress;
  private static Image statusImgComplete;
  
  static
  {
    statusImgNotStarted = new Image(UserStory.class.getResourceAsStream("/img/red_light.png"));
    statusImgInProgress = new Image(UserStory.class.getResourceAsStream("/img/yellow_light.png"));
    statusImgComplete = new Image(UserStory.class.getResourceAsStream("/img/green_light.png"));
//    statusImgNotStarted = new Image(UserStory.class.getResourceAsStream("/img/traffic_light_red.png"));
//    statusImgInProgress = new Image(UserStory.class.getResourceAsStream("/img/traffic_light_yellow.png"));
//    statusImgComplete = new Image(UserStory.class.getResourceAsStream("/img/traffic_light_green.png"));
  }
  public UserStory(int size, Color color, String text, String summary, List<String> labels, StoryStatus status) 
  {
    this.size = size;
    this.color = color;
    this.text = text;
    this.summary = summary;
    this.labels = labels;
    this.statusImage = status == StoryStatus.COMPLETE || status == StoryStatus.CLOSED ? statusImgComplete : 
                       status == StoryStatus.IN_PROGRESS ? statusImgInProgress : statusImgNotStarted;
  }

  public String getText()
  {
    return text;
  }
  
  /**
   * @return the summary
   */
  public String getSummary()
  {
    return summary;
  }

  /**
   * @return the labels
   */
  public List<String> getLabels()
  {
    return labels;
  }
  
  public int getSize()
  {
    return size;
  }

  public Color getColor()
  {
    return color;
  }
  
  /**
   * @return the statusImage
   */
  public Image getStatusImage()
  {
    return statusImage;
  }

}
