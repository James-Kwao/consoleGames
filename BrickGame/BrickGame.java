import java.util.*;
import org.jline.terminal.*;
import org.jline.reader.*;

public class BrickGame {
    private Timer timer,timer2;
    private static volatile boolean pauseGame,gameOver;
    private int repeat,WIDTH,HEIGHT,ballX,ballY;
    private int speedX=1, speedY=1, levelSize;
    private int[][] bricks;
    private int brkX, brkY, padX, padY, padLen, life, score;
    private String prevBoard = "";

    public BrickGame(){
	ReadInput.start();
        initBricks(10,40);
	clear();
	ReadInput.input = ' ';
	changeSpeed();
	update();
    }

    void changeSpeed() {
	if(!gameOver) {
	   if(timer != null) timer.cancel();
	   timer = null;
	   timer = new Timer();
	   timer.scheduleAtFixedRate(new TimerTask() {
		@Override
		public void run() {
		     if(!pauseGame && !gameOver) moveBall();
		}
	   },0,repeat);
	}
    }

    void update() {
	if(timer2 != null) timer2.cancel();
	timer2 = null;
	timer2 = new Timer();
	timer2.scheduleAtFixedRate(new TimerTask() {
	    @Override
	    public void run() {
		if(WIDTH != ReadInput.width()) {
		   WIDTH = ReadInput.width();
		   HEIGHT = ReadInput.height();
		   padY = HEIGHT - 3;
		   brkX = (WIDTH - bricks[0].length)/2;
		   clear();
		}
		if(!gameOver && !pauseGame) updateBoard();
		else if(gameOver && !prevBoard.isEmpty()){
		   clear();
		   System.out.println("\n\n\n\n\n\u001B[31m");
		   try {
			Process process = Runtime.getRuntime().exec(new String[] {"figlet","-ctf","shadow", "G A M E\nO V E R\nYour Score:  " + String.valueOf(score)});
			java.util.Scanner s = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
			System.out.println(s.hasNext()?s.next():"");
			System.out.print("\u001B[33mR to restart or E to exit: ");
                } catch (Exception e) {}
		   prevBoard = "";
		}
		readInput();
	    }
	},0,repeat/4);
    }

    String getBoard(){
	StringBuilder sb = new StringBuilder();
	String l = "";
        for(int i=0; i < HEIGHT; i++){
	    if(i == 0 && !gameOver){
		l = "  ";
		for(int d=0; d<life; d++)
		    l += "💙";
		sb.append(l);
		l = "\u001B[34m\t\t\t\t   Your Score:" + score;
		sb.append(l);                                                   sb.append("\n");
	    }
            for(int a=0; a < WIDTH; a++){
                if(i == 0 || i == HEIGHT-1)
		    sb.append("\u001B[35m#");
                else{
                    if(a == 0 || a == WIDTH-1) 
                        sb.append("\u001B[35m#");
		    else if((i >= brkY && i < brkY + bricks.length) && (a >= brkX && a < brkX + bricks[0].length) && !gameOver){
                        if(bricks[i - brkY][a - brkX] > 0){
			    if(ballX == a && ballY == i){
                            	bricks[i - brkY][a - brkX] = 0;
				speedX = -speedX;
				speedY = -speedY;
				score++;
				sb.append("\u001B[33m"+(char)9864);
			    } else sb.append("❤");
			}
			else if(ballX == a && ballY == i)
			    sb.append("\u001B[33m"+(char)9864);
                        else {
			     sb.append(" ");
			}
                    }
		     else if(ballX == a && ballY == i && !gameOver) sb.append("\u001B[33m"+(char)9864);
		    else if((a >= padX && a <= padX+padLen) && i == padY && !gameOver) sb.append("\u001B[32m"+(char) 9724);
		    else sb.append(" ");
                }
            }
            sb.append("\n");
        }
	sb.append("A to move '<' and L to move '>'");
	return sb.toString();
    }

    void updateBoard(){
	String curBoard = getBoard();
	if(!curBoard.equals(prevBoard)){
	    System.out.print("\033[H");
	    System.out.flush();
	    System.out.print(curBoard);
	    prevBoard = curBoard;
	}
    }

    void moveBall(){
	if(ballY > padY) {
	    pauseGame = true;
	    --life;
	    gameOver = life == 0 ? true:false;
	    ballX = WIDTH/2;
	    ballY = HEIGHT-3;
	    padX = ballX - padLen/2;
	    clear();
	    System.out.print(prevBoard);
	    return;
	}
	if(ballX <= 1 || ballX >= WIDTH-2) speedX = -speedX;
	if(ballY <= 1) speedY = -speedY;
	if(ballY == HEIGHT-3 && ballX >= padX && ballX <= padX + padLen){
	    if(ballX == padX || ballX == padX + padLen)
		speedX = -speedX;
	    speedY = -speedY;
	}
	ballX += speedX;
        ballY += speedY;
	if(ballY == 1 || (ballY == padY && ballX == padX + padLen/2)){
            if(ballX >= 1 && ballX <= WIDTH/2 -1) ballX++;
            else if(ballX <= WIDTH-2) ballX--;
	}
    }

    void clear(){
	try {
	    new ProcessBuilder("clear")
		    .inheritIO().start().waitFor();
	}catch(Exception n) {}
    }

    void readInput(){
	switch(ReadInput.input) {
	    case 'a':
	    case 'A':
		if(pauseGame) pauseGame = false;
		if(!pauseGame && padX > 1)
		   padX--;
		break;
	    case 'l':
	    case 'L':
		if(pauseGame) pauseGame = false;
		if(!pauseGame && padX + padLen < WIDTH - 2)
		   padX++;
		break;
	    case 'e':
	    case 'E':
		exitGame();
		break;
	    case 'p':
	    case 'P':
		pauseGame = !pauseGame;
		break;
	    case 'r':
	    case 'R':
		pauseGame = gameOver = false;
		initBricks(10,40);
		changeSpeed();
		update();
		System.gc();
		break;

	}
	ReadInput.input = ' ';
    }

    void initBricks(int y,int x){
	life = 3;
	score = 0;
	repeat = 100;
	WIDTH = ReadInput.width();
	HEIGHT = ReadInput.height();
	ballX = WIDTH/2;
	ballY = HEIGHT-4;
	brkY = 3;
	padLen = 6;
	padX = ballX - padLen/2;
	padY = HEIGHT - 3;

	levelSize = y*x;
	brkX = ballX - x/2;
	bricks = new int[y][x];
	for(int i=0; i<y; i++)
	   for(int a=0; a<x; a++)
	       bricks[i][a] = 1;
	clear();
    }

    void exitGame() {
	ReadInput.end();                                                timer.cancel();
	clear();
	System.exit(0);
    }

    public static void main(String... args){
	BrickGame bg = new BrickGame();
    }
}

class ReadInput {
    static Thread thread;
    static Terminal terminal;
    static volatile char input;
    static volatile boolean run = true;

    public static void start() {
	try {
	    terminal = TerminalBuilder.terminal();
	    LineReader reader = LineReaderBuilder.builder()
			.terminal(terminal)
			.build();
	    terminal.enterRawMode();
	    input();
	}catch (Exception e){}
    }

    public static void end() {
	try {
	    run = false;
	    if(terminal != null) terminal.close();
	    if(thread != null) {
		thread.interrupt();
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

