package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PieceMovesCalculator {
    protected ChessPiece piece;
    protected ChessPosition myPosition;
    protected ChessBoard board;
    protected int row;
    protected int col;
    protected ChessGame.TeamColor team;
    protected ArrayList<ChessMove> validMoves;


    public PieceMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        this.piece = piece;
        this.myPosition = myPosition;
        this.board = board;

        this.row = myPosition.getRow();
        this.col = myPosition.getColumn();

        this.team = piece.getTeamColor();

        this.validMoves = new ArrayList<>();
    }

    protected Collection<ChessMove> pieceMoves() {
        return this.validMoves;
    }

    protected boolean addPiece(int row, int col) {
        ChessPosition endPosition = new ChessPosition(row, col);
        ChessPiece newPiece = this.board.getPiece(endPosition);

        if (newPiece != null) {
            if (newPiece.getTeamColor() != team) {
                validMoves.add(new ChessMove(this.myPosition, endPosition, null));
            }
            return true;
        }
        validMoves.add(new ChessMove(this.myPosition, endPosition, null));
        return false;
    }
}

class RookMovesCalculator extends PieceMovesCalculator {

    public RookMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        super(piece, myPosition, board);
    }

    protected Collection<ChessMove> pieceMoves() {
        for (int i=row+1; i<=8; i++) {
            if (addPiece(i, this.col)) {
                break;
            }
        }

        for (int i=row-1; i>0; i--) {
            if (addPiece(i, this.col)) {
                break;
            }
        }

        for (int j=col+1; j<=8; j++) {
            if (addPiece(this.row, j)) {
                break;
            }
        }

        for (int j=col-1; j>0; j--) {
            if (addPiece(this.row, j)) {
                break;
            }
        }

        return this.validMoves;
    }
}

class BishopMovesCalculator extends PieceMovesCalculator {

    public BishopMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        super(piece, myPosition, board);
    }

    protected Collection<ChessMove> pieceMoves() {
        int i;
        int j;

        for (i=this.row+1, j=this.col+1; i<=8 && j<=8; i++, j++) {
            if (addPiece(i, j)) {
                break;
            }
        }

        for (i=this.row+1, j=this.col-1; i<=8 && j>0; i++, j--) {
            if (addPiece(i, j)) {
                break;
            }
        }

        for (i=this.row-1, j=this.col+1; i>0 && j<=8; i--, j++) {
            if (addPiece(i, j)) {
                break;
            }
        }

        for (i=this.row-1, j=this.col-1; i>0 && j>0; i--, j--) {
            if (addPiece(i, j)) {
                break;
            }
        }

        return this.validMoves;
    }
}

class QueenMovesCalculator extends PieceMovesCalculator {

    public QueenMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        super(piece, myPosition, board);
    }

    protected Collection<ChessMove> pieceMoves() {
        RookMovesCalculator rook = new RookMovesCalculator(this.piece, this.myPosition, this.board);
        BishopMovesCalculator bishop = new BishopMovesCalculator(this.piece, this.myPosition, this.board);

        this.validMoves.addAll(rook.pieceMoves());
        this.validMoves.addAll(bishop.pieceMoves());
        return this.validMoves;
    }
}

class KingMovesCalculator extends PieceMovesCalculator {

    public KingMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        super(piece, myPosition, board);
    }

    protected Collection<ChessMove> pieceMoves() {
        int[] rows;
        int[] columns;

        switch (this.row) {
            case 1 -> rows = new int[]{1, 2};
            case 8 -> rows = new int[]{7, 8};
            default -> rows = new int[]{this.row-1, this.row, this.row+1};
        }

        switch (this.col) {
            case 1 -> columns = new int[]{1, 2};
            case 8 -> columns = new int[]{7, 8};
            default -> columns = new int[]{this.col-1, this.col, this.col+1};
        }

        for (int row : rows) {
            for (int column : columns) {
                addPiece(row, column);
            }
        }

        return this.validMoves;
    }
}

