import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

/**
 * �L�����o�X
 */
public class TowerCanvas extends GameCanvas implements Runnable, CommandListener
{
	/** �L�����o�X�� */
	public final int CANVAS_WIDTH = getWidth();
	/** �L�����o�X���� */
	public final int CANVAS_HEIGHT = getHeight();

	/** �I���R�}���h */
	private static final Command cmdQuit = new Command("�I��", Command.SCREEN, 1);
	/** ����(�}�j���A���\��)�R�}���h */
	private static final Command cmdManual = new Command("����", Command.SCREEN, 1);
	/** �߂�R�}���h */
	private static final Command cmdBack = new Command("�߂�", Command.SCREEN, 1);

	/** ���C�����[�v���p�����邩�ǂ��� */
	private volatile boolean threadLoop = true;
	/** �����I�����ꂽ���ǂ��� */
	private volatile boolean isKilled = false;
	/** �}�j���A���\�����[�h */
	private volatile boolean manualMode = false;
	/** �����ꂽ�L�[�R�[�h */
	private volatile int pressedKeyCode = 0;

	/** �T�E���h�v���C���[ */
	private Player[] player = null;

	/**
	 * �R���X�g���N�^
	 */
	public TowerCanvas()
	{
		super(false);

		addCommand(cmdQuit);
		addCommand(cmdManual);
		setCommandListener(this);
	}

