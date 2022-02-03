package game_chess.board;

import game_chess.pieces.Pawn;
import game_chess.pieces.Piece;
import game_chess.pieces.Rook;


/**
 * Created by :
 * Date Created:
 *
 * game_chess.board.Move class to represent possible moves
 * **/


public abstract class Move {

    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;
    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate){
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public int hashCode(){
        final int prime = 31;
        int result = 1;

        result = prime * result + this.movedPiece.getPiecePosition();
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();

        return result;

    }
    public boolean equals(final Object other){
            if (this == other ){
                 return true ;
        }
            if (!(other instanceof Move)){
                return false;
            }
            final Move otherMove = (Move) other;
            return getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                    getMovedPiece().equals(otherMove.getMovedPiece());
    }
    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }


    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public boolean isAttack(){
        return false;
    }

    public int getDestinationCoordinate(){
     return this.destinationCoordinate;
    }

    public boolean isCastlingMove(){
      return false;
    }
public Piece getAttackedPiece(){
        return null;
}

    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                 if(!this.movedPiece.equals(piece)){
                     builder.setPiece(piece);
                 }

        }
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece(piece);
        }
        // move to the moved piece
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(null);
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

        return builder.build();

    }

    //Regular Move
    public static class Regular extends Move{

        public Regular(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece){
            super(board, movedPiece, destinationCoordinate);
        }

    }

    //Attack Move
    public static final class Attack extends Move{

        final Piece attackedPiece;

        public Attack(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece){
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }


        public int hashCode(){
            return  this.attackedPiece.hashCode() + super.hashCode();
        }


        public boolean equals(final Object other){
            if (this == other){
                return true ;
            }
            if (!(other instanceof Regular)){
                return false;
            }
            final Attack otherAttack = (Attack) other;
            return super.equals(otherAttack) && getAttackedPiece().equals(otherAttack.getAttackedPiece());
        }

        @Override
        public Board execute() {
            return null;
        }

        public boolean isAttack(){
            return true;
        }


        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }
    }



    public static final class PawnMove extends Move{

        public PawnMove(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }

    }


    public static final class PawnAttackMove extends Regular{

        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece){
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

    }

    public static final class PawnEnPassantAttackMove extends Regular{

        public PawnEnPassantAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece){
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

    }

    public static final class PawnJump extends Move{

        public PawnJump(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate){
            super(board, movedPiece, destinationCoordinate);
        }


        @Override
        public Board execute() {
            final Board.Builder builder  = new Board.Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);

                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces() ){
                builder.setPiece(piece);
            }
             final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    static abstract class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination
        ) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        public boolean isCastlingMove(){
            return true;
        }



        public Board execute(){

            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);

                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces() ){
                builder.setPiece(piece);
            }
          builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

   public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
public String toString(){
    return "O-O";


        }
    }




    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook,castleRookStart,castleRookDestination);
        }

        public String toString(){
            return "O-O-O";
        }
    }


    public static final class NullMove extends Move {
        public NullMove() {
            super(null,null, -1);
        }
        public Board execute(){
            throw new RuntimeException("Cannot execute the null move ");
        }
    }


    public static class MoveFactory {


        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;

                }
            }
            return NULL_MOVE;
        }
    }
}
