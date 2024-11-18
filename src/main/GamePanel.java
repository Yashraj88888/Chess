package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;




public class GamePanel extends JPanel implements Runnable{
    //basically this is a JPanel class but we can customise it (inheritance?)
    //implements stuff from runnable ig? need to read more on this

    //this class is going to be the game screen

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP,checkingP;
    public static  Piece castlingP;
    //contain pieces on the board
    //simPieces is like a backup if u want to reset position or smth

    //COLOR
    public static final int WHITE=0;
    public static final int BLACK=1;
    int currentColor = WHITE;

    //BOOLEANS 
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;

    //usually game starts from white in chess so current color = white
    //white = 0 and black = 1 is used to toggle turns by putting 1 and 0 in the turn thing 


    //final means that the values are constant and cannot be changed.
    //The static keyword means the value is the same for every instance of the class. Final means the variable can't change.

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
       
        copyPieces(pieces, simPieces); //passes pieces as source and simPieces as target array
        //so we copy pieces to simPieces



    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
        //we instantiate thread and then start method basically calls run method
    }

    public void setPieces() {

        //White Team
        pieces.add(new Pawn(WHITE,0,6));
        pieces.add(new Pawn(WHITE,1,6));
        pieces.add(new Pawn(WHITE,2,6));
        pieces.add(new Pawn(WHITE,3,6));
        pieces.add(new Pawn(WHITE,4,6));
        pieces.add(new Pawn(WHITE,5,6));
        pieces.add(new Pawn(WHITE,6,6));
        pieces.add(new Pawn(WHITE,7,6));
        pieces.add(new Rook(WHITE,0,7));
        pieces.add(new Rook(WHITE,7,7));
        pieces.add(new Bishop(WHITE,1,7));
        pieces.add(new Bishop(WHITE,6,7));
        pieces.add(new Knight(WHITE,2,7));
        pieces.add(new Knight(WHITE,5,7));
        pieces.add(new King(WHITE,4,7));
        pieces.add(new Queen(WHITE,3,7));

        //Black Team
        pieces.add(new Pawn(BLACK,0,1));
        pieces.add(new Pawn(BLACK,1,1));
        pieces.add(new Pawn(BLACK,2,1));
        pieces.add(new Pawn(BLACK,3,1));
        pieces.add(new Pawn(BLACK,4,1));
        pieces.add(new Pawn(BLACK,5,1));
        pieces.add(new Pawn(BLACK,6,1));
        pieces.add(new Pawn(BLACK,7,1));
        pieces.add(new Rook(BLACK,0,0));
        pieces.add(new Rook(BLACK,7,0));
        pieces.add(new Bishop(BLACK,1,0));
        pieces.add(new Bishop(BLACK,6,0));
        pieces.add(new Knight(BLACK,2,0));
        pieces.add(new Knight(BLACK,5,0));
        pieces.add(new King(BLACK,4,0));
        pieces.add(new Queen(BLACK,3,0));


    }


   

    //we will need to update the piece position multiple times so we use the below function
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        //recieves 2 lists (source list and target list)

        target.clear(); //clears target list

        for (int i=0; i<source.size();i++) {
            target.add(source.get(i));
        }  //adds elements from source list to target list

    }

    @Override 
    public void run() {
        //create the gameloop in this

        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();
            delta+= (currentTime-lastTime)/drawInterval;
            lastTime=currentTime;

            if (delta>=1) {
                update();
                repaint();
                delta--; 
                
            }
        }

        //this is not the only way to create a game loop. 
        //there are multiple methods to create loop

    }

    private void update() {
        //will handle updating stuff like position of pieces (X,Y) and no. of pieces left and shit
        if(promotion){
            promoting();
        }
        else{
            if (mouse.pressed)
        {
            if (activeP==null)
            {
                //check if you can pick up a piece
                for (Piece piece:simPieces)
                {
                    //if mouse is on ally piece, pick it up
                    if (piece.color == currentColor && piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE)
                    {
                        activeP = piece;
                    }
                }
            }
            else
            //if player is alr holding piece,simulate move
            {
                simulate();
            }
        }

        //when mouse button is released
        if (mouse.pressed==false)
        {
            if (activeP!=null){
                if (validSquare){
                    //move confirmed

                    //move confirmed, update piece list in case a piece has been captured
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    if(castlingP != null){
                        castlingP.updatePosition();
                }

                    if(isKingInCheck()){

                    }
                    //else{
                    //   if(canPromote()){
                    //        promotion = true;
                    //    }
                    //   else{
                    //        changePlayer();
                    //    }
                    //}
                    if(canPromote()){
                        promotion = true;
                    }
                    else{
                        changePlayer();
                    }
                   
                    //for changing player after move is made
                }
                else{
                    //move is not valid so reset everything
                    copyPieces(pieces, simPieces);
                    activeP.resetPosition();
                    activeP = null;
                }
                
            }
        }
        }
        //when mouse button is pressed,
        

    }

    private void simulate(){

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        canMove = false;
        validSquare = false;

        //reset the piece list in every loop
        //this is basically for restoring the removed piece during the simulation
        copyPieces(pieces, simPieces);

        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        //check if piece is hovering over a movable square 
        if (activeP.canMove(activeP.col,activeP.row)){
            canMove= true;

            //removing piece if hit

            if (activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();
            
            if(isIllegal(activeP) == false){
                validSquare = true;
            }

            validSquare= true;
        }

    }

    private boolean isIllegal(Piece King){
        if(King.type == Type.KING){
            for(Piece piece : simPieces){
                if(piece != King && piece.color != King.color && piece.canMove(King.col, King.row)){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isKingInCheck(){
        Piece King = getKing(true);

        if(activeP.canMove(King.col, King.row)){
            checkingP = activeP;
            return true;
        }
        else{
            checkingP = null;
        }
        return false;
    }
    private piece getKing(boolean opponent){
        Piece King = null;
        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    King = piece;
                }}
                else{
                    if(piece.type == Type.KING && piece.color == currentColor){
                        King = piece;
                    }
                }
        }
        return King;
    }

    private void checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col +=3;
            }
            else if(castlingP.col == 7){
                castlingP.col -=2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
            
        }
    }

    private void changePlayer(){
        if (currentColor == WHITE){
            currentColor= BLACK;
            //reset black's two stepped status
            for(Piece piece: pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor=WHITE;
            for(Piece piece: pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }

        }
        activeP=null;
       
    }

    private boolean canPromote(){
        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor,9, 2));
                promoPieces.add(new Knight(currentColor,9, 3));
                promoPieces.add(new Bishop(currentColor,9, 4));
                promoPieces.add(new Queen(currentColor,9, 5));
                return true;
            }
        }
        return false;
    }

    private void promoting(){
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                        case ROOK : simPieces.add(new Rook(currentColor, activeP.col, activeP.row));break;
                        case KNIGHT : simPieces.add(new Knight(currentColor, activeP.col, activeP.row));break;
                        case BISHOP : simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));break;
                        case QUEEN : simPieces.add(new Queen(currentColor, activeP.col, activeP.row));break;
                        default : break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer() ;   
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        //handles all the drawing stuff like chessboard pieces etc
        //paintComponent is a method in JComponent that JPanel inherits and is used to draw objects on the panel
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        //type converting g from Graphics type to Graphics2D type

        board.draw(g2); //board

        //pieces
        for(Piece p: simPieces) {
            p.draw(g2);
        }

        
        if (activeP != null) {
            if (canMove) {
                    g2.setColor(Color.white);
                    g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.7f)); 
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board. SQUARE_SIZE);
                    g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 1f));
                
             
                 // Draw the active piece in the end so it won't be hidden by the board or the colored square activeP.draw(g2);|

            }

            activeP.draw(g2);

        }

        //status messages
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion){
            g2.drawString("Promote to :", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE,null);
            }
        }
        else{
            if (currentColor == WHITE){
                g2.drawString("White's Turn",840,550);
                if(checkingP != null && checkingP.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840,650);
                    g2.drawString("is in check", 840,700);
                }
            }
            else{
                g2.drawString("Black's Turn",840,250);
                if(checkingP != null && checkingP.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840,100);
                    g2.drawString("is in check", 840,150);
                }
                
            }
        }
}

}
