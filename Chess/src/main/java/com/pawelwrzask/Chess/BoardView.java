package com.pawelwrzask.Chess;


import javax.swing.*;

import com.pawelwrzask.Chess.Piece.Type;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;


/**
 * Created by Paweł Wrzask on 2017-05-01.
 */
class BoardView {

    private static final int BOARD_SIZE = 8;
    private static final int RESOLUTION = 600;
    private static final String FONTNAME = "Arial Unicode MS";
    private static final Color BLACK_BACKGROUND = new Color(34, 187, 69);
    private static final Color WHITE_BACKGROUND = new Color(230, 230, 230);
    private static final Color HIGHLIGHTED_BACKGROUND = new Color(255, 204, 0);

    private static final int FONT_SIZE = RESOLUTION / 12;

    private static final Insets insets = new Insets(0, 0, 0, 0);


    private final JPanel gui;
    private JButton[][] fields;
    private BoardController controller;

    BoardView() {

        changeDefaultFonts();

        gui = new JPanel(new BorderLayout(5, 5));
        fields = new JButton[BOARD_SIZE][BOARD_SIZE];

        JFrame frame = createFrame();

        gui.add(createBoard());
        frame.add(gui);

        resetBackgrounds();
    }

    private JFrame createFrame() {

        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(RESOLUTION, RESOLUTION);
        frame.setVisible(true);
        return frame;
    }

    private void changeDefaultFonts() {
        UIManager.put("Button.font", new Font(FONTNAME, Font.PLAIN, FONT_SIZE));
        UIManager.put("Label.font", new Font(FONTNAME, Font.PLAIN, FONT_SIZE / 2));
    }

    private JPanel createBoard() {
        JPanel chessBoard = new JPanel(new GridLayout(0, BOARD_SIZE + 2));
        createFilesRow(chessBoard);
        for (int i = 0; i < BOARD_SIZE; i++) {
            createBoardRow(chessBoard, i);
        }
        createFilesRow(chessBoard);
        return chessBoard;
    }

    private void createBoardRow(JPanel chessBoard, int i) {
        chessBoard.add(new JLabel("" + (BOARD_SIZE - i), SwingConstants.CENTER));
        for (int j = 0; j < BOARD_SIZE; j++) {

            JButton button = new JButton();
            button.setMargin(insets);
            createListener(button, j, i);
            fields[j][i] = button;
            chessBoard.add(button);
        }
        chessBoard.add(new JLabel("" + (BOARD_SIZE - i), SwingConstants.CENTER));
    }


    private void createListener(JButton button, final int x, final int y) {
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.fieldClicked(x, y);
            }
        });

    }


    private void createFilesRow(JPanel jpanel) {
        jpanel.add(new JLabel("", SwingConstants.CENTER));
        for (int i = 0; i < BOARD_SIZE; i++) {
            jpanel.add(new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER));
        }
        jpanel.add(new JLabel("", SwingConstants.CENTER));
    }


    private void resetBackgrounds() {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if ((i + j) % 2 == 0)
                    fields[j][i].setBackground(WHITE_BACKGROUND);
                else fields[j][i].setBackground(BLACK_BACKGROUND);
    }


    private String PieceToAscii(Piece piece) {
        String s = "";
        switch (piece.getColor()) {
            case BLACK:
                switch (piece.getType()) {
                    case ROOK:
                        s = "♜";
                        break;
                    case KNIGHT:
                        s = "♞";
                        break;
                    case BISHOP:
                        s = "♝";
                        break;
                    case QUEEN:
                        s = "♛";
                        break;
                    case KING:
                        s = "♚";
                        break;
                    case BLACKPAWN:
                        s = "♟";
                        break;
                }
                break;
            case WHITE:
                switch (piece.getType()) {
                    case ROOK:
                        s = "♖";
                        break;
                    case KNIGHT:
                        s = "♘";
                        break;
                    case BISHOP:
                        s = "♗";
                        break;
                    case QUEEN:
                        s = "♕";
                        break;
                    case KING:
                        s = "♔";
                        break;
                    case WHITEPAWN:
                        s = "♙";
                        break;
                }
                break;
            default:
                s = "";

        }
        //s=String.valueOf(piece.getMoves());
        return s;
    }
    
    private Piece asciiToPiece(String ascii) {
        switch (ascii) {
            case "♜" :
                return new Piece(Piece.Type.ROOK, Piece.Color.BLACK);
            case "♞":
                return new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK);
            case "♝":
            	return new Piece(Piece.Type.BISHOP, Piece.Color.BLACK);
            case "♛":
            	return new Piece(Piece.Type.QUEEN, Piece.Color.BLACK);
            case "♚":
            	return new Piece(Piece.Type.KING, Piece.Color.BLACK);
            case "♟":
            	return new Piece(Piece.Type.BLACKPAWN, Piece.Color.BLACK);
            case "♖":
            	return new Piece(Piece.Type.ROOK, Piece.Color.WHITE);
            case "♘":
            	return new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE);
            case "♗":
            	return new Piece(Piece.Type.BISHOP, Piece.Color.WHITE);
            case "♕":
            	return new Piece(Piece.Type.QUEEN, Piece.Color.WHITE);
            case "♔":
            	return new Piece(Piece.Type.KING, Piece.Color.WHITE);
            case "♙":
            	return new Piece(Piece.Type.WHITEPAWN, Piece.Color.WHITE);
            default:
                throw new IllegalArgumentException(ascii);
        }

    }

    void appendController(BoardController controller) {
        this.controller = controller;
    }


    void updateView(Piece[][] pieces) {
        for (int i = 0; i < fields.length; i++)
            for (int j = 0; j < fields.length; j++)
                fields[j][i].setText(PieceToAscii(pieces[j][i]));


    }


    void highlight(Collection<Point> hightlights) {
        resetBackgrounds();
        for (Point highlight : hightlights) {
            fields[highlight.x][highlight.y].setBackground(HIGHLIGHTED_BACKGROUND);
        }

    }

    void resetHighlights() {
        resetBackgrounds();
    }

	public Piece askForPromotionPieceType(Piece piece) {
		//Custom button text
		String[] options = getPromotionPiecesFor(piece);

		int index = JOptionPane.CLOSED_OPTION;
		while(index == JOptionPane.CLOSED_OPTION){ // force user to pick some piece
			index = JOptionPane.showOptionDialog(gui,
				    "",
				    "PROMOTION",
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.PLAIN_MESSAGE,
				    null,
				    options,
				    options[2]);
		}
		return asciiToPiece(options[index]);
	}
	public String[] getPromotionPiecesFor(Piece piece){
		switch (piece.getColor()) {
        case BLACK: 
        	return new String[] {"♜","♞","♝","♛"};
        case WHITE:
        	return new String[] {"♖","♘","♗","♕"};
		}
		return null;
	}
}
