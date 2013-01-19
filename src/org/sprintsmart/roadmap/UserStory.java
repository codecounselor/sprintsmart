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

  public UserStory(int size, Color color, String text) 
  {
    this.size = size;
    this.color = color;
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

  public int getSize()
  {
    return size;
  }

  public Color getColor()
  {
    return color;
  }

}
