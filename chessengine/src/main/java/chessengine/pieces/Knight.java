package chessengine.pieces;

import chessengine.*;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    private static final int[] DR = {-2,-1,1,2,2,1,-1,-2};
    private static final int[] DC = {1,2,2,1,-1,-2,-2,-1};
    public Knight(Color color, Posicao pos){ super(color,pos); }
    @Override public List<Posicao> legalMoves(Board board){ List<Posicao> out = new ArrayList<>(); for(int i=0;i<8;i++){ Posicao p = new Posicao(pos.linha+DR[i], pos.coluna+DC[i]); if (!p.inBounds()) continue; Piece other = board.get(p); if (other==null || other.color()!=color) out.add(p);} return out; }
}



