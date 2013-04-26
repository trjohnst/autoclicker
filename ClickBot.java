import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.MouseInfo;


class ClickBot extends Thread{
	
	/**
	 * fields
	 */
	private Robot clickBot;
	int second = 1000;
	long sleepTime;
	int mouseX;
	int mouseY;
	int configuredX;
	int configuredY;
	
	
	/**
	 * Constructor
	 * @param clicksps
	 * @param configuredX
	 * @param configuredY
	 */
	public ClickBot(double clicksps, int configuredX, int configuredY) {
		try {
			clickBot = new Robot();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		this.configuredX = configuredX;
		this.configuredY = configuredY;
		
		this.sleepTime = (long)(second/clicksps);
	}

	/**
	 * while running, sleep, move the cursor, click and move the cursor back
	 */
	public void run() {
		while(true) {
			try{
				sleep(sleepTime);
			}
			catch(InterruptedException e){
				break;
			}
			mouseX = MouseInfo.getPointerInfo().getLocation().x;
			mouseY = MouseInfo.getPointerInfo().getLocation().y;
			
			clickBot.mouseMove(configuredX, configuredY);
			clickBot.mousePress(InputEvent.BUTTON1_MASK);
			clickBot.mouseRelease(InputEvent.BUTTON1_MASK);
			clickBot.mouseMove(mouseX, mouseY);
		}
	}
	
	/**
	 * allows for "in use" updating of the configured click location
	 * sleeps momentarily and then changes its click location
	 * @param configuredX
	 * @param configuredY
	 */
	public void updateClickLocation(int configuredX, int configuredY) {
		try {
			sleep(10);
		} catch(InterruptedException e){
			System.out.println(e.getMessage());
		}
		this.configuredX = configuredX;
		this.configuredY = configuredY;
	}
	
}