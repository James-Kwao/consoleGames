import java.util.*;
import java.util.Random;
import org.jline.terminal.*;
import org.jline.reader.*;

public class SnakeGame {
    private final char food = (char)9775;
    private final char snakeBody = (char)9706;
    private final char snakeHead = (char)9865;
    private final char[][] board;
    private int WIDTH;
    private int HEIGHT;

    private int[] snakeX;
    private int[] snakeY;
    private int snakeLength;
    private int foodX;
    private int foodY;
    private int score;
    private int life;
    private int repeatTime;
    private char direction, space;
    private Random random;
    private Timer gameLoop, inputLoop;
    private boolean gameOver,isPause;

    public SnakeGame() {
	ReadInput.start();
	clear();

	WIDTH = ReadInput.width();
	HEIGHT = ReadInput.height();
        snakeX = new int[(WIDTH * HEIGHT) - 2];
        snakeY = new int[(WIDTH * HEIGHT) - 2];
	board = new char[HEIGHT-1][WIDTH-1];

	initializeGame();
        random = new Random();
	generateFood();
	changeSpeed();
    }

    private String getBoard(){
        StringBuilder sb = new StringBuilder();
        String l = "  ";
	for(int i=0; i<life; i++) l += "💙";
	sb.append(l);

	l = "\u001B[34mYour Score: " + score;
        for(int i=0; i < HEIGHT; i++){
	    for(int a=0; a<WIDTH; a++){
		if(i == 0) {
		   if(a+l.length()+6 == WIDTH-1) {
		      sb.append(l);
		      break;
		   }
		   else sb.append(" ");
		}
		else {
		  if(i == 1 || i == HEIGHT-1)
		     sb.append("\u001B[35m#");
		  else if(a == 0 || a == WIDTH - 1)
		     sb.append("\u001B[35m#");
		  else {
		      if(snakeY[0] == i&&snakeX[0] == a) {
                         sb.append("\u001B[32m");                                   space = snakeHead;                                        }
		      else if(foodY == i && foodX == a) {
			sb.append("\u001B[33m");
			space = food;
		      }
		      else space = ' ';
		      board[i][a] = space;
		      sb.append(board[i][a]);
		  }
		}
	    }
	    sb.append("\n");
        }
        sb.append("Enter 'Y' 'A' 'V' 'L' to move direction: "+direction);
        return sb.toString();
    }

    private void initializeGame() {
	snakeY[0] = HEIGHT/2;
	snakeX[0] = WIDTH/2;
	snakeLength = 2;
        gameOver = isPause = false;
	score = 0;
        space = direction = ' ';                                   life = 3;
        repeatTime = 300;

        for(int i = 1; i < HEIGHT; i++) {
	    snakeX[i] = -1;
	    snakeY[i] = -1;
        }
    }

    private void generateFood() {
        foodX = random.nextInt(1, WIDTH-1);
        foodY = random.nextInt(2, HEIGHT-1);
	if(board[foodY][foodX] == snakeBody) 
	   generateFood();
    }

    private void updateSnake() {
	if (snakeX[0] <= 0 || snakeX[0] >= WIDTH-1 ||
	       snakeY[0] < 2 || snakeY[0] >= HEIGHT-1) {
	    gameOver = true;
	    clear();
	    System.out.println("\n\n\n\n\n\u001B[31m");
	    try {
		Process process = Runtime.getRuntime().exec(new String[] {"figlet","-ctf","slant", "G AM E\nOV ER\nYour Score:  " + String.valueOf(score)});
		java.util.Scanner s = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");

		System.out.println(s.hasNext()?s.next():"");
		System.out.print("\u001B[33mR to restart or Q to exit: ");
		} catch (Exception e) {}
	    return;
	}
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            score++;
            snakeLength++;
            generateFood();
        }
    }

    public void play() {
	switch (ReadInput.input) {
	    case 'Y':
	    case 'y':
		if (direction != 'v') direction = 'y';
		if(isPause) isPause = false;
		break;
	    case 'a':
	    case 'A':
		if (direction != 'l') direction = 'a';
		if(isPause) isPause = false;
		break;
	    case 'L':
	    case 'l':
		if (direction != 'a') direction = 'l';
		if(isPause) isPause = false;
                break;
	    case 'v':
	    case 'V':
		if (direction != 'y') direction = 'v';
		if(isPause) isPause = false;
		break;
	    case 'q':
	    case 'Q':
		clear();
		System.exit(0);
		break;
	    case 'p':
	    case 'P':
		isPause = !isPause;
		break;
	    case 'r':
	    case 'R':
		clear();
		initializeGame();
		break;
	}
	ReadInput.input = ' ';

	if(direction == 'v' && !isPause) snakeY[0]++;
	else if(direction == 'l' && !isPause) snakeX[0]++;
	else if(direction == 'a' && !isPause) snakeX[0]--;
	else if(direction == 'y' && !isPause) snakeY[0]--;
    }

    void changeSpeed() {
	if(gameLoop != null) gameLoop.cancel();
	gameLoop = null;
	gameLoop = new Timer();
	gameLoop.scheduleAtFixedRate(new TimerTask() {
	    @Override
		public void run() {
		   play();
		   if(!gameOver && !isPause) {
			System.out.print("\033[H");
			System.out.flush();
			System.out.print(getBoard());
			updateSnake();
		   }
		}
	   },0,repeatTime);                                
    }

    void clear(){
        try {
            new ProcessBuilder("clear")
                    .inheritIO().start().waitFor();
        }catch(Exception n) {}                                      }

    public static void main(String[] args) {
        new SnakeGame();
    }
}

class ReadInput {
    static Thread thread;
    static Terminal terminal;                                       static volatile char input;
    static volatile boolean run = true;

    public static void start() {
	if(terminal == null) {
	   try {
		terminal = TerminalBuilder.terminal();
		LineReader reader = LineReaderBuilder.builder()                             .terminal(terminal)                                             .build();
		terminal.enterRawMode();
		input();
	   }catch (Exception e){}
	}
    }
                                                                    public static void end() {
        try {
            run = false;
            if(terminal != null) terminal.close();                          if(thread != null) {                                                thread.interrupt();
            }
            thread = null;
        }catch(Exception e){}
    }
                                                                    private static void input() {
        try {
            thread = new Thread(() -> {
                try {
                    while(run) input = (char) terminal.reader().read();
                } catch(Exception q){}
            });
            thread.start();
        }catch(Exception e){}
    }

    static int height() {
        int h = 30;
        try {
            h = terminal.getHeight();
        }catch(Exception n){}
        return h > 40? h/2 + 4 : h - 2;
    }

    static int width() {
        int w = 62;
        try {
            w = terminal.getWidth();
        }catch(Exception n){}
        return w;
    }
}
