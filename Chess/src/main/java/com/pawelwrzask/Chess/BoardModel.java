package com.pawelwrzask.Chess;

import java.awt.*;

/**
 * Created by Paweł Wrzask on 2017-05-03.
 */
class BoardModel {
    private Piece[][] fields;
    private Piece emptyPiece;
    private int size;

    BoardModel() {
        size = 8;
        emptyPiece = new Piece(Piece.Type.EMPTY, Piece.Color.WHITE);
        fields = new Piece[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                setPiece(i, j, emptyPiece);
    }

    void setPiece(int x, int y, Piece piece) {
        fields[x][y] = piece;
        piece.setPosition(x, y);
    }
    
	public void setPiece(Point point, Piece piece) {
		setPiece(point.x, point.y, piece);
	}

    Piece[][] getFields() {
        return fields;
    }

    Piece getPiece(int x, int y) {
        return fields[x][y];
    }

    Piece getPiece(Point point) {
        return getPiece(point.x, point.y);
    }
    
    Piece getKingOf(Piece piece){
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++){
            	Piece potentialKing = getPiece(i,j);
            	if(potentialKing.isKing() && potentialKing.isSameColorAs(piece)) return potentialKing;
            }
        return null;
    }
    
    /**
     * Returns piece which is:
     * - on the edge of the board determined by direction of the movement
     * - in the same row as provided piece
     */
    Piece getEdgePieceTowardsTarget(Piece piece, Point target){
        //determine horizontal position of the edge piece
        //if the piece is moving to the right it's 7 otherwise 0
        int x = (isMovingToTheRight(piece, target) ? 7 : 0);
        
        return getPiece(x,piece.getY());
    }

    void removePiece(int x, int y) {
        setPiece(x, y, emptyPiece);
    }

    void removePiece(Piece piece) {
        removePiece(piece.getX(), piece.getY());
    }

    void movePieceTo(int x, int y, Piece piece) {
        removePiece(piece);
        setPiece(x, y, piece);
    }

    void movePieceTo(Point target, Piece piece) {
    	piece.setMoves(piece.getMoves()+1);
        movePieceTo(target.x, target.y, piece);
    }

    boolean isEmpty(int x, int y) {
        return getPiece(x, y) == emptyPiece;
    }

    boolean isEmpty(Point point) {
        return isEmpty(point.x, point.y);
    }

    boolean isValidField(int x, int y) {
        return (x >= 0 && x < size && y >= 0 && y < size);
    }

    boolean isValidField(Point point) {
        return isValidField(point.x, point.y);
    }

    Point getPointBetween(Piece piece, Point point) {
        return new Point(piece.getX() - (piece.getX() - point.x) / 2, piece.getY() - (piece.getY() - point.y) / 2);
    }

    boolean isEmptyBetween(Piece piece, Point point) {
        return isEmpty(getPointBetween(piece, point));
    }
    
    boolean isMovingToTheRight(Piece piece, Point target){
    	return piece.getPosition().x < target.getX();
    }


    
    


}
