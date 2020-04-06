package com;

import com.*;
import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Random;

public class GameBoard extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int COLOR_MIN = 35;
	public static final int COLOR_MAX = 255 - COLOR_MIN;
	public static final int COL_COUNT = 10;
	public static final int VISIBLE_ROW_COUNT = 20;
	public static final int HIDDEN_ROW_COUNT = 2;
	public static final int ROW_COUNT = VISIBLE_ROW_COUNT + HIDDEN_ROW_COUNT;
	public static final int TILE_SIZE = 24;
	public static final int SHADE_WIDTH = 4;
	private Tetris tetris;
	TileType[][] tiles;

	public GameBoard(Tetris tetris) {
		tiles = new TileType[ROW_COUNT][COL_COUNT];
		setBackground(Color.black);
		this.tetris = tetris;
	}

	public GameBoard() {
		tiles = new TileType[ROW_COUNT][COL_COUNT];
	}

	// 画出一个方块
	/**
	 * @param:base,the main
	 *                     color of tile
	 * @param:light,draw the
	 *                       shade
	 * @param:dark,draw the
	 *                      shade
	 */
	public void DrawTile(Color base, Color light, Color dark, int x, int y, Graphics g) {
		// 画出
		g.setColor(base);
		g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
		// 画出下方的阴影
		g.setColor(dark);
		g.fillRect(x, y + TILE_SIZE - SHADE_WIDTH, TILE_SIZE, SHADE_WIDTH);
		g.fillRect(x + TILE_SIZE - SHADE_WIDTH, y, SHADE_WIDTH, TILE_SIZE);
		// 画出上方的阴影
		g.setColor(light);

		for (int i = 0; i < SHADE_WIDTH; i++) {
			g.drawLine(x, y + i, x + TILE_SIZE - i - 1, y + i);
			g.drawLine(x + i, y, x + i, y + TILE_SIZE - i - 1);
		}
	}

	public void DrawTile(TileType type, int x, int y, Graphics g) {
		// 重新封装函数，java中的坐标系于一般的行列不同
		DrawTile(type.baseColor, type.brightColor, type.darkColor, x, y, g);
	}

	private boolean isOccupied(int x, int y) {
		return tiles[y][x] != null;
	}

	public void addPiece(int x, int y, int rotation, TileType type) {
		for (int row = 0; row < type.dimensions; row++) {
			for (int col = 0; col < type.dimensions; col++) {
				if (type.isTile(col, row, rotation)) {
					tiles[row + y][col + x] = type;
				}
			}
		}
	}

	public boolean isValidAndEmpty(TileType type, int x, int y, int rotation) {
		if (x + type.leftCol(rotation) < 0 || x + type.rightCol(rotation) >= COL_COUNT)
			return false;

		if (y + type.topRow(rotation) < 0 || y + type.bottomRow(rotation) >= ROW_COUNT)
			return false;

		for (int row = 0; row < type.dimensions; row++) {
			for (int col = 0; col < type.dimensions; col++) {
				if (type.isTile(col, row, rotation) && isOccupied(col + x, row + y)) {
					return false;
				}
			}
		}

		return true;
	}

	public int clearLine() {
		int fullLine = 0;

		// 从上到下遍历，清除已经满的行
		for (int row = HIDDEN_ROW_COUNT; row <= ROW_COUNT - 1; row++) {
			if (checkLine(row))
				fullLine++;
		}
		return fullLine;
	}

	private boolean checkLine(int line) {
		for (int col = 0; col < COL_COUNT; col++) {
			if (!isOccupied(col, line))
				return false;
		}

		// 清空已经满了的一行
		for (int row = line - 1; row >= 0; row--) {
			for (int col = 0; col < COL_COUNT; col++) {
				tiles[row + 1][col] = tiles[row][col];
			}
		}

		return true;
	}

	public void resetGameBoard() {
		for (int row = 0; row < ROW_COUNT; row++) {
			for (int col = 0; col < COL_COUNT; col++) {
				tiles[row][col] = null;
			}
		}
	}

	public void paint(Graphics g) {
		g.drawImage(this.tetris.image, 0,0,null);
		if (tetris.isGameOver) {
			Font f = new Font("consolas", Font.PLAIN, 30);
			g.setFont(f);
			g.setColor(Color.RED);
			g.drawString("Game Over", 3 * TILE_SIZE, 10 * TILE_SIZE);
			f = new Font("consolas", Font.PLAIN, 20);
			g.setFont(f);
			g.drawString("Press Enter continue", TILE_SIZE, 11 * TILE_SIZE);
			return;
		}
		for (int row = HIDDEN_ROW_COUNT; row < ROW_COUNT; row++) {
			for (int col = 0; col < COL_COUNT; col++) {
				if (isOccupied(col, row)) {
					DrawTile(tiles[row][col], col * TILE_SIZE, (row - HIDDEN_ROW_COUNT) * TILE_SIZE, g);
				}
			}
		}

		// 画网格线
		g.setColor(Color.DARK_GRAY);

		for (int x = 0; x < COL_COUNT; x++) {
			for (int y = 0; y < VISIBLE_ROW_COUNT; y++) {
				g.drawLine(0, y * TILE_SIZE, COL_COUNT * TILE_SIZE, y * TILE_SIZE);
				g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, VISIBLE_ROW_COUNT * TILE_SIZE);
			}
		}

		g.drawRect(0, 0, COL_COUNT * TILE_SIZE, VISIBLE_ROW_COUNT * TILE_SIZE);

		// draw the ghost
		int ghostRow;
		int ghostCol = tetris.getCurrentCol();
		int ghostRotation = tetris.getCurrentRotation();
		TileType ghostTile = tetris.getCurrentType();
		// straight drop down till touch the exist blocks
		for (ghostRow = tetris.getCurrnetRow(); ghostRow < ROW_COUNT; ghostRow++) {
			if (!isValidAndEmpty(ghostTile, ghostCol, ghostRow + 1, ghostRotation)) {
				break;
			}
		}
		// because there are two hidden row above, and it is the same as shift the ghost
		// up two unit.Therefore,have shift two unit down to show resonablly.
		ghostRow -= HIDDEN_ROW_COUNT;
		// construt a transparent blocks
		int red = ghostTile.baseColor.getRed();
		int green = ghostTile.baseColor.getGreen();
		int blue = ghostTile.baseColor.getBlue();
		int alpha = ghostTile.baseColor.getAlpha();
		alpha = 80;
		Color transparentColor = new Color(red, green, blue, alpha);
		for (int row = 0; row < ghostTile.dimensions; row++) {
			for (int col = 0; col < ghostTile.dimensions; col++) {
				if (ghostTile.isTile(col, row, ghostRotation)) {
					DrawTile(transparentColor, transparentColor, transparentColor, (ghostCol + col) * TILE_SIZE,
							(ghostRow + row) * TILE_SIZE, g);
				}
			}
		}
		// 画出正在移动的方块
		TileType tempType = tetris.getCurrentType();
		int currentRow = tetris.getCurrnetRow();
		int currentCol = tetris.getCurrentCol();
		int currentRotation = tetris.getCurrentRotation();

		for (int row = 0; row < tempType.dimensions; row++) {
			for (int col = 0; col < tempType.dimensions; col++) {
				if (tempType.isTile(col, row, currentRotation))
					DrawTile(tempType, (col + currentCol) * TILE_SIZE,
							(row - HIDDEN_ROW_COUNT + currentRow) * TILE_SIZE, g);
			}
		}

	}
}