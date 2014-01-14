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

public enum StoryStatus
{
  OPEN, IN_PROGRESS, COMPLETE;
  
  public static StoryStatus parse(String statusText)
  {
    StoryStatus returnStatus = StoryStatus.OPEN;
    for( StoryStatus s : StoryStatus.values() )
    {
      if( s.name().equalsIgnoreCase(statusText) )
      {
        returnStatus = s;
      }
    }
    return returnStatus;
  }
}