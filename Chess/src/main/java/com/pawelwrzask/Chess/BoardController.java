package com.pawelwrzask.Chess;

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


    private boolean lastMoveWasAPawnTwoSquareMove;
    private Point lastPointPawnCrossed;


    BoardController() {
        isSelected = false;
        initialSetting();

    }

    private void initialSetting() {
    	board = new BoardModel();
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
        
        lastMovedPiece = board.getPiece(0, 0);
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
        checkIfGameFinished();
    }

    private void onSecondClick(int x, int y) {

        Point target = new Point(x, y);
        if (validMoves.contains(target)) {
            if (lastMoveWasAPawnTwoSquareMove = isAPawnTwoSquareMove(selectedPiece, target)) {
                lastPointPawnCrossed = board.getPointBetween(selectedPiece, target);
            }
            if (isAPawnInPassingCaptureMove(selectedPiece, target)){
                board.removePiece(lastMovedPiece);
            }
            if(isCastlingMove(selectedPiece, target)){
            	Point middlePoint = board.getPointBetween(selectedPiece, target);
            	//this is a VALID castling so move we can safely assume that there will be rook in the corner
            	//which should be moved to the middle point
            	board.movePieceTo(middlePoint, board.getEdgePieceTowardsTarget(selectedPiece, target));
            }


            board.movePieceTo(target, selectedPiece);
            lastMovedPiece = selectedPiece;
            
            if(isAPawnPromotionMove(selectedPiece)){
            	board.promote(selectedPiece, view.askForPromotionPieceType(selectedPiece));
            }
            
        }

        isSelected = false;
        view.resetHighlights();
    }

    /**
     * Game finishes when there is no possibility to move for the current player.
     * It can be either a checkmate if the player's king is under attack or a draw.
     * 
     * This method iterates over all fields on the board checking if any of the current player
     * piece has at least one possible move.
     * @return
     */
	private void checkIfGameFinished() {
		Piece currentPlayerPiece = null;
    	for(int i=0 ; i<8 ; i++){
        	for(int j=0 ; j<8 ; j++){
        		Piece checkingPiece = board.getPiece(i,j);
        		if(checkingPiece.isSameColorAs(lastMovedPiece)) continue;
        		currentPlayerPiece = checkingPiece;
        		Collection<Point> moves = generateValidMoves(checkingPiece,false,false);
        		if(moves.size()>0) return;
        	}
    	}
    	boolean draw = !isInCheck(board.getKingOf(currentPlayerPiece));
    	view.gameOver(draw, board.getKingOf(lastMovedPiece));
    	initialSetting();
    	view.updateView(board.getFields());
	}

	private void onFirstClick(int x, int y) {
        if (!board.isEmpty(x, y)) {
            selectedPiece = board.getPiece(x, y);
            //prevents the player from making two moves in a row.
            if(lastMovedPiece.getColor()==selectedPiece.getColor()) return; 
            isSelected = true;

            validMoves = generateValidMoves(board.getPiece(x, y));
            if (validMoves.isEmpty()) isSelected = false;
            view.highlight(validMoves);
        }
    }

    private Collection<Point> generateValidMoves(Piece piece) {
        return generateValidMoves(piece, false, false);
    }
    /*
     * returns all valid moves for given piece
     * capturingOnly and allowCheck parameter are there to prevent endless loop while evaluating simulation moves
     */
    private Collection<Point> generateValidMoves(Piece piece, boolean capturingOnly, boolean allowCheck) {
        Collection<Point> moves = new ArrayList<Point>();
        for (Point point : piece.getPossibleMoves()) {
            for (int i = 1; i < piece.getDistance() + 1; i++) {
                Point target = new Point(point.x * i + piece.getX(), point.y * i + piece.getY());
                if (board.isValidField(target)) {

                    switch (piece.getType()) {
                        case BLACKPAWN:
                        case WHITEPAWN:
                            if (isValidPawnMove(piece, target, capturingOnly)) {
                            	if(allowCheck || isCheckSafeMove(piece, target)){
                            		moves.add(target);
                            	}
                            }
                            break;
                        case KING:
                        	//if it is a castling move and is NOT a VALID castling move - break.
                        	//otherwise fallback to the default validity check mechanism
                        	//in capturingOnly mode we cannot do a castling move
                        	if(!capturingOnly && isCastlingMove(piece, target) && !isValidCastlingMove(piece, target)){
                        		break;
                        	}
                        default:
                            if (isValidMove(piece, target)) {
                            	if(allowCheck || isCheckSafeMove(piece, target)){
                            		moves.add(target);
                            	}
                            }
                    }
                    if (!board.isEmpty(target)) break;
                }

            }
        }
        return moves;
    }



    /*
     * simulate move of given piece to the given point and check if after that own king is not in check.
     */
	private boolean isCheckSafeMove(Piece piece, Point targetPoint){
    	Piece king = board.getKingOf(piece);
    	
    	//save initial state
    	Piece targetPointPieceTemp = board.getPiece(targetPoint);
    	Point startingPointOfPieceTemp = piece.getPosition();
    	//Temporarily simulate that piece was moved
    	board.removePiece(piece);
    	board.setPiece(targetPoint, piece);
    	
    	
    	//actual check
    	boolean check = isInCheck(king);
    	
    	//restore initial state
		board.setPiece(targetPoint, targetPointPieceTemp);
		board.setPiece(startingPointOfPieceTemp, piece);
		
		//return whether move is safe so is NOT under check
    	return !check;
    }
    

	/*
	 * shortcut for case where piece and position to check are the same
	 */
	private boolean isInCheck(Piece king){
		return isInCheck(king, king.getPosition());
	}
	
	/*
	 * Iterate over all fields on boards and check if given point is not under attack of OPPONENT to the given piece
	 */
	private boolean isInCheck(Piece piece, Point point){
    	for(int i=0 ; i<8 ; i++){
        	for(int j=0 ; j<8 ; j++){
        		Piece checkingPiece = board.getPiece(i,j);
        		if(checkingPiece.isSameColorAs(piece)) continue;
        		Collection<Point> moves = generateValidMoves(checkingPiece,true,true);
        		for(Point p : moves){
        			if(point.equals(p)) return true;
        		}
        	}
    	}
    	return false;
	}
    
    private boolean isCastlingMove(Piece piece, Point target) {
		return piece.isHorizontallyTwoSquareTo(target) && piece.getType() == Piece.Type.KING;
	}
    
    /*
     *  Castling consists of moving the king two squares towards a rook, then placing the rook on the other side of the king, adjacent to it. 
     *  Castling is only permissible if all of the following conditions hold:
	 *
	 *	The king and rook involved in castling must not have previously moved;
	 *-	There must be no pieces between the king and the rook; 
	 *	The king may not currently be in check, nor may the king pass through or end up in a square that is under attack by an enemy piece (though the rook is permitted to be under attack and to pass over an attacked square);
	 *	The king and the rook must be on the same rank
     */
	private boolean isValidCastlingMove(Piece king, Point target) {
        //determine rook involved in castling by taking piece from the corner of the board from the same row/rank as king.
        //it's not a problem if it's another piece moved there - moves counter of this piece will be greater than 0
        //which will be detected later.
        Piece castlingRook = board.getEdgePieceTowardsTarget(king, target);
        
        //check if there is any piece
        if(castlingRook.getType() == Piece.Type.EMPTY) return false;
        
        //check if the king or the rook has moved previously;
        if(castlingRook.getMoves()>0 || king.getMoves()>0) return false;
		
		//check if the king does not jump over another piece
        if (!board.isEmptyBetween(king, target)) return false;
        
        //check if target point is empty
        if(!board.isEmpty(target))  return false;
        
        //if there is a point between rook and target point (queenside castling) it has to be empty aswell
        if(castlingRook.isHorizontallyTwoSquareTo(target) && !board.isEmptyBetween(castlingRook, target)) return false;
        
        //check if king is not in check, nor pass through or end up in a square that is under attack
        if( isInCheck(king) ||
        	isInCheck(king, board.getPointBetween(king, target)) ||
        	isInCheck(king, target)) 
        		return false;
        
        //if all conditions are met it is valid castling move
		return true;
	}
	

    private boolean isValidPawnMove(Piece piece, Point targetPoint, boolean capturingOnly) {
    	if(capturingOnly){
            if (!isValidPawnCapturingMove(piece, targetPoint)) return false;
            return true;
    	}
    	
        if (piece.isVerticallyTwoSquareTo(targetPoint)) {
            if (!isValidPawnTwoSquareMove(piece, targetPoint)) return false;
        }
        if (!board.isEmpty(targetPoint)) {
            if (!isValidPawnCapturingMove(piece, targetPoint)) return false;
        } else {
            if (isValidPawnInPassingCapturingMove(piece, targetPoint)) return true;
            if(!isValidPawnNonCapturingMove(piece, targetPoint)){
            	return false;
            }
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

    private boolean isAPawnPromotionMove(Piece piece) {
		return piece.isPawn() && piece.isInPromotionRow();
	}
}