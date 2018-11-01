package com.pawelwrzask.Chess;


public class Chess {

    public static void main(String[] args){


        BoardView boardView = new BoardView();
        BoardController boardController = new BoardController();

        boardController.appendView(boardView);
        boardView.appendController(boardController);


    }


}
