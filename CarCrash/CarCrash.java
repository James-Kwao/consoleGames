import java.util.*;
import org.jline.terminal.*;
import org.jline.reader.*;

/**                                                              * CarCrash is a console-based car driving game.
 * It uses the JLine library for terminal input and output handling.
 */
public class CarCrash {
   private Timer gameLoop;
   private final Random rand = new Random();
   private int repeatTime = 400;
   private int score,life,divider,max,frameCounter;
   private int HEIGHT,WIDTH;
   private boolean pauseGame,gameOver;
   private Entity player;
   private ArrayList<Entity> collisions;
   private char direction;
   private String[] enemies = {"🐃","🐏","🐌","🐢","🦆","🦔","🐓","🐫","🐘","🐥","🦍","🦌"};

   public CarCrash() {
	ReadInput.start();
	clear();

	collisions = new ArrayList<>();
	HEIGHT = ReadInput.height();
	WIDTH = ReadInput.width()/2;
	divider = (WIDTH/3)*2 - 2;
	player = new Entity(rand.nextInt(1, divider), 1 ,"🚘");
	if(player.x % 6 == 0) player.x++;
	score = max = frameCounter = 0;
	life = 3;

	addEnemies();
	ReadInput.input = ' ';
	changeSpeed();
   }

   private String getBoard() {
       StringBuilder sb = new StringBuilder();
       
       for(int i=0; i < HEIGHT; i++) {
	    int m = 0;
	    for(int a=0; a<WIDTH; a++) {
                if(i == 0 || i == HEIGHT - 1)
		   sb.append("\u001B[35m##");
		else if(a == 0 || a == WIDTH-1)
		   sb.append("\u001B[95m##");
		else if(player.x == a && player.y == i)
		   sb.append(player.n);
		else if(a == divider)
		   sb.append("\u001B[35m🚧");
		else if (a % 6 == 0 && a < divider) {
		   int offset = (i + frameCounter / 2) % 5;;

		   if(offset == 0 || offset == 6) 
		      sb.append("\u001B[97m||"); // Bright
		   else if (offset == 1 || offset == 7|| offset == 11)
		      sb.append("\u001B[37m::"); // Medium
		   else if (offset == 2 || offset == 8 || offset == 10)
		      sb.append("\u001B[90m.."); // Dim
		   else sb.append("  "); // invisible/faded
		}
		else if(a < divider) {
		   boolean drawn = false;
		   for(Entity e : collisions) {
		       if(e.x == a && e.y == i) {
			  sb.append(e.n);
			  drawn = true;
			  break;
		       }
		   }
		   if (!drawn)sb.append("  ");
		}
		else if((i >= HEIGHT/2 - 4 && 
			i <= HEIGHT /2 + 4) && a > divider) {
		   String s = "  Remaining life";
		   int l = (len(s)%2 != 0?len(s+=" "):len(s));
		   int cur = infoX(s);
		   char[] arr = s.toCharArray();
		   sb.append("\u001B[34m");
		   if(i == HEIGHT/2-4 && a >= cur && a < cur+l/2) {
		      sb.append(arr[a-cur+m++]);
		      sb.append(arr[a-cur+m]);
		   }
		   else if(i == HEIGHT/2-3) {
		      s = "";
		      for(int o=0; o<life; o++) s += "💙";
		      cur = infoX(s);
		      if(a >= cur && a < cur+life) 
			 sb.append("💙");
		      else sb.append("  ");
		   }
		   else if(i == HEIGHT/2 + 3) {
		      s = "  Your Score";
		      l = (len(s)%2 != 0?len(s+=" "):len(s));
		      cur = infoX(s);
		      arr = s.toCharArray();
		      if(a >= cur && a < cur+l/2) {                                      sb.append(arr[a-cur+m++]);                                      sb.append(arr[a-cur+m]);                                     }                                                               else sb.append("  ");                                        }
		   else if(i == HEIGHT/2 + 4) {
		      s = "  " + String.valueOf(score);
		      l = (len(s)%2 != 0?len(s+=" "):len(s));
		      cur = infoX(s);
		      arr = s.toCharArray();
		      if(a >= cur && a < cur+l/2) {
			 sb.append(arr[a-cur+m++]);
			 sb.append(arr[a-cur+m]);
		      } else sb.append("  ");
		   } else sb.append("  ");
		} else sb.append("  ");
	    }
	    sb.append("\n");
       }
       sb.append("Use 'A' to move left and 'L' to move right ");
       sb.append(collisions.size());
       return sb.toString();
   }

   void readInput() {
	switch (ReadInput.input) {
	    case 'A':
	    case 'a':
		if(pauseGame) {
		   if(!player.n.equals("🚘")) player.n = "🚘";
		   pauseGame = false;
		}
		if(!gameOver && !pauseGame) {
		   if(player.x > 1 ) player.x--;
		   if(player.x % 6 == 0) player.x--;
		}
		break;
	    case 'l':
	    case 'L':
		if(pauseGame) {
                   if(!player.n.equals("🚘")) player.n = "🚘";                     pauseGame = false;                                           }
		if(!gameOver && !pauseGame) { 
		   if(player.x < divider-1) player.x++;
		   if(player.x % 6 == 0) player.x++;
		}
		break;
	    case 'p':
	    case 'P':
		if(!player.n.equals("🚘")) player.n = "🚘";
		pauseGame = !pauseGame;
		break;
	    case 'R':
	    case 'r':
		break;
	    case 'q':
	    case 'Q':
		gameLoop.cancel();
		ReadInput.end();
		System.gc();
		System.exit(0);
		break;

	}
	ReadInput.input = ' ';
   }

