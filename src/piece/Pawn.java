package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;

        if (color==GamePanel.WHITE) {
            //if its whites turn that means color will be equal to GamePanel.WHITE which is 0
            //so then image of white pawn is used 
            //to customise maybe put inside this loop itself (if customisation1) then change path used
            image = getImage("/piece/w-pawn");
        }
        else {
            image= getImage("/piece/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow){

        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow)==false)
        {

            int moveValue;
            if (color== GamePanel.WHITE){
                moveValue= -1;
            }
            else{
                moveValue=1;
            }

            //check hitting piece
            hittingP = getHittingP(targetCol, targetRow);

            //1 square forward movement
            if (targetCol== preCol && targetRow == preRow + moveValue && hittingP == null)
            {
                return true;
            }

            //2 square starting movement
            if (targetCol== preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false && pieceIsOnStraightLine(targetCol, targetRow)==false)
            {
                return true;
            }

            //capturing diagonal piece
            if (Math.abs(targetCol-preCol)==1 && targetRow == preRow + moveValue && hittingP != null && hittingP.color != color)
            {
                return true;
            }
            if (Math.abs(targetCol - preCol)==1 && targetRow == preRow + moveValue){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true){
                        hittingP = piece;
                        return true;
                    }
                }
            }
            //En Passant

        }
        return false;
    }


}
