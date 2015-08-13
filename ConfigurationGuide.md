# Introduction #
This page is intended to get you setup with the application and be able to write a configuration file that is customized for your own Agile Team(s).  You may find it most useful if you have to generate roadmaps across several teams.  It will help you visualize cross dependencies and help increase confidence when making longer term commitments.

## Installation Instructions ##
This application uses JavaFX.  Therefore, Java7 is required for you to execute the application, make sure you have the latest JDK from oracle installed on your system.

  1. Download the latest executable jar from the [http://code.google.com/p/sprintsmart/downloads/list Downloads Page}
  1. Either copy the sample XML below into your own xml configuration file or define your own based on your roadmap requirements.
  1. Execute the following command
` java -jar release-planner.jar RoadMapConfig.xml `

This will launch the GUI frame that contains the diagram.  Do not worry if it doesn't fit on the screen completely.  A image file will be automatically generated in the user directory.  You may exit the window at any time at this point.

## Configuration Instructions ##
  * **Connectors** - The current implementation supports a JIRA connector which is no more than an RSS feed.  You are expected to write a JIRA Query that returns atleast a minimum the following fields:
    1. key
    1. status
    1. summary
    1. story points (you must provide the xpath expression relative to the story item for this)
      * **Warning** - If you do not provide a value > 0 for the story points the story will not be rendered.
    1. labels (if you are using them to assign story colors.  The default color is Gray)
  * **Markers** - A marker represents a "velocity mark" that will project when you may complete work over a fixed number of sprints.  You may define as many markers are you want for each backlog.  The first marker will always be rendered to the left of the story following by all remaining markers on the right.
  * **Label Themes** - If you want to assign a color theme to your stories you may do this by assigning a color to a label.  The first label that matches a defined color will be used.  It is assumed that a story will only belong to one functional deliverable (i.e. Epic).
    * Two different attributes are supported for the colors
      1. color - Any Color constant [available here ](http://docs.oracle.com/javafx/2/api/javafx/scene/paint/Color.html)
      1. webColor - any web hex value

```
<SprintRoadmap imageFileName="SampleBacklog.png" >

  <ProductBacklog
    name="Sample Backlog 1"
    rssFeed="https://jira.atlassian.com/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=project+%3D+GHS+AND+summary+~+%22As+a+user%22+AND+issuetype+%3D+Story+AND+cf[10653]+%3C+%2220%22+AND+cf[10653]+%3E+%222%22+AND+created+%3E%3D+-20w&amp;tempMax=200&amp;field=key&amp;field=labels&amp;field=summary&amp;field=customfield_10653&amp;field=status"
    storySizeXPath="customfields/customfield[@id='customfield_10653']//customfieldvalue">
    <CanvasConfiguration storyWidth="300" markerWidth="145" storySizePixelFactor="20" />

    <Sprints>
      <Sprint startDate="2013-01-09" endDate="2013-01-29" label="Sprint 13.01" />
      <Sprint startDate="2013-01-30" endDate="2013-02-19" label="Sprint 13.02" />
      <Sprint startDate="2013-02-20" endDate="2013-03-12" label="Sprint 13.03" />
      <Sprint startDate="2013-03-13" endDate="2013-04-02" label="Sprint 13.04" />
    </Sprints>

    <LabelThemes>
      <Label value="Triaged" color="CORAL" />
      <Label value="blitz"   color="ORANGE" />
      <Label value="must"    color="FIREBRICK" />
    </LabelThemes>

    <VelocityMarkers>
      <Marker velocity="3" color="LIGHTGREEN" title="Release Deliverables/ @ Velocity=3" label="Release Commit Level" />
      <Marker velocity="5" color="YELLOW" title="Release Deliverables/ @ Velocity=5" label="Current Delivery Level" />
      <Marker velocity="7" color="RED" title="Release Deliverables/ @ Velocity=7" label="Potential Delivery Level" />
    </VelocityMarkers>

  </ProductBacklog>

  <ProductBacklog
    name="Sample Backlog 2"
    rssFeed="https://jira.atlassian.com/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=project+%3D+GHS+AND+summary+~+%22As+a+user%22+AND+issuetype+%3D+Story+AND+cf[10653]+%3C+%2220%22+AND+cf[10653]+%3E+%222%22+AND+created+%3E%3D+-20w&amp;tempMax=200&amp;field=key&amp;field=labels&amp;field=summary&amp;field=customfield_10653&amp;field=status"
    storySizeXPath="customfields/customfield[@id='customfield_10653']//customfieldvalue">
    <CanvasConfiguration storyWidth="300" markerWidth="145" storySizePixelFactor="20" />

    <Sprints>
      <Sprint startDate="2013-01-09" endDate="2013-01-29" label="Sprint 13.01" />
      <Sprint startDate="2013-01-30" endDate="2013-02-19" label="Sprint 13.02" />
      <Sprint startDate="2013-02-20" endDate="2013-03-12" label="Sprint 13.03" />
      <Sprint startDate="2013-03-13" endDate="2013-04-02" label="Sprint 13.04" />
    </Sprints>

    <LabelThemes>
      <Label value="Triaged" color="DARKKHAKI" />
      <Label value="blitz" color="ORANGE" />
      <Label value="must" color="FIREBRICK" />
    </LabelThemes>

    <VelocityMarkers>
      <Marker velocity="8" color="BLUE" title="Release Deliverables/ @ Velocity=8" label="Release Commit Level" />
      <Marker velocity="12" color="ORANGE" title="Release Deliverables/ @ Velocity=11" label="Current Delivery Level" />
    </VelocityMarkers>

  </ProductBacklog>

</SprintRoadmap>
```

<img src='http://sprintsmart.googlecode.com/files/SampleBacklog.png' />