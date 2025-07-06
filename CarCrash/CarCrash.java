import java.util.*;                                             import org.jline.terminal.*;                                    import org.jline.reader.*;

/**                                                              * CarCrash is a console-based car driving game.
 * It uses the JLine library for terminal input and output handling.
 */
public class CarCrash {
   private Timer gameLoop;
   private int repeatTime = 250;
   private int score,life,divider;
   private int HEIGHT,WIDTH;
   private boolean pauseGame,gameOver;

   public CarCrash() {
	ReadInput.start();
	clear();

	HEIGHT = ReadInput.height();
	WIDTH = ReadInput.width();
	divider = (WIDTH/3)*2 - 5;
	life = 3;

	changeSpeed();
   }

   private String getBoard() {
       StringBuilder sb = new StringBuilder();
       
       for(int i=0; i < HEIGHT; i++) {
            for(int a=0; a<WIDTH; a++) {
                  if(i == 0 || i == HEIGHT - 1)
                     sb.append("\u001B[35m#");
                  else if(a == 0 || a == WIDTH - 1)
                     sb.append("\u001B[35m#");
		  else {
		     String s = "Remaining Live";
		     int cur = infoX(s);
		     char[] arr = s.toCharArray();
		     sb.append("\u001B[34m");

		     if(a == divider) sb.append("\u001B[35m#");
		     else if(i == (HEIGHT - 1)/2 - 6 && 
			a >= cur && a < cur+len(s)) {
			sb.append(arr[a - cur]);
		     }
		     else if(i == (HEIGHT - 1)/2 - 5) {
			s = "";
			for(int y=0; y<life; y++) s += "♥️";
			cur = infoX(s);
                        if(a >= cur && a < cur+life) {
			   sb.append("♥️");
			}
			else sb.append(" ");
                     }
		     else sb.append(" ");
		  }
	    }
	    sb.append("\n");
       }
       return sb.toString();
   }

   void changeSpeed() {
        if(gameLoop != null) gameLoop.cancel();
        gameLoop = new Timer();
        gameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
                public void run() {
		    if(WIDTH != ReadInput.width()) {
			WIDTH = ReadInput.width();
			HEIGHT = ReadInput.height();
			divider = (WIDTH/3)*2 - 5;
			pauseGame = true;
		    }
		    if(!pauseGame && !gameOver) {
			System.out.print("\033[H");
                        System.out.flush();                                             System.out.print(getBoard());
		    }
		}
	},0,repeatTime);
    }

    void updateScreen() {
	
    }

    void clear(){
	try {
	    new ProcessBuilder("clear")
                    .inheritIO().start().waitFor();
	}catch(Exception n) {}
    }

    private int infoX(String info) {
	int x = (WIDTH - 1 - divider);
	return divider + x/2 - len(info)/2;
    }

    private int len(String i) {
	return i.length();
    }

    public static void main(String... args) {
       new CarCrash();
   }
}

/**                                                              * ReadInput handles keyboard input from the terminal in a separ
ate thread.
 */                                                             class ReadInput {                                                   static Thread thread;                                           static Terminal terminal;
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
