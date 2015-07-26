/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Speed of ball */
	private static final int BALL_SPEED = 3;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 1;

	/** Delay time for game */
	private static final int DELAY = 10;

	/* Method: init() */
	/** Sets up the Breakout program. */
	public void init() {
		setBricks();
		setPaddle();
		setBall();
		setLabels();
		addMouseListeners();
	}
	
	// Sets up the bricks for the game
	private void setBricks() {
		brickXOffset = (WIDTH - BRICK_WIDTH * NBRICKS_PER_ROW - BRICK_SEP * (NBRICKS_PER_ROW - 1)) / 2;
		for(int row = 0; row < NBRICK_ROWS; row++) {
			for(int rowBrick = 0; rowBrick < NBRICKS_PER_ROW; rowBrick++) {
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(getColor(row));
					add(brick, getBrickPoint(row, rowBrick));
				}
		}
	}
	
	// Returns color for brick based on which row brick is on
	private Color getColor(int row) {
		if(row < 2) {
			return Color.RED;
		} else if (row < 4) {
			return Color.ORANGE;
		} else if (row < 6) {
			return Color.YELLOW;
		} else if (row < 8) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	// Returns point where each brick should be placed
	private GPoint getBrickPoint(int row, int rowBrick) {
		int brickX = brickXOffset + (BRICK_WIDTH + BRICK_SEP) * rowBrick;
		int brickY = BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * row;
		return new GPoint(brickX, brickY);
	}
	
	// Sets up the paddle for the game
	private void setPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, (WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
	
	// Sets up the ball for the game
	private void setBall() {
		ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
    	ball.setFilled(true);
    	add(ball, paddle.getX() + paddle.getWidth() / 2 - BALL_RADIUS, paddle.getY() - BALL_RADIUS * 2 - 1);
	}
	
	// Initializes ball velocities
	private void setBallVelocities() {
		ballVX = rgen.nextDouble(1, BALL_SPEED);
		if(rgen.nextBoolean()) ballVX = -ballVX;
		ballVY = -BALL_SPEED;
	}
	
	// Sets up labels in game
	private void setLabels() {
		setCenterLabel("Click Paddle to Begin!");
		livesLeft = NTURNS;
		setLivesLeftLabel();
	}
	
	// Sets up label to show message in the center of the game
	private void setCenterLabel(String label) {
		centerLabel = new GLabel(label);
		centerLabel.setFont("CenturyGothic-26-bold");
		add(centerLabel, (WIDTH - centerLabel.getWidth()) / 2, (HEIGHT + centerLabel.getHeight()) / 2);
	}
	
	// Sets up label to show how many lives are left on the top left of the game
	private void setLivesLeftLabel() {
		livesLeftLabel = new GLabel("Lives Left: " + livesLeft);
		livesLeftLabel.setFont("CenturyGothic-15-bold");
		add(livesLeftLabel, 3, 3 + livesLeftLabel.getAscent());
	}

	// Stores point in program where mouse clicks
	public void mousePressed(MouseEvent e) {
		if(livesLeft > 0) {
	    	ptClicked = new GPoint(e.getPoint());
	    	objClicked = getElementAt(ptClicked);
	    	if(objClicked == paddle && ballVX == 0) {
	    		if(centerLabel.isVisible()) {
	    			centerLabel.setVisible(false);
	    			remove(centerLabel);
	    		}
	    		setBallVelocities();
	    	}
		}
	}
	
	// Drags paddle if it is clicked on
	public void mouseDragged(MouseEvent e) {
    	if(objClicked == paddle && livesLeft > 0) {
    		if (ballVX == 0) setBallVelocities();
          	if (0 > e.getX() - PADDLE_WIDTH / 2){
                	paddle.setLocation(0, paddle.getY());
          	} else if (WIDTH < e.getX() + PADDLE_WIDTH / 2) {
                	paddle.setLocation(WIDTH - PADDLE_WIDTH, paddle.getY());
          	} else {
                	paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, paddle.getY());
          	}
          	ptClicked = new GPoint(e.getPoint());
    	}
	}
	
	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		while(livesLeft > 0) {
			ball.move(ballVX, ballVY);
			checkIfBallHits();
			if(winCounter == (NBRICK_ROWS * NBRICKS_PER_ROW)) break;
			pause(DELAY);
		}
		remove(ball);
		displayMessage();
	}
	
	// Check if ball hits anything in program
	private void checkIfBallHits() {
		checkBallHitsWall();
		checkBallFallsOut();
		checkBallHitsObj();
	}
	
	// Checks if ball hits wall
	private void checkBallHitsWall() {
		// Case 1: if ball hits top left corner, ball will bounce off
		if ((ball.getX() < 0) && (ball.getY() < 0)) {
          	ballVX = -ballVX;
          	ballVY = -ballVY;
        // Case 2: if ball hits top right corner, ball will bounce off
    	} else if ((ball.getX() + BALL_RADIUS * 2 > WIDTH) && (ball.getY() < 0)) {
    		ballVX = -ballVX;
    		ballVY = -ballVY;
    	// Case 3: if ball hits left or right wall, ball will bounce off
    	} else if ((ball.getX() + BALL_RADIUS * 2 > WIDTH + BALL_SPEED) || (ball.getX() < -BALL_SPEED)) {
    		ballVX = -ballVX;
    	// Case 4: if ball hits top wall, ball will bounce off
    	} else if (ball.getY() < -BALL_SPEED) {
    		ballVY = -ballVY;
    	}
	}
	
	// Checks if ball falls out under paddle
	private void checkBallFallsOut() {
		if(ball.getY() > HEIGHT) {
			livesLeft--;
			remove(livesLeftLabel);
			setLivesLeftLabel();
			if(livesLeft > 0) ball.setLocation(paddle.getX() + paddle.getWidth() / 2 - BALL_RADIUS, paddle.getY() - BALL_RADIUS * 2);
			ballVX = 0;
			ballVY = 0;
		}
	}
	
	// Checks if the ball hits paddle or bricks
	private void checkBallHitsObj() {
		for(int ballSide = 0; ballSide < 4; ballSide++) {
			objHit = getElementAt(ballSidePoint(ballSide));
			if(objHit != null && objHit != livesLeftLabel) {
				if(objHit != paddle && objHit != livesLeftLabel) {
					remove(objHit);
					winCounter++;
				} else {
					checkIfStuck(ballSide);
				}
				changeVelocity(ballSide);
			}
		}
	}
	
	// Checks each four sides of the ball if it hits any object
	private GPoint ballSidePoint(int ballSide) {
		switch(ballSide) {
			// Checks right side of ball
			case 0:
				return new GPoint(ball.getX() + BALL_RADIUS * 2 + 1, ball.getY() + BALL_RADIUS);
			// Checks left side of ball
			case 1:
				return new GPoint(ball.getX() - 1, ball.getY() + BALL_RADIUS);
			// Checks bottom side of ball
			case 2: 
				return new GPoint(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2 + 1);
			// Checks top side of ball
			default:
				return new GPoint(ball.getX() + BALL_RADIUS, ball.getY() - 1);
		}
	}
	
	// Changes the velocity of the ball based on which side hits
	private void changeVelocity(int ballSide) {
		if(ballSide < 2) {
			ballVX = -ballVX;
		} else {
			ballVY = -ballVY;
		}
	}
	
	// Checks if ball is stuck in paddle
	private void checkIfStuck(int ballSide) {
		if(ballSide == 0 && ball.getX() + BALL_RADIUS * 2 > paddle.getX()) {
			ball.setLocation(paddle.getX() - BALL_RADIUS * 2 - BALL_SPEED, ball.getY());
		} else if(ballSide == 1 && ball.getX() < paddle.getX() + PADDLE_WIDTH) {
			ball.setLocation(paddle.getX() + PADDLE_WIDTH + BALL_SPEED, ball.getY());
		} else if(ballSide == 2 && ball.getY() + BALL_RADIUS * 2 > paddle.getX()) {
			ball.setLocation(ball.getX(), paddle.getY() - BALL_RADIUS * 2 - BALL_SPEED);
		} else if(ballSide == 3 && ball.getY() < paddle.getY() + PADDLE_HEIGHT) {
			ball.setLocation(ball.getX(), paddle.getY() + PADDLE_HEIGHT + BALL_SPEED);
		}
	}
	
	// Displays message whether player wins or loses
	private void displayMessage() {
		if(livesLeft > 0) {
			setCenterLabel("You Win!");
			livesLeft = 0;
		} else {
			setCenterLabel("Game Over!");
		}
	}
	
	// Private instance variables
	private GRect paddle;
	private GOval ball;
	private GPoint ptClicked;
	private GObject objClicked, objHit;
	private GLabel livesLeftLabel, centerLabel;
	private int brickXOffset, livesLeft, winCounter;
	private double ballVX, ballVY;
	private RandomGenerator rgen = new RandomGenerator();
}