/******************************************
 *  Author : Marcos Tavares   
 *  Created On : Fri Mar 8 2024
 *  File : TestTransportation.java
 *  Description: This program showcases random walk animations
 *******************************************/
import java.util.Arrays;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

public class IndividualProject extends Application {

    private boolean[] touched = new boolean[10]; // Boolean array to keep track of any circle that has been touched

    public static void main(String[] args) throws Exception {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        Pane pane = new Pane(); // Creating a pane
        Scene scene = new Scene(pane, 950, 500); // Creating a scene and adding a pane
        Arrays.fill(touched, false); // Filling the boolean array with false values(no green circle has been touched yet)
        Circle[] points = new Circle[2000]; // Initial array for the first random walk
        Circle[] greenCircles = new Circle[10]; // Array for the green circles
        
        greenCircles(pane, greenCircles); // Green circles created and added to the pane with this method call
        
        /**
         * The random walk method call starts it all, after it is called it starts the first random walk(blue), and within itself it
         * calls for the other walks(red). The animation is included in the random walk also
         */
        randomWalk(points, pane.getWidth()/2, pane.getHeight()/2, pane, Color.BLUE, greenCircles);

        primaryStage.setTitle("Random Walk"); // TItle of the window set to Random Walk
        primaryStage.setResizable(false); // Making the window fixed size(not resizable)
        primaryStage.setScene(scene);   // Setting the scene in the window
        primaryStage.show(); // Showing the window
    }

