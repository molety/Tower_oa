import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
//import javax.microedition.lcdui.game.*;

/**
 * MIDlet (�A�v���P�[�V����)
 */
public class TowerMIDlet extends MIDlet
{
	/** �������g��ێ����� */
	private static TowerMIDlet instance;
	/** Canvas�I�u�W�F�N�g */
	private TowerCanvas t3dCanvas;
	/** �X���b�h */
	private static Thread th = null;

	/**
	 * �R���X�g���N�^
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
	 * �A�v���P�[�V�����J�n���ɌĂ΂��B
	 */
	public void startApp()
	{
//		System.out.println("startApp() !!");
	}

	/**
	 * �A�v���P�[�V�����ꎞ��~���ɌĂ΂��B
	 */
	public void pauseApp()
	{
//		System.out.println("pauseApp() !!");
	}

	/**
	 * �A�v���P�[�V�����I�����ɌĂ΂��B
	 * @param unconditional (IN ) �������ɏI������K�v������ꍇ�͐^
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
	 * MIDlet�I�u�W�F�N�g���擾����B
	 * @return MIDlet�I�u�W�F�N�g
	 */
	public static TowerMIDlet getInstance()
	{
		return instance;
	}

	/**
	 * �A�v���P�[�V�������I������B
	 */
	public void terminate()
	{
		destroyApp(true);
		notifyDestroyed();
	}
}

