package chessengine;

import chessengine.pieces.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Piece[][] board = new Piece[8][8];

    
    private Posicao lastMoveFrom = null;
    private Posicao lastMoveTo = null;
    private boolean lastMoveWasPawnDouble = false;

    
    public Posicao getLastMoveFrom() { return lastMoveFrom; }
    public Posicao getLastMoveTo() { return lastMoveTo; }
    public boolean wasLastMovePawnDouble() { return lastMoveWasPawnDouble; }

    public Piece get(Posicao p) { if (!p.inBounds()) return null; return board[p.linha][p.coluna]; }
    public void set(Posicao p, Piece piece) { if (p.inBounds()) board[p.linha][p.coluna] = piece; }
    public void place(Piece piece) { set(piece.Posicao(), piece); }

    
    public Posicao findKing(Color color) {
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
            Piece pc = board[r][c];
            if (pc!=null && pc instanceof King && pc.color()==color) return new Posicao(r,c);
        }
        return null;
    }

    
    public boolean isSquareAttacked(Posicao pos, Color byColor) {
        for (Piece pc : pieces()) {
            if (pc.color()!=byColor) continue;
            List<Posicao> moves = pc.legalMoves(this);
            for (Posicao m : moves) if (m.linha==pos.linha && m.coluna==pos.coluna) return true;
        }
        return false;
    }

    
    public boolean wouldLeaveKingInCheck(Posicao from, Posicao to) {
        Piece p = get(from); if (p==null) return true;
        Piece captured = get(to);
        Posicao origPos = p.Posicao();
        
        set(to, p); set(from, null); p.setPosition(to);
        
        Piece removedEnPassant = null;
        if (p instanceof Pawn && lastMoveWasPawnDouble && lastMoveTo!=null) {
            
            int passedRow = (lastMoveFrom.linha + lastMoveTo.linha)/2;
            if (to.linha==passedRow && to.coluna==lastMoveTo.coluna && from.coluna!=to.coluna && captured==null) {
                
                removedEnPassant = get(lastMoveTo);
                set(lastMoveTo, null);
            }
        }
        Posicao kingPos = findKing(p.color());
        boolean inCheck = kingPos==null ? false : isSquareAttacked(kingPos, opposite(p.color()));
        
        set(from, p); set(to, captured); p.setPosition(origPos);
        if (removedEnPassant!=null) set(lastMoveTo, removedEnPassant);
        return inCheck;
    }

    private Color opposite(Color c){ return c==Color.WHITE?Color.BLACK:Color.WHITE; }

    public boolean move(Posicao from, Posicao to) {
        Piece p = get(from); if (p==null) return false;
        List<Posicao> legal = p.legalMoves(this);
        boolean allowed=false;
        for (Posicao poss: legal) if (poss.linha==to.linha && poss.coluna==to.coluna) { allowed=true; break; }
        if (!allowed) return false;
        
        if (wouldLeaveKingInCheck(from,to)) return false;

        
        Piece captured = get(to);
        
        if (p instanceof Pawn && lastMoveWasPawnDouble && lastMoveTo!=null) {
            int passedRow = (lastMoveFrom.linha + lastMoveTo.linha)/2;
            if (to.linha==passedRow && to.coluna==lastMoveTo.coluna && from.coluna!=to.coluna && captured==null) {
                
                captured = get(lastMoveTo);
                set(lastMoveTo, null);
            }
        }

        set(to, p); set(from, null); p.setPosition(to);

        
        if (p instanceof Pawn) {
            if (p.Posicao().linha==0 || p.Posicao().linha==7) {
                
                set(p.Posicao(), new Queen(p.color(), p.Posicao()));
            }
        }

        
        lastMoveFrom = from; lastMoveTo = to;
        lastMoveWasPawnDouble = (p instanceof Pawn) && (Math.abs(from.linha - to.linha) == 2);

        return true;
    }

    public List<Piece> pieces(){ List<Piece> out = new ArrayList<>(); for(int r=0;r<8;r++) for(int c=0;c<8;c++) if (board[r][c]!=null) out.add(board[r][c]); return out; }

    public void clear(){ for(int r=0;r<8;r++) for(int c=0;c<8;c++) board[r][c]=null; lastMoveFrom=null; lastMoveTo=null; lastMoveWasPawnDouble=false; }

    public boolean isInCheck(Color color) {
        Posicao kp = findKing(color); if (kp==null) return false; return isSquareAttacked(kp, opposite(color));
    }

    public boolean hasAnyLegalMove(Color color) {
        for (Piece pc : pieces()) {
            if (pc.color()!=color) continue;
            List<Posicao> moves = pc.legalMoves(this);
            for (Posicao m : moves) {
                if (!wouldLeaveKingInCheck(pc.Posicao(), m)) return true;
            }
        }
        return false;
    }

    public boolean isCheckmate(Color color) {
        return isInCheck(color) && !hasAnyLegalMove(color);
    }

    @Override public String toString(){ StringBuilder sb = new StringBuilder(); for(int r=0;r<8;r++){ for(int c=0;c<8;c++){ sb.append(board[r][c]==null?"." : board[r][c].getClass().getSimpleName().charAt(0)); } sb.append('n'); } return sb.toString(); }
}






