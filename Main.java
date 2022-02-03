package game_chess;

import game_chess.board.Board;

import javax.swing.*;
import java.util.*;
import java.awt.*;

public class Main{


    public static void main(String[] args) {

        Board board = Board.createStandardBoard();

        System.out.println(board);
    }
}
