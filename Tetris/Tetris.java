import java.util.*;
import org.jline.terminal.*;
import org.jline.reader.*;


public class Tetris {
    volatile boolean pauseGame, gameOver;
    final String textColor = new Color(255, 108, 180).color;
    final String frameColor = new Color(245, 222, 179).color;
    final String dividerColor = new Color(160, 82, 45).color;
    int WIDTH, HEIGHT;
    char[][] gameArea;
    int[][] temp;
    Timer timer;
    int repeat, divider, d, u;
    Mino curMino;

    public Tetris() {
	ReadInput.start();
	clear();
	initialize();
	spawnNewMino();
	System.out.print(getBoard());
	gameLoop();
    }

    void initialize() {
	WIDTH = ReadInput.width();
	HEIGHT = ReadInput.height();
	divider = (WIDTH/3)*2 - 2;

	gameArea = new char[HEIGHT - 2][divider - 1];
	initGameArea();

	ReadInput.input = ' ';
	repeat = 200;
    }

    String getBoard() {
	StringBuilder sb = new StringBuilder();
	System.out.print("\033[H");
	for(int a=0; a<HEIGHT; a++) {
	   for(int b=0; b<WIDTH; b++) {
	      if(a == 0 || a == HEIGHT - 1)
		  sb.append(frameColor + (char) 9618);
	      else if(b == 0 || b == WIDTH -1)
		  sb.append(frameColor + (char) 9618);
	      else if(b == divider) 
		  sb.append(frameColor + (char) 9612);
	      else if(a == HEIGHT/3 && b > divider)
		  sb.append(dividerColor + (char) 9612);
	      else if(a == HEIGHT/3 * 2 && b > divider)
		  sb.append(dividerColor + (char) 9612);
	      else if(b < divider) {
		  boolean draw = false;
		  for(Block c : curMino.blk) {
		      if(c.y == a && c.x == b) {
			 sb.append(c.c.color);
			 sb.append(c.block);
			 draw = true;
			 break;
		      }
		  }
		  if(!draw) {
		     char ch = gameArea[a - 1][b - 1];
		     if(ch != ' ') {
			sb.append(Color.reset());
			sb.append(ch);
		     }else {
			sb.append(" ");
		     }
		  }
	      }
	      else sb.append(" ");
	   }
	   sb.append("\n");
	}
	sb.append(textColor);
	sb.append("Moves A(Left), L(Right), V(Down), Y(Rotate), Q(Quit), R(Restart) ");
	sb.append(gameArea.length+"\t"+HEIGHT);
	return sb.toString();
    }

    boolean hasLanded() {
	for(Block b : curMino.blk) {
        if(b.y >= HEIGHT - 2) return true;
        if(gameArea[b.y][b.x - 1] != ' ') return true;
	}
	return false;
    }

    void lockMino() {
	for(Block b : curMino.blk) {
	    if (b.y >= 1 && b.y <= gameArea.length && 
		b.x >= 1 && b.x - 1 <= gameArea[0].length)
		    gameArea[b.y - 1][b.x - 1] = b.block;
	}
    }

    void spawnNewMino() {
	switch (new Random().nextInt(7)) {
	    case 0: curMino = new Mino_L1(); break;
	    case 1: curMino = new Mino_L2(); break;
	    case 2: curMino = new Mino_T(); break;
	    case 3: curMino = new Mino_B(); break;
	    case 4: curMino = new Mino_Z1(); break;
	    case 5: curMino = new Mino_Z2(); break;
	    case 6: curMino = new Mino_S(); break;
	}
	curMino = new Mino_B();
	switch(curMino.type) {
            case 'z':
	    case 'Z':
	    case 'B': d = new Random().nextInt(1, 3); break;
	    case 'T':
	    case 'L':
	    case 'l': d = new Random().nextInt(1, 5); break;
	}
	curMino.setXY(divider/2, 2, d);

	for(Block b : curMino.blk) {
	    if(gameArea[b.y - 1][b.x - 1] != ' ') {
		gameOver = true;
		exitGame();
	    }
	}
    }