	/**
	 * �X���b�h�J�n���ɌĂ΂��B
	 */
	public void run()
	{
		try {
			// ��ʏ�����
			Graphics g = getGraphics();
			final int LEFT_TOP = g.LEFT|g.TOP;
//			Display d = Display.getDisplay(TowerMIDlet.getInstance());

			Font mainFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
			g.setFont(mainFont);
			g.setColor(255, 255, 255);
			g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
			g.setColor(0, 0, 0);
			g.drawString("T O W E R", 80, 0, LEFT_TOP);
			g.drawString("FLOOR", 60, 200, LEFT_TOP);
			g.drawString("KEY", 60, 220, LEFT_TOP);
			g.drawString("CRYSTAL", 60, 240, LEFT_TOP);



			player = new Player[7];
			InputStream sndis;
			sndis = getClass().getResourceAsStream("/stairs.mid");
			player[0] = Manager.createPlayer(sndis, "audio/midi");
			sndis = getClass().getResourceAsStream("/door.mid");
			player[1] = Manager.createPlayer(sndis, "audio/midi");
			sndis = getClass().getResourceAsStream("/hole.mid");
			player[2] = Manager.createPlayer(sndis, "audio/midi");
			sndis = getClass().getResourceAsStream("/key.mid");
			player[3] = Manager.createPlayer(sndis, "audio/midi");
			sndis = getClass().getResourceAsStream("/crystal.mid");
			player[4] = Manager.createPlayer(sndis, "audio/midi");
			sndis = getClass().getResourceAsStream("/exit.mid");
			player[5] = Manager.createPlayer(sndis, "audio/midi");
			sndis = getClass().getResourceAsStream("/congratulations.mid");
			player[6] = Manager.createPlayer(sndis, "audio/midi");
			for (int i = 0; i < 7; i++) {
				player[i].setLoopCount(1);
				player[i].prefetch();
				VolumeControl vc = (VolumeControl)player[i].getControl("VolumeControl");
				vc.setLevel(30);
				long duration = player[i].getDuration();
				player[i].setMediaTime(0);
//				player[i].start();
//				while (player[i].getMediaTime() < duration) {
//				}
//				player[i].stop();
				player[i].deallocate();
			}



			int keyState;
			String mapFile = "/MAPDATA0.DAT";
			while ((keyState = getKeyStates()) == 0);
			if ((keyState & UP_PRESSED) != 0) {
				mapFile = "/MAPDATA1.DAT";
			} else if ((keyState & DOWN_PRESSED) != 0) {
				mapFile = "/MAPDATA2.DAT";
			} else if ((keyState & LEFT_PRESSED) != 0) {
				mapFile = "/MAPDATA3.DAT";
			} else if ((keyState & RIGHT_PRESSED) != 0) {
				mapFile = "/MAPDATA4.DAT";
			}

			InputStream is = getClass().getResourceAsStream(mapFile);
			for (int i = 0; i < 7; i++) {
				is.read();
			}
			byte[][][] map = new byte[20][20][20];
			for (int floor = 0; floor < 20; floor++) {
				for (int y = 0; y < 20; y++) {
					for (int x = 0; x < 20; x++) {
						map[floor][x][y] = (byte)is.read();
					}
				}
			}
			int manIniX = is.read();
			int manIniY = is.read();
			int manIniFloor = is.read();
			int totalDoors = is.read();
			int totalExits = is.read();
			int totalKeys = is.read();
			int totalCrystals = is.read();
			is.close();

			Image chipImg = Image.createImage("/chip.png");
			Image manImg = Image.createImage("/man.png");

			int manX = manIniX;
			int manY = manIniY;
			int manFloor = manIniFloor;

			int manPrevX = manX;
			int manPrevY = manY;

			int nKeys = 0;
			int nCrystals = 0;

			boolean needAllRedraw = true;

			// ���C�����[�v
			while (threadLoop) {
				if (needAllRedraw) {
					for (int y = 0; y < 20; y++) {
						for (int x = 0; x < 20; x++) {
							g.drawRegion(chipImg, map[manFloor][x][y] * 8, 0, 8, 8,
										 Sprite.TRANS_NONE,
										 x * 8 + 40, y * 8 + 40, LEFT_TOP);
						}
					}
					needAllRedraw = false;
				} else {
					g.drawRegion(chipImg, map[manFloor][manPrevX][manPrevY] * 8, 0, 8, 8,
								 Sprite.TRANS_NONE,
								 manPrevX * 8 + 40, manPrevY * 8 + 40, LEFT_TOP);
				}
				g.drawRegion(manImg, 0, 0, 8, 8,
							 Sprite.TRANS_NONE,
							 manX * 8 + 40, manY * 8 + 40, LEFT_TOP);

				g.setColor(255, 255, 255);
				g.fillRect(120, 200, 40, 60);
				g.setColor(0, 0, 0);
				g.drawString(Integer.toString(manFloor + 1), 120, 200, LEFT_TOP);
				g.drawString(Integer.toString(nKeys), 120, 220, LEFT_TOP);
				g.drawString(Integer.toString(nCrystals), 120, 240, LEFT_TOP);

				flushGraphics();

				manPrevX = manX;
				manPrevY = manY;

				keyState = getKeyStates();
				if ((keyState & FIRE_PRESSED) != 0) {
					switch (map[manFloor][manX][manY]) {
					  case 2:
						manFloor--;
						needAllRedraw = true;
						playSound(0);
						break;
					  case 3:
						manFloor++;
						needAllRedraw = true;
						playSound(0);
						break;
					}
				} else {
					if ((keyState & UP_PRESSED) != 0) {
						manY--;
					} else if ((keyState & DOWN_PRESSED) != 0) {
						manY++;
					} else if ((keyState & LEFT_PRESSED) != 0) {
						manX--;
					} else if ((keyState & RIGHT_PRESSED) != 0) {
						manX++;
					}
					switch (map[manFloor][manX][manY]) {
					  case 1:
						manX = manPrevX;
						manY = manPrevY;
						break;
					  case 4:
						if (manX != manPrevX || manY != manPrevY) {
							do {
								manFloor--;
							} while (map[manFloor][manX][manY] == 4);
							needAllRedraw = true;
							playSound(2);
						}
						break;
					  case 5:
						if (nKeys > 0) {
							nKeys--;
							map[manFloor][manX][manY] = 0;
							playSound(1);
						} else {
							manX = manPrevX;
							manY = manPrevY;
						}
						break;
					  case 6:
						if (nCrystals > 9) {
							map[manFloor][manX][manY] = 0;
							playSound(5);
							playSound(6);
						} else {
							manX = manPrevX;
							manY = manPrevY;
						}
						break;
					  case 7:
						nKeys++;
						map[manFloor][manX][manY] = 0;
						playSound(3);
						break;
					  case 8:
						nCrystals++;
						map[manFloor][manX][manY] = 0;
						playSound(4);
						break;
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

			if (isKilled == false) {
				TowerMIDlet.getInstance().terminate();
			}
		} catch (Exception e) {
//			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * �R�}���h���s���ɌĂ΂��B
	 * @param c (IN ) �R�}���h
	 * @param d (IN ) �\���Ώ�
	 */
	public void commandAction(Command c, Displayable d)
	{
		if (c == cmdQuit) {
			threadLoop = false;
		} else if (c == cmdManual) {
			removeCommand(cmdManual);
			addCommand(cmdBack);
			manualMode = true;
		} else if (c == cmdBack) {
			removeCommand(cmdBack);
			addCommand(cmdManual);
			manualMode = false;
		}
	}

	/**
	 * �L�[�������ꂽ���ɌĂ΂��B
	 * @param keyCode (IN ) �L�[�R�[�h
	 */
	public void keyPressed(int keyCode)
	{
		pressedKeyCode = keyCode;
	}

	/**
	 * �L�[�������ꂽ���ɌĂ΂��B
	 * @param keyCode (IN ) �L�[�R�[�h
	 */
	public void keyReleased(int keyCode)
	{
		pressedKeyCode = 0;
	}

	/**
	 * �L�����o�X�������I������B
	 */
	public void kill()
	{
		isKilled = true;
		threadLoop = false;
	}

	/**
	 * �T�E���h�����t����B
	 */
	public void playSound(int i) throws MediaException
	{
		player[i].prefetch();
		player[i].setMediaTime(0);
		player[i].start();
		while (player[i].getMediaTime() < player[i].getDuration()) {
		}
		player[i].stop();
		player[i].deallocate();
	}
}
