package com.pawelwrzask.chess;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Paweł Wrzask on 2017-05-03.
 */
class BoardController {
    private BoardView view;
    private BoardModel board;
    private boolean isSelected;
    private Piece selectedPiece;
    private Piece lastMovedPiece;

    private Collection<Point> validMoves;

    private Piece.Color activePlayer;

    private boolean lastMoveWasAPawnTwoSquareMove;
    private Point lastPointPawnCrossed;


    BoardController() {
        activePlayer = Piece.Color.WHITE;
        isSelected = false;
        board = new BoardModel();
        initialSetting();

    }

    private void initialSetting() {

        Piece pieces[] = {
                new Piece(Piece.Type.ROOK, Piece.Color.BLACK),
                new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK),
                new Piece(Piece.Type.BISHOP, Piece.Color.BLACK),
                new Piece(Piece.Type.QUEEN, Piece.Color.BLACK),
                new Piece(Piece.Type.KING, Piece.Color.BLACK),
                new Piece(Piece.Type.BISHOP, Piece.Color.BLACK),
                new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK),
                new Piece(Piece.Type.ROOK, Piece.Color.BLACK),

                new Piece(Piece.Type.ROOK, Piece.Color.WHITE),
                new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE),
                new Piece(Piece.Type.BISHOP, Piece.Color.WHITE),
                new Piece(Piece.Type.QUEEN, Piece.Color.WHITE),
                new Piece(Piece.Type.KING, Piece.Color.WHITE),
                new Piece(Piece.Type.BISHOP, Piece.Color.WHITE),
                new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE),
                new Piece(Piece.Type.ROOK, Piece.Color.WHITE)
        };

        for (int i = 0; i < 8; i++) {
            board.setPiece(i % 8, 0, pieces[i]);
        }
        for (int i = 0; i < 8; i++) {
            board.setPiece(i % 8, 1, new Piece(Piece.Type.BLACKPAWN, Piece.Color.BLACK));
        }
        for (int i = 0; i < 8; i++) {
            board.setPiece(i % 8, 6, new Piece(Piece.Type.WHITEPAWN, Piece.Color.WHITE));
        }
        for (int i = 0; i < 8; i++) {
            board.setPiece(i % 8, 7, pieces[i + 8]);
        }
    }

    void appendView(BoardView view) {

        this.view = view;
        view.updateView(board.getFields());
    }

    void fieldClicked(int x, int y) {

        if (!isSelected)
            onFirstClick(x, y);
        else
            onSecondClick(x, y);

        view.updateView(board.getFields());
    }

    private void onFirstClick(int x, int y) {
        if (!board.isEmpty(x, y)) {
            selectedPiece = board.getPiece(x, y);
            if(selectedPiece.getColor() != activePlayer) return;
            isSelected = true;

            validMoves = generateValidMoves(board.getPiece(x, y));
            if (validMoves.isEmpty()) isSelected = false;
            view.highlight(validMoves);

        }
    }

    private void onSecondClick(int x, int y) {

        Point target = new Point(x, y);
        if (validMoves.contains(target)) {
            if (lastMoveWasAPawnTwoSquareMove = isAPawnTwoSquareMove(selectedPiece, target)) {
                lastPointPawnCrossed = board.getPointBetween(selectedPiece, target);
            }
            if (isAPawnInPassingCaptureMove(selectedPiece, target))
                board.removePiece(lastMovedPiece);
            board.movePieceTo(target, selectedPiece);
            lastMovedPiece = selectedPiece;
            changePlayer();
        }

        isSelected = false;
        view.resetHighlights();
    }

    private void changePlayer() {
        activePlayer = activePlayer.toggle();
        view.invertView();
    }


    private Collection<Point> generateValidMoves(Piece piece) {
        Collection<Point> moves = new ArrayList<>();
        for (Point point : piece.getPossibleMoves()) {
            for (int i = 1; i < piece.getDistance() + 1; i++) {
                Point target = new Point(point.x * i + piece.getX(), point.y * i + piece.getY());
                if (board.isValidField(target)) {

                    switch (piece.getType()) {
                        case BLACKPAWN:
                        case WHITEPAWN:
                            if (isValidPawnMove(piece, target)) moves.add(target);
                            break;
                        default:
                            if (isValidMove(piece, target)) moves.add(target);
                    }
                    if (!board.isEmpty(target)) break;
                }

            }
        }
        return moves;
    }

    private boolean isValidPawnMove(Piece piece, Point targetPoint) {
        if (piece.isVerticallyTwoSquareTo(targetPoint)) {
            if (!isValidPawnTwoSquareMove(piece, targetPoint)) return false;
        }
        if (!board.isEmpty(targetPoint)) {
            if (!isValidPawnCapturingMove(piece, targetPoint)) return false;
        } else {
            if (isValidPawnInPassingCapturingMove(piece, targetPoint)) return true;
            if (!isValidPawnNonCapturingMove(piece, targetPoint)) return false;
        }
        return true;
    }

    private boolean isValidPawnTwoSquareMove(Piece piece, Point targetPoint) {
        if (!piece.isFirstMove()) return false;
        if (!board.isEmptyBetween(piece, targetPoint)) return false;
        return true;
    }

    private boolean isValidPawnCapturingMove(Piece piece, Point targetPoint) {
        Piece targetPiece = board.getPiece(targetPoint);
        if (piece.isSameColorAs(targetPiece)) return false;
        if (piece.isInSameColumnAs(targetPoint)) return false;
        return true;
    }

    private boolean isValidPawnInPassingCapturingMove(Piece piece, Point targetPoint) {
        if (!lastMoveWasAPawnTwoSquareMove) return false;
        if (!targetPoint.equals(lastPointPawnCrossed)) return false;
        if (piece.isSameColorAs(lastMovedPiece)) return false;
        return true;
    }

    private boolean isValidPawnNonCapturingMove(Piece piece, Point targetPoint) {
        if (!piece.isInSameColumnAs(targetPoint)) return false;
        return true;
    }

    private boolean isAPawnTwoSquareMove(Piece piece, Point targetPoint) {
        return (piece.isPawn() && piece.isVerticallyTwoSquareTo(targetPoint));
    }

    private boolean isAPawnInPassingCaptureMove(Piece piece, Point targetPoint) {
        if (!piece.isPawn()) return false;
        if (piece.isInSameColumnAs(targetPoint)) return false;
        if (!board.isEmpty(targetPoint)) return false;
        return true;
    }

    private boolean isValidMove(Piece piece, Point targetPoint) {
        if (!board.isEmpty(targetPoint.x, targetPoint.y)) {
            Piece targetPiece = board.getPiece(targetPoint.x, targetPoint.y);
            if (piece.isSameColorAs(targetPiece)) return false;
        }
        return true;
    }


}