    void checkCompletedLines() {
	for(int r = gameArea.length - 1; r >= 0; r--) {
	    boolean fullLine = true;
	    for(int c = 0; c < gameArea[r].length; c++) {
		if(gameArea[r][c] == ' ') {
		   fullLine = false;
		   break;
		}
	    }

	    if(fullLine) {
		clearLine(r);
		shiftDown(r);
		r++;
	    }
	}
    }

    void clearLine(int r) {
	for(int c = 0; c < gameArea[r].length; c++) 
	    gameArea[r][c] = ' ';
    }

    void shiftDown(int fromRow) {
	u = fromRow;
	for(int row = fromRow; row > 0; row--)
	    for(int col = 0; col < gameArea[0].length; col++) 
		gameArea[row][col] = gameArea[row - 1][col];

	for(int col = 0; col < gameArea[0].length; col++)
	    gameArea[fromRow][col] = ' ';
    }

    void gameLoop() {
	if (!gameOver) {
	   if (timer != null) timer.cancel();
	   timer = new Timer();
	   timer.scheduleAtFixedRate(new TimerTask() {
		@Override
		public void run() {
		    if(HEIGHT != ReadInput.height() 
			|| WIDTH != ReadInput.width()) { 
			    initialize();
			    clear();
			}
		    input();
		    if(hasLanded()) {
			lockMino();
			checkCompletedLines();
			spawnNewMino();
		    }

		    System.out.print(getBoard());
		}
            }, 0, repeat);
        }
    }

    void input() {
	boolean valid = false;
	switch(ReadInput.input) {
	    case 'a':
	    case 'A':
		for(Block b : curMino.blk) {
		    if(b.x > 1) valid = true;
		    else {
			valid = false;
			break;
		    }
		}
		if(valid) curMino.move(-1,0);
		break;
	    case 'l':
	    case 'L':
		for(Block b : curMino.blk) {
		    if(b.x < divider - 1) valid = true;
		    else {
			valid = false;
			break;
		    }
		}
		if(valid) curMino.move(1,0);
		break;
	    case 'v':
	    case 'V':
		for(Block b : curMino.blk) {
		    if(b.y < HEIGHT - 2) valid = true;
		    else {
			valid = false;
			break;
		    }
		}
		if(valid) curMino.move(0,1);
		break;
	    case 'y':
	    case 'Y':
		if(curMino.type != 'S')
		for(Block b : curMino.blk) {
		    if(b.x > 1 && b.x < divider - 1) valid=true;
		    else {
			valid = false;
			break;
		    }
		}
		if(valid) {
		   d++;
		   curMino.rotate(d);
		   switch(curMino.type) {
		       case 'z':
		       case 'Z':
		       case 'B': if(d >= 2) d = 0; break;
		       case 'T':
		       case 'L':
		       case 'l': if(d >= 4) d = 0; break;
		   }
		}
		break;
	    case 'Q':
	    case 'q':
		exitGame();
		break;
	    case 'r':
	    case 'R':
		break;
	}
	valid = false;
	ReadInput.input = 'v';
    }

    void initGameArea() {
	for(int a=0; a<gameArea.length; a++)
	    Arrays.fill(gameArea[a], ' ');
    }

    void clear() {
	try {
	    new ProcessBuilder("clear")
		    .inheritIO()
		    .start()
		    .waitFor();
	} catch (Exception n) {}
     }

    void exitGame() {
	ReadInput.end();
	timer.cancel();
	Color.resetColor();
	System.gc();
	System.out.println();
	System.exit(0);
    }

     public static void main(String... args) {
	new Tetris();
    }
}

class Mino_L1 extends Mino {

