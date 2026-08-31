package chessengine.pieces;

import chessengine.*;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(Color color, Posicao pos){ super(color,pos); }
    @Override public List<Posicao> legalMoves(Board board){ List<Posicao> out = new ArrayList<>(); int[] dr={1,1,-1,-1}; int[] dc={1,-1,1,-1}; for(int k=0;k<4;k++){ for(int s=1;s<8;s++){ Posicao p=new Posicao(pos.linha+dr[k]*s,pos.coluna+dc[k]*s); if(!p.inBounds()) break; Piece o=board.get(p); if(o==null) { out.add(p); } else { if(o.color()!=color) out.add(p); break; } } } return out; }
}