class KnightMovesCalculator extends PieceMovesCalculator {

    public KnightMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        super(piece, myPosition, board);
    }

    protected Collection<ChessMove> pieceMoves() {
        ArrayList<ChessPosition> newPositions = new ArrayList<>();

        newPositions.add(new ChessPosition(this.row+2, this.col+1));
        newPositions.add(new ChessPosition(this.row+2, this.col-1));
        newPositions.add(new ChessPosition(this.row-2, this.col+1));
        newPositions.add(new ChessPosition(this.row-2, this.col-1));
        newPositions.add(new ChessPosition(this.row+1, this.col+2));
        newPositions.add(new ChessPosition(this.row+1, this.col-2));
        newPositions.add(new ChessPosition(this.row-1, this.col+2));
        newPositions.add(new ChessPosition(this.row-1, this.col-2));

            for (ChessPosition newPos : newPositions) {
                try {
                    addPiece(newPos);
                } catch (ArrayIndexOutOfBoundsException e) {}

            }

        return this.validMoves;
    }

    protected void addPiece(ChessPosition endPosition) {
        ChessPiece newPiece = this.board.getPiece(endPosition);

        if (newPiece != null) {
            if (newPiece.getTeamColor() != team) {
                validMoves.add(new ChessMove(this.myPosition, endPosition, null));
            }
        } else {
            validMoves.add(new ChessMove(this.myPosition, endPosition, null));
        }
    }
}

class PawnMovesCalculator extends PieceMovesCalculator {
    private int incrementer = 1;
    private boolean initialMove = false;
    private boolean rightEdge = false;
    private boolean leftEdge = false;
    private ArrayList<ChessPiece.PieceType> promotionPieces = new ArrayList<>(Arrays.asList(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK));

    public PawnMovesCalculator(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
        super(piece, myPosition, board);
        
        if (this.team == ChessGame.TeamColor.BLACK) {
            this.incrementer = -1;
            if (this.row == 7) {
                initialMove = true;
            }
        } else if (this.row == 2) {
            initialMove = true;
        }
        
        if (this.col == 1) {
            this.leftEdge = true;
        } else if (this.col == 8) {
            this.rightEdge = true;
        }
    }

    protected Collection<ChessMove> pieceMoves() {
        if (this.initialMove) {
            if (this.board.getPiece(new ChessPosition(this.row+incrementer, this.col)) == null) {
                ChessPosition endPos = new ChessPosition(this.row+(incrementer*2), this.col);
                ChessPiece newPiece = this.board.getPiece(endPos);

                if (newPiece == null) {
                    this.validMoves.add(new ChessMove(this.myPosition, endPos, null));
                }
            }
        }
        
        if (!leftEdge) {
            ChessPosition endPos = new ChessPosition(this.row+incrementer, this.col-1);
            ChessPiece newPiece = this.board.getPiece(endPos);

            if (newPiece != null && newPiece.getTeamColor() != this.team) {
                addPiece(endPos);
            }
        }

        if (!rightEdge) {
            ChessPosition endPos = new ChessPosition(this.row+incrementer, this.col+1);
            ChessPiece newPiece = this.board.getPiece(endPos);

            if (newPiece != null && newPiece.getTeamColor() != this.team) {
                addPiece(endPos);
            }
        }

        ChessPosition endPos = new ChessPosition(this.row+incrementer, this.col);
        ChessPiece newPiece = this.board.getPiece(endPos);

        if (newPiece == null) {
            addPiece(endPos);
        }

        return this.validMoves;
    }

    protected void addPiece(ChessPosition endPosition) {
        if ((this.team == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1) || (this.team == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8)) {
            for (ChessPiece.PieceType promotion : promotionPieces) {
                validMoves.add(new ChessMove(this.myPosition, endPosition, promotion));
            }
        } else {
            validMoves.add(new ChessMove(this.myPosition, endPosition, null));
        }
    }
}