    /**
     * 1■
     * 0■
     * 2■ ■3
     **/
    Mino_L1() {
	create(new Color(c(), c(), c()));
	type = 'L';
    }

    void setXY(int x, int y, int d) {
	blk[0].x = x;
	blk[0].y = y;

	switch(d) {
	    case 1:
		blk[1].x = blk[0].x;
		blk[1].y = blk[0].y - 1;

		blk[2].x = blk[0].x;
		blk[2].y = blk[0].y + 1;

		blk[3].x = blk[0].x + 1;
		blk[3].y = blk[0].y + 1;
		break;
	    case 2:
		blk[1].x = blk[0].x + 1;                                        blk[1].y = blk[0].y;                                            
		blk[2].x = blk[0].x - 1;                                        blk[2].y = blk[0].y;                                            
		blk[3].x = blk[0].x - 1;                                        blk[3].y = blk[0].y + 1;
		break;
	    case 3:
		blk[1].x = blk[0].x;                                            blk[1].y = blk[0].y + 1;                                                                                                        blk[2].x = blk[0].x;                                            blk[2].y = blk[0].y - 1;                                                                                                        blk[3].x = blk[0].x - 1;                                        blk[3].y = blk[0].y - 1;
		break;
	    case 4:
		blk[1].x = blk[0].x - 1;                                        blk[1].y = blk[0].y;                                            
		blk[2].x = blk[0].x + 1;                                        blk[2].y = blk[0].y;                                            
		blk[3].x = blk[0].x + 1;                                        blk[3].y = blk[0].y - 1;
		break;
	}
    }

    void rotate(int d) {                                                int x = blk[0].x;                                               int y = blk[0].y;                                               setXY(x, y, d);                                             }
}

class Mino_L2 extends Mino {

    /**
     *    ■1
     *    ■0
     * 3■ ■2
     **/
    Mino_L2() {
        create(new Color(c(), c(), c())); 
	type = 'l';
    }
    void setXY(int x, int y, int d) {
        blk[0].x = x;
        blk[0].y = y;                                                     
	switch(d) {
	    case 1:
		blk[1].x = blk[0].x;                                            blk[1].y = blk[0].y - 1;

		blk[2].x = blk[0].x;
		blk[2].y = blk[0].y + 1;                                                                                                        blk[3].x = blk[0].x - 1;
		blk[3].y = blk[0].y + 1;
		break;
	    case 2:                                                             blk[1].x = blk[0].x + 1;                                        blk[1].y = blk[0].y;                                            
										blk[2].x = blk[0].x - 1;                                        blk[2].y = blk[0].y;
										blk[3].x = blk[0].x - 1;                                        blk[3].y = blk[0].y - 1;                                        break;
	    case 3:                                                             blk[1].x = blk[0].x;                                            blk[1].y = blk[0].y + 1;                                                                                                        blk[2].x = blk[0].x;                                            blk[2].y = blk[0].y - 1;                                                                                                        blk[3].x = blk[0].x + 1;                                        blk[3].y = blk[0].y - 1;                                        break;
	    case 4:                                                             blk[1].x = blk[0].x - 1;                                        blk[1].y = blk[0].y;                                            
										blk[2].x = blk[0].x + 1;                                        blk[2].y = blk[0].y;                                            
										blk[3].x = blk[0].x + 1;                                        blk[3].y = blk[0].y + 1;                                        break;
	}
    }

    void rotate(int d) {                                                int x = blk[0].x;                                               int y = blk[0].y;                                               setXY(x, y, d);                                             }
}

class Mino_B extends Mino {

    /**
     * 0■
     * 1■
     * 2■ 
     * 3■
     **/
    Mino_B() {
        create(new Color(c(), c(), c()));
	type = 'B';
    }

    void rotate(int d) {                                                int x = blk[0].x;                                               int y = blk[0].y;                                               setXY(x, y, d);                                             }