   void changeSpeed() {
        if(gameLoop != null) gameLoop.cancel();
        gameLoop = new Timer();
        gameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
                public void run() {
		    readInput();
		    if(HEIGHT != ReadInput.height()) {
			clear();
			WIDTH = ReadInput.width()/2;
			HEIGHT = ReadInput.height();
			divider = (WIDTH/3)*2 - 2;
			System.out.print("\033[H");
                        System.out.flush();
                        System.out.print(getBoard());
			pauseGame = true;
		    }
		    if(!pauseGame && !gameOver) {
			System.out.print("\033[H");
                        System.out.flush();
			max++;
			frameCounter++;
			if (max == repeatTime/8) {
			   addEnemies();
			   max = frameCounter = 0;
			}
			move();
			if(gameOver) return;
			System.out.print(getBoard());
		    }
		    else if (gameOver) {

		    }
		}
	},0,repeatTime);
    }

    void move() {
	for(Entity e: collisions) {
	    e.move();
	    if(e.y < 1) score++;
	    if(e.x == player.x && e.y == player.y) {
		life--;
		if(life == 0) {
                   gameOver = true;
                   gameOverSequence("");
                } else {
		    player.n = "💥";
		    pauseGame = true;
		    gameOverSequence("Crashed into\n" + e.n);
		    try{Thread.sleep(1700);}catch(Exception o){}
		    clear();
		    System.out.print("\033[H");
		    System.out.flush();
		}
		return;
	    }
	    e.reset(HEIGHT, divider);
	}
    }

    void addEnemies() {
	for(int q=0; q < 8; q++) {
            collisions.add(new Entity(                                          rand.nextInt(1,divider),
                rand.nextInt(HEIGHT/2, HEIGHT),
		enemies[rand.nextInt(0,enemies.length)]));
	    if(collisions.get(q).x % 6 == 0)                                   collisions.get(q).x--;
	}
    }

    void clear(){
	try {
	    new ProcessBuilder("clear")
                    .inheritIO().start().waitFor();
	}catch(Exception n) {}
    }

    private int infoX(String info) {
	int x = (WIDTH - 1 - divider);
	return divider + x/2 - len(info)/4;
    }

    private int len(String i) {
	return i.length();
    }

    void gameOverSequence(String reason) {
        String over = gameOver ? "G AM E\nOV ER\nYour Score:  " + String.valueOf(score) : reason;
        clear();
        try{
            Process process = Runtime.getRuntime()
                .exec(new String[] {
                        "figlet","-ctf","slant", over});
            java.util.Scanner s = new java.util
                    .Scanner(process.getInputStream())
                    .useDelimiter("\\A");

            if(gameOver)System.out.print("\u001B[31m");
            System.out.println(s.hasNext()?s.next():"");
        } catch (Exception e) {}
    }

    public static void main(String... args) { new CarCrash();}
}

/**                                                              * ReadInput handles keyboard input from the terminal in a separ
ate thread.
 */
class ReadInput {                                                   static Thread thread;                                           static Terminal terminal;
    static volatile char input;                                     static volatile boolean run = true;

    /**                                                              * Starts the input thread and initializes the terminal.
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

    /**                                                              * Stops the input thread and closes the terminal.
     */
    public static void end() {
        try {                                                               run = false;                                                    if (terminal != null) terminal.close();                         if (thread != null) {
                thread.interrupt();                                         }
            thread = null;
        } catch (Exception e) {}                                    }

    /**
     * Spawns a new thread that reads single characters from the
 terminal.
     */
    private static void input() {
        try {
            thread = new Thread(() -> {                                         try {
                    while (run) input = (char) terminal.reader()
.read();                                                                        } catch (Exception q) {}                                    });
            thread.start();
        } catch (Exception e) {}
    }                                                                                                                               /**                                                              * Gets the terminal height, with fallback if not available.
     *
     * @return The height of the terminal window.
     */
    static int height() {
        int h = 30;
        try {                                                               h = terminal.getHeight();                                   } catch (Exception n) {}                                        return h > 40 ? h / 2 + 4 : h - 2;                          }

    /**
     * Gets the terminal width, with fallback if not available.      *                                                               * @return The width of the terminal window.                     */                                                             static int width() {                                                int w = 62;
        try {                                                               w = terminal.getWidth();                                    } catch (Exception n) {}                                        return w;
    }
}

public class Entity {
    int x, y;
    String n;
    Random rand = new Random();

    public Entity(int x, int y, String n) {
        this.x = x;
        this.y = y;
        this.n = n;
    }

    void move() {
        y--;
    }

    void reset(int p, int divider) {
        if (y < 1) {
            y = rand.nextInt(p/2, p);
            x = rand.nextInt(1, divider);
            if (x % 6 == 0) x++;
        }
    }
}

