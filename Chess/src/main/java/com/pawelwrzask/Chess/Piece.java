package com.pawelwrzask.Chess;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Paweł Wrzask on 2017-05-03.
 */
class Piece {
    enum Color {
        WHITE, BLACK
    }

    enum Type {


        ROOK(7,
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0),
                new Point(0, -1)
        ),
        BISHOP(7,
                new Point(1, 1),
                new Point(-1, 1),
                new Point(1, -1),
                new Point(-1, -1)
        ),
        KING(1,
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0),
                new Point(0, -1),
                new Point(1, 1),
                new Point(-1, 1),
                new Point(1, -1),
                new Point(-1, -1),
                new Point(2, 0), 	 //castling
                new Point(-2, 0)	 //castling
        ),
        QUEEN(7,
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0),
                new Point(0, -1),
                new Point(1, 1),
                new Point(-1, 1),
                new Point(1, -1),
                new Point(-1, -1)
        ),

        KNIGHT(1,
                new Point(1, 2),
                new Point(-1, 2),
                new Point(1, -2),
                new Point(-1, -2),
                new Point(2, 1),
                new Point(-2, 1),
                new Point(2, -1),
                new Point(-2, -1)
        ),
        WHITEPAWN(1,
                new Point(0, -1),
                new Point(0, -2),
                new Point(1, -1),
                new Point(-1, -1)

        ),
        BLACKPAWN(1,
                new Point(0, 1),
                new Point(0, 2),
                new Point(1, 1),
                new Point(-1, 1)
        ),

        EMPTY(0);


        private Collection directions;
        private int distance;


        Type(int distance, Point... points) {
            this.distance = distance;
            this.directions = Arrays.asList(points);
        }

    }

    private Type type;
    private Color color;
    private int moves;
    private int x;
    private int y;


    Piece(Type type, Color color) {
        moves = 0;
        this.type = type;
        this.color = color;
    }

    Type getType() {
        return type;
    }

    Color getColor() {
        return color;
    }

    Collection<Point> getPossibleMoves() {
        return type.directions;
    }

    int getDistance() {
        return type.distance;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }
    
    int getMoves(){
    	return this.moves;
    }
    
    void setMoves(int value){
    	this.moves = value;
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    void setMovesCounter(int value){
    	this.moves = value;
    }

    boolean isFirstMove() {
    	return ((type == Type.WHITEPAWN && this.y == 6 )|| (type == Type.BLACKPAWN && this.y == 1));
    }

    boolean isSameColorAs(Piece piece) {
        return this.color == piece.color;
    }

    boolean isInSameColumnAs(Point point) {
        return this.x == point.x;
    }

    boolean isVerticallyTwoSquareTo(Point point) {
        return Math.abs(this.y - point.y) == 2;
    }

    boolean isHorizontallyTwoSquareTo(Point point) {
        return Math.abs(this.x - point.x) == 2;
    }

    boolean isPawn() {
        return (type == Type.WHITEPAWN || type == Type.BLACKPAWN);
    }
    
	public boolean isKing() {
		return (type == Type.KING);
	}

    boolean isInPosition(Point point){
    	return point.x == this.x && point.y == this.y;
    }

	public Point getPosition() {
		return new Point(x,y);
	}
	
	public boolean isInPromotionRow() {
		return this.y==0||this.y==7;
	}



}
