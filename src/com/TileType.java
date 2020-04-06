package com;
import com.*;
import java.awt.Color;
import javax.swing.*;
/**
 * enumerate the basic block type
 */
public enum TileType {
    // following are pre-defined value for TileType include shape(bool matrix),color range
    TypeI(new Color(GameBoard.COLOR_MIN, GameBoard.COLOR_MAX, GameBoard.COLOR_MAX), 4, 4, 1, new int [][]{
        {
            0, 0, 0, 0,
            1, 1, 1, 1,
            0, 0, 0, 0,
            0, 0, 0, 0,
        },
        {
            0, 0, 1, 0,
            0, 0, 1, 0,
            0, 0, 1, 0,
            0, 0, 1, 0,
        },
        {
            0, 0, 0, 0,
            0, 0, 0, 0,
            1, 1, 1, 1,
            0, 0, 0, 0,
        },
        {
            0, 1, 0, 0,
            0, 1, 0, 0,
            0, 1, 0, 0,
            0, 1, 0, 0,
        }
    }),
    TypeJ(new Color(GameBoard.COLOR_MIN, GameBoard.COLOR_MIN, GameBoard.COLOR_MAX), 3, 3, 2, new int[][] {
        {
            1,  0,  0,
            1,  1,  1,
            0,  0,  0,
        },
        {
            0,  1,  1,
            0,  1,  0,
            0,  1,  0,
        },
        {
            0,  0,  0,
            1,  1,  1,
            0,  0,  1,
        },
        {
            0,  1,  0,
            0,  1,  0,
            1,  1,  0,
        }
    }),

    TypeL(new Color(GameBoard.COLOR_MAX, 127, GameBoard.COLOR_MIN), 3, 3, 2, new int[][] {
        {
            0,  0,  1,
            1,  1,  1,
            0,  0,  0,
        },
        {
            0,  1,  0,
            0,  1,  0,
            0,  1,  1,
        },
        {
            0,  0,  0,
            1,  1,  1,
            1,  0,  0,
        },
        {
            1,  1,  0,
            0,  1,  0,
            0,  1,  0,
        }
    }),

    TypeO(new Color(GameBoard.COLOR_MAX, GameBoard.COLOR_MAX, GameBoard.COLOR_MIN), 2, 2, 2, new int[][] {
        {
            1,  1,
            1,  1,
        },
        {
            1,  1,
            1,  1,
        },
        {
            1,  1,
            1,  1,
        },
        {
            1,  1,
            1,  1,
        }
    }),

    TypeS(new Color(GameBoard.COLOR_MIN, GameBoard.COLOR_MAX, GameBoard.COLOR_MIN), 3, 3, 2, new int[][] {
        {
            0,  1,  1,
            1,  1,  0,
            0,  0,  0,
        },
        {
            0,  1,  0,
            0,  1,  1,
            0,  0,  1,
        },
        {
            0,  0,  0,
            0,  1,  1,
            1,  1,  0,
        },
        {
            1,  0,  0,
            1,  1,  0,
            0,  1,  0,
        }
    }),

    TypeT(new Color(128, GameBoard.COLOR_MIN, 128), 3, 3, 2, new int[][] {
        {
            0,  1,  0,
            1,  1,  1,
            0,  0,  0,
        },
        {
            0,  1,  0,
            0,  1,  1,
            0,  1,  0,
        },
        {
            0,  0,  0,
            1,  1,  1,
            0,  1,  0,
        },
        {
            0,  1,  0,
            1,  1,  0,
            0,  1,  0,
        }
    }),

    TypeZ(new Color(GameBoard.COLOR_MAX, GameBoard.COLOR_MIN, GameBoard.COLOR_MIN), 3, 3, 2, new int[][] {
        {
            1,  1,  0,
            0,  1,  1,
            0,  0,  0,
        },
        {
            0,  0,  1,
            0,  1,  1,
            0,  1,  0,
        },
        {
            0,  0,  0,
            1,  1,  0,
            0,  1,  1,
        },
        {
            0,  1,  0,
            1,  1,  0,
            1,  0,  0,
        }
    });
    public int col;
    public int row;
    public int dimensions;
    public Color baseColor;
    public Color brightColor;
    public Color darkColor;
    public int[][] tiles;
    private TileType(Color color, int dimensions, int row, int col, int[][] tiles) {
        this.baseColor = color;
        this.darkColor = color.darker();
        this.brightColor = color.brighter();
        this.dimensions = dimensions;
        this.row = row;
        this.col = col;
        this.tiles = tiles;
    }
    //judge whether a tile exist in certain blocks of certain rotation
    boolean isTile(int x, int y, int rotation) {
        return tiles[rotation][dimensions * y + x] == 1;
    }
    public int topRow(int rotation) {
        for (int row = 0; row < dimensions; row++) {
            for (int col = 0; col < dimensions; col++) {
                if (tiles[rotation][row * dimensions + col] != 0)
                    return row;
            }
        }

        return -1;
    }

    public int bottomRow( int rotation) {
        for (int row = dimensions - 1; row >= 0; row--) {
            for (int col = dimensions - 1; col >= 0; col--) {
                if (tiles[rotation][row * dimensions + col] != 0)
                    return row;
            }
        }

        return -1;
    }
    public int leftCol(int rotation) {
        for (int col = 0; col < dimensions; col++) {
            for (int row = 0; row < dimensions; row++) {
                if (tiles[rotation][row * dimensions + col] != 0)
                    return col;
            }
        }

        return -1;
    }
    public int rightCol( int rotation) {
        for (int col = dimensions - 1; col >= 0; col--) {
            for (int row = dimensions - 1; row >= 0; row--) {
                if (tiles[rotation][row * dimensions + col] != 0)
                    return col;
            }
        }

        return -1;
    }
    public static void main(String[] args) {
        TileType type = TileType.TypeT;
        System.out.println(type.rightCol( 0));
    }
}