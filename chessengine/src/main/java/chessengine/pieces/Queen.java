package chessengine.pieces;

import chessengine.*;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Color color, Posicao pos){ super(color,pos); }
    @Override public List<Posicao> legalMoves(Board board){ List<Posicao> out = new ArrayList<>(); 
        out.addAll(new Rook(color,pos).legalMoves(board)); out.addAll(new Bishop(color,pos).legalMoves(board)); return out; }
}