    /**
     * Description: This method picks a random direction and distance and performs an animation of points walking, it limits the walk to
     * the size of the pane. The red walk animation only starts if there is an intersection between the animation of the blue walk with 
     * the random green circles. Lines are drawn connecting all the points.
     * @param points Contains the points of a walk as an array
     * @param x The initial X coordinate of the first walk(middle of pane)
     * @param y The initial Y coordinate of the first walk(middle of pane)
     * @param pane  The pane where everything happens
     * @param color The color of which I want the walk to be
     * @param greenCircles The green circles array that are already in the pane
     */
    public void randomWalk(Circle[] points, double x, double y, Pane pane, Color color, Circle[] greenCircles )
    {
        Random random = new Random(); // Randomizer for which direction
        Random steps = new Random(); // Randomizer for how many steps(1-9)
        
        
        for(int i = 0; i < 2000; i++) // This loop iterates through all the points in the first walk and following walks
        {
            int cardinal = random.nextInt(8) + 1; // Random direction picked
            int distance = steps.nextInt(15) + 5; // Random amount of steps picked

            double newX = x; // New X variable for managing the coordinates
            double newY = y; // New Y variable for managing the coordinates
            
            //(N, NE, E, SE, S, SW, W, NW)
            // This switch statement does random changes to coordinates depending on the random direction picked
            switch (cardinal){
                case 1: // Point moves to North(Y decreases)
                    newY -= distance; 
                    break;
                case 2: // Point moves to Northeast(Y decreases, X increases)
                    newX += distance;
                    newY -= distance;
                    break;
                case 3: // Point moves to East(X increases)
                    newX += distance;
                    break;
                case 4:  // Point moves to Southeast(Y increases, X increases)
                    newX += distance;
                    newY += distance;
                    break;
                case 5: // Point moves to South(Y increases)
                    newY += distance;
                    break;
                case 6: // Point moves to Southwest(Y increases, X decreases)
                    newX -= distance;
                    newY += distance;
                    break;
                case 7: // Point moves to West(X decreases)
                    newX -= distance;
                    break;
                case 8: // Point moves to Northwest(Y decreases, X decreases)
                    newX -= distance;
                    newY -= distance;
                    break;  
            }

            // Limiting the walk within the pane
            newX = Math.max(0, Math.min(pane.getWidth(), newX));
            newY = Math.max(0, Math.min(pane.getHeight(), newY));

            // After a direction and coordinate is picked the point is added to the array and so on
            points[i] = new Circle(newX, newY, 3, color);

            x = newX; // x is changed to the coordinate of the new point
            y = newY; // y is changed to the coordinate of the new point
        }
        
        // This array is to hold the coordinates of the first point(for better coordinate managing later on)
        double coordinates[] = {points[0].getCenterX(), points[0].getCenterY()};
        
        Timeline timeline = new Timeline(); // Timeline object created for the animation of both points and lines i.e Walks.
        // For loop for the animation of each point in the points array
        for (int i = 1; i < 2000; i++) {
           final int index =i; // Making a final variable to store the index of the loop

            timeline.getKeyFrames().add( // Key frames being added to the timeline
                new KeyFrame(Duration.millis(180000 * i / 2000.0), (e) ->{ // event handler for when a walk animation touches a green circle
                // This time duration is for making the animation last 180000 milliseconds and each point animation time be equally divided
                    
                    for (int j = 0; j < greenCircles.length; j++) 
                    {
                        // Creates a shape based on the intersection of the walk with green circles
                        Shape intersection = Shape.intersect(points[0], greenCircles[j]); 

                        // If the shape is not 0 there was an intersection and if the circle was never before touched the if statement runs
                        if (intersection.getBoundsInLocal().getWidth() != -1 && !touched[j]) 
                        {
                            greenCircles[j].setFill(Color.RED); // makes the green circle that was touched red
                            touched[j] = true; // keeps track of the touched green circles
                            Circle[] circle = new Circle[2000]; // new array for the next random walk
                            // Calls random walk with a brand new array and checks it again until all circles 
                            // that have been touched has generated a random red walk
                            randomWalk(circle, greenCircles[j].getCenterX(), greenCircles[j].getCenterY(), pane, Color.RED, greenCircles);
                        }   
                    }
                    // If statement to draw points whenever the directions is changed, it also draws the lines connecting the points
                    if(points[0].getBoundsInLocal().intersects(points[index].getBoundsInLocal()))
                    {
                        pane.getChildren().addAll(points[index]); // draws the point when the animation reaches its coordinates
                        double newLineX = points[index].getCenterX(); // new X coordinate variable for line creations
                        double newLineY = points[index].getCenterY(); // new Y coordinate variable for line creations
                        // Line is drawn with the first and next point along the walk
                        Line drawLine = new Line(coordinates[0], coordinates[1], newLineX, newLineY);
                        drawLine.setStroke(color); // The color is set to the color of the walk
                        pane.getChildren().add(drawLine); // Finally it is drawn/added to the pane

                        coordinates[0] = newLineX; // We set the initial X coordinates to the next point 
                        coordinates[1] = newLineY; // We set the initial Y coordinates to the next point 
                    }
                },// event handling ends here
                    new KeyValue(points[0].centerYProperty(), points[i].getCenterY()), // Key Values for the Y animation
                    new KeyValue(points[0].centerXProperty(), points[i].getCenterX())  // Key Values for the X animation
                )// It is inside the KeyFrame
            ); // Added to the timeline
        }
        timeline.setCycleCount(1); // Statement for the animation to run once
        timeline.play(); // Statement to start the animation

        pane.getChildren().addAll(points[0]); // The first point is added and animated in the pane 
    }

    /**
     * Description: This method generates green circles of double radius of the circles in the walk, with random coordinates in the pane 
     * adds the circles to an array and adds them to the pane.
     * @param pane The pane the green circles are added to
     * @param greenCircles Array to hold the green circles
     */
    public void greenCircles(Pane pane, Circle[] greenCircles) // This method fills the greencircles array with green circles 
    {                                                                   //at random coordinates inside the pane
        Random rand = new Random(); // Random object for the coordinates
        for(int i = 0; i < 10; i++)
        {    
            double x = rand.nextDouble(pane.getWidth()); // random X coordinate inside pane
            double y = rand.nextDouble(pane.getHeight()); // random Y coordinate inside pane
            greenCircles[i] = new Circle(x, y, 6, Color.GREEN); // Circle created and added to the array
            pane.getChildren().addAll(greenCircles[i]); // And they're all added to the pane
        }   
    }
}