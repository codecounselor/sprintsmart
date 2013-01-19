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
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

/**
 * 
 * @author nate
 */
public class AgileRoadmapUI extends Application
{
  Canvas storyCanvas;
  Canvas sprintMarkerCanvas;
  Canvas velocityMarkerCanvas;
  Canvas legendCanvas;
  
  static CanvasConfig canvasConfig;
  
  int currentStoryYPos;

  
  public void start(Stage primaryStage)
  {
    primaryStage.setTitle("Drawing Operations Test");

    int requiredWidth = canvasConfig.getWidth();
    int requiredHeight = canvasConfig.getHeight();    
    
    storyCanvas = new Canvas(requiredWidth, requiredHeight);
    sprintMarkerCanvas = new Canvas(requiredWidth, requiredHeight);
    velocityMarkerCanvas = new Canvas(requiredWidth, requiredHeight);
    legendCanvas = new Canvas(requiredWidth, requiredHeight);
    
    drawMarkers(sprintMarkerCanvas.getGraphicsContext2D(), canvasConfig.getMarkers(), 4);
    drawStories(storyCanvas.getGraphicsContext2D(), canvasConfig.getStories());

    Group root = new Group();
    root.getChildren().add(storyCanvas);
    root.getChildren().add(sprintMarkerCanvas);
    root.getChildren().add(velocityMarkerCanvas);
    root.getChildren().add(legendCanvas);

    sprintMarkerCanvas.toFront();

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setPrefSize(requiredWidth, requiredHeight);
    scrollPane.setContent(root);
    
    //This doesn't render to a file correctly
    Scene theScene = new Scene(scrollPane);

    //Have to set the root if we want to create a file, to view interactively we must use a scroll pane though
    //Scene theScene = new Scene(root);
    
    primaryStage.setScene(theScene);
    primaryStage.show();
    
    try
    {
      WritableImage image = new WritableImage(requiredWidth, requiredHeight);
      theScene.snapshot(image);
      File file = new File("/Users/nate/Pictures/AgileMap.png");
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    } 
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

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
    List<UserStory> stories = new ArrayList<UserStory>();
    stories.add(new UserStory(5, Color.BLUE, "Story 1"));
    stories.add(new UserStory(8, Color.RED, "Story 2"));
    stories.add(new UserStory(13, Color.GREEN, "Story 3"));
    stories.add(new UserStory(5, Color.CHOCOLATE, "Story 4"));
    stories.add(new UserStory(20, Color.CORAL, "Story 5"));
    stories.add(new UserStory(2, Color.CRIMSON, "Story 6"));
    stories.add(new UserStory(20, Color.BLUE, "Story 7"));
    stories.add(new UserStory(13, Color.GREEN, "Story 8"));
    
    stories = new JiraConnector("").getStories();

    List<VelocityMarker> markers = new ArrayList<VelocityMarker>();
    markers.add(new VelocityMarker("Release Deliverables/ @ Velocity=14", "Release Commit Level", 14, Color.GREEN));
    markers.add(new VelocityMarker("Release Deliverables/ @ Velocity=20", "Potential Delivery Level", 20, Color.valueOf("RED")));
    //markers.add(new VelocityMarker("Release Deliverables/ @ Velocity=16", "Current Delivery Level", 16, Color.YELLOW));
    
    canvasConfig = new CanvasConfig(stories, markers, 150, 300);
    
    launch(args);
  }

  private void drawStories(GraphicsContext gc, List<UserStory> userStories)
  {
    gc.setStroke(Color.GREY);
    gc.setLineWidth(1);
    gc.setLineJoin(StrokeLineJoin.MITER);

    currentStoryYPos = canvasConfig.offsetY;
    int storyCount = 1;
    for (UserStory story : userStories)
    {
      int size = story.getSize();
      Color color = story.getColor();

      // Create the story
      double xPosRight = canvasConfig.storyXPos + canvasConfig.storyWidth;
      double height = size * canvasConfig.storyPointPixelFactor;

      // Make sure the canvas is big enough to render the next story
      if (storyCanvas.getHeight() < currentStoryYPos + height)
      {
        System.out.println(storyCanvas.isResizable());
        storyCanvas.resize(storyCanvas.getWidth(), currentStoryYPos + height);
      }

      draw3DRectangle(gc, color, canvasConfig.storyXPos, currentStoryYPos, canvasConfig.storyWidth, height, canvasConfig.storyDepth, true);

      // Make the first story have a "lid"
      if (storyCount == 1)
      {
        addPolyLid(gc, color, canvasConfig.storyXPos, xPosRight, canvasConfig.storyDepth, currentStoryYPos);
      }

      // Add Story Text
      Font font = new Font(12.0);
      gc.setFont(font);
      gc.setFill(Color.BLACK);
      gc.fillText(story.getText(), canvasConfig.storyXPos + 10, currentStoryYPos + 15);
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
      //Only Add Border when using Gradients
      gc.strokeRect(xPosLeft, yPos, width, height);
      gc.strokePolygon(new double[] { xPosRight, xPosRight, xPosDepth, xPosDepth }, new double[] { yPos, yPos + height, yPos + height - depth, yPos - depth }, 4);
    } 
    else
    {
      gc.setFill(color);
    }
    
    //Front
    gc.fillRect(xPosLeft, yPos, width, height);
    
    //3D Side
    gc.fillPolygon(new double[] { xPosRight, xPosRight, xPosDepth, xPosDepth }, new double[] { yPos, yPos + height, yPos + height - depth, yPos - depth }, 4);
  }

