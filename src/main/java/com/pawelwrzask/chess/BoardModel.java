package com.pawelwrzask.chess;

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

    Piece[][] getFields() {
        return fields;
    }

    Piece getPiece(int x, int y) {
        return fields[x][y];
    }

    Piece getPiece(Point point) {
        return getPiece(point.x, point.y);
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


}