    void setXY(int x, int y, int d) {
        blk[0].x = x;
        blk[0].y = y;                                                   
	switch(d) {
	    case 1:
		blk[1].x = blk[0].x;                                            blk[1].y = blk[0].y + 1;

		blk[2].x = blk[0].x;
		blk[2].y = blk[0].y + 2;                                                                                                        blk[3].x = blk[0].x;
		blk[3].y = blk[0].y + 3;
		break;
	    case 2:                                                             blk[1].x = blk[0].x + 1;                                        blk[1].y = blk[0].y;                                            
										blk[2].x = blk[0].x + 2;                                        blk[2].y = blk[0].y;
										blk[3].x = blk[0].x + 3;                                        blk[3].y = blk[0].y;						break;
	}
    }
}

class Mino_T extends Mino {

    /**
     *  102
     *  ■■■ 
     *   ■
     *   3
     **/
    Mino_T() {
        create(new Color(c(), c(), c()));
	type = 'T';
    }
    void setXY(int x, int y,int d) {
        blk[0].x = x;
        blk[0].y = y;                                                  
	switch(d) {
	    case 1:
		blk[1].x = blk[0].x - 1;
		blk[1].y = blk[0].y;

		blk[2].x = blk[0].x + 1;
		blk[2].y = blk[0].y;

		blk[3].x = blk[0].x;
		blk[3].y = blk[0].y + 1;
		break;
	    case 2:                                                             blk[1].x = blk[0].x;						blk[1].y = blk[0].y - 1;                                                                                                        blk[2].x = blk[0].x;						blk[2].y = blk[0].y + 1;
										blk[3].x = blk[0].x - 1;                                        blk[3].y = blk[0].y;						break;
	    case 3:                                                             blk[1].x = blk[0].x + 1;                                        blk[1].y = blk[0].y;                                                                                                            blk[2].x = blk[0].x - 1;                                        blk[2].y = blk[0].y;                                                                                                            blk[3].x = blk[0].x;                                            blk[3].y = blk[0].y - 1;                                        break;
	    case 4:                                                             blk[1].x = blk[0].x;
		blk[1].y = blk[0].y + 1;
										blk[2].x = blk[0].x;						blk[2].y = blk[0].y - 1;					
										blk[3].x = blk[0].x + 1;					blk[3].y = blk[0].y;						break;
	}
    }

    void rotate(int d) {                                                int x = blk[0].x;                                               int y = blk[0].y;                                               setXY(x, y, d);                                             }
}

class Mino_Z1 extends Mino {

    /**
     * 10
     * ■■
     *  ■■
     *  23
     **/
    Mino_Z1() {                                                         create(new Color(c(), c(), c()));
	type = 'Z';
    }

    void setXY(int x, int y, int d) {
	blk[0].x = x;                                                   blk[0].y = y;                                                   
	switch(d) {
	    case 1:
		blk[1].x = blk[0].x - 1;
		blk[1].y = blk[0].y;

		blk[2].x = blk[0].x;                                            blk[2].y = blk[0].y + 1;                                                                                                        blk[3].x = blk[0].x + 1;                                        blk[3].y = blk[0].y + 1;
		break;

	    case 2:
		blk[1].x = blk[0].x;
		blk[1].y = blk[0].y + 1;

		blk[2].x = blk[0].x + 1;
		blk[2].y = blk[0].y;

		blk[3].x = blk[0].x + 1;                                        blk[3].y = blk[0].y - 1;
		break;
	}
    }

    void rotate(int d) {
	int x = blk[0].x;
	int y = blk[0].y;
	setXY(x, y, d);
    }
}

class Mino_Z2 extends Mino {