  private void addPolyLid(GraphicsContext gc, Color color, int xPosLeft, double xPosRight, double depth, int yPos)
  {
    double xPosDepth = xPosRight + depth;
    gc.setFill(color);
    gc.fillPolygon(new double[] { xPosLeft, xPosRight, xPosDepth, xPosLeft + depth }, new double[] { yPos, yPos, yPos - depth, yPos - depth }, 4);
    gc.strokePolygon(new double[] { xPosLeft, xPosRight, xPosDepth, xPosLeft + depth }, new double[] { yPos, yPos, yPos - depth, yPos - depth }, 4);
  }

  private void drawMarkers(GraphicsContext gc, List<VelocityMarker> markers, int sprintsUntilRelease)
  {
    int width = canvasConfig.storyWidth;
    int markerCount = 0;
    int currentXPos = canvasConfig.storyXPos - canvasConfig.markerColumnPadding;
    for (VelocityMarker marker : markers)
    {
      //Draw the "watermark" on the story boxes
      int yPos = canvasConfig.offsetY + marker.getVelocity() * canvasConfig.storyPointPixelFactor * sprintsUntilRelease;
      draw3DRectangle(gc, marker.getColor(), canvasConfig.storyXPos-canvasConfig.markerColumnWidth, yPos, width+canvasConfig.markerColumnWidth, canvasConfig.markerHeight, canvasConfig.storyDepth, false);
      gc.setFill(Color.BLACK);      
      gc.fillText(marker.getText(), canvasConfig.storyXPos-canvasConfig.markerColumnWidth, yPos);

      //Render Title Box
      boolean arrowDirection = true;
      int arrowXPos = canvasConfig.storyXPos;
      int titleXPos;
      //Boxes on the Left (first marker)
      if (markerCount == 0)
      {
        titleXPos = currentXPos - canvasConfig.markerColumnWidth;
        currentXPos = canvasConfig.storyXPosRight; //setup for the marker on the right
      }
      //Boxes on the Right
      else
      {
        arrowDirection = false;
        currentXPos += canvasConfig.markerColumnWidth * (markerCount - 1) + canvasConfig.markerColumnPadding;
        arrowXPos = canvasConfig.storyXPosRight - canvasConfig.storyDepth;
        titleXPos = currentXPos;        
      }
      draw3DRectangle(legendCanvas.getGraphicsContext2D(), Color.LIGHTSKYBLUE, titleXPos, canvasConfig.offsetY - 15, canvasConfig.markerColumnWidth, 40, 0, true);
      wrapText(legendCanvas.getGraphicsContext2D(), marker.getTitle(), titleXPos + 10, canvasConfig.offsetY, canvasConfig.markerColumnWidth);
      
      //Add the Sprint Arrows for this velocity marker
      yPos = canvasConfig.offsetY;
      for (int i = 0; i < sprintsUntilRelease; i++)
      {
        yPos += (marker.getVelocity() * canvasConfig.storyPointPixelFactor);
        int arrowLength = markerCount == 0 ? 
          canvasConfig.markerColumnPadding + canvasConfig.markerColumnWidth : 
          markerCount * (canvasConfig.markerColumnWidth + 2 * canvasConfig.markerColumnPadding); 
        addSprintArrow(velocityMarkerCanvas.getGraphicsContext2D(), "Sprint " + (i + 1), arrowXPos, yPos, arrowLength, arrowDirection);
      }
      markerCount++;
    }
  }
  
  /**
   * Splits a string on the "/" delimiter and puts tokens on new lines 15 pixels apart.
   * 
   * @param gc
   * @param title
   * @param xPos
   * @param yPos
   * @param width
   */
  private void wrapText(GraphicsContext gc, String title, int xPos, int yPos, int width)
  {
    int lineHeight = 15;
    int currentYPos = yPos;
    gc.setFill(Color.BLACK);
    for( String line : title.split("/") )
    {
      gc.fillText(line, xPos, currentYPos, width);
      currentYPos += lineHeight;
    }
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
    int arrowHeadLength = 15 * (pointRight ? -1 : 1);
    int arrowAngle = 10 * (pointRight ? -1 : 1);
    int textPos = pointRight ? xPos - Math.abs(arrowLength) : xPos + Math.abs(arrowLength);

    // Arrow
    gc.strokeLine(xPos, yPos, xPos + arrowLength, yPos);
    // ArrowHead
    gc.strokeLine(xPos, yPos, xPos + arrowHeadLength, yPos + arrowAngle);
    gc.strokeLine(xPos, yPos, xPos + arrowHeadLength, yPos - arrowAngle);
    //Label
    if (!pointRight)
    {
      gc.setTextAlign(TextAlignment.RIGHT);
    }
    gc.fillText(text, textPos, yPos - 10);

  }
}