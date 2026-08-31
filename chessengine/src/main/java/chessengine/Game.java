package chessengine;

import chessengine.pieces.*;

public class Game {
    public static void main(String[] args) {
        Board b = new Board();
        
        Pawn p1 = new Pawn(Color.WHITE, new Posicao(6,0)); b.place(p1);
        Pawn p2 = new Pawn(Color.BLACK, new Posicao(1,0)); b.place(p2);
        Knight k1 = new Knight(Color.WHITE, new Posicao(7,1)); b.place(k1);
        Bishop b1 = new Bishop(Color.BLACK, new Posicao(0,2)); b.place(b1);
        System.out.println("Initial board:n"+b);
        
        boolean ok = b.move(new Posicao(6,0), new Posicao(5,0));
        System.out.println("Move pawn white 6,0->5,0: "+ok);
        System.out.println(b);
    }
}






