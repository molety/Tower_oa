import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
//import javax.microedition.lcdui.game.*;

/**
 * MIDlet (アプリケーション)
 */
public class TowerMIDlet extends MIDlet
{
	/** 自分自身を保持する */
	private static TowerMIDlet instance;
	/** Canvasオブジェクト */
	private TowerCanvas t3dCanvas;
	/** スレッド */
	private static Thread th = null;

	/**
	 * コンストラクタ
	 */
	public TowerMIDlet()
	{
		instance = this;

		t3dCanvas = new TowerCanvas();

		Display.getDisplay(this).setCurrent(t3dCanvas);

//		if (th != null) {
//			System.out.println("th != null !!");
//			if (th.isAlive()) {
//				System.out.println("th.isAlive() !!");
//			}
//		}
		th = new Thread(t3dCanvas);
		th.start();
//		System.out.println("TowerMIDlet() !!");
	}

	/**
	 * アプリケーション開始時に呼ばれる。
	 */
	public void startApp()
	{
//		System.out.println("startApp() !!");
	}

	/**
	 * アプリケーション一時停止時に呼ばれる。
	 */
	public void pauseApp()
	{
//		System.out.println("pauseApp() !!");
	}

	/**
	 * アプリケーション終了時に呼ばれる。
	 * @param unconditional (IN ) 無条件に終了する必要がある場合は真
	 */
	public void destroyApp(boolean unconditional)
	{
		try {
			if (th != Thread.currentThread()) {
				t3dCanvas.kill();
				th.join();
//				System.out.println("joined !!");
			}
		} catch (InterruptedException e) {
		}
//		th = null;
//		System.out.println("destroyApp() !!");
	}

	/**
	 * MIDletオブジェクトを取得する。
	 * @return MIDletオブジェクト
	 */
	public static TowerMIDlet getInstance()
	{
		return instance;
	}

	/**
	 * アプリケーションを終了する。
	 */
	public void terminate()
	{
		destroyApp(true);
		notifyDestroyed();
	}
}

