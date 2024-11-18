
package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {

    final int MAX_ROW = 8;
    final int MAX_COL = 8;
    //chess board consists of 8x8 squares
    public static final int SQUARE_SIZE = 100;
    //size of single square is set to 100 by 100
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;
    //will use this later

    public void draw(Graphics2D g2) {
        int c=0;

        for (int row=0; row<MAX_ROW; row++) {
            for (int col=0; col<MAX_COL; col++) {
                if (c==0) {
                    g2.setColor(new Color(210,165,125));
                    c=1;
                }

                else {
                    g2.setColor(new Color(175,115,70));
                    c=0;
                }

                //alternates colored squares

                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                


                
            }
            if (c==0) {
                c=1;
            }
            else {
                c=0;
            }
            //after every row, we dont need to change the color. if row1 ends at white, row2 will begin from white.
                //thats why its outside the row loop

        }

    }

}
