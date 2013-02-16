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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import org.sprintsmart.roadmap.connectors.JiraConnector;
import org.sprintsmart.roadmap.model.Marker;
import org.sprintsmart.roadmap.model.ProductBacklog;
import org.sprintsmart.roadmap.model.Sprint;
import org.sprintsmart.roadmap.model.SprintRoadmap;
import org.sprintsmart.roadmap.model.SprintRoadmapConfiguration;
import org.sprintsmart.roadmap.model.StoryStatus;
import org.sprintsmart.roadmap.model.UserStory;
import org.sprintsmart.roadmap.model.VelocityAdjustment;

/**
 * 
 * @author nate
 */
public class AgileRoadmapUI extends Application
{
  Canvas storyCanvas;
  Canvas storyLabelCanvas;
  /** Used to render marker columns */
  Canvas sprintMarkerCanvas;
  /** Used to render sprint arrows */
  Canvas velocityMarkerCanvas;
  Canvas legendCanvas;
  
  GraphicsContext textGraphicsContext;
  
  CanvasContext canvasConfig;

  SprintRoadmapConfiguration config;
  int currentStoryYPos;

  /**
   * The main() method is ignored in correctly deployed JavaFX application.
   * main() serves only as fallback in case the application can not be launched
   * through deployment artifacts, e.g., in IDEs with limited FX support.
   * NetBeans ignores main().
   * 
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args)
  {
    launch(args);
  }

  /**
   * This method initializes the view with any arguments provided by the command
   * line or JNLP configuration.
   */
  private void initialize() throws Exception
  {
    List<String> params = getParameters().getRaw();
    String configFile = params.get(0); //"configFile"
    config = new SprintRoadmapConfiguration(configFile);
  }

