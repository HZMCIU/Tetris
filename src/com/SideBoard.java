package com;
import com.*;
import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

public class SideBoard extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Tetris tetris;
	public static final int TILE_SIZE=15;
	public static final int SHADE_WIDTH=2;
	public static final int PREDICT_POSITION_X=4*TILE_SIZE;
	public static final int PREDICT_POSITION_Y=4*TILE_SIZE;
	public static final int WINDOWS_TILE_NUM_X=5;//the number of tiles on the horizon direction
	public static final int WINDOWS_TILE_NUM_Y=5;// the number of tiles on the verticall direction
	public static final int SCORE_POSITION_X=2*TILE_SIZE;
	public static final int SCORE_POSITION_Y=15*TILE_SIZE;
	public static final int WORD_INTERVAL=20;
	SideBoard(Tetris tetris){
		this.tetris=tetris;
	}
	public void paint(Graphics g) {
		g.drawImage(this.tetris.image, 0,0,null);
		//draw the grid to 
		g.setColor(Color.BLACK);
		// horizon line 
		for(int row=0;row<=WINDOWS_TILE_NUM_Y;row++) {
			g.drawLine(PREDICT_POSITION_X, PREDICT_POSITION_Y+row*TILE_SIZE, PREDICT_POSITION_X+WINDOWS_TILE_NUM_X*TILE_SIZE,  PREDICT_POSITION_Y+row*TILE_SIZE);
		}
		//vertical line
		for(int col=0;col<=WINDOWS_TILE_NUM_X;col++) {
			g.drawLine(PREDICT_POSITION_X+col*TILE_SIZE, PREDICT_POSITION_Y, PREDICT_POSITION_X+col*TILE_SIZE, PREDICT_POSITION_Y+WINDOWS_TILE_NUM_Y*TILE_SIZE);
		}
		TileType nextType=tetris.getNextType();
		for(int row=0;row<nextType.dimensions;row++) {
			for(int col=0;col<nextType.dimensions;col++) {
				if(nextType.isTile(col, row, 0)) {
					DrawTile(nextType,PREDICT_POSITION_Y+(TILE_SIZE*(col+1)),PREDICT_POSITION_X+(TILE_SIZE*(row+1)),g);
				}
			}
		}
		Font f=new Font("consolas",Font.PLAIN,14);
		g.setFont(f);
		g.setColor(Color.black);
		g.drawString("Score:"+tetris.score,SCORE_POSITION_X,SCORE_POSITION_Y);
		g.drawString("Cleared lines:"+tetris.clearedLines, SCORE_POSITION_X, SCORE_POSITION_Y+WORD_INTERVAL);
		g.drawString("Current Level"
				+ ":"+tetris.level, SCORE_POSITION_X, SCORE_POSITION_Y+2*WORD_INTERVAL);
		g.drawString("←: move left",SCORE_POSITION_X, SCORE_POSITION_Y+3*WORD_INTERVAL);
		g.drawString("→: move right",SCORE_POSITION_X, SCORE_POSITION_Y+4*WORD_INTERVAL);
		g.drawString("↑: rotate tile",SCORE_POSITION_X, SCORE_POSITION_Y+5*WORD_INTERVAL);
		g.drawString("↓: drop down",SCORE_POSITION_X, SCORE_POSITION_Y+6*WORD_INTERVAL);
		g.drawString("P: pause game",SCORE_POSITION_X, SCORE_POSITION_Y+7*WORD_INTERVAL);
	}
	public void DrawTile(Color base, Color light, Color dark, int x, int y, Graphics g) {
        g.setColor(base);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        //画出下方的阴影
        g.setColor(dark);
        g.fillRect(x, y + TILE_SIZE - SHADE_WIDTH, TILE_SIZE, SHADE_WIDTH);
        g.fillRect(x + TILE_SIZE - SHADE_WIDTH, y, SHADE_WIDTH, TILE_SIZE);
        //画出上方的阴影
        g.setColor(light);

        for (int i = 0; i < SHADE_WIDTH; i++) {
            g.drawLine(x, y + i, x + TILE_SIZE - i - 1, y + i);
            g.drawLine(x + i, y, x + i, y + TILE_SIZE - i - 1);
        }
    }
    public void DrawTile(TileType type, int x, int y, Graphics g) {
        //重新封装函数，java中的坐标系于一般的行列不同
        DrawTile(type.baseColor, type.brightColor, type.darkColor, x, y, g);
    }
}