    /**
     *  01
     *  ■■
     * ■■
     * 23
     **/
    Mino_Z2() {                                                         create(new Color(c(), c(), c()));
	type = 'z';
    }                                                                                                                               void setXY(int x, int y, int d) {
	blk[0].x = x;                                                   blk[0].y = y;

	switch(d) {
	    case 1:
		blk[1].x = blk[0].x + 1;
		blk[1].y = blk[0].y;

		blk[2].x = blk[0].x - 1;
		blk[2].y = blk[0].y + 1;                                                                                                        blk[3].x = blk[0].x;
		blk[3].y = blk[0].y + 1;
		break;
	    case 2:
		blk[1].x = blk[0].x;
		blk[1].y = blk[0].y + 1;

		blk[2].x = blk[0].x - 1;                                        blk[2].y = blk[0].y - 1;                                                                                                        blk[3].x = blk[0].x - 1;
                blk[3].y = blk[0].y;
                break;
	}
    }

    void rotate(int d) {                                                int x = blk[0].x;
        int y = blk[0].y;
        setXY(x, y, d);
    }
}

class Mino_S extends Mino {

    /**
     * 01
     * ■■
     * ■■
     * 23
     **/
    Mino_S() {
	create(new Color(c(), c(), c()));
	type = 'S';
    }

    void setXY(int x, int y, int d) {
	blk[0].x = x;                                                   blk[0].y = y;                                                                                                                   blk[1].x = blk[0].x + 1;                                        blk[1].y = blk[0].y;                                                                                                            blk[2].x = blk[0].x;
	blk[2].y = blk[0].y + 1;                                                                                                        blk[3].x = blk[0].x + 1;
	blk[3].y = blk[0].y + 1;
    }
}

/**
 * Sets the terminal text color using RGB values.
 */
class Color {
    String color;

    /*
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     */
    Color(int r, int g, int b) {
        color = String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
    }

    /**
     * Resets the terminal text color to default.
     */
    static void resetColor() {
        System.out.print("\u001B[0m");
    }

    static String reset() {
	return "\u001B[0m";
    }
}

class Mino {
    public Block[] blk = new Block[4];
    public char type;

    void create(Color c) {
	for(int a=0; a<4; a++) {
	   blk[a] = new Block(c);
	}
    }

    void setXY(int x, int y, int d) {}

    void rotate(int d) {}

    void move(int x, int y) {
	for(Block b:blk) {
	   b.x += x;
	   b.y += y;
	}
    }

    int c() {
	return new Random().nextInt(256);
    }
}

class Block {
    final char block = (char) 9608;
    int x, y;
    Color c;

    Block(Color c) {this.c = c;}
}

/**
 * ReadInput handles keyboard input from the terminal in a separate thread.
 */
class ReadInput {
    static Thread thread;
    static Terminal terminal;
    static volatile char input;
    static volatile boolean run = true;

    /**
     * Starts the input thread and initializes the terminal.
     */
    public static void start() {
        try {
            terminal = TerminalBuilder.terminal();
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();
            terminal.enterRawMode();
            input();
        } catch (Exception e) {}
    }

    /**
     * Stops the input thread and closes the terminal.
     */
    public static void end() {
        try {
            run = false;
            if (terminal != null) terminal.close();
            if (thread != null) thread.interrupt();
            thread = null;
        } catch (Exception e) {}
    }

    /**
     * Spawns a new thread that reads single characters from the terminal.
     */
    private static void input() {
        try {
            thread = new Thread(() -> {
                try {
                    while (run) input = (char) terminal.reader().read();
                } catch (Exception q) {}
            });
            thread.start();
        } catch (Exception e) {}
    }

    /**
     * Gets the terminal height, with fallback if not available.
     *
     * @return The height of the terminal window.
     */
    static int height() {
        int h = 30;
        try {
            h = terminal.getHeight();
        } catch (Exception n) {}
        return h > 40 ? h / 2 + 4 : h - 2;
    }

    /**
     * Gets the terminal width, with fallback if not available.
     *
     * @return The width of the terminal window.
     */
    static int width() {
        int w = 62;
        try {
            w = terminal.getWidth();
        } catch (Exception n) {}
        return w;
    }
}
