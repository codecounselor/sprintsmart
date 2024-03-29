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

import java.util.List;

import javafx.scene.text.Font;

import org.sprintsmart.roadmap.model.CanvasConfiguration;
import org.sprintsmart.roadmap.model.Marker;
import org.sprintsmart.roadmap.model.UserStory;

/**
 * This class holds the context for rendering the canvas objects.
 * Any attribute related to sizing or positioning of elements should be maintained here.
 * 
 * @author Nate Good
 *
 */
public class CanvasContext
{
  final List<Marker> markers;
  final List<UserStory> stories;
  final int markerColumnWidth;
  final int storyWidth;

  int storyXPos;
  int storyXPosRight;

  int storyPointPixelFactor;
  int storyDepth = 20;
  
  int markerHeight = 5;
  int markerColumnPadding = 10;

  int fontHeightOffset = 15;
  int summaryFontHeightOffset = 12;
  
  int textCharsPerStoryLine;
  final Font defaultFont = new Font("Arial", 14);
  final Font summaryFont = new Font("Arial", 12); 
  final private double characterPerPixel = 0.17; 
  
  int offsetY = 50;
  int width;
  int height;

  public CanvasContext(List<UserStory> stories, List<Marker> markers, CanvasConfiguration config, int startingXPos) 
  {
    this.stories = stories;
    this.markers = markers;
    this.markerColumnWidth = config.getMarkerWidth();
    this.storyWidth = config.getStoryWidth();
    this.storyPointPixelFactor = config.getStorySizePixelFactor();

    textCharsPerStoryLine = (int) (characterPerPixel * this.storyWidth);
    storyXPos = startingXPos + markers.size() > 0 ? markerColumnWidth + (2 * markerColumnPadding) : 0;
    storyXPosRight = storyXPos + storyWidth + storyDepth;

    width = storyXPos + storyWidth + storyDepth;
    for (int i=1; i < markers.size(); i++)
    {
      width += (markerColumnWidth + ( 2 * markerColumnPadding ) );
    }
    
    height = offsetY;
    for (UserStory s : stories)
    {
      height += (storyPointPixelFactor * s.getSize());
    }
    height += 20; //for some space at the bottom
  }

  public List<Marker> getMarkers()
  {
    return markers;
  }
  
  public List<UserStory> getStories()
  {
    return stories;
  }
  
  int getWidth()
  {
    return width;
  }

  int getHeight()
  {
    return height;
  }

}