  public void start(Stage primaryStage)
  {
    try
    {
      initialize();

      primaryStage.setTitle("SprintSmart - Agile Release Planning Tool");
      Group root = new Group();

      SprintRoadmap roadmap = config.getRoadmap();
      int startingXPos = 0;
      int maxHeight = 0;
      for( ProductBacklog productBacklog : roadmap.getProductBacklog() )
      {            
        BacklogContext backlogContext = new BacklogContext(productBacklog);
        List<UserStory> stories = new JiraConnector(backlogContext).getStories();
        List<Sprint> sprintList = productBacklog.getSprints().getSprint();
        List<Marker> velocities = productBacklog.getVelocityMarkers().getMarker();
        
        int lowestVelocity = getLowestVelocity(velocities);        
        stories = addVelocityAdjustmentStories(backlogContext, stories, sprintList, lowestVelocity);
        
        canvasConfig = new CanvasContext(stories, velocities, productBacklog.getCanvasConfiguration(), startingXPos);
        int requiredWidth = canvasConfig.getWidth();
        int requiredHeight = canvasConfig.getHeight();
        
        storyCanvas = new Canvas(requiredWidth, requiredHeight);
        storyLabelCanvas = new Canvas(requiredWidth, requiredHeight);
        sprintMarkerCanvas = new Canvas(requiredWidth, requiredHeight);
        velocityMarkerCanvas = new Canvas(requiredWidth, requiredHeight);
        legendCanvas = new Canvas(requiredWidth, requiredHeight);
        
        //Set the Text Style
        textGraphicsContext = storyLabelCanvas.getGraphicsContext2D();
        textGraphicsContext.setFont(canvasConfig.defaultFont);
        textGraphicsContext.setFill(Color.BLACK);
        
        drawMarkers(sprintMarkerCanvas.getGraphicsContext2D(), canvasConfig.getMarkers(), sprintList);
        drawStories(storyCanvas.getGraphicsContext2D(), canvasConfig.getStories(), productBacklog.getName());
        
        List<Canvas> canvasList = new ArrayList<Canvas>();
        canvasList.add(storyCanvas);
        canvasList.add(sprintMarkerCanvas);
        canvasList.add(velocityMarkerCanvas);
        canvasList.add(storyLabelCanvas);
        canvasList.add(legendCanvas);
        root.getChildren().addAll(canvasList);
        
        sprintMarkerCanvas.toFront();
        velocityMarkerCanvas.toFront();      
        storyLabelCanvas.toFront();
        
        for( Canvas c : canvasList )
        {
          c.setTranslateX(startingXPos);
        }
        
        startingXPos += canvasConfig.width;
        maxHeight = Math.max(requiredHeight, maxHeight);
      }

      ScrollPane scrollPane = new ScrollPane();
      scrollPane.setPrefSize(1200, 800);
      scrollPane.setContent(root);

      // This doesn't render to a file correctly
      //Scene theScene = new Scene(scrollPane);

      Scene theScene = new Scene(root);

      primaryStage.setScene(theScene);
      primaryStage.show();

      WritableImage image = new WritableImage(startingXPos, maxHeight);
      theScene.snapshot(image);
      File file = new File(roadmap.getImageFileName());
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);      
    } 
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    } 
    finally
    {
    }
  }

  /**
   * @param velocities
   * @return
   */
  private int getLowestVelocity(List<Marker> velocities)
  {
    int lowValue = 0;
    for( Marker m : velocities )
    {
      if( lowValue == 0 )
      {
        lowValue = m.getVelocity();
      }
      else
      {
        lowValue = Math.min(lowValue, m.getVelocity());        
      }
    }
    return lowValue;
  }

  /**
   * @param backlogContext 
   * @param stories
   * @param sprintList
   * @param velocity - This is the velocity value that will be used to place velocity adjustments into the backlog
   */
  private List<UserStory> addVelocityAdjustmentStories(BacklogContext backlogContext, List<UserStory> stories, List<Sprint> sprintList, int velocity)
  {
    List<UserStory> allStories = new ArrayList<UserStory>();
    Iterator<UserStory> storyIterator = stories.iterator();

    String label = "VelocityAdjustment";
    List<String> labelList = Arrays.asList(label);
    
    int totalStorySize = 0;
    while( storyIterator.hasNext() )
    {
      //Add the adjustment stories, if the backlog runs past the sprints defined then we must stop
      int sprintIndex = totalStorySize / velocity;
      if( sprintIndex < sprintList.size() )
      {
        Sprint activeSprint = sprintList.get(sprintIndex);
        Iterator<VelocityAdjustment> adjustments = activeSprint.getVelocityAdjustment().iterator();
        while( adjustments.hasNext() )
        {
          VelocityAdjustment a = adjustments.next();
          totalStorySize += a.getPointValue();
          allStories.add(new UserStory(a.getPointValue(), backlogContext.getColorForLabel(label), a.getTitle(), a.getDescription(), labelList, StoryStatus.parse(a.getStatus())));
          adjustments.remove();
        }    
      }
      //Add the next story in the backlog
      UserStory story = storyIterator.next();
      allStories.add(story);
      totalStorySize += story.getSize();
    }
    return allStories;
  }

  private void drawStories(GraphicsContext gc, List<UserStory> userStories, String header)
  {
    gc.setStroke(Color.GREY);
    gc.setLineWidth(1);
    gc.setLineJoin(StrokeLineJoin.MITER);

    currentStoryYPos = canvasConfig.offsetY;
    
    textGraphicsContext.setTextAlign(TextAlignment.CENTER);
    textGraphicsContext.fillText(header, canvasConfig.storyXPos + (canvasConfig.storyWidth / 2), 20, canvasConfig.storyWidth);
    textGraphicsContext.setTextAlign(TextAlignment.LEFT);
  
    int storyCount = 1;
    for (UserStory story : userStories)
    {
      int size = story.getSize();
      Color color = story.getColor();

      // Create the story
      double xPosRight = canvasConfig.storyXPos + canvasConfig.storyWidth;
      int height = size * canvasConfig.storyPointPixelFactor;

      draw3DRectangle(gc, color, canvasConfig.storyXPos, currentStoryYPos, canvasConfig.storyWidth, height, canvasConfig.storyDepth, true);

      // Make the first story have a "lid"
      if (storyCount == 1)
      {
        addPolyLid(gc, color, canvasConfig.storyXPos, xPosRight, canvasConfig.storyDepth, currentStoryYPos);
      }

      //Add Status Icon
      int imgHeight = 40;
      int imgWidth = 15;
      //int imgWidth = (437/389) * imgHeight;
      gc.drawImage(story.getStatusImage(), canvasConfig.storyXPos + 3, currentStoryYPos + 3, imgWidth, imgHeight); 

      // Add Story Header Text
      String keyAndPoints = story.getText() + " (" + story.getSize() + ")";
      String labels = story.getLabels().toString();
      int textYPos = currentStoryYPos + canvasConfig.fontHeightOffset;
      textGraphicsContext.fillText(keyAndPoints + "  " + labels, canvasConfig.storyXPos + imgWidth + 10, textYPos);
      
      //Add Story Summary
      textYPos += canvasConfig.fontHeightOffset;
      writeStorySummary(story.getSummary(), canvasConfig.storyXPos + imgWidth + 10, textYPos, canvasConfig.textCharsPerStoryLine, height);
      
      
      currentStoryYPos += height;
      storyCount++;
    }
    // Remove any extra space at the bottom of the canvas
    storyCanvas.resize(storyCanvas.getWidth(), currentStoryYPos);
  }

  private void draw3DRectangle(GraphicsContext gc, Color color, int xPosLeft, int yPos, int width, double height, double depth, boolean useGradient)
  {
    double xPosRight = xPosLeft + width;
    double xPosDepth = xPosRight + depth;
    if (useGradient)
    {
      gc.setFill(new LinearGradient(1, 1, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0.0, color), new Stop(1, Color.WHITE)));
      // Only Add Border when using Gradients
      gc.strokeRect(xPosLeft, yPos, width, height);
      gc.strokePolygon(new double[] { xPosRight, xPosRight, xPosDepth, xPosDepth }, new double[] { yPos, yPos + height, yPos + height - depth, yPos - depth }, 4);
    } else
    {
      gc.setFill(color);
    }

    // Front
    gc.fillRect(xPosLeft, yPos, width, height);

    // 3D Side
    gc.fillPolygon(new double[] { xPosRight, xPosRight, xPosDepth, xPosDepth }, new double[] { yPos, yPos + height, yPos + height - depth, yPos - depth }, 4);
  }

  private void addPolyLid(GraphicsContext gc, Color color, int xPosLeft, double xPosRight, double depth, int yPos)
  {
    double xPosDepth = xPosRight + depth;
    gc.setFill(color);
    gc.fillPolygon(new double[] { xPosLeft, xPosRight, xPosDepth, xPosLeft + depth }, new double[] { yPos, yPos, yPos - depth, yPos - depth }, 4);
    gc.strokePolygon(new double[] { xPosLeft, xPosRight, xPosDepth, xPosLeft + depth }, new double[] { yPos, yPos, yPos - depth, yPos - depth }, 4);
  }

  private void drawMarkers(GraphicsContext gc, List<Marker> markers, List<Sprint> sprints)
  {
    int sprintsUntilRelease = sprints.size();
    int width = canvasConfig.storyWidth;
    int markerCount = 0;
    int currentXPos = canvasConfig.storyXPos - canvasConfig.markerColumnPadding;
    for (Marker marker : markers)
    {
      // Draw the "watermark" on the story boxes
      int yPos = canvasConfig.offsetY + marker.getVelocity() * canvasConfig.storyPointPixelFactor * sprintsUntilRelease;
      draw3DRectangle(gc, Color.valueOf(marker.getColor()), canvasConfig.storyXPos - canvasConfig.markerColumnWidth, yPos, width + canvasConfig.markerColumnWidth, canvasConfig.markerHeight, canvasConfig.storyDepth,
          false);
      
      //This is the marker text, render it below the line so it doesn't overlap the sprint label is possible
      textGraphicsContext.fillText(marker.getLabel(), canvasConfig.storyXPos - canvasConfig.markerColumnWidth, yPos + canvasConfig.fontHeightOffset);

      // Render Title Box at the top of the diagram 
      boolean arrowDirection = true;
      int arrowXPos = canvasConfig.storyXPos;
      int titleXPos;
      // Boxes on the Left (first marker)
      if (markerCount == 0)
      {
        titleXPos = currentXPos - canvasConfig.markerColumnWidth;
        currentXPos = canvasConfig.storyXPosRight; // setup for the marker on
                                                   // the right
      }
      // Boxes on the Right
      else
      {
        arrowDirection = false;
        currentXPos += canvasConfig.markerColumnWidth * (markerCount - 1) + canvasConfig.markerColumnPadding;
        arrowXPos = canvasConfig.storyXPosRight - canvasConfig.storyDepth;
        titleXPos = currentXPos;
      }
      draw3DRectangle(legendCanvas.getGraphicsContext2D(), Color.LIGHTSKYBLUE, titleXPos, 10, canvasConfig.markerColumnWidth, 40, 0, true);
      wrapText(marker.getTitle(), titleXPos + 10, 10 + canvasConfig.fontHeightOffset, canvasConfig.markerColumnWidth);

      // Add the Sprint Arrows for this velocity marker
      yPos = canvasConfig.offsetY;
      for (Sprint sprint : sprints)
      {
        yPos += (marker.getVelocity() * canvasConfig.storyPointPixelFactor);
        int arrowLength = markerCount == 0 ? canvasConfig.markerColumnPadding + canvasConfig.markerColumnWidth : //Left Marker Column
                             (markerCount * (canvasConfig.markerColumnPadding + canvasConfig.markerColumnWidth)) + canvasConfig.markerColumnPadding; //Right Marker Columns
        String sprintArrowLabel = sprint.getLabel() + " (" + new SimpleDateFormat("M/d").format(sprint.getEndDate().getTime()) + ")";
        addSprintArrow(velocityMarkerCanvas.getGraphicsContext2D(), sprintArrowLabel, arrowXPos, yPos, arrowLength, arrowDirection);
      }
      markerCount++;
    }
  }

  /**
   * Splits a string on the "/" delimiter and puts tokens on new lines 15 pixels
   * apart.
   * 
   * @param gc
   * @param title
   * @param xPos
   * @param yPos
   * @param width
   */
  private void wrapText(String title, int xPos, int yPos, int width)
  {
    int currentYPos = yPos;
    for (String line : title.split("/"))
    {
      textGraphicsContext.fillText(line, xPos, currentYPos, width);
      currentYPos += canvasConfig.fontHeightOffset;
    }
  }
  
  private void writeStorySummary(String text, int xPos, int yPos, int maxChars, int maxHeight)
  { 
    textGraphicsContext.setFont(canvasConfig.summaryFont);
    
    int currentYPos = yPos;
    int textHeight = canvasConfig.fontHeightOffset + canvasConfig.summaryFontHeightOffset; //Story Title + 1st Row of summary
    StringBuilder currentLine = new StringBuilder();
    for (String word : text.split(" "))
    {
      if( currentLine.length() + word.length() > maxChars )
      {
        textGraphicsContext.fillText(currentLine.toString(), xPos, currentYPos);
        currentYPos += canvasConfig.summaryFontHeightOffset;  
        currentLine.setLength(0);
        textHeight += canvasConfig.summaryFontHeightOffset;
      }
      //Stop if we are going to overrun the next story
      if( textHeight >= maxHeight )
        break;
      
      currentLine.append(word).append(" ");
    }
    if( currentLine.length() > 0 )
    {
      textGraphicsContext.fillText(currentLine.toString(), xPos, currentYPos);      
    }
    textGraphicsContext.setFont(canvasConfig.defaultFont);
  }

  /**
   * 
   * @param gc
   * @param text
   * @param xPos
   *          The xPos of where the arrow head should be
   * @param yPos
   *          The xYpos of where the arrow head should be
   * @param pointRight
   *          true to point right, false to point left
   */
  private void addSprintArrow(GraphicsContext gc, String text, int xPos, int yPos, int length, boolean pointRight)
  {
    gc.setTextAlign(TextAlignment.LEFT);
    gc.setTextBaseline(VPos.BOTTOM);

    int arrowLength = length * (pointRight ? -1 : 1);
    int arrowHeadLength = 10 * (pointRight ? -1 : 1);
    int arrowAngle = 5 * (pointRight ? -1 : 1);
    int textPos = pointRight ? xPos - Math.abs(arrowLength) : xPos + Math.abs(arrowLength);

    // Arrow
    gc.strokeLine(xPos, yPos, xPos + arrowLength, yPos);
    // ArrowHead
    gc.strokeLine(xPos, yPos, xPos + arrowHeadLength, yPos + arrowAngle);
    gc.strokeLine(xPos, yPos, xPos + arrowHeadLength, yPos - arrowAngle);
    // Label
    if (!pointRight)
    {
      gc.setTextAlign(TextAlignment.RIGHT);
    }
    gc.fillText(text, textPos, yPos - 3);

  }
}