package com;
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import javazoom.jl.decoder.JavaLayerException;

import java.util.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

public class Tetris extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6516653894681803156L;
	private final int FRAME_TIME = 1000 / 50;//刷新的频率,每隔50ms刷新一次,下落的最快的速度无法超过刷新频率
    private GameBoard board;
    private SideBoard sideboard;
    private Clock logicTimer; //用于控制方块下落的速度
    private TileType currentType;
    private TileType nextType;
    private SoundManager soundManager;
    private int currentRow;
    private int currentCol;
    private int currentRotation;
    private float gameSpeed;
    private Random random;
    private String musicPath=".//src//com//music.mp3";
    
    private String backgroundPath=".//src//com//background.jpg";
    public Image image=null;
    
    public boolean isGameOver;
    public int score;
    public int clearedLines=0;
    public int level=0;
    /**
     * Initialize the reference to other class;
     * Set the the bounds of main screen;
     * Initialize the inner class KeyListener to get the message from keyboard;
     * @throws JavaLayerException 
     * @throws FileNotFoundException 
     */
    public Tetris(){
    	try {
    		this.image=ImageIO.read(new File(backgroundPath));
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	setLayout(new BorderLayout());
    	setResizable(false);
        board = new GameBoard(this);
        sideboard=new SideBoard(this);
        logicTimer = new Clock(this);
        gameSpeed = 1;
        add(board,BorderLayout.CENTER);
        // set the size of sideboard to be seen
        sideboard.setPreferredSize(new Dimension((SideBoard.WINDOWS_TILE_NUM_X+6)*SideBoard.TILE_SIZE,GameBoard.TILE_SIZE * (GameBoard.VISIBLE_ROW_COUNT + 2)));
        add(sideboard,BorderLayout.EAST);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(0, 0, GameBoard.TILE_SIZE * (GameBoard.COL_COUNT + 1)+(SideBoard.WINDOWS_TILE_NUM_X+6)*SideBoard.TILE_SIZE, GameBoard.TILE_SIZE * (GameBoard.VISIBLE_ROW_COUNT + 2));
        setLocation(600, 350);
        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                case KeyEvent.VK_LEFT:
                    if (board.isValidAndEmpty(currentType, currentCol - 1, currentRow, currentRotation)) {
                        currentCol--;
                    }

                    break;

                case KeyEvent.VK_RIGHT:
                    if ( board.isValidAndEmpty(currentType, currentCol + 1, currentRow, currentRotation)) {
                        currentCol++;
                    }

                    break;

                case KeyEvent.VK_UP:
                    if (true) {
                        rotateTile((++currentRotation) % 4);
                    }

                    break;

                case KeyEvent.VK_DOWN: {
                	if(gameSpeed<=30) {
                		logicTimer.setCyclePerSecond(30);
                	}else {
                		logicTimer.setCyclePerSecond(gameSpeed+10);
                	}
                }
                break;
                case KeyEvent.VK_ENTER:{
                	if(isGameOver==true) {
                		isGameOver=false;
                		restartGame();
                	}
                }
                break;
                case KeyEvent.VK_P:{
                	if(logicTimer.isPaused==true)
                		logicTimer.isPaused=false;
                	else
                		logicTimer.isPaused=true;
                }
                }
            }

            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN: {
                    logicTimer.setCyclePerSecond(gameSpeed);
                    logicTimer.reset();
                }
                }
            }
        });
    }
    public void startGame() throws JavaLayerException, FileNotFoundException{
    	currentCol = 3;
        currentRow = 0;
    	random = new Random();
    	currentType=TileType.values()[random.nextInt(7)];
        nextType=TileType.values()[random.nextInt(7)];
    	soundManager=new SoundManager(musicPath,logicTimer);
        currentRotation = 0;
        // Set the speed of tile dropping down
        logicTimer.setCyclePerSecond(gameSpeed);
        soundManager.start();
        while (true) {
            long start = System.nanoTime();

            logicTimer.update();

            // if time has elapsed a cycle , then the block drop down one unit
            if (logicTimer.hasElapsedCycle()&&logicTimer.isPaused==false&&isGameOver==false)
                updateGame();

            // repaint the board
            if(logicTimer.isPaused==false&&isGameOver==false)
            	flash();
            // the time previous statement used add the time thread sleep used is constant
            long delta = (System.nanoTime() - start) / 1000L;

            if (gameOver()) {
                isGameOver = true;
            }

            try {
                Thread.sleep(FRAME_TIME - delta);
            } catch (Exception e) {

            }
        }
    }
    public boolean gameOver() {
        for (int col = 0; col < GameBoard.COL_COUNT; col++) if (board.tiles[0][col] != null) return true;

        return false;
    }
    /**
     * Judge the next step whether conflict with the exist blocks
     * If not,the drop down one more unit,and gradually add the dropping speed
     * Else, add the current piece the exist blocks.and then generate next pieces.
     */
    public void updateGame() {

        if (board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation) ) {
            currentRow++;
            if(gameSpeed<=4)
            	gameSpeed += 0.005;
            level=(int) gameSpeed;
            //删去这一行，能够加快下落的反应速度，但是这样做如果没有按向下的方向键的话，方块的速度是不会加快的
            //logicTimer.setCyclePerSecond(gameSpeed);
        } else  {
        	int clearedLines=0;
            board.addPiece(currentCol, currentRow, currentRotation, currentType);
            clearedLines=board.clearLine();
            if(clearedLines==1) 
            	score+=10;
            else if(clearedLines==2)
            	score+=25;
            else if(clearedLines==3)
            	score+=40;
            else if(clearedLines==4)
            	score+=45;
            this.clearedLines+=clearedLines;
            currentType = nextType;
            nextType =  TileType.values()[random.nextInt(7)];
            currentCol = 5 ;
            currentRow = 0 ;
            currentRotation = 0;
        }
    }
    public void flash() {
    	sideboard.repaint();
    	board.repaint();
    }
    /**
     * rotate the block and then adjust it according to blocks is whether too close to
     * the right or left
     * Still has some bugs.When blocks keep rotating till the bottom,the block will disappear
     * rather be added to exist blocks
     */
    public void rotateTile(int newRotation) {
        int newRow = currentRow;
        int newCol = currentCol;
        int left = currentType.leftCol(newRotation);
        int right = currentType.rightCol(newRotation);
        int top = currentType.topRow(newRotation);
        int bottom = currentType.bottomRow(newRotation);

        if (currentCol + left < 0) {
            newCol -= (currentCol - left);
        } else if (currentCol + right >= GameBoard.COL_COUNT) {
            newCol -= (newCol - right + 1);
        }

        if (currentRow + top <= 0  ) {
            newRow -= (currentRow - top);
        } else if (currentRow + bottom >= GameBoard.ROW_COUNT) {
            newRow -= (currentRow - bottom);
        }

        if (board.isValidAndEmpty(currentType, newCol, newRow, newRotation)) {
            currentRotation = newRotation;
            currentCol = newCol;
            currentRow = newRow;
        }
    }
    public void restartGame() {
    	board.resetGameBoard();
		gameSpeed=1;
		clearedLines=0;
		score=0;
		logicTimer.setCyclePerSecond(gameSpeed);
		logicTimer.reset();
    }
    public int getCurrnetRow() {
        return currentRow;
    }
    public int getCurrentCol() {
        return currentCol;
    }
    public TileType getCurrentType() {
        return currentType;
    }
    public TileType getNextType() {
    	return nextType;
    }
    public int getCurrentRotation() {
        return currentRotation;
    }
    public static void main(String[] args) throws FileNotFoundException, JavaLayerException {
        Tetris tetris = new Tetris();
        tetris.setVisible(true);
        tetris.startGame();
    }
}