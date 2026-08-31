package chessengine.pieces;

import chessengine.*;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Color color, Posicao pos) { super(color,pos); }
    @Override public List<Posicao> legalMoves(Board board) {
        List<Posicao> out = new ArrayList<>();
        int dir = (color==Color.WHITE)?-1:1;
        
        Posicao one = new Posicao(pos.linha+dir, pos.coluna);
        if (one.inBounds() && board.get(one)==null) {
            out.add(one);
            
            boolean atStart = (color==Color.WHITE && pos.linha==6) || (color==Color.BLACK && pos.linha==1);
            Posicao two = new Posicao(pos.linha+2*dir, pos.coluna);
            if (atStart && two.inBounds() && board.get(two)==null) out.add(two);
        }
        
        Posicao c1 = new Posicao(pos.linha+dir, pos.coluna-1);
        Posicao c2 = new Posicao(pos.linha+dir, pos.coluna+1);
        if (c1.inBounds()) {
            Piece p = board.get(c1);
            if (p!=null && p.color()!=color) out.add(c1);
            else {
                
                Posicao lastTo = board.getLastMoveTo();
                if (lastTo!=null && lastTo.linha==pos.linha && lastTo.coluna==pos.coluna-1 && board.wasLastMovePawnDouble()) {
                    out.add(c1);
                }
            }
        }
        if (c2.inBounds()) {
            Piece p = board.get(c2);
            if (p!=null && p.color()!=color) out.add(c2);
            else {
                Posicao lastTo = board.getLastMoveTo();
                if (lastTo!=null && lastTo.linha==pos.linha && lastTo.coluna==pos.coluna+1 && board.wasLastMovePawnDouble()) {
                    out.add(c2);
                }
            }
        }
        return out;
    }
}



