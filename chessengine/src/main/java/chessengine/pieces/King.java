package chessengine.pieces;

import chessengine.*;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private static final int[] DR={1,1,1,0,0,-1,-1,-1};
    private static final int[] DC={1,0,-1,1,-1,1,0,-1};
    public King(Color color, Posicao pos){ super(color,pos); }
    @Override public List<Posicao> legalMoves(Board board){ List<Posicao> out=new ArrayList<>(); for(int i=0;i<8;i++){ Posicao p=new Posicao(pos.linha+DR[i], pos.coluna+DC[i]); if(!p.inBounds()) continue; Piece o=board.get(p); if(o==null || o.color()!=color) out.add(p);} return out; }
}



