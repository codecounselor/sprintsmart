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

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * @author Nate Good
 * 
 */
public class SprintRoadmapConfiguration
{
  SprintRoadmap roadmap;
  
  /**
   * Loads a configuration file from the filesystem
   */
  public SprintRoadmapConfiguration(String pConfigFileLocation) throws Exception
  {
    File file = new File(pConfigFileLocation);
    JAXBContext jaxbContext = JAXBContext.newInstance(SprintRoadmap.class);
 
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    roadmap = (SprintRoadmap) jaxbUnmarshaller.unmarshal(file);
  }
  
  /**
   * @return the roadmap
   */
  public SprintRoadmap getRoadmap()
  {
    return roadmap;
  }
